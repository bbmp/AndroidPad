package com.robam.dishwasher.ui.activity;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWaherModeBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;

public class ModeSelectActivity extends DishWasherBaseActivity {
    private RadioGroup radioGroup;
    //模式
    private TextView tvMode;
    private TextView tvTime, tvTemp, tvTempUnit;
    private RadioButton rButton1, rButton2, rButton3, rButton4;
    private TextView tvStartHint;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_activity_mode_select;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        radioGroup = findViewById(R.id.rg_aux);
        tvMode = findViewById(R.id.tv_mode);
        tvTime = findViewById(R.id.tv_time);
        tvTemp = findViewById(R.id.tv_temp);
        tvTempUnit = findViewById(R.id.tv_temp_unit);
        rButton1 = findViewById(R.id.rb_button1);
        rButton2 = findViewById(R.id.rb_button2);
        rButton3 = findViewById(R.id.rb_button3);
        rButton4 = findViewById(R.id.rb_button4);
        tvStartHint = findViewById(R.id.tv_start);
        setRight(R.string.dishwasher_appointment);
        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.btn_start);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_button1) {
                    DishWasher.getInstance().auxMode = DishWasherConstant.AUX_PAN_POWFULL;
                } else if (checkedId == R.id.rb_button2) {
                    DishWasher.getInstance().auxMode = DishWasherConstant.AUX_KILL_POWFULL;
                } else if (checkedId == R.id.rb_button3) {
                    DishWasher.getInstance().auxMode = DishWasherConstant.AUX_FLUSH;
                } else if (checkedId == R.id.rb_button4) {
                    DishWasher.getInstance().auxMode = DishWasherConstant.AUX_DOWN_WASH;
                } else
                    DishWasher.getInstance().auxMode = -1;
            }
        });
    }

    @Override
    protected void initData() {
        //当前模式
        DishWaherModeBean modeBean = DishWasher.getInstance().getDishWaherModeBean(DishWasher.getInstance().workMode);
        if (null != modeBean) {
            setData(modeBean);
            switch (modeBean.code) {
                case DishWasherConstant.MODE_FLUSH:
                case DishWasherConstant.MODE_SELFCLEAN: {
                    //是否立即启动
                    radioGroup.setVisibility(View.GONE);
                    tvStartHint.setVisibility(View.VISIBLE);
                }
                break;
                case DishWasherConstant.MODE_SMART:
                case DishWasherConstant.MODE_QUICK: {
                    rButton1.setVisibility(View.INVISIBLE);
                    rButton4.setVisibility(View.INVISIBLE);
                }
                break;
                case DishWasherConstant.MODE_POWFULL:
                case DishWasherConstant.MODE_SAVING:
                case DishWasherConstant.MODE_DAILY:
                case DishWasherConstant.MODE_BABYCARE: {

                }
                break;
                case DishWasherConstant.MODE_BRIGHT: {
                    rButton1.setVisibility(View.INVISIBLE);
                    rButton4.setVisibility(View.INVISIBLE);
                    rButton2.setText(R.string.dishwasher_flush);
                    rButton3.setText(R.string.dishwasher_down_wash);
                }
                break;
            }
        }
    }

    //模式参数设置
    private void setData(DishWaherModeBean modeBean) {
        tvMode.setText(modeBean.name);
        String time = TimeUtils.secToHourMinH(modeBean.time);
        SpannableString spannableString = new SpannableString(time);
        int pos = time.indexOf("h");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pos = time.indexOf("min");
        if (pos >= 0)
            spannableString.setSpan(new RelativeSizeSpan(0.5f), pos, pos + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTime.setText(spannableString);
        int temp = modeBean.temp;
        if (temp > 0) {
            tvTemp.setText(temp + "");
            tvTempUnit.setVisibility(View.VISIBLE);
        } else {
            tvTemp.setText("");
            tvTempUnit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //预约
            startActivity(new Intent(this, AppointmentActivity.class));
        } else if (id == R.id.btn_start) {
            //开始工作
            startActivity(WorkActivity.class);
            finish();
        } else if (id == R.id.ll_left) {
            //返回
            finish();
        }
    }
}