package com.robam.pan.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.pan.R;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

//电量提示框
public class ElectricQuantityDialog extends BaseDialog {
    private TextView mOkTv;
    private TextView mContent;
    private Banner banner;


    public ElectricQuantityDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.pan_dialog_layout_electric_quantity, null);
        mOkTv = rootView.findViewById(R.id.tv_ok);
        mContent = rootView.findViewById(R.id.tv_work_content);
        banner = rootView.findViewById(R.id.banner_recharge);
        List<Integer> mDatas = new ArrayList();
        mDatas.add(R.drawable.pan_step1);
        mDatas.add(R.drawable.pan_step2);
        BannerImageAdapter bannerImageAdapter = new BannerImageAdapter<Integer>(mDatas) {

            @Override
            public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                holder.imageView.setImageResource(data);
            }
        };
        banner.setAdapter(bannerImageAdapter).setIndicator(new CircleIndicator(mContext), false);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
    }

    @Override
    public void setContentText(CharSequence contentStr) {
        mContent.setText(contentStr);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != banner)
            banner.stop();
    }
}
