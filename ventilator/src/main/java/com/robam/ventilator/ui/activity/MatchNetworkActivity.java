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


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_match_network;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvHint = findViewById(R.id.tv_match_hint);

        setOnClickListener(R.id.tv_ok);
    }

    @Override
    protected void initData() {
        String string = getResources().getString(R.string.ventilator_match_hint1);
        SpannableString spannableString = new SpannableString(string);
        Drawable drawable = null;
        int pos = string.indexOf(" ");
        if (pos < 0 )
            pos = 0;
        drawable = getResources().getDrawable(R.drawable.logo_roki);
        drawable.setBounds(0, 0, (int) getResources().getDimension(com.robam.common.R.dimen.dp_32),
                (int) getResources().getDimension(com.robam.common.R.dimen.dp_32));

        spannableString.setSpan(new ExtImageSpan(drawable), pos, pos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvHint.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_ok)
            finish();
    }
}