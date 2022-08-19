package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.UiAutomation;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.robam.common.utils.ImageUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.AccountInfo;
import com.robam.ventilator.bean.UserInfo;

public class PersonalCenterActivity extends VentilatorBaseActivity {
    private TextView tvLogin;

    private RecyclerView rvDevice, rvDot;
    private Group group1, group2;
    //头像
    private ImageView ivHead;
    //昵称和手机
    private TextView tvName, tvPhone;


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_personal_center;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvDevice = findViewById(R.id.rv_device);
        rvDot = findViewById(R.id.rv_dot);
        group1 = findViewById(R.id.ventilator_group3); //登录
        group2 = findViewById(R.id.ventilator_group4); //未登录
        ivHead = findViewById(R.id.iv_head);
        tvName = findViewById(R.id.tv_nickname);
        tvPhone = findViewById(R.id.tv_phone);
        setOnClickListener(R.id.tv_login, R.id.btn_exit_login);

        rvDevice.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    @Override
    protected void initData() {
        //监听用户信息
        AccountInfo.getInstance().getUser().observe(this, new Observer<UserInfo>() {
            @Override
            public void onChanged(UserInfo userInfo) {
                setUserInfo(userInfo);
            }
        });
    }

    private void setUserInfo(UserInfo user) {
        if (null == user) { //未登录
            group1.setVisibility(View.GONE);
            group2.setVisibility(View.VISIBLE);
        } else {
            UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
            group1.setVisibility(View.VISIBLE);
            group2.setVisibility(View.GONE);
            if (null != userInfo) {
                ImageUtils.loadImage(this, userInfo.figureUrl, ivHead);
                tvName.setText(userInfo.nickname);
                tvPhone.setText(userInfo.phone);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login) {  //默认手机登录
            startActivity(LoginPhoneActivity.class);
        } else if (id == R.id.btn_exit_login) { //退出登录

            AccountInfo.getInstance().getUser().setValue(null);
        }
    }

    //获取绑定的设备
}