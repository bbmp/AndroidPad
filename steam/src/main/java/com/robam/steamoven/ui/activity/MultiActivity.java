package com.robam.steamoven.ui.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.robam.common.ui.dialog.IDialog;
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
import com.robam.steamoven.ui.dialog.SteamCommonDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//一体机多段
public class MultiActivity extends SteamBaseActivity {

    //底部父容器
    private ViewGroup optContentParentView;
    //段数父容器
    private ViewGroup optBottomParentView;

    private TextView totalDurationView;

    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  CUR_ITEM_VIEW_COUNT = 3;
    public static final int  DATA_KEY = R.id.multi_opt;
    public static final int  START_WORK_CODE = 350;
    //跳转集合
    private List<FuntionBean> funtionBeans;
    //多段是否已启动
    private boolean isStart = false;


    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_multi;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        //setRight(R.string.steam_title_delete);
        optContentParentView = findViewById(R.id.multi_opt);
        optBottomParentView = findViewById(R.id.multi_bottom);
        totalDurationView = findViewById(R.id.steam_total_duration_value);
        initOptViewTag();
        initOptContent();
        initDelBtnView();
        setOnClickListener(R.id.btn_start);
    }


    /**
     * 设置删除文本与删除按钮图标
     */
    private void initDelBtnView(){
        setOnClickListener(R.id.ll_right);
        //设置删除ICON TODO("后期补上图片")
        //((ImageView)findViewById(R.id.iv_right)).setImageResource();
        ((TextView)findViewById(R.id.tv_right)).setText(R.string.steam_title_delete);
    }


    private void setDelBtnState(boolean isShow){
        findViewById(R.id.ll_right).setVisibility(isShow? View.VISIBLE:View.INVISIBLE);
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
                optContentParentView.getChildAt(i).findViewById(R.id.multi_item_del).setTag(indexTag);
                optContentParentView.getChildAt(i).findViewById(R.id.multi_item_del).setOnClickListener(this);//删除按钮
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
            FuntionBean funtionBean = getFuntionBean(((MultiSegment)data).funCode);
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
        FuntionBean funtionBean = getFuntionBean(multiSegment.funCode);
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
        int enableColor = getResources().getColor(R.color.steam_white);
        int disableColor = getResources().getColor(R.color.steam_mode_d9);
        for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setTag(DATA_KEY,null);

            TextView segmentView = itemGroup.findViewById(R.id.multi_item_name);
            segmentView.setTextColor(i == 0 ? enableColor:disableColor);
            segmentView.setText(this.getSegmentName(i));


            TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
            temperatureView.setTextColor(i == 0 ? enableColor:disableColor);
            temperatureView.setText("");

            TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
            modelView.setTextColor(i == 0 ? enableColor:disableColor);
            modelView.setText("");


            TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
            durationView.setTextColor(i == 0 ? enableColor:disableColor);
            durationView.setText("");

            if(i == 0) {
                ((ImageView) itemGroup.findViewById(R.id.multi_item_add)).setImageResource(R.drawable.steam_ic_multi_item_add);
            }else {
                ((ImageView) itemGroup.findViewById(R.id.multi_item_add)).setImageResource(R.drawable.steam_ic_multi_item_add_disabled);
            }
            itemGroup.findViewById(R.id.multi_item_add).setVisibility(View.VISIBLE);
            itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.INVISIBLE);
        }
    }

    private void setOptContent(List<MultiSegment> multiSegments){
        if(multiSegments.size() == 0){
            initOptContent();
            optBottomParentView.setVisibility(View.GONE);
            return;
        }

        int enableColor = getResources().getColor(R.color.steam_white);
        int disableColor = getResources().getColor(R.color.steam_mode_d9);
        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        optBottomParentView.setVisibility(maxCount >= 1 ? View.VISIBLE : View.GONE);
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,multiSegments.get(i),i,false,enableColor,R.drawable.steam_ic_multi_item_add);
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,null,i,true,
                    (i == maxCount?enableColor:disableColor),
                    (i == maxCount?R.drawable.steam_ic_multi_item_add:R.drawable.steam_ic_multi_item_add_disabled));
        }
    }

    private void setOptItemContent(ViewGroup itemGroup,MultiSegment multiSegmentBean,int index,boolean isInit,int textColor,int addDrawableRes){


        TextView segmentView = itemGroup.findViewById(R.id.multi_item_name);
        segmentView.setTextColor(textColor);
        segmentView.setText(this.getSegmentName(index));
        itemGroup.setTag(DATA_KEY,isInit ? null:multiSegmentBean);

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        temperatureView.setTextColor(textColor);
        String temperature = isInit ? "" : (multiSegmentBean.defTemp + "°c");
        temperatureView.setText(temperature);

        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        modelView.setTextColor(textColor);
        String model = isInit ? "" : multiSegmentBean.model;
        modelView.setText(model);


        TextView durationView = itemGroup.findViewById(R.id.multi_item_duration);
        durationView.setTextColor(textColor);
        String duration = isInit ? "" : (multiSegmentBean.duration + "min");
        durationView.setText(duration);

        ((ImageView)itemGroup.findViewById(R.id.multi_item_add)).setImageResource(addDrawableRes);
        itemGroup.findViewById(R.id.multi_item_add).setVisibility(isInit ? View.VISIBLE:View.INVISIBLE);
        itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.INVISIBLE);

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
            if(isDelState()){
                setDelBtnItemState(false);
                return;
            }
            finish();
        }else if(id == R.id.ll_right){//显示删除按钮
            showItemDelView();
        }else if(id == R.id.multi_item_del){//删除按钮被点击
            showDealDialog(view);
        }else if(id == R.id.btn_start){
            toWorkAc();
        }
    }

    private void toWorkAc(){
        if(multiSegments.size() == 0){
            return;
        }

        if(!isStart){
            isStart = true;
            multiSegments.get(0).isCooking = true;
        }
        Intent intent = new Intent(this,MultiWorkActivity.class);
        intent.putParcelableArrayListExtra(SteamConstant.SEGMENT_DATA_FALG, (ArrayList<? extends Parcelable>) multiSegments);
        startActivityForResult(intent,START_WORK_CODE);
    }


    private void showDealDialog(final View view){
        //final int index = ;//被点击的位置，从0开始
        //展示Dialog
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(MultiActivity.this);
        steamCommonDialog.setContentText(R.string.steam_delete_ok_content);
        steamCommonDialog.setOKText(R.string.steam_delete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                dealDataItemWasDel(Integer.parseInt(view.getTag()+""));
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    private void dealDataItemWasDel(int index){
        multiSegments.remove(index);
        setOptContent(multiSegments);
        setDelBtnItemState(true);
        setTotalDuration();
    }




    private boolean isDelState(){
        for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            if(itemGroup.findViewById(R.id.multi_item_del).getVisibility() == View.VISIBLE){
                return true;
            }
        }
        return false;
    }

    /**
     * 展示删除按钮
     */
    private void showItemDelView() {
        setDelBtnState(false);//隐藏删除按钮
        setDelBtnItemState(true);
    }

    private void setDelBtnItemState(boolean isShow){
        if(isShow){
            int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
            for(int i = 0; i < maxCount;i++){
                ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
                itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.VISIBLE);
            }
            for(int i = maxCount ;i < CUR_ITEM_VIEW_COUNT;i++){
                ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
                itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.INVISIBLE);
            }
        }else{
            for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
                ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
                itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.INVISIBLE);
            }
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
            settedList.add(multiSegments.get(i).funCode);
        }
        return settedList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("MultiActivity onActivityResult " + resultCode);
        if(resultCode == RESULT_OK){
            if(requestCode == START_WORK_CODE){//多段模式进入暂停模式，此时需要更新页面状态与数据

            }else{
                dealResult(requestCode,data);
                setDelBtnState(multiSegments.size() > 0 ? true:false);
                setTotalDuration();
            }

        }
    }



    private void dealResult(int requestCode, Intent data){
        if(multiSegments.size() > requestCode){//修改当前历史
            MultiSegment resultData  = data.getParcelableExtra("resultData");
            multiSegments.remove(requestCode);
            multiSegments.add(requestCode,resultData);
        }else{//添加新对象
            MultiSegment resultData  = data.getParcelableExtra("resultData");
            multiSegments.add(resultData);
        }
        setOptContent(multiSegments);
    }

    private void setTotalDuration(){
        int totalDuration = 0;
        for(int i = 0;i < multiSegments.size();i++){
            totalDuration += Integer.parseInt(multiSegments.get(i).duration);
        }
        totalDurationView.setText(totalDuration+"min");
    }


}
