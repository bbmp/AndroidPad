package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.TimeUtils;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.device.HomeSteamOven;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 预约中
 */
public class AppointingActivity extends SteamBaseActivity {
    /**
     * 倒计时
     */
    private MCountdownView tvCountdown;
    //启动提示
    private TextView tvAppointmentHint;
    //工作模式
    private TextView tvMode;
    //工作时长
    private TextView tvWorkHours;

    private MultiSegment segment;

    private TextView defTemp;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_appointing;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        tvCountdown = findViewById(R.id.tv_countdown);
        tvAppointmentHint = findViewById(R.id.tv_appointment_hint);
        tvMode = findViewById(R.id.tv_mode);
        tvWorkHours = findViewById(R.id.tv_time);
        defTemp = findViewById(R.id.tv_temp);
        setOnClickListener(R.id.ll_left, R.id.iv_start);
    }

    @Override
    protected void initData() {
        segment = getIntent().getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        //setCountDownTime();
        //工作时长
        tvWorkHours.setText(HomeSteamOven.getInstance().workHours + "min");
        //工作模式
        tvMode.setText(SteamModeEnum.match(HomeSteamOven.getInstance().workMode));
        defTemp.setText(getSpanTemp(segment.defTemp+""));

        tvWorkHours.setText(getSpan(segment.duration*60));
        int totalTime =HomeSteamOven.getInstance().orderTime * 60;
        tvCountdown.setTotalTime(totalTime);
        tvCountdown.setText(getTimeStr(HomeSteamOven.getInstance().orderTime));
        tvAppointmentHint.setText(startTimePoint(HomeSteamOven.getInstance().orderTime));
    }


    private void initModelView(){

    }

    /**
     * 设置倒计时
     */
    private void setCountDownTime() {
        String orderTime = HomeSteamOven.getInstance().orderTime +"";

        tvAppointmentHint.setText(String.format(getString(R.string.steam_work_order_hint1), orderTime ));
        int housGap = DateUtil.getHousGap(orderTime);
        int minGap = DateUtil.getMinGap(orderTime);
        int totalTime = housGap * 60 * 60 + minGap * 60;
//        SteamOven.getInstance().orderTime = totalTime;
        tvCountdown.setTotalTime(totalTime);

        tvCountdown.addOnCountDownListener(currentSecond -> {
//                SteamOven.getInstance().orderLeftTime = currentSecond;
            String time = DateUtil.secForMatTime2(currentSecond);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvCountdown.setText(time);
                    if (currentSecond <= 0)
                        toStartWork();
                }
            });
        });
        tvCountdown.start();
    }

    private void toStartWork() {
//        CabinetAbstractControl.getInstance().startWork();
        startActivity(WorkActivity.class);
        finish();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            //结束倒计时
            tvCountdown.stop();
            finish();
        } else if (id == R.id.iv_start) {
            //立即开始
            tvCountdown.stop();
            finish();
            Intent intent = new Intent(this,ModelWorkActivity.class);
            List<MultiSegment> list = new ArrayList<>();
            list.add(segment);
            list.get(0).setWorkModel(1);
            intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
            startActivity(intent);
        }
    }

    private String  getTimeStr(int remainingTime){
        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        return (aHour <= 9 ? ("0"+aHour) : aHour) + ":" + (aHour_surplus <= 9 ? ("0"+aHour_surplus) : aHour_surplus);
    }

    private String startTimePoint(int remainingTime){
        Calendar calendar = GregorianCalendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        int aHour = remainingTime / 60;
        int aHour_surplus = remainingTime % 60;
        int addHour = (min + aHour_surplus) / 60;
        int addHour_surplus = (min + aHour_surplus) % 60;

        int totalHour = hour + aHour + addHour;
        int totalMin = addHour_surplus;

        return "将在" + (totalHour <= 9 ? ("0"+totalHour) : totalHour) + ":" + (totalMin <= 9 ? ("0"+totalMin) : totalMin) +"启动工作";
    }

    private SpannableString getSpanTemp(String temp){
        SpannableString spannableString = new SpannableString(temp+Constant.TEMP_UNIT);
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        spannableString.setSpan(new RelativeSizeSpan(0.5f), temp.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(superscriptSpan, temp.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private SpannableString getSpan(int remainTime){
        String time = TimeUtils.secToHourMinUp(remainTime);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}