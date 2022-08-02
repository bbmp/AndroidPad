package com.robam.steamoven.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robam.steamoven.R;
import com.robam.steamoven.base.AppAdapter;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.constant.SteamOvenModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;

import java.util.List;

/**
 * author : 210190
 * robam
 * time   : 2021/12/15
 * desc   : 模式温度选择
 */
public final class RvModeFootAdapter extends AppAdapter<Integer> {
    /**
     * 当前选中的数据
     */
    private int index = 0;
    /**
     * 当前底部的模式
     */
    ModeBean modeBean;

    public RvModeFootAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder();
        return viewHolder;
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;
        private final TextView mTvUnitTemp;
        private final TextView mTvUnitTime;
        private final TextView mTvUpDown;
        private final ImageView mImageSelectCheck;

        private ViewHolder() {
            super(R.layout.steam_item_mode_foot);
            mTextView = findViewById(R.id.tv_select);
            mTvUnitTemp = findViewById(R.id.tv_unit_temp);
            mTvUnitTime = findViewById(R.id.tv_unit_time);
            mTvUpDown = findViewById(R.id.tv_up_down);
            mImageSelectCheck = findViewById(R.id.iv_image_select_check);
        }

        @Override
        public void onBindView(int position) {
            int value = getItem(position);
            mTvUnitTemp.setVisibility(View.GONE);
            mTvUnitTime.setVisibility(View.GONE);
            mTvUpDown.setVisibility(View.GONE);
            if (modeBean.steamSelect()) {
                //加湿烤模块 澎湃蒸
                if (position == 0) {
//                SteamOvenModeEnum match = SteamOvenModeEnum.match(value);
                    mTextView.setText(modeBean.name);
                } else if (position == 1) {
                    SteamOvenSteamEnum match = SteamOvenSteamEnum.match(value);
                    mTextView.setText(match.getValue());
                } else if (position == 2) {
                    mTextView.setText(String.valueOf(value));
                    mTvUnitTemp.setVisibility(View.VISIBLE);
                    mTvUnitTime.setVisibility(View.GONE);
                } else if (position == 3) {
                    mTextView.setText(String.valueOf(value));
                    mTvUnitTemp.setVisibility(View.GONE);
                    mTvUnitTime.setVisibility(View.VISIBLE);
                }
            } else if (modeBean.code == SteamOvenModeEnum.EXP.getCode()){
                if (position == 0) {
                    mTextView.setText(modeBean.name);
                } else if (position == 1) {
                    mTextView.setText(String.valueOf(value));
                    mTvUpDown.setText("上");
                    mTvUnitTemp.setVisibility(View.VISIBLE);
                    mTvUnitTime.setVisibility(View.GONE);
                    mTvUpDown.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    mTextView.setText(String.valueOf(value));
                    mTvUpDown.setText("下");
                    mTvUnitTemp.setVisibility(View.VISIBLE);
                    mTvUnitTime.setVisibility(View.GONE);
                    mTvUpDown.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    mTextView.setText(String.valueOf(value));
                    mTvUnitTemp.setVisibility(View.GONE);
                    mTvUnitTime.setVisibility(View.VISIBLE);
                }
            }else {
                if (position == 0) {
                    SteamOvenModeEnum match = SteamOvenModeEnum.match(value);
                    mTextView.setText(match.getValue());
                } else if (position == 1) {
                    mTextView.setText(String.valueOf(value));
                    mTvUnitTemp.setVisibility(View.VISIBLE);
                    mTvUnitTime.setVisibility(View.GONE);
                } else if (position == 2) {
                    mTextView.setText(String.valueOf(value));
                    mTvUnitTemp.setVisibility(View.GONE);
                    mTvUnitTime.setVisibility(View.VISIBLE);
                }
            }

            if (position == index) {
                mTextView.setTextSize(34);
                mTextView.setTextColor(getColor(R.color.steam_common_select_mode_text_color));
                mImageSelectCheck.setVisibility(View.VISIBLE);
            } else {
                mTextView.setTextSize(30);
                mTextView.setTextColor(getColor(R.color.steam_common_select_mode_text_color2));
                mImageSelectCheck.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public int getIndex() {
        return index;
    }

    public void setData(@Nullable List<Integer> data, ModeBean modeBean) {
        super.setData(data);
        this.modeBean = modeBean;

    }

    public ModeBean getModeBean() {
        return modeBean;
    }

    public void setModeBean(ModeBean modeBean) {
        this.modeBean = modeBean;
    }
}
