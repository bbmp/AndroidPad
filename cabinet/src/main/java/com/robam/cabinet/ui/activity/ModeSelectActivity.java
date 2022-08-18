package com.robam.cabinet.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBaseActivity;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.bean.CabModeBean;
import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetModeEnum;
import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.factory.CabinetDialogFactory;
import com.robam.cabinet.ui.adapter.RvTimeAdapter;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ClickUtils;

import java.util.ArrayList;
import java.util.List;

public class ModeSelectActivity extends CabinetBaseActivity {
    private RecyclerView rvMode;
    private RvTimeAdapter rvTimeAdapter;
    private PickerLayoutManager pickerLayoutManager;
    private TextView tvMode, tvNum;

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_activity_layout_modeselect;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        setRight(R.string.cabinet_appointment);
        showFloat();

        rvMode = findViewById(R.id.rv_mode);
        tvMode = findViewById(R.id.tv_mode);
        tvNum = findViewById(R.id.tv_num);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(5)
//                .setAlpha(false)
                .setScale(0.44f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        rvTimeAdapter.setPickPosition(position);
                        tvNum.setText(rvTimeAdapter.getItem(position).toString());
                        //设置工作时长
                        Cabinet.getInstance().workHours = Integer.parseInt(rvTimeAdapter.getItem(position));
                    }
                }).build();
        rvMode.setLayoutManager(pickerLayoutManager);
        setOnClickListener(R.id.ll_right, R.id.ll_right_center, R.id.btn_start, R.id.iv_float);
    }

    @Override
    protected void initData() {
        CabFunBean cabFunBean = (CabFunBean) getIntent().getParcelableExtra("mode");
        //当前模式
        Cabinet.getInstance().workMode = (short) cabFunBean.funtionCode;

        List<CabModeBean> beanList = CabinetModeEnum.getModeList();
        List<String> lists = new ArrayList();
        for (CabModeBean bean: beanList) {
            if (bean.code == Cabinet.getInstance().workMode) {
                tvMode.setText(bean.name);
                for (int i = bean.minTime; i<=bean.maxTime; i+=bean.stepTime)
                    lists.add(i + "");
                break;
            }
        }
        rvTimeAdapter = new RvTimeAdapter();
        rvMode.setAdapter(rvTimeAdapter);
        rvTimeAdapter.setList(lists);
        //初始位置
        int initPos = (Integer.MAX_VALUE/2) - (Integer.MAX_VALUE / 2) % lists.size();
        pickerLayoutManager.scrollToPosition(initPos);
        //默认
        tvNum.setText(lists.get(0) + "");
        //工作时长
        Cabinet.getInstance().workHours = Integer.parseInt(lists.get(0));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //预约
            startActivity(new Intent(this, AppointmentActivity.class));
        } else if (id == R.id.btn_start) {
            //开始工作
            startActivity(WorkActivity.class);
            finish();
        } else if (id == R.id.ll_right_center) {
            //童锁
            screenLock();
        } else if (view.getId() == R.id.iv_float) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        }
    }

    private void screenLock() {
        IDialog iDialog = CabinetDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SCREEN_LOCK);
        iDialog.setCancelable(false);
        //长按解锁
        ImageView imageView = iDialog.getRootView().findViewById(R.id.iv_screen_lock);
        ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iDialog.dismiss();
                return true;
            }
        });
        iDialog.show();
    }
}