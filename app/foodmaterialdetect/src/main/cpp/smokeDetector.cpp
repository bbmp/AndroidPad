// smokeDetector.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//

#include <iostream>

#include "opencv2/opencv.hpp"
#include <jni.h>

using namespace std;
using namespace cv;
#define HEIGHT 600
#define WIDTH 800
#define uchar unsigned char

#define TAG "SMD"
#include <android/log.h>
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define MODEL_IN_WIDTH  299
#define MODEL_IN_HEIGHT 299

/// <summary>
/// 从大到小排序，并返回下标
/// </summary>
/// <typeparam name="T"></typeparam>
/// <param name="array"></param>
/// <returns></returns>
template<typename T> std::vector<int> argsort(const std::vector<T>& array)
{
	const int array_len(array.size());
	std::vector<int> array_index(array_len, 0);
	for (int i = 0; i < array_len; ++i)
		array_index[i] = i;

	std::sort(array_index.begin(), array_index.end(),
		[&array](int pos1, int pos2) {return (array[pos1] < array[pos2]); });

	return array_index;
}

/// <summary>
/// 获取RGB三个通道中最小值
/// </summary>
/// <param name="img"></param>
/// <returns></returns>
Mat getMinRGB(Mat& img) {
	Mat desImg(Size(WIDTH, HEIGHT), CV_8UC1);
	for (int i = 0; i < HEIGHT; i++) {
		for (int j = 0; j < WIDTH; j++) {
			int r = img.ptr<Vec3b>(i)[j][0];
			int g = img.ptr<Vec3b>(i)[j][1];
			int b = img.ptr<Vec3b>(i)[j][2];
			desImg.ptr<uchar>(i)[j] = MIN(MIN(img.ptr<Vec3b>(i)[j][0], img.ptr<Vec3b>(i)[j][1]), img.ptr<Vec3b>(i)[j][2]);
		}
	}
	return desImg;
}

/// <summary>
/// 最小值滤波，邻域内最小,卷积核大小默认15
/// </summary>
/// <param name="src"></param>
/// <param name="des"></param>
/// <param name="ksize"></param>
Mat MinFilter(Mat& src, int ksize = 15)
{
	int r = (ksize - 1) / 2;
	Mat des(Size(WIDTH, HEIGHT), CV_8UC1);
	Mat dst_ex(Size(src.cols + ksize - 1, src.rows + ksize - 1), CV_8UC1);
	copyMakeBorder(src, dst_ex, r, r, r, r, BORDER_DEFAULT);//padding
	uchar min = 255;
	for (int i = r; i < HEIGHT + r; i++)
	{
		for (int j = r; j < WIDTH + r; j++)
		{
			min = 255;
			for (int m = i - r; m < i + ksize - r; m++)
			{
				for (int n = j - r; n < j + ksize - r; n++)
				{
					if (dst_ex.ptr<uchar>(m)[n] < min)
						min = dst_ex.ptr<uchar>(m)[n];
				}
			}
			des.ptr<uchar>(i - r)[j - r] = min;
		}
	}
	return des;
}

/// <summary>
/// 获取大气光强
/// </summary>
/// <param name="src"></param>
/// <param name="dark"></param>
/// <param name="outA"></param>
void AtmLight(Mat src, Mat dark, float outA[3])
{
	int row = src.rows;
	int col = src.cols;
	int imgSize = row * col;

	//将暗图像和原图转为列向量
	vector<int> darkVector = dark.reshape(1, imgSize);
	Mat srcVector = src.reshape(3, imgSize);

	//按照亮度的大小取前0.1%的像素（亮度高）
	int numpx = int(max(floor(imgSize / 1000), 1.0));
	vector<int> indices = argsort(darkVector);
	vector<int> dstIndices(indices.begin() + (imgSize - numpx), indices.end());

	for (int i = 0; i < numpx; ++i)
	{
		outA[0] += srcVector.at<Vec3b>(dstIndices[i], 0)[0];
		outA[1] += srcVector.at<Vec3b>(dstIndices[i], 0)[1];
		outA[2] += srcVector.at<Vec3b>(dstIndices[i], 0)[2];
	}
	outA[0] /= numpx;
	outA[1] /= numpx;
	outA[2] /= numpx;
}

/// <summary>
/// 获取折射率图
/// </summary>
/// <param name="img"></param>
/// <param name="A"></param>
/// <param name="oumiga"></param>
/// <returns></returns>
Mat getRefraction(Mat img, float A[3], float oumiga) {
	Mat des(Size(WIDTH, HEIGHT), CV_8UC3);
	vector<Mat> chanels;
	split(img, chanels);
	for (int i = 0; i < 3; ++i)
	{
		chanels[i] = ((chanels[i] / A[i]) * 255);
	}
	merge(chanels, des);
	Mat desImg(Size(WIDTH, HEIGHT), CV_8UC1);
	Mat outMin = getMinRGB(des);
	desImg = 255 - MinFilter(outMin, 15) * oumiga;
	return desImg;
}

/// <summary>
/// 计算油烟浓度
/// </summary>
/// <param name="oriImg"></param>输入图像
/// <returns></returns>返回值为-1时表示图像为空，大于0的值表示烟雾浓度
double smokescope(Mat oriImg)
{
	if (oriImg.empty()) return -1;
	//resize图片大小
	resize(oriImg, oriImg, Size(WIDTH, HEIGHT));
	Mat gray;
	cvtColor(oriImg, gray, COLOR_BGR2GRAY);
	Mat dst;
	cv::threshold(gray, dst, 196, 255, THRESH_BINARY_INV);
	int n = cv::countNonZero(dst);
	//1.找出RGB三个通道最小值
	Mat minRGBImg = getMinRGB(oriImg);
	//2.最小值滤波得到暗通道图
	Mat darkChannel(Size(WIDTH, HEIGHT), CV_8UC1);
	darkChannel = MinFilter(minRGBImg, 15);
	//3.计算大气光强
	float A[3] = { 0 };
	AtmLight(oriImg, darkChannel, A);
	//4.计算折射率图
	float oumiga = 0.7;
	Mat refractionImg(Size(WIDTH, HEIGHT), CV_8UC1);
	refractionImg = getRefraction(oriImg, A, oumiga);

	Mat dst2 = cv::Mat::zeros(WIDTH, HEIGHT, CV_8UC1);
	refractionImg.copyTo(dst2, dst);
	Mat dst3 = cv::Mat::zeros(WIDTH, HEIGHT, CV_8UC1);
	gray.copyTo(dst3, dst);
	double m = sum(dst3)[0]/sum(dst2)[0] ;
	return m;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_com_robam_foodmaterialdetect_FoodMaterialHelper_smokeDetect(JNIEnv *env, jobject /*this*/, jstring image_path) {
    double result;
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

        return 0;
    }

//    cv::Mat img = orig_img.clone();
//    if(orig_img.cols != MODEL_IN_WIDTH || orig_img.rows != MODEL_IN_HEIGHT) {
//        printf("resize %d %d to %d %d\n", orig_img.cols, orig_img.rows, MODEL_IN_WIDTH, MODEL_IN_HEIGHT);
//        cv::resize(orig_img, img, cv::Size(MODEL_IN_WIDTH, MODEL_IN_HEIGHT), (0, 0), (0, 0), cv::INTER_LINEAR);
//    }

    result = smokescope(orig_img);

    return result;
}

