package com.robam.steamoven.ui.activity;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.BaseAdapter;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.model.ModeBean;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamOvenModeEnum;
import com.robam.steamoven.ui.adapter.RvDotAdapter;
import com.robam.steamoven.ui.adapter.RvModeAdapter;
import com.robam.steamoven.ui.adapter.RvModeFootAdapter;
import com.robam.steamoven.ui.adapter.RvSteamAdapter;
import com.robam.steamoven.ui.adapter.RvTimeOrTempAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSelectActivity extends SteamBaseActivity {
    /**
     * 选择要设置的功能
     */
    private RecyclerView rvSelect1;
    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect2;
    /**
     * 指示器
     */
    private RecyclerView rvDot;
    /**
     * 功能
     */
    private FuntionBean funBean;
    /**
     * 指示器adapter
     */
    private RvDotAdapter rvDotAdapter;
    /**
     * 模式选择adapter
     */
    private RvModeAdapter rvModeAdapter;
    /**
     * 底部选择adapter
     */
    private RvModeFootAdapter rvModeFootAdapter;

    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;
    /**
     * 温度adapter 单独写不用切换回来时候刻意保存状态
     */
    private RvTimeOrTempAdapter tempAdapter;

    /**
     * 下温度adapter 单独写不用切换回来时候刻意保存状态
     */
    private RvTimeOrTempAdapter tempDownAdapter;
    /**
     * 时间adapter
     */
    private RvTimeOrTempAdapter timeAdapter;

    /**
     * 蒸汽量
     */
    private RvSteamAdapter steamAdapter;
    /**
     * 功能下所有模式
     */
    private List<ModeBean> modes;
    /**
     * 选中的模式
     */
    private ModeBean modeBean;
    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_mode_select;
    }

    @Override
    protected void initView() {
        setBgImg(this, R.drawable.steam_ic_bg_xingkong2);
        hideItem2();

        rvSelect1 = findViewById(R.id.rv_select_1);
        rvSelect2 = findViewById(R.id.rv_select_2);
        rvDot = findViewById(R.id.rv_dot);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvSelect1.setLayoutManager(gridLayoutManager);
        rvModeFootAdapter = new RvModeFootAdapter(this);
        //设置底部adapter点击事件
        setAdapterClick();
        rvSelect1.setAdapter(rvModeFootAdapter);
        //设置选择recycleView的layoutManage
        setLayoutManage(5, 0.44f);
        rvDot.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));


        tempAdapter = new RvTimeOrTempAdapter(this, getString(R.string.steam_unit_temp));
        timeAdapter = new RvTimeOrTempAdapter(this, getString(R.string.steam_unit_minu));
        steamAdapter = new RvSteamAdapter(this);
        tempDownAdapter = new RvTimeOrTempAdapter(this, getString(R.string.steam_unit_temp));
        setOnClickListener(R.id.ll_title_item2, R.id.ll_title_item5, R.id.btn_start);
    }

    /**
     * 设置底部adapter数据
     *
     * @param modeBean
     */
    private void setFootData(ModeBean modeBean) {
        ArrayList<Integer> footData = new ArrayList<>();
        footData.add(modeBean.code);
        if (SteamOvenModeEnum.CHUGOU.getCode() == modeBean.code) {
            //除垢
            rvModeFootAdapter.setData(footData, modeBean);
        } else if (modeBean.steamSelect()) {
            rvSelect1.setLayoutManager(new GridLayoutManager(this, 4));
            //加湿烤 澎湃蒸
            footData.add(modeBean.defSteam);
            footData.add(modeBean.defTemp);
            footData.add(modeBean.defTime);
            rvModeFootAdapter.setData(footData, modeBean);
        } else if (modeBean.code == SteamOvenModeEnum.EXP.getCode()) {
            //EXP模式
            rvSelect1.setLayoutManager(new GridLayoutManager(this, 4));
            footData.add(modeBean.defTemp);
            footData.add(modeBean.getDefDownTemp(modeBean.defTemp));
            footData.add(modeBean.defTime);
            rvModeFootAdapter.setData(footData, modeBean);
        } else {
            rvSelect1.setLayoutManager(new GridLayoutManager(this, 3));
            footData.add(modeBean.defTemp);
            footData.add(modeBean.defTime);
            rvModeFootAdapter.setData(footData, modeBean);

        }

    }

    /**
     * 设置模式的adapter点击事件
     */
    private void setAdapterClick() {
        rvModeFootAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {

                //模式
                if (position == 0) {
                    rvSelect2.setAdapter(rvModeAdapter);
                    setLayoutManage(5, 0.33f);
                    rvModeFootAdapter.setIndex(0);
                    pickerLayoutManager.scrollToPosition(rvModeAdapter.getIndex());
                    //指示器
                    rvDot.setVisibility(View.VISIBLE);
                } else {
                    //指示器
                    rvDot.setVisibility(View.GONE);
                    if (rvModeFootAdapter.getModeBean().steamSelect()) {
                        //加湿烤模块 澎湃蒸
                        if (position == 1) {
                            setSteamSelect(position);
                        } else if (position == 2) {
                            setTempSelect(position);
                        } else if (position == 3) {
                            setTimeSelect(position);
                        }
                    } else if (rvModeFootAdapter.getModeBean().code == SteamOvenModeEnum.EXP.getCode()) {
                        //EXP
                        if (position == 1) {
                            setTempSelect(position);
                        } else if (position == 2) {
                            setDownTempSelect(position);
                        } else if (position == 3) {
                            setTimeSelect(position);
                        }
                    } else {
                        if (position == 1) {
                            setTempSelect(position);
                        } else if (position == 2) {
                            setTimeSelect(position);
                        }
                    }

                }

            }
        });
    }

    /**
     * 选中模式后初始化模式相关的参数
     */
    private void initParameter(ModeBean modeBean) {
        //蒸汽量
        if (modeBean.steamSelect()) {
            List<Integer> steamData = modeBean.getSteamData();
            steamAdapter.setData(steamData);
            steamAdapter.setIndex(modeBean.getDefSteamIndex());
        }

        //温度
        List<Integer> tempData = modeBean.getTempData();
        tempAdapter.setData(tempData);
        tempAdapter.setIndex(modeBean.getDefTempIndex());
        //时间
        List<Integer> timeData = modeBean.getTimeData();
        timeAdapter.setData(timeData);
        timeAdapter.setIndex(modeBean.getDefTimeIndex());
        if (modeBean.code == SteamOvenModeEnum.EXP.getCode()) {
            //下温度
            List<Integer> tempDownData = rvModeFootAdapter.getModeBean().getDownTempData(modeBean.defTemp);
            tempDownAdapter.setData(tempDownData);
        }

    }

    /**
     * 设置蒸汽选择
     */
    private void setSteamSelect(int position) {
        setLayoutManage(3, 0.2f);

        rvSelect2.setAdapter(steamAdapter);
        pickerLayoutManager.scrollToPosition(steamAdapter.getIndex());
        rvModeFootAdapter.setIndex(position);
    }

    /**
     * 设置温度选择
     */
    private void setTempSelect(int position) {

        List<Integer> tempData = modeBean.getTempData();
        if (tempData.size() == 1) {
            SteamOvenModeEnum mode = SteamOvenModeEnum.match(rvModeFootAdapter.getData().get(0));
            String format = String.format(getString(R.string.steam_temp_message), mode.getValue());
            ToastUtils.showShort(this, format);
            return;
        }
//        tempAdapter.setData(tempData);
        setLayoutManage(5, 0.2f);

        rvSelect2.setAdapter(tempAdapter);
        pickerLayoutManager.scrollToPosition(tempAdapter.getIndex());
        rvModeFootAdapter.setIndex(position);
    }
    @Override
    protected void initData() {
//获取当前功能下的模式
        funBean = (FuntionBean) getIntent().getParcelableExtra(Constant.FUNTION_BEAN);
        if (funBean != null && funBean.mode != null) {
            //辅助模式取消预约入口
            if (funBean.mode.equals("auxmode")) {
                hideItem5();
            }
//            modes = FuntionModeManage.getMode(this, funBean.mode);
            modes = LitePal.where("funCode = ?", funBean.funtionCode + "").find(ModeBean.class);
            rvModeAdapter = new RvModeAdapter(this, modes.size());
            rvModeAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                    scollToPosition(position);
                }
            });
            rvSelect2.setAdapter(rvModeAdapter);
            rvModeAdapter.addData(modes);

            rvDotAdapter = new RvDotAdapter();
            rvDot.setAdapter(rvDotAdapter);

            List<String> dotList = new ArrayList<>();
            for (ModeBean bean: modes) {
                dotList.add(bean.name);
            }
            rvDotAdapter.setList(dotList);
            rvDotAdapter.setPickPosition(Integer.MAX_VALUE / 2);

            pickerLayoutManager.scrollToPosition(Integer.MAX_VALUE / 2);
            rvModeAdapter.setIndex(Integer.MAX_VALUE / 2);
            modeBean = rvModeAdapter.getItem(rvModeAdapter.getIndex());
            setFootData(modeBean);
            initParameter(modeBean);
        }
    }

    /**
     * 设置时间选择
     */
    private void setTimeSelect(int position) {
        List<Integer> timeData = modeBean.getTimeData();
        if (timeData.size() == 1) {
            SteamOvenModeEnum mode = SteamOvenModeEnum.match(rvModeFootAdapter.getData().get(0));
            String format = String.format(getString(R.string.steam_time_message), mode.getValue());
            ToastUtils.showShort(this, format);
            return;
        }
        setLayoutManage(5, 0.2f);

        rvSelect2.setAdapter(timeAdapter);
        pickerLayoutManager.scrollToPosition(timeAdapter.getIndex());
        rvModeFootAdapter.setIndex(position);

    }

    /**
     * 设置下温度选择
     */
    private void setDownTempSelect(int position) {
        setLayoutManage(5, 0.2f);

        //设置的下温度
        rvSelect2.setAdapter(tempDownAdapter);
        pickerLayoutManager.scrollToPosition(tempDownAdapter.getIndex());
        rvModeFootAdapter.setIndex(position);
    }

    /**
     * 设置layout
     *
     * @param maxItem
     * @param scale
     */
    private void setLayoutManage(int maxItem, float scale) {
        pickerLayoutManager = new PickerLayoutManager.Builder(this)
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(maxItem)
                .setScale(scale)
                .setOnPickerListener(new PickerLayoutManager.OnPickerListener() {
                    @Override
                    public void onPicked(RecyclerView recyclerView, int position) {
                        if (rvModeFootAdapter.getIndex() == 0) {
                            if (modeBean.code == rvModeAdapter.getItem(position).code) {
                                return;
                            }
                            modeBean = rvModeAdapter.getItem(position);

                            rvModeAdapter.setIndex(position);
                            rvDotAdapter.setPickPosition(position);
                            setFootData(modeBean);
                            //初始化该模式相关参数
                            initParameter(modeBean);
                        } else {
                            if (rvModeFootAdapter.getModeBean().steamSelect()) {
                                //加湿烤  澎湃蒸
                                if (rvModeFootAdapter.getIndex() == 1) {
                                    steamAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(1, steamAdapter.getItem(position));
                                } else if (rvModeFootAdapter.getIndex() == 2) {
                                    tempAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(2, tempAdapter.getItem(position));
                                } else if (rvModeFootAdapter.getIndex() == 3) {
                                    timeAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(3, timeAdapter.getItem(position));
                                }
                            } else if (rvModeFootAdapter.getModeBean().code == SteamOvenModeEnum.EXP.getCode()) {
                                //EXP
                                if (rvModeFootAdapter.getIndex() == 1) {
                                    tempAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(1, tempAdapter.getItem(position));
                                    rvModeFootAdapter.setItem(2, rvModeFootAdapter.getModeBean().getDefDownTemp(tempAdapter.getItem(position)));
                                    //获取下温度范围
                                    List<Integer> tempDownData = rvModeFootAdapter.getModeBean().getDownTempData(tempAdapter.getItem(position));
                                    tempDownAdapter.setData(tempDownData);
                                } else if (rvModeFootAdapter.getIndex() == 2) {
                                    tempDownAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(2, tempDownAdapter.getItem(position));
                                } else if (rvModeFootAdapter.getIndex() == 3) {
                                    timeAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(3, timeAdapter.getItem(position));
                                }
                            } else {
                                if (rvModeFootAdapter.getIndex() == 1) {
                                    tempAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(1, tempAdapter.getItem(position));
                                } else if (rvModeFootAdapter.getIndex() == 2) {
                                    timeAdapter.setIndex(position);
                                    rvModeFootAdapter.setItem(2, timeAdapter.getItem(position));
                                }
                            }


                        }

                    }
                })
                .build();
        rvSelect2.setLayoutManager(pickerLayoutManager);
    }

    /**
     * 滚动并居中
     */
    private void scollToPosition(int index) {
        if (index == rvModeAdapter.getIndex()) {
            return;
        }
        pickerLayoutManager.smoothScrollToPosition(rvSelect2, new RecyclerView.State(), index);
    }

    @Override
    public void onClick(View view) {
        if (R.id.ll_title_item2 == view.getId()) {

        } else if (R.id.ll_title_item5 == view.getId()) {

        } else if (R.id.btn_start == view.getId()) {

        }
    }
}
