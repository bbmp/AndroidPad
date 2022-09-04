package com.robam.dishwasher.ui.pages;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.ui.view.ExtImageSpan;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.base.DishWasherBasePage;
import com.robam.dishwasher.bean.DishWaherModeBean;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.ui.adapter.RvMainModeAdapter;

import java.util.List;

public class HomePage extends DishWasherBasePage {
    private RecyclerView rvMain;
    private RvMainModeAdapter rvMainModeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private ImageView imageView;
    private TextView tvFunhint;
    private TextView tvTime, tvTemp, tvTempUnit;

    @Override
    protected int getLayoutId() {
        return R.layout.dishwasher_page_layout_home;
    }

    @Override
    protected void initView() {
        showFloat(); //快捷图标
        rvMain = findViewById(R.id.rv_main);
        imageView = findViewById(R.id.iv_bg);
        tvFunhint = findViewById(R.id.tv_fun_hint);
        tvTime = findViewById(R.id.tv_time);
        tvTemp = findViewById(R.id.tv_temp);
        tvTempUnit = findViewById(R.id.tv_temp_unit);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvMainModeAdapter.setPickPosition(position);
                        //设置参数
                        setData(rvMainModeAdapter.getItem(position));
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainModeAdapter = new RvMainModeAdapter();
        rvMain.setAdapter(rvMainModeAdapter);
        showCenter();
        setOnClickListener(R.id.iv_float);
    }
    //模式参数设置
    private void setData(DishWaherModeBean modeBean) {
        if (null != modeBean) {
            tvFunhint.setText(modeBean.desc);
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
    }

    @Override
    protected void initData() {
        DishWasher.getInstance().init(getContext());
        List<DishWaherModeBean> modeBeanList = DishWasher.getInstance().getDishWaherModeBeans();

        rvMainModeAdapter.setList(modeBeanList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % modeBeanList.size();
        rvMainModeAdapter.setPickPosition(initPos);
        pickerLayoutManager.scrollToPosition(initPos);
        setBackground(initPos);

        setData(rvMainModeAdapter.getItem(initPos));

        rvMainModeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                DishWaherModeBean dishWaherModeBean = (DishWaherModeBean) adapter.getItem(position);

                DishWasher.getInstance().workMode = dishWaherModeBean.code;
                Intent intent = new Intent();
//                intent.putExtra("mode", dishWaherFunBean);
                intent.setClassName(getContext(), dishWaherModeBean.into);
                startActivity(intent);
            }
        });
    }
    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
        int resId = getResources().getIdentifier(rvMainModeAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        ImageUtils.loadGif(getContext(), resId, imageView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_float)
            getActivity().finish();
    }
}
