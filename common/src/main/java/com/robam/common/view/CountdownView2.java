package com.robam.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class CountdownView2 extends AppCompatTextView implements Runnable {

    /** 倒计时秒数 */
    private int mTotalSecond = 3;
    /** 秒数单位文本 */
    private static final String TIME_UNIT = "s";

    /** 当前秒数 */
    private int mCurrentSecond;
    /** 记录原有的文本 */
    private CharSequence mRecordText;

    public CountdownView2(Context context) {
        super(context);
    }

    public CountdownView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountdownView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置倒计时总秒数
     */
    public void setTotalTime(int totalTime) {
        this.mTotalSecond = totalTime;
    }

    /**
     * 开始倒计时
     */
    public void start(StopLinstener stopLinstener) {
        this.stopLinstener = stopLinstener ;
//        mRecordText = getText();
//        setEnabled(false);
        mCurrentSecond = mTotalSecond;
        post(this);
    }

    /**
     * 结束倒计时
     */
    public void stop() {
//        setText(mRecordText);
        setEnabled(true);
        stopLinstener.stop();
    }
    public void artStop(){
//        setText(mRecordText);
        setEnabled(true);
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
        if (mCurrentSecond == 0) {
            stop();
            return;
        }

        setText("跳过" +mCurrentSecond + "" + TIME_UNIT );
        mCurrentSecond--;
        postDelayed(this, 1000);
    }

    StopLinstener stopLinstener ;
    public interface StopLinstener{
        void stop();
    }
}
