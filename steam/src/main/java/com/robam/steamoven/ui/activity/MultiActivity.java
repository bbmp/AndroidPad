package com.robam.steamoven.ui.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robam.common.manager.FunctionManager;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamEnum;
import com.robam.steamoven.device.HomeSteamOven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//一体机多段
public class MultiActivity extends SteamBaseActivity {

    //底部父容器
    private ViewGroup optContentParentView;
    //段数父容器
    private ViewGroup optBottomParentView;

    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  CUR_ITEM_VIEW_COUNT = 3;
    public static final int  DATA_KEY = R.id.multi_opt;
    //跳转集合
    private List<FuntionBean> funtionBeans;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_multi;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        optContentParentView = findViewById(R.id.multi_opt);
        optBottomParentView = findViewById(R.id.multi_bottom);
        initOptViewTag();
        initOptContent();
        initActivityResult();

    }

    private void initActivityResult() {

    }

    /**
     * 初始化每个可用ItemView 的 TAG值，方便后期直接通过TAG来查找ItemView
     */
    private void initOptViewTag(){
        for(int i = 0; i < optContentParentView.getChildCount();i++){
            if(i % 2 != 0){
                String indexTag = i/2 +"";
                optContentParentView.getChildAt(i).setTag(indexTag);
                optContentParentView.getChildAt(i).setTag(DATA_KEY,null);
                optContentParentView.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!checkSegmentState(view)){
                            Toast.makeText(getContext(),"请设置前面内容",Toast.LENGTH_LONG).show();
                            return;
                        }
                        //调整到模式设置页面 ： 第一段 - 默认调整到专业模式下的 - 营养蒸； 第二段 - 默认跳转到考模式下的 - 烘焙 ； 第三段 - 默认调整到炸模式 - 空气炸
                        MultiActivity.this.toTagPageModel(view);
                    }
                });
            }
        }
    }

    private void toTagPageModel(View view){
        Object data = view.getTag(DATA_KEY);
        if(data == null){//去往默认规则页面
            List<Integer> settedFunList= getSettedFunList();
            List<Integer> allFunList = getModelFunList();
            if(settedFunList != null){
                for(int i = 0;i < settedFunList.size();i++){
                    allFunList.remove(settedFunList.get(i));
                }
            }
            FuntionBean funtionBean = getFuntionBean(allFunList.get(0));
            this.toModelPage(funtionBean,Integer.parseInt(view.getTag()+""));
        }else{//去往当前选择模式页面
            //this.toCurModelPage((MultiSegment)data,Integer.parseInt(view.getTag()+""));
            FuntionBean funtionBean = getFuntionBean(((MultiSegment)data).modelNum);
            this.toModelPage(funtionBean,Integer.parseInt(view.getTag()+""));
        }
    }

    private void toDefaultModelPage(int index){
        List<Integer> settedFunList= getSettedFunList();
        List<Integer> allFunList = getModelFunList();
        if(settedFunList != null){
            for(int i = 0;i < settedFunList.size();i++){
                allFunList.remove(settedFunList.get(i));
            }
        }

        FuntionBean funtionBean = getFuntionBean(allFunList.get(0));
        this.toModelPage(funtionBean,index);
    }


    private void toCurModelPage(MultiSegment multiSegment,int index){
        FuntionBean funtionBean = getFuntionBean(multiSegment.modelNum);
        this.toModelPage(funtionBean,index);
    }

    private void toModelPage(FuntionBean funtionBean,int index){
        if (funtionBean == null || funtionBean.into == null || funtionBean.into.length() == 0) {
            ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(SteamConstant.EXTRA_MODE_LIST, funtionBean.mode);
        intent.setClassName(getContext(), funtionBean.into);
        intent.putExtra(SteamConstant.NEED_SET_RESULT,true);
        startActivityForResult(intent,index);
    }



    /**
     * 跳转到蒸设置页面
     */
    private void toSteamPage(){
       if(funtionBeans == null){
           throw new IllegalArgumentException("未在R.raw.steam中查询到设置页面，请检查R.raw.steam文件中内容是否有误");
       }


    }

    private FuntionBean getFuntionBean(int code){
        for(int i = 0;i < funtionBeans.size() ;i++){
            if(code == funtionBeans.get(i).funtionCode){
                return funtionBeans.get(i);
            }
        }
        return null;
    }


    private void toBAKEPage(){}





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
            TextView segmentView = itemGroup.findViewById(R.id.multi_item_name);
            segmentView.setText(this.getSegmentName(i));


            TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
            temperatureView.setText("");

            TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
            modelView.setText("");


            TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
            durationView.setText("");

            itemGroup.findViewById(R.id.multi_item_add).setVisibility(View.VISIBLE);
        }
    }

    private void setOptContent(List<MultiSegment> multiSegments){
        if(multiSegments.size() == 0){
            initOptContent();
            optBottomParentView.setVisibility(View.GONE);
            return;
        }

        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        optBottomParentView.setVisibility(maxCount >= 1 ? View.VISIBLE : View.GONE);
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,multiSegments.get(i),i,false);
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,null,i,true);
        }
    }

    private void setOptItemContent(ViewGroup itemGroup,MultiSegment multiSegmentBean,int index,boolean isInit){

        TextView segmentView = itemGroup.findViewById(R.id.multi_item_name);
        segmentView.setText(this.getSegmentName(index));
        itemGroup.setTag(DATA_KEY,isInit ? null:multiSegmentBean);

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        String temperature = isInit ? "" : multiSegmentBean.temperature;
        temperatureView.setText(temperature);

        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        String model = isInit ? "" : multiSegmentBean.model;
        modelView.setText(model);


        TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
        String duration = isInit ? "" : multiSegmentBean.duration;
        durationView.setText(duration);

        itemGroup.findViewById(R.id.multi_item_add).setVisibility(isInit ? View.VISIBLE:View.INVISIBLE);
    }

    private String getSegmentName(int index){
        switch (index){
            case 0:
                return "一段";
            case 1:
                return "二段";
            case 2:
                return "三段";
            default:
                return "";
        }
    }


    @Override
    protected void initData() {
        funtionBeans = FunctionManager.getFuntionList(getContext(), FuntionBean.class, R.raw.steam);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            finish();
        }
    }


    private List<Integer> getModelFunList(){
        Integer[] modelFunArray = new Integer[]{SteamEnum.STEAM.fun,SteamEnum.OVEN.fun,SteamEnum.FRY.fun};
        return new ArrayList(Arrays.asList(modelFunArray));
    }

    private List<Integer> getSettedFunList(){
        if(multiSegments== null || multiSegments.size() == 0){
            return null;
        }
        List<Integer> settedList = new ArrayList<>();
        for(int i = 0;i < multiSegments.size();i++){
            settedList.add(multiSegments.get(i).modelNum);
        }
        return settedList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("MultiActivity onActivityResult " + resultCode);
        if(resultCode == RESULT_OK){
            dealResult(requestCode,data);
        }
    }

    private void dealResult(int requestCode, Intent data){
        if(multiSegments.size() > requestCode){//修改当前历史
            MultiSegment segment = multiSegments.get(requestCode);
            segment.modelNum = data.getIntExtra("fun",-1);
            segment.model =   data.getStringExtra("model");
            segment.temperature =   data.getStringExtra("TEMP");
            segment.duration =   data.getStringExtra("duration");
        }else{//添加新对象
            MultiSegment segment = new MultiSegment();
            segment.modelNum = data.getIntExtra("fun",-1);
            segment.model =   data.getStringExtra("model");
            segment.temperature =   data.getStringExtra("TEMP");
            segment.duration =   data.getStringExtra("duration");
            multiSegments.add(segment);
        }
        setOptContent(multiSegments);

    }

}
