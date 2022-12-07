package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.robam.common.utils.LogUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class ScreenBrightnessActivity extends VentilatorBaseActivity {
    private AppCompatSeekBar sbrLight;
    /**
     * 屏幕亮度模式
     */
    private int screenMode;
    /**
     * 屏幕亮度最大值
     */
    private int screenBrightness;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_screen_brightness;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_screen_brightness);
        sbrLight = findViewById(R.id.sbr_light);
        sbrLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                setScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void initData() {
        customScreenBrightness();
    }

    /**
     * 获取屏幕亮度模式和屏幕亮度，并设置为手动模式调节亮度到最高
     */
    private void customScreenBrightness() {
        try {
            /**
             * * 获得当前屏幕亮度的模式
             * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
             * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
             *
             */
            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            LogUtils.i( "screenMode = " + screenMode);
            // 获得当前屏幕亮度值 0--255
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            LogUtils.i( "screenBrightness = " + screenBrightness);

            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            sbrLight.setProgress(screenBrightness);
//            sbrLight.setMax(screenBrightness);
//            setScreenBrightness(255);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置屏幕亮度模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     *
     * @param value
     */
    private void setScreenMode(int value) {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, value);
    }

    /**
     * 设置屏幕亮度
     *
     * @param value
     */
    private void setScreenBrightness(float value) {
        try {
            Window mWindow = getWindow();
            WindowManager.LayoutParams params = mWindow.getAttributes();
            float f = value / 255.0f;
            params.screenBrightness = f;
            mWindow.setAttributes(params);
            // 保存设置的屏幕亮度值
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}