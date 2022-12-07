package com.robam.foodmaterialdetect;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodMaterialHelper {
    static {
        System.loadLibrary("foodmaterialdetect");
    }

    /**
     * A native method that is implemented by the 'foodmaterialdetect' native library,
     * which is packaged with this application.
     */
    public static native boolean modelInit(AssetManager am, String modelName);
    public static native float[] imageClassify(String imagePath);
    //烟雾检测
    public native double smokeDetect(String imagePath);

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static void init(Context context) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                modelInit(context.getResources().getAssets(), "model.rknn");//加载模型,只需加载1次,阻塞型接口,耗时1~2秒左右
            }
        });
    }

    private static ArrayList<String> getFromAssets(Context context, String fileName) {
        ArrayList<String> arr = new ArrayList<String>(0);
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open(fileName), StandardCharsets.UTF_8);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            while((line = bufReader.readLine()) != null){
                arr.add(line);
            }
            inputReader.close();
            bufReader.close();
            return arr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }
    public static void classify(Context context, String imgpath, FoodMaterialDetectCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                float [] result = imageClassify(imgpath);//输入图片输出识别结果,耗时200ms左右,结果是不排序的,需要自己找最大值
                if(result.length == 0) {
                    if (null != callback)
                        callback.onResult(null); //失败
                } else {
                    ArrayList<String> labels = getFromAssets(context, "labels.txt");
                    if(labels.size() != result.length) {
                        if (null != callback)
                            callback.onResult(""); //识别成功,但标签和模型不一致
                    } else {
                        float max = 0;
                        String classes = null;
                        for(int i = 0; i < result.length; i++) {
                            if (result[i] > max) {
                                max = result[i];
                                classes = labels.get(i);
                            }
                        }
                        if (null != callback)
                            callback.onResult(classes); //返回识别类型
                    }
                }
            }
        });
    }

}
