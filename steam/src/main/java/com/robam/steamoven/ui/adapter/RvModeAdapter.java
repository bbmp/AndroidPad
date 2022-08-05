package com.robam.steamoven.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.robam.steamoven.R;
import com.robam.steamoven.base.AppAdapter;
import com.robam.steamoven.bean.model.ModeBean;

/**
 *    author : 210190
 *    robam
 *
 *    time   : 2021/12/15
 *    desc   : 模式选择
 */
public final class RvModeAdapter extends AppAdapter<ModeBean> {
    /**
     * 当前选中居中的
     */
    private int index ;
    /**
     * 需要选择的模式 模式 温度 时间(0, 1, 2)
     */
    private int type ;
    /**
     * 数据长度（无线循环取余数用）
     */
    private int num ;
    /**
     * 总长度
     */
    private int count  ;
    public RvModeAdapter(Context context , int num ) {
        super(context);
        this.num = num ;
    }

    public RvModeAdapter(Context context, int num, int type) {
        super(context);
        this.num = num;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder();

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if(num == 1){
            return num ;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public ModeBean getItem(int position) {
        position = position % num ;
        return super.getItem(position);
    }

    private final class ViewHolder extends AppAdapter<?>.ViewHolder {

        private final TextView mTextView;

        private ViewHolder() {
            super(R.layout.steam_item_mode_select);
            mTextView = findViewById(R.id.tv_select);
        }

        @Override
        public void onBindView(int position) {
            ModeBean item = getItem(position);
            int i = item.code;
            if (type == 0){
//                SteamOvenModeEnum match = SteamOvenModeEnum.match(i);
                mTextView.setText(item.name);
            }

        }
    }

    public void setIndex(int index) {
        this.index = index;
        if (num == 1){
            this.index = 0 ;
        }
    }

    public int getIndex() {
        return index;
    }
}
