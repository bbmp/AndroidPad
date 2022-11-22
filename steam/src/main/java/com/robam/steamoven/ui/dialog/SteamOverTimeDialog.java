package com.robam.steamoven.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.robam.common.ui.dialog.BaseDialog;
import com.robam.common.ui.dialog.FullDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;
import java.util.ArrayList;
import java.util.List;

public class SteamOverTimeDialog extends BaseDialog {
    private TextView mCancelTv;
    private TextView mOkTv;
    private RecyclerView rvSelect;
    /**
     * 模式选择
     */
    private RvTimeAdapter rvTimeAdapter;



    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private String value;

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
        setLayoutManage(5, 0.44f);
        //setLayoutManage(1,1);
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
        rvTimeAdapter = new RvTimeAdapter(1);
        rvSelect.setAdapter(rvTimeAdapter);

        //默认模式
        setList(getDataList());
    }

    private List<String> getDataList(){
        List<String> timeList = new ArrayList<>();
        for(int i = 1;i <= 10;i++){
            timeList.add(i+"");
        }
        return timeList;
    }

    public void setList(List<String> selectList) {
        rvTimeAdapter.setList(selectList);

        //初始位置
        int initPos = 4;
        value = selectList.get(initPos%selectList.size());
        pickerLayoutManager.scrollToPosition(initPos);
        rvTimeAdapter.setPickPosition(initPos);
    }

    private void setLayoutManage(int maxItem, float scale) {
        pickerLayoutManager = new PickerLayoutManager.Builder(rvSelect.getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(maxItem)
                .setScale(scale)
                .setOnPickerListener((recyclerView, position) -> {

                    rvTimeAdapter.setPickPosition(position);
                    value = rvTimeAdapter.getItem(position);
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }

    public String getCurValue(){
        return value;
    }


}
