package com.robam.cabinet.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.cabinet.R;
import com.robam.cabinet.base.CabinetBasePage;
import com.robam.cabinet.bean.CabFunBean;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.Constant;
import com.robam.cabinet.ui.adapter.RvDotAdapter;
import com.robam.cabinet.ui.adapter.RvMainFunctionAdapter;
import com.robam.common.manager.FunctionManager;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HomePage extends CabinetBasePage {
    private RecyclerView rvMain, rvDot;
    private PickerLayoutManager pickerLayoutManager;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    private RvDotAdapter rvDotAdapter;
    //private ImageView imageView;
    private GifImageView gifImageView; //背景图片
    //主功能

    @Override
    protected int getLayoutId() {
        return R.layout.cabinet_page_layout_home;
    }

    @Override
    protected void initView() {
        //showCenter();


        rvMain = findViewById(R.id.rv_main);
        rvDot = findViewById(R.id.rv_dot);
        //imageView = findViewById(R.id.iv_bg);
        gifImageView = findViewById(R.id.iv_bg);
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(3)
                .setScale(0.66f)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        setBackground(position);
                        //指示器更新
                        rvDotAdapter.setPickPosition(position);
                        rvMainFunctionAdapter.setPickPosition(position);
                    }
                }).build();
        rvMain.setLayoutManager(pickerLayoutManager);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFunctionAdapter = new RvMainFunctionAdapter();

        rvMain.setAdapter(rvMainFunctionAdapter);
        rvDotAdapter = new RvDotAdapter();
        rvDot.setAdapter(rvDotAdapter);

        showCenter();
        showRightCenter();
        setOnClickListener(R.id.iv_float,R.id.ll_right_center);
        findViewById(R.id.ll_right_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getContext(),"解锁了",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void initData() {
//初始化数据
        List<CabFunBean> functionList = FunctionManager.getFuntionList(getContext(), CabFunBean.class, R.raw.cabinet);

        if (null != functionList) {
            rvMainFunctionAdapter.setList(functionList);
            List<String> dotList = new ArrayList<>();
            for (CabFunBean cabFunBean : functionList) {
                dotList.add(cabFunBean.funtionName);
            }

            rvDotAdapter.setList(dotList);
            //初始位置第一个
            rvDotAdapter.setPickPosition(0);
            int initPos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % functionList.size();
            rvMainFunctionAdapter.setPickPosition(initPos);
            pickerLayoutManager.scrollToPosition(initPos);
            setBackground(initPos);

            rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    CabFunBean cabFunBean = (CabFunBean) adapter.getItem(position);

                    Intent intent = new Intent();
                    intent.putExtra(Constant.EXTRA_MODE_BEAN, cabFunBean.mode);
                    intent.setClassName(getContext(), cabFunBean.into);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 设置背景图片
     *
     * @param index
     */
    private void setBackground(int index) {
        //设置背景图片
        int resId = getResources().getIdentifier(rvMainFunctionAdapter.getItem(index).backgroundImg, "drawable", getContext().getPackageName());
        //ImageUtils.loadGif(getContext(), resId, imageView);
        gifImageView.setImageResource(resId);
    }

    /**
     * 滚动并居中
     */
    private void scollToPosition(int index) {
        pickerLayoutManager.smoothScrollToPosition(rvMain, new RecyclerView.State(), index);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_float) {
//            getActivity().finish();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClassName(getContext(), "com.robam.ventilator.ui.activity.HomeActivity");
            startActivity(intent);
        }else if(view.getId() == R.id.ll_right_center){

        }
    }
}
