package com.robam.steamoven.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robam.steamoven.R;
import com.robam.steamoven.base.AppAdapter;


/**
 *    author : 210190
 *    robam
 *    time   : 2021/12/15
 *    desc   : 模式 温度时间选择
 */
public final class RvTimeOrTempAdapter extends AppAdapter<Integer> {
    /**
     * 当前选中居中的
     */
    private int index ;
    /**
     * 需要选择的模式 模式 温度 时间
     */
    private String unit = " " ;

    public RvTimeOrTempAdapter(Context context , String unit ) {
        super(context);
        this.unit = unit ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder();
//        int parentWidth = parent.getWidth();
//        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
//        layoutParams.width =  (parentWidth/ 5);//显示三条
        return viewHolder;
    }



    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;
        private final TextView mTvUnitTemp;
        private final TextView mTvUnitTime;

        private ViewHolder() {
            super(R.layout.steam_item_temp_time_select);
            mTextView = findViewById(R.id.tv_select);
            mTvUnitTemp = findViewById(R.id.tv_unit_temp);
            mTvUnitTime = findViewById(R.id.tv_unit_time);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindView(int position) {
            int item = getItem(position);
                mTextView.setText(String.valueOf(item));
                //角标单位控制
            if (position == index){
                if (getString(R.string.steam_unit_temp).equals(unit)){
                    mTvUnitTemp.setVisibility(View.VISIBLE);
                    mTvUnitTime.setVisibility(View.GONE);
                }else if(getString(R.string.steam_unit_minu).equals(unit)){
                    mTvUnitTemp.setVisibility(View.GONE);
                    mTvUnitTime.setVisibility(View.VISIBLE);
                }else {
                    mTvUnitTemp.setVisibility(View.GONE);
                    mTvUnitTime.setVisibility(View.GONE);
                }
            }else {
                mTvUnitTemp.setVisibility(View.GONE);
                mTvUnitTime.setVisibility(View.GONE);
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
}