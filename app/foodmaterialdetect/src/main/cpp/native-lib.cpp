#include <jni.h>
#include <string>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <android/log.h>
#include <ctime>

#include "rknn_api.h"
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/imgcodecs.hpp"

#define TAG "FMD"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

#define MODEL_IN_WIDTH  299
#define MODEL_IN_HEIGHT 299

static rknn_context ctx = 0;
static unsigned char *model = nullptr;
static rknn_input_output_num io_num;

static void printRKNNTensor(rknn_tensor_attr *attr) {
    LOGD("index=%d name=%s n_dims=%d dims=[%d %d %d %d] n_elems=%d size=%d fmt=%d type=%d qnt_type=%d fl=%d zp=%d scale=%f\n",
         attr->index, attr->name, attr->n_dims, attr->dims[0], attr->dims[1], attr->dims[2], attr->dims[3],
         attr->n_elems, attr->size, 0, attr->type, attr->qnt_type, attr->fl, attr->zp, attr->scale);
}

static bool internal_model_init(AAssetManager *mgr, const char *str_name) {
    int model_len = 0;
    int ret;
    struct timespec start = {0}, end = {0};

    if(model) {
        delete [] model;
        model = nullptr;
    }

    clock_gettime(CLOCK_MONOTONIC, &start);
    AAsset *asset = AAssetManager_open(mgr, str_name, AASSET_MODE_UNKNOWN);
    off_t buffer_size = AAsset_getLength(asset);
    model = new unsigned char[buffer_size + 1];
    model_len = AAsset_read(asset, model, buffer_size);
    //LOGD("read %d bytes", model_len);
    AAsset_close(asset);

    if(!model_len) {
        LOGD("load model failed\n");
        return false;
    }
    LOGD("load model ok");
    ret = rknn_init(&ctx, model, model_len, 0, nullptr);
    if(ret < 0) {
        LOGD("rknn_init fail! ret=%d\n", ret);
        return false;
    }
    clock_gettime(CLOCK_MONOTONIC, &end);
    LOGD("rknn_init ok %ld,%ld", end.tv_sec - start.tv_sec, end.tv_nsec - start.tv_nsec);

    ret = rknn_query(ctx, RKNN_QUERY_IN_OUT_NUM, &io_num, sizeof(io_num));
    if (ret != RKNN_SUCC) {
        LOGD("rknn_query fail! ret=%d\n", ret);
        return false;
    }
    LOGD("model input num: %d, output num: %d\n", io_num.n_input, io_num.n_output);

    LOGD("input tensors:\n");
    rknn_tensor_attr input_attrs[io_num.n_input];
    memset(input_attrs, 0, sizeof(input_attrs));
    for (int i = 0; i < io_num.n_input; i++) {
        input_attrs[i].index = i;
        ret = rknn_query(ctx, RKNN_QUERY_INPUT_ATTR, &(input_attrs[i]), sizeof(rknn_tensor_attr));
        if (ret != RKNN_SUCC) {
            LOGD("rknn_query fail! ret=%d\n", ret);
            return false;
        }
        printRKNNTensor(&(input_attrs[i]));
    }

    return true;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_robam_foodmaterialdetect_FoodMaterialHelper_modelInit(JNIEnv *env, jobject /*this*/, jobject am,
                                                           jstring model_name) {
    jboolean ret = false;
    AAssetManager *mgr = AAssetManager_fromJava(env, am);
    if(!mgr) {
        LOGD("AAssetManager_fromJava failed");
        return ret;
    }

    jclass class_str = env->FindClass("java/lang/String");
    jstring str_encode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(class_str, "getBytes", "(Ljava/lang/String;)[B");
    auto barr= (jbyteArray)env->CallObjectMethod(model_name, mid, str_encode);
    jsize str_len = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (str_len > 0 && ba) {
        char *str_name = new char[str_len + 1];
        memcpy(str_name, ba, str_len);
        str_name[str_len] = '\0';

        if(internal_model_init(mgr, str_name)) {
            ret = true;
        }
        delete [] str_name;
    } else {
        LOGD("GetByteArrayElements failed");
    }

    env->ReleaseByteArrayElements(barr, ba, 0);
    return ret;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_robam_foodmaterialdetect_FoodMaterialHelper_imageClassify(JNIEnv *env, jobject /*this*/, jstring image_path) {
    jfloatArray result;
    struct timespec start = {0}, end = {0};
    bool ret;
    cv::Mat orig_img;

    clock_gettime(CLOCK_MONOTONIC, &start);
    jclass class_str = env->FindClass("java/lang/String");
    jstring str_encode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(class_str, "getBytes", "(Ljava/lang/String;)[B");
    auto barr= (jbyteArray)env->CallObjectMethod(image_path, mid, str_encode);
    jsize str_len = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (str_len > 0 && ba) {
        char *str = new char[str_len + 1];
        memcpy(str, ba, str_len);
        str[str_len] = '\0';
        orig_img = imread(str, cv::IMREAD_COLOR);
        delete [] str;
    } else {
        LOGD("GetByteArrayElements 2 failed");
    }

    if(orig_img.data == nullptr) {
        LOGD("imread fail");
        result = env->NewFloatArray(0);
        env->SetFloatArrayRegion(result, 0, 0, nullptr);
        return result;
    }

    cv::Mat img = orig_img.clone();
    if(orig_img.cols != MODEL_IN_WIDTH || orig_img.rows != MODEL_IN_HEIGHT) {
        printf("resize %d %d to %d %d\n", orig_img.cols, orig_img.rows, MODEL_IN_WIDTH, MODEL_IN_HEIGHT);
        cv::resize(orig_img, img, cv::Size(MODEL_IN_WIDTH, MODEL_IN_HEIGHT), (0, 0), (0, 0), cv::INTER_LINEAR);
    }

    // Set Input Data
    rknn_input inputs[1];
    memset(inputs, 0, sizeof(inputs));
    inputs[0].index = 0;
    inputs[0].type = RKNN_TENSOR_UINT8;
    inputs[0].size = img.cols * img.rows * img.channels();
    inputs[0].fmt = RKNN_TENSOR_NHWC;
    inputs[0].buf = img.data;

    ret = rknn_inputs_set(ctx, io_num.n_input, inputs);
    if(ret < 0) {
        LOGD("rknn_input_set fail! ret=%d\n", ret);
        result = env->NewFloatArray(0);
        env->SetFloatArrayRegion(result, 0, 0, nullptr);
        return result;
    }

    // Run
    LOGD("rknn_run\n");
    ret = rknn_run(ctx, nullptr);
    if(ret < 0) {
        LOGD("rknn_run fail! ret=%d\n", ret);
        result = env->NewFloatArray(0);
        env->SetFloatArrayRegion(result, 0, 0, nullptr);
        return result;
    }

    // Get Output
    rknn_output outputs[1];
    memset(outputs, 0, sizeof(outputs));
    outputs[0].want_float = 1;
    ret = rknn_outputs_get(ctx, 1, outputs, nullptr);
    if(ret < 0) {
        LOGD("rknn_outputs_get fail! ret=%d\n", ret);
        result = env->NewFloatArray(0);
        env->SetFloatArrayRegion(result, 0, 0, nullptr);
        return result;
    }
    clock_gettime(CLOCK_MONOTONIC, &end);
    LOGD("calc:%ld, %ld", end.tv_sec - start.tv_sec, end.tv_nsec - start.tv_nsec);

    LOGD("output tensors:\n");
    rknn_tensor_attr output_attrs[io_num.n_output];
    memset(output_attrs, 0, sizeof(output_attrs));
    for (int i = 0; i < io_num.n_output; i++) {
        output_attrs[i].index = i;
        ret = rknn_query(ctx, RKNN_QUERY_OUTPUT_ATTR, &(output_attrs[i]), sizeof(rknn_tensor_attr));
        if (ret != RKNN_SUCC) {
            LOGD("rknn_query fail! ret=%d\n", ret);
            result = env->NewFloatArray(0);
            env->SetFloatArrayRegion(result, 0, 0, nullptr);
            return result;
        }
        printRKNNTensor(&(output_attrs[i]));
    }

    // Post Process
//    for (int i = 0; i < io_num.n_output; i++) {
//        for (int j = 0; j < (int)output_attrs[i].n_elems; j++) {
//            float val = ((float *)(outputs[i].buf))[j];
//            LOGD("%d:%f", j, val);
//        }
//    }
    float *data = new float[output_attrs[0].n_elems];
    for (int j = 0; j < (int)output_attrs[0].n_elems; j++) {
        data[j] = ((float *)(outputs[0].buf))[j];
        LOGD("%d:%f", j, data[j]);
    }
    result = env->NewFloatArray(output_attrs[0].n_elems);
    env->SetFloatArrayRegion(result, 0, output_attrs[0].n_elems, data);
    delete [] data;

    // Release rknn_outputs
    rknn_outputs_release(ctx, 1, outputs);

    return result;
}