package com.robam.ventilator.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robam.common.ui.AbsPage;
import com.robam.ventilator.R;
import com.robam.ventilator.base.BasePage;

public class HomeFragment extends BasePage {


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_home_fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


}