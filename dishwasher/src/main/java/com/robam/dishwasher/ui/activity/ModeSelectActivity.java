package com.robam.dishwasher.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBaseActivity;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherEnum;
import com.robam.dishwasher.constant.ModeConstant;

public class ModeSelectActivity extends DishWasherBaseActivity {
    private RadioGroup radioGroup;
    private TextView tvMode;
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
                    DishWasher.getInstance().auxMode = ModeConstant.AUX_PAN_POWFULL;
                } else if (checkedId == R.id.rb_button2) {
                    DishWasher.getInstance().auxMode = ModeConstant.AUX_KILL_POWFULL;
                } else if (checkedId == R.id.rb_button3) {
                    DishWasher.getInstance().auxMode = ModeConstant.AUX_FLUSH;
                } else if (checkedId == R.id.rb_button4) {
                    DishWasher.getInstance().auxMode = ModeConstant.AUX_DOWN_WASH;
                } else
                    DishWasher.getInstance().auxMode = -1;
            }
        });
    }

    @Override
    protected void initData() {
        //当前工作模式
        tvMode.setText(DishWasherEnum.match(DishWasher.getInstance().workMode));
        switch (DishWasher.getInstance().workMode) {
            case ModeConstant.MODE_FLUSH:
            case ModeConstant.MODE_SELFCLEAN:{
                //是否立即启动
                radioGroup.setVisibility(View.GONE);
                tvStartHint.setVisibility(View.VISIBLE);
            }
            break;
            case ModeConstant.MODE_SMART:
            case ModeConstant.MODE_QUICK: {
                rButton1.setVisibility(View.INVISIBLE);
                rButton4.setVisibility(View.INVISIBLE);
            }
            break;
            case ModeConstant.MODE_POWFULL:
            case ModeConstant.MODE_SAVING:
            case ModeConstant.MODE_DAILY:
            case ModeConstant.MODE_BABYCARE: {

            }
            break;
            case ModeConstant.MODE_BRIGHT: {
                rButton1.setVisibility(View.INVISIBLE);
                rButton4.setVisibility(View.INVISIBLE);
                rButton2.setText(R.string.dishwasher_flush);
                rButton3.setText(R.string.dishwasher_down_wash);
            }
            break;
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