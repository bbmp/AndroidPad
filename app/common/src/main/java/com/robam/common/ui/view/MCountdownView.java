package com.robam.common.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;

/**
 * 倒计时控件
 */
public class MCountdownView extends AppCompatTextView implements Runnable {

    /**
     * 倒计时秒数
     */
    private int mTotalSecond = 60;
    /**
     * 秒数单位文本
     */
    private static final String TIME_UNIT = "S";

    /**
     * 当前秒数
     */
    private int mCurrentSecond;
    /**
     * 记录原有的文本
     */
    private CharSequence mRecordText;
    /**
     * 暂停倒计时
     */
    public boolean pause = false;
    /**
     * 已经开始标志
     */
    public boolean strat = false;

    public MCountdownView(Context context) {
        super(context);
    }

    public MCountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MCountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置倒计时总秒数
     */
    public void setTotalTime(int totalTime) {
        this.mTotalSecond = totalTime;
        mCurrentSecond = mTotalSecond;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        mRecordText = getText();
        setEnabled(false);
        //从+1开始
        mCurrentSecond = mTotalSecond + 1;
        post(this);
        strat = true ;
    }

    /**
     * 暂停
     */
    public void pause() {
        pause = true;
    }

    /**
     * 继续倒计时
     */
    public void continueSecond() {
        if(pause){
            pause = false;
        }else {
            start();
        }

    }

    /**
     * 结束倒计时
     */
    public void stop() {
//        setText(mRecordText);
        setEnabled(true);
        pause = false ;
        strat = false ;
        removeCallbacks(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除延迟任务，避免内存泄露
        removeCallbacks(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void run() {
        LogUtils.i("mCurrentSecond = " + mCurrentSecond);

        if (pause) {
//            setText(mCurrentSecond + " " + TIME_UNIT);
//            String time = DateUtil.secForMatTime(mCurrentSecond);
//            setText(time);
        } else {
            mCurrentSecond--;
//            String time = DateUtil.secForMatTime2(mCurrentSecond);
//            setText(time);
            if (mCurrentSecond <= 0) {
                stop();
                if (onCountDownListener != null){
                    onCountDownListener.onCountDown(mCurrentSecond);
                }
                return;
            }
            //去应用层自己设置
            if (onCountDownListener != null){
                onCountDownListener.onCountDown(mCurrentSecond);
            }
        }
        postDelayed(this, 1000);
    }

    /**
     * 剩余时间
     * @return
     */
    public int getMCurrentSecond(){
        return mCurrentSecond;
    }
    /**
     * 倒计时剩余时间监听
     */
    OnCountDownListener onCountDownListener ;
    /**
     * 倒计时剩余时间监听
     */
    public interface OnCountDownListener{
        void onCountDown(int currentSecond) ;
    }

    public void addOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }
}
