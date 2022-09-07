package com.robam.pan.ui.pages;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.device.Pan;
import com.robam.common.device.Stove;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.ToastUtils;
import com.robam.pan.constant.Constant;
import com.robam.pan.R;
import com.robam.pan.base.PanBasePage;
import com.robam.pan.bean.PanFunBean;
import com.robam.pan.ui.adapter.RvMainFunctionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends PanBasePage {
    /**
     * 主功能
     */
    private RecyclerView rvMain;
    private RvMainFunctionAdapter rvMainFunctionAdapter;
    //快炒
    private LinearLayout llQuick, llStir;
    //快炒
    private TextView tvQuick;
    //十秒翻炒
    private MCountdownView tvStir;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_page_layout_home;
    }

    @Override
    protected void initView() {
        showCenter();
        rvMain = findViewById(R.id.rv_main);
        llQuick = findViewById(R.id.ll_quick_fry);
        llStir = findViewById(R.id.ll_stir_fry);
        tvQuick = findViewById(R.id.tv_quick);
        tvStir = findViewById(R.id.tv_stir);
        rvMain.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvMainFunctionAdapter = new RvMainFunctionAdapter();
        rvMainFunctionAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                keyTone();
                Intent intent = new Intent();
                PanFunBean panFunBean = (PanFunBean) adapter.getItem(position);
                intent.putExtra(Constant.FUNCTION_BEAN, panFunBean);
                if (panFunBean.into == null || panFunBean.into.length() == 0) {
                    ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
                    return;
                }
                intent.setClassName(getContext(), panFunBean.into);
                startActivity(intent);

            }

        });
        rvMain.setAdapter(rvMainFunctionAdapter);
        setOnClickListener(llQuick, llStir);
    }

    @Override
    protected void initData() {
        List<PanFunBean> functionList = new ArrayList<>();
        functionList.add(new PanFunBean(1, "云端菜谱", "", "recipe", "com.robam.pan.ui.activity.RecipeActivity"));
        functionList.add(new PanFunBean(2, "我的最爱", "", "favorite", "com.robam.pan.ui.activity.FavoriteActivity"));
        functionList.add(new PanFunBean(3, "烹饪曲线", "", "curve", "com.robam.pan.ui.activity.CurveActivity"));
        rvMainFunctionAdapter.setList(functionList);
        Stove.getInstance().leftStove.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    //开火状态

                } else {
                    //关火状态
                    Stove.getInstance().leftWorkMode = 0;
                    Stove.getInstance().leftWorkHours = "";
                    Stove.getInstance().leftWorkTemp = "";
                }
            }
        });
        //初始左灶状态
        Stove.getInstance().rightStove.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    //开火状态
                } else {
                    //关火状态

                    Stove.getInstance().rightWorkMode = 0;
                    Stove.getInstance().rightWorkHours = "";
                    Stove.getInstance().rightWorkTemp = "";
                }
            }
        });
        //检测锅温度
        Pan.getInstance().panTemp.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer < 60)
                    ;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_quick_fry) {
            if (!llQuick.isSelected()) {
                //关闭当前模式，持续快炒中
                llQuick.setSelected(true);
                tvQuick.setText(R.string.pan_quick_frying);
                llStir.setSelected(false);
                tvStir.setText(R.string.pan_stir_fry);
                tvStir.stop();
            }
        } else if (id == R.id.ll_stir_fry) {
            if (!llStir.isSelected()) {
                llStir.setSelected(true);
                llQuick.setSelected(false);
                tvQuick.setText(R.string.pan_quick_fry);
                tvStir.setTotalTime(10);
                tvStir.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
                    @Override
                    public void onCountDown(int currentSecond) {
                        if (currentSecond <= 0) {
                            llStir.setSelected(false);
                            tvStir.setText(R.string.pan_stir_fry);
                            return;
                        }
                        tvStir.setText("十秒翻炒 " + currentSecond + "s");
                    }
                });
                tvStir.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != tvStir)
            tvStir.stop();
    }
}
