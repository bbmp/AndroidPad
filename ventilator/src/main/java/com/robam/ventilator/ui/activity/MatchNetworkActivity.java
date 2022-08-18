package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.view.ExtImageSpan;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class MatchNetworkActivity extends VentilatorBaseActivity {
    private TextView tvHint;
    private String model = "5"; //设备类型
    private TextView tvNext, tvOk;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_match_network;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvHint = findViewById(R.id.tv_match_hint);
        tvNext = findViewById(R.id.tv_next);
        tvOk = findViewById(R.id.tv_ok);

        setOnClickListener(R.id.tv_next, R.id.tv_ok);
    }

    @Override
    protected void initData() {
        SpannableString spannableString = null;
        String string = null;
        if ("1".equals(model)) {
            //灶具
            string = getResources().getString(R.string.ventilator_match_hint4);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ("2".equals(model)) {
            //锅
            string = getResources().getString(R.string.ventilator_match_hint3);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if ("3".equals(model)) {
            //消毒柜
            string = getResources().getString(R.string.ventilator_match_hint1);
            spannableString = new SpannableString(string);
            Drawable drawable = getResources().getDrawable(R.drawable.logo_roki);
            drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                    (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));
            int pos = string.indexOf("[");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            pos = string.indexOf("\"");
            if (pos >= 0)
                spannableString.setSpan(new ExtImageSpan(drawable), pos + 1, pos + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else if ("4".equals(model)) {
            //洗碗机
            string = getResources().getString(R.string.ventilator_match_hint6);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        } else {
            string = getResources().getString(R.string.ventilator_match_hint7);
            spannableString = new SpannableString(string);
            tvNext.setVisibility(View.GONE);
            tvOk.setVisibility(View.VISIBLE);
        }


        tvHint.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_ok)
            finish();
        else if (id == R.id.tv_next) {
            //下一步
            //开启蓝牙
        }
    }
}