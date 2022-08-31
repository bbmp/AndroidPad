package com.robam.stove.ui.pages;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ClickUtils;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBasePage;
import com.robam.stove.bean.Stove;
import com.robam.stove.bean.StoveFunBean;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.ui.adapter.RvMainFunctionAdapter;

import java.util.List;

public class HomePage extends StoveBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private PickerLayoutManager pickerLayoutManager;
    //浮标
    private ImageView ivFloat;
    private LinearLayout llLeftStove, llRightStove;
    private TextView tvLeftStove, tvRightStove;
    //背景
    private ImageView imageView;
    private ImageView ivLock;

    private IDialog iDialogStop, iDialogAffirm;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();

        rvMain = findViewById(R.id.rv_main);
        ivFloat = findViewById(R.id.iv_float);
        imageView = findViewById(R.id.iv_bg);
        llLeftStove = findViewById(R.id.ll_left_stove);
        llRightStove = findViewById(R.id.ll_right_stove);
        tvLeftStove = findViewById(R.id.tv_left_stove);
        tvRightStove = findViewById(R.id.tv_right_stove);
        ivLock = findViewById(R.id.iv_lock);

        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setAlpha(false)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvMainFunctionAdapter.setPickPosition(position);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvMainFunctionAdapter = new RvMainFunctionAdapter();

        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(R.id.iv_float, R.id.ll_left_stove, R.id.ll_right_stove, R.id.iv_lock);
    }

    @Override
    protected void initData() {
        //初始化数据
        Stove.getInstance().init(getContext());
        List<StoveFunBean> functionList = Stove.getInstance().getStoveFunBeans();

        rvMainFunctionAdapter.setList(functionList);

        //初始位置
        int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
        rvMainFunctionAdapter.setPickPosition(initPos);
        pickerLayoutManager.scrollToPosition(initPos);
        setBackground(initPos);

        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                StoveFunBean stoveFunBean = (StoveFunBean) adapter.getItem(position);

                Stove.getInstance().funCode = stoveFunBean.funtionCode;
                Intent intent = new Intent();
                intent.setClassName(getContext(), stoveFunBean.into);
                startActivity(intent);
            }
        });

    }

    //锁屏
    private void screenLock() {
        IDialog iDialog = StoveDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_LOCK);
        iDialog.setCancelable(false);
        //长按解锁
        ImageView imageView = iDialog.getRootView().findViewById(R.id.iv_lock);
        ClickUtils.setLongClick(new Handler(), imageView, 2000, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iDialog.dismiss();

                return true;
            }
        });
        //分别设置左右灶
        LinearLayout leftStove = iDialog.getRootView().findViewById(R.id.ll_left_stove);
        LinearLayout rightStove = iDialog.getRootView().findViewById(R.id.ll_right_stove);
        leftStove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCook();
            }
        });
        rightStove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCook();
            }
        });
        iDialog.show();
    }

    //锁屏确认
    private void affirmLock() {
        if (null == iDialogAffirm) {
            iDialogAffirm = StoveDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogAffirm.setCancelable(false);
            iDialogAffirm.setContentText(R.string.stove_affirm_lock_hint);
            iDialogAffirm.setOKText(R.string.stove_ok);
            iDialogAffirm.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok)
                        screenLock();
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogAffirm.show();
    }
    //确认结束烹饪
    private void stopCook() {
        if (null == iDialogStop) {
            iDialogStop = StoveDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_STOVE_COMMON);
            iDialogStop.setCancelable(false);
            iDialogStop.setContentText(R.string.stove_stop_cook_hint);
            iDialogStop.setOKText(R.string.stove_stop_cook);
            iDialogStop.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok)
                        ;
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        iDialogStop.show();
    }

    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
        int resId = getResources().getIdentifier(rvMainFunctionAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        ImageUtils.loadGif(getContext(), resId, imageView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_float) {
            //快捷入口 隐式启动 降低耦合
            Intent intent = new Intent();
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.ShortcutActivity");
            startActivity(intent);
        } else if (id == R.id.ll_left_stove) {
            stopCook();
        } else if (id == R.id.ll_right_stove) {

        } else if (id == R.id.iv_lock) {
            //锁屏提示
            affirmLock();
        }
    }
}
