package com.robam.steamoven.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.ui.adapter.RvModeAdapter;
import java.util.ArrayList;
import java.util.List;

public class SteamOverTimeDialog extends BaseDialog {
    private TextView mCancelTv;
    private TextView mOkTv;
    private RecyclerView rvSelect;
    /**
     * 模式选择
     */
    private RvModeAdapter rvModeAdapter;

    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    public SteamOverTimeDialog(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.steam_dialog_layout_overtime, null);
        mCancelTv = rootView.findViewById(R.id.tv_cancel);
        mOkTv = rootView.findViewById(R.id.tv_ok);
        rvSelect = rootView.findViewById(R.id.pager);
        if (mDialog == null) {
            mDialog = new FullDialog(mContext, rootView);
        }
        setLayoutManage(1,1);
    }
    @Override
    public void setContentText(int res) {
        //mContent.setText(res);
    }

    @Override
    public void setCancelText(int res) {
        mCancelTv.setText(res);
    }

    @Override
    public void setOKText(int res) {
        mOkTv.setText(res);
    }

    public void setData(){
        rvModeAdapter = new RvModeAdapter();
        rvSelect.setAdapter(rvModeAdapter);

        //默认模式
        setList(getTestDataList());
    }

    private List<ModeBean> getTestDataList(){
        List<ModeBean> modeBeanList = new ArrayList<>();
        for(int i = 0;i < 10;i++){
            ModeBean modeBean = new ModeBean();
            modeBean.name = i + "";
            modeBeanList.add(modeBean);
        }
        return modeBeanList;
    }

    public void setList(List<ModeBean> selectList) {
        rvModeAdapter.setList(selectList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE/2) % selectList.size();
        pickerLayoutManager.scrollToPosition(initPos);
        rvModeAdapter.setPickPosition(initPos);

    }

    private void setLayoutManage(int maxItem, float scale) {
        pickerLayoutManager = new PickerLayoutManager.Builder(rvSelect.getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(maxItem)
                .setScale(scale)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {

                        rvModeAdapter.setPickPosition(position);

//                        if (null != tab) {
//                            //切换模式
//                            TextView textView = tab.getCustomView().findViewById(R.id.tv_mode);
//                            textView.setText(rvModeAdapter.getItem(position).name);
//                        }
//                        if (null != iModeSelect) {
//                            iModeSelect.updateTab( rvModeAdapter.getItem(position).code);
//                        }
                    }
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }


}
