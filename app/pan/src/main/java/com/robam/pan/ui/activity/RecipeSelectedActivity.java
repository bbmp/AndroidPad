package com.robam.pan.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;

//菜谱选中页面
public class RecipeSelectedActivity extends PanBaseActivity {
    private TextView tvRight;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRight();
        tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.pan_recipe_detail);
        setOnClickListener(R.id.tv_right, R.id.tv_start_cook);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_right) {
            //菜谱详情
            startActivity(RecipeDetailActivity.class);
        } else if (id == R.id.tv_start_cook) {
            //开始烹饪
            startActivity(CurveRestoreActivity.class);
        }
    }
}