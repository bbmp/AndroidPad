package com.robam.steamoven.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robam.steamoven.R;
import com.robam.steamoven.base.AppAdapter;
import com.robam.steamoven.constant.SteamOvenSteamEnum;

/**
 * author : 210190
 * robam
 * time   : 2021/12/15
 * desc   : 蒸汽量adapter
 */
public final class RvSteamAdapter extends AppAdapter<Integer> {
    /**
     * 当前选中居中的
     */
    private int index;

    public RvSteamAdapter(Context context) {
        super(context);
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

        private ViewHolder() {
            super(R.layout.steam_item_steam);
            mTextView = findViewById(R.id.tv_select);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindView(int position) {
            int item = getItem(position);
            SteamOvenSteamEnum match = SteamOvenSteamEnum.match(item);
            mTextView.setText(match.getValue());


        }
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public int getIndex() {
        return index;
    }
}