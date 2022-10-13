package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robam.common.manager.FunctionManager;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import java.util.ArrayList;
import java.util.List;

//一体机多段
public class MultiWorkActivity extends SteamBaseActivity {

    //段数父容器
    private ViewGroup optContentParentView;

    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  CUR_ITEM_VIEW_COUNT = 3;
    public static final int  DATA_KEY = R.id.multi_opt;

    private ImageView pauseCookView;
    private ImageView continueCookView;
    private TextView cookDurationView;
    private ViewGroup curCookInfoViewGroup;

    private boolean isStart = false;

    private static final int REQUEST_CODE_SETTING  = 321;



    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_multi_work;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        optContentParentView = findViewById(R.id.multi_work_model_list);
        pauseCookView = findViewById(R.id.multi_work_pause);
        continueCookView = findViewById(R.id.multi_work_start);
        cookDurationView = findViewById(R.id.multi_work_total);
        curCookInfoViewGroup = findViewById(R.id.multi_work_cur_info);
        initOptViewTag();
        setOnClickListener(R.id.multi_work_pause,R.id.multi_work_start);
    }


    /**
     * 初始化每个可用ItemView 的 TAG值，方便后期直接通过TAG来查找ItemView
     */
    private void initOptViewTag(){
        for(int i = 0; i < optContentParentView.getChildCount();i++){
            String indexTag = i +"";
            optContentParentView.getChildAt(i).setTag(indexTag);
            optContentParentView.getChildAt(i).setTag(DATA_KEY,null);
            optContentParentView.getChildAt(i).setOnClickListener(view -> {
                if(!checkSegmentState(view)){
                    //Toast.makeText(getContext(),"请设置前面内容",Toast.LENGTH_LONG).show();
                    return;
                }
                if(isWorking()){
                    Toast.makeText(getContext(),R.string.steam_cook_setting_prompt,Toast.LENGTH_LONG).show();
                    return;
                }
                //调整到模式设置页面 ： 第一段 - 默认调整到专业模式下的 - 营养蒸； 第二段 - 默认跳转到考模式下的 - 烘焙 ； 第三段 - 默认调整到炸模式 - 空气炸
                MultiWorkActivity.this.backSettingPage();
            });
        }
    }



    private boolean checkSegmentState(View view){
        String tag = (String) view.getTag();
        int indexTag = Integer.parseInt(tag);
        if(indexTag <= multiSegments.size()){
            return true;
        }
        return false;
    }


    private void initOptContent(){
        for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setTag(DATA_KEY,null);



            TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
            temperatureView.setText("");

            TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
            modelView.setText("");


            TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
            durationView.setText("");


        }
    }

    private void setOptContent(List<MultiSegment> multiSegments){
        if(multiSegments.size() == 0){
            initOptContent();
            return;
        }

        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,multiSegments.get(i));
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setVisibility(View.INVISIBLE);
        }
    }

    private void setOptItemContent(ViewGroup itemGroup,MultiSegment multiSegmentBean){
        itemGroup.setVisibility(View.VISIBLE);
        itemGroup.setTag(DATA_KEY,multiSegmentBean);

        boolean isWork = multiSegmentBean.isCooking() || multiSegmentBean.isPause();
        int textColor = getResources().getColor(isWork ? R.color.steam_white: R.color.steam_mode_d9);

        View view = itemGroup.findViewById(R.id.multi_work_item_index);
        view.setVisibility(isWork ? View.VISIBLE:View.INVISIBLE);

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        temperatureView.setTextColor(textColor);
        String temperature =  multiSegmentBean.defTemp +"°c";
        temperatureView.setText(temperature);

        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        modelView.setTextColor(textColor);
        String model = multiSegmentBean.model;
        modelView.setText(model);

        TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
        durationView.setTextColor(textColor);
        String duration = multiSegmentBean.duration +"min";
        durationView.setText(duration);
    }



    @Override
    protected void initData() {
        //获取上一个页面传递过来的参数
        //展示
        multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            if(isWorking()){
                showStopWorkDialog();
                return;
            }
            this.backSettingPage();
        }else if(id == R.id.multi_work_pause){//暂停工作
            this.setOptViewsState(false);
        }else if(id==R.id.multi_work_start){//继续工作
            this.setOptViewsState(true);
        }
    }

    private void backSettingPage(){
        Intent result = new Intent();
        result.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
        setResult(RESULT_OK,result);
        finish();
    }

    private void showStopWorkDialog(){
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(this);
        steamCommonDialog.setContentText(R.string.steam_work_multi_back_message);
        steamCommonDialog.setOKText(R.string.steam_finish_now);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                Intent intent = new Intent(MultiWorkActivity.this,MainActivity.class);
                startActivity(intent);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }


    /**
     * 是否正在烹饪
     * @return
     */
    private boolean isWorking(){
        return pauseCookView.getVisibility() == View.VISIBLE;
    }


    /**
     *
     * @param cookState true -- 烹饪状态;false -- 暂停烹饪状态
     */
    private void setOptViewsState(boolean cookState){
        pauseCookView.setVisibility(cookState?View.VISIBLE:View.INVISIBLE);
        continueCookView.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        curCookInfoViewGroup.setVisibility(cookState?View.INVISIBLE:View.VISIBLE);
        if(cookState){
            cookDurationView.setText("70min");
        }else{
            cookDurationView.setText(R.string.steam_cook_in_pause);
            //设置当前段工作信息 - 后面的时间是否为剩余时长？

            for(MultiSegment segment : multiSegments){
                if(segment.isStart()){
                    TextView  curModel = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_model);
                    TextView  curTemp = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_temperature);
                    TextView  curDuration = curCookInfoViewGroup.findViewById(R.id.multi_item_cur_duration);
                    curModel.setText(segment.model);
                    curTemp.setText(segment.defTemp +"°c");
                    //TODO("设置总多段剩余时长")
                    curDuration.setText(segment.duration+"min");
                }
            }


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("MultiActivity onActivityResult " + resultCode);
        if(resultCode == RESULT_OK){
            dealResult(requestCode,data);
            //setDelBtnState(multiSegments.size() > 0 ? true:false);
        }
    }



    private void dealResult(int requestCode, Intent data){
//        if(multiSegments.size() > requestCode){//修改当前历史
//            MultiSegment resultData  = data.getParcelableExtra("resultData");
//            multiSegments.remove(requestCode);
//            multiSegments.add(requestCode,resultData);
//        }else{//添加新对象
//            MultiSegment resultData  = data.getParcelableExtra("resultData");
//            multiSegments.add(resultData);
//        }
        multiSegments = data.getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
    }


}
