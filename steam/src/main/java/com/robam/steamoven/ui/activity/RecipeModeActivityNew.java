package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.robam.common.bean.MqttDirective;
import com.robam.common.ui.helper.PickerLayoutManager;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.adapter.RvTimeAdapter;
import java.util.ArrayList;
import java.util.List;


public class RecipeModeActivityNew extends SteamBaseActivity {

    /**
     * 模式 温度 时间选择
     */
    private RecyclerView rvSelect;
    /**
     * 重写选择器
     */
    private PickerLayoutManager pickerLayoutManager;

    private RvTimeAdapter rvTimeAdapter;

    //当前模式
    private ModeBean curMode;


    private List<ModeBean> modes;

    private TextView tvTime;

    private long recipeId;

    public static final int SEND_START_WORK = 111;

    private int directive_offset = 11000000;
    private static final int DIRECTIVE_OFFSET_END = 10;
    private static final int DIRECTIVE_OFFSET_PAUSE_CONTINUE = 20;
    private static final int DIRECTIVE_OFFSET_OVER_TIME = 40;
    private static final int DIRECTIVE_OFFSET_WORK_FINISH = 60;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_recipe_mode_new;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        showRightCenter();
        rvSelect = findViewById(R.id.rv_select);
        tvTime = findViewById(R.id.tv_mode);
        findViewById(R.id.iv_select).setVisibility(View.VISIBLE);
        setLayoutManage(5, 0.44f);
        setOnClickListener(R.id.btn_start);
        MqttDirective.getInstance().getDirective().observe(this, s -> {
            switch (s){
                case SEND_START_WORK:
                    toWorkPage();
                    break;

            }
        });

    }

    @Override
    protected void initData() {
        modes = (ArrayList<ModeBean>) getIntent().getSerializableExtra(SteamConstant.EXTRA_MODE_LIST);
        recipeId = getIntent().getLongExtra(SteamConstant.EXTRA_RECIPE_ID,0);
        curMode = modes.get(0);
        rvTimeAdapter = new RvTimeAdapter(1);
        rvSelect.setAdapter(rvTimeAdapter);
        updateTimeTab(curMode);
    }

    /**
     * 设置layout
     *
     * @param maxItem
     * @param scale
     */
    private void setLayoutManage(int maxItem, float scale) {
        pickerLayoutManager = new PickerLayoutManager.Builder(getContext())
                .setOrientation(RecyclerView.HORIZONTAL)
                .setMaxItem(maxItem)
                .setScale(scale)
                .setOnPickerListener((recyclerView, position) -> {
                    //指示器更新
                    rvTimeAdapter.setPickPosition(position);
                    //curTime = rvTimeAdapter.getItem(position);
                    tvTime.setText(rvTimeAdapter.getItem(position));
                })
                .build();
        rvSelect.setLayoutManager(pickerLayoutManager);
    }

    public void updateTimeTab(ModeBean modeBean) {
        if (null == rvTimeAdapter)
            return;

        ArrayList<String> timeList = new ArrayList<>();
        for (int i = modeBean.minTime; i <= modeBean.maxTime; i++) {
            timeList.add(i + "");
        }

        rvTimeAdapter.setList(timeList);
        int offset = modeBean.defTime - modeBean.minTime;
        int position = Integer.MAX_VALUE / 2-(Integer.MAX_VALUE / 2)%timeList.size() + offset;
        pickerLayoutManager.scrollToPosition(position);
        rvTimeAdapter.setPickPosition(position);
        tvTime.setText(rvTimeAdapter.getItem(position));
        //默认时间
        //curTime = rvTimeAdapter.getItem(position);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_left) {
            finish();
        }else if(view.getId() == R.id.btn_start){
            sendStartWorkCommand();
        }
    }

    /**
     * 跳转到工作页面
     */
    private void sendStartWorkCommand(){
        SteamCommandHelper.sendRecipeWork(recipeId,Integer.parseInt(tvTime.getText().toString()),SEND_START_WORK);
    }

    private void toWorkPage(){
        Intent intent = new Intent(this,ModelWorkActivity.class);
        List<MultiSegment> list = new ArrayList<>();
        list.add(getResult());
        list.get(0).setWorkModel(MultiSegment.COOK_STATE_PREHEAT);
        list.get(0).setCookState(MultiSegment.COOK_STATE_START);
        intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) list);
        intent.putExtra(Constant.RECIPE_ID, recipeId);
        startActivity(intent);
    }

    private MultiSegment getResult(){
        //获取真实数据
        MultiSegment segment = new MultiSegment();
        segment.defTemp = Integer.parseInt(tvTime.getText().toString());
        segment.workRemaining = Integer.parseInt(tvTime.getText().toString()) * 60;
        return segment;
    }
}
