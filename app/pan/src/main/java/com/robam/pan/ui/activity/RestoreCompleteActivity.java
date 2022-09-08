package com.robam.pan.ui.activity;

import android.view.View;
import android.widget.TextView;

import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.bean.PanRecipeDetail;
import com.robam.pan.constant.PanConstant;

//曲线还原结束界面
public class RestoreCompleteActivity extends PanBaseActivity {
    private TextView tvRecipeName;

    //曲线详情
    private PanCurveDetail panCurveDetail;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_restore_complete;
    }

    @Override
    protected void initView() {
        showCenter();

        if (null != getIntent())
            panCurveDetail = (PanCurveDetail) getIntent().getSerializableExtra(PanConstant.EXTRA_CURVE_DETAIL);
        tvRecipeName = findViewById(R.id.tv_recipe_name);

        setOnClickListener(R.id.btn_back_home);
    }

    @Override
    protected void initData() {
        if (null != panCurveDetail) {
            tvRecipeName.setText(panCurveDetail.name);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_back_home) {
            //返回锅首页
            startActivity(MainActivity.class);
        }
    }
}