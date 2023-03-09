package com.robam.stove.device;

import android.content.Context;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.Plat;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.bean.StepParams;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.bean.StoveRecipeDetail;

import java.lang.ref.WeakReference;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeStove {

    //当前进入的灶具锅首页
    public static HomeStove getInstance() {
        return HomeStove.StoveHolder.instance;
    }
    private static class StoveHolder {
        private static final HomeStove instance = new HomeStove();
    }
    //当前guid
    public String guid;
    /**
     * 当前功能
     */
    public int funCode;
    //获取设备平台
    public String getDp() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (null != device.guid && device.guid.equals(guid) && null != device.dp)
                return device.dp;
        }
        return "";
    }
//
//    /**
//     * 左灶工作模式
//     */
//    public int leftWorkMode;
//    /**
//     * 左灶工作时长
//     */
//    public String leftWorkHours;
//    //左灶工作温度
//    public String leftWorkTemp;
//    //左灶
//    public MutableLiveData<Boolean> leftStove = new MutableLiveData<>(false);
//    /**
//     * 右灶工作模式
//     */
//    public int rightWorkMode;
//    /**
//     * 右灶工作时长
//     */
//    public String rightWorkHours;
//    //右灶工作温度
//    public String rightWorkTemp;
//    //右灶
//    public MutableLiveData<Boolean> rightStove = new MutableLiveData<>(false);
    public boolean isStoveOffline() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RRQZ) && device.status == Device.ONLINE) {

                return false;
            }
        }
        return true;
    }
    private ThreadPoolExecutor cookCountDown = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    public interface CookCallback {
        void workComplete(int stoveId);

        void notifyItemChanged(int curStep); //刷进度

        void loadImage(String image); //刷图片

        void setFan(String value);// 设置风量

        void setGear(String value); //设置挡位

        void nextStep(int curStep);
    }

    public void startCook(StoveRecipeDetail stoveRecipeDetail, int stoveId, CookCallback cookCallback) {
        WeakReference<CookCallback> weakReference = new WeakReference<>(cookCallback);
        if (null != stoveRecipeDetail) {

            cookCountDown.execute(new Runnable() {

                @Override

                public void run() {
                    int curStep = 0;

                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (null == stoveRecipeDetail.stepRespDtoList || curStep >= stoveRecipeDetail.stepRespDtoList.size()) {
                            //工作结束
                            //提示烹饪完成
                            if (null != weakReference.get())
                                weakReference.get().workComplete(stoveId);
                            return;
                        }
                        RecipeStep recipeStep = stoveRecipeDetail.stepRespDtoList.get(curStep);

                        if (recipeStep.needTime > 0) {
                            recipeStep.elapsedTime++;
                            if (recipeStep.elapsedTime == recipeStep.needTime) {

                                nextStep(stoveRecipeDetail, curStep, stoveId, weakReference);
                            }
                            if (null != weakReference.get())
                                weakReference.get().notifyItemChanged(curStep);

                        }
                    }
                }

            });
        }
    }

    private void nextStep(StoveRecipeDetail stoveRecipeDetail, int curStep, int stoveId, WeakReference<CookCallback> weakReference) {
        curStep++;
        if (null != weakReference.get())
            weakReference.get().nextStep(curStep);
        setData(stoveRecipeDetail, curStep, stoveId, weakReference);
    }

    private void setData(StoveRecipeDetail stoveRecipeDetail, int curStep, int stoveId, WeakReference<CookCallback> weakReference) {
        if (curStep >= stoveRecipeDetail.stepRespDtoList.size())
            return;
        RecipeStep recipeStep = stoveRecipeDetail.stepRespDtoList.get(curStep);
        //图片
        if (null != weakReference.get())
            weakReference.get().loadImage(recipeStep.image);

        if (null != recipeStep && null != recipeStep.devicePlatformStrList) {
            if (recipeStep.devicePlatformStrList.contains(HomeStove.getInstance().getDp())) {

                //烟机风量
                if (null != weakReference.get())
                    weakReference.get().setFan("风量：" + recipeStep.fanGear);
                //设置烟机风量
                IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
                if (null != iPublicVentilatorApi) {
                    if (recipeStep.fanGear >= 1 && recipeStep.fanGear <= 3)
                        iPublicVentilatorApi.setFanGear(1); //弱档
                    else if (recipeStep.fanGear >= 4 && recipeStep.fanGear <= 6)
                        iPublicVentilatorApi.setFanGear(3); //强档
                    else if (recipeStep.fanGear >= 7 && recipeStep.fanGear <= 9)
                        iPublicVentilatorApi.setFanGear(6); //强档
                }

                //炉头
                if (null != weakReference.get())
                    weakReference.get().setGear("火力：" + recipeStep.stoveGear);
                //设置灶具挡位
                StoveAbstractControl.getInstance().setLevel(HomeStove.getInstance().guid, stoveId, 0x01, recipeStep.stoveGear, (int) stoveRecipeDetail.id, recipeStep.no);


            }

        }
    }
}
