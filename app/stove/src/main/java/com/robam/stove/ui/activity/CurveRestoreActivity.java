package com.robam.stove.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.factory.StoveDialogFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CurveRestoreActivity extends StoveBaseActivity {
    //曲线详情
    private StoveCurveDetail stoveCurveDetail;
    //
    private IDialog closeCookDialog;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    //从0开始
    private int curTime = 0;
    private int lastMark = 0; //最后点
    private TextView tvFire, tvTemp, tvTime;
    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_restore;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        if (null != getIntent())
            stoveCurveDetail = (StoveCurveDetail) getIntent().getSerializableExtra(StoveConstant.EXTRA_CURVE_DETAIL);
        tvFire = findViewById(R.id.tv_fire);
        tvTemp = findViewById(R.id.tv_temp);
        tvTime = findViewById(R.id.tv_time);
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {
        if (null != stoveCurveDetail) {
            Map<Integer, String> params = null;
            try {
                params = new Gson().fromJson(stoveCurveDetail.temperatureCurveParams, new TypeToken<LinkedHashMap<Integer, String>>(){}.getType());
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                params = null;
            }
            if (null != params)
                startRestore(params);
        }
    }

    //开始还原
    private void startRestore(Map<Integer, String> params) {

        Iterator<Integer> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            lastMark = iterator.next();
        }
        runnable = new Runnable() {

            @Override

            public void run() {

                String value = params.get(curTime);
                if (!TextUtils.isEmpty(value)) {
                    String[] values = value.split("-");
                    if (null != values && values.length >= 2) {
                        tvFire.setText("火力 " + values[1] + "档");
                        tvTemp.setText(values[0] + "℃");
                    }
                }
                curTime++;
                tvTime.setText(DateUtil.secForMatTime3(curTime));

                if (curTime >= lastMark) {
                    //工作结束
                    //提示烹饪完成
                    workComplete();
                    return;
                }
                mHandler.postDelayed(runnable, 1000L);

            }

        };
        mHandler.post(runnable);
    }
    //还原结束提示
    private void workComplete() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_COMPLETE);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {
                //回首页
                startActivity(MainActivity.class);
            }
        }, R.id.tv_ok);
        iDialog.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left) {
            stopCook();
        }
    }
    //结束烹饪提示
    private void stopCook() {
        if (null == closeCookDialog) {
            closeCookDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            closeCookDialog.setCancelable(false);
            closeCookDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok)
                        finish();
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        closeCookDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}