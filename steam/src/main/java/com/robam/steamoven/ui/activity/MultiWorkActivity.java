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
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamEnum;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;

import java.util.ArrayList;
import java.util.Arrays;
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
    //跳转集合
    private List<FuntionBean> funtionBeans;

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
        //initOptContent();
//       initDelBtnView();
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
                MultiWorkActivity.this.toSettingPage(view);
            });
        }
    }

    private void toSettingPage(View view){
        Intent intent = new Intent(this,MultiActivity.class);
        intent.putParcelableArrayListExtra(SteamConstant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
        intent.putExtra(SteamConstant.SEGMENT_WORK_FLAG,true);
        startActivityForResult(intent,REQUEST_CODE_SETTING);
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
            //optBottomParentView.setVisibility(View.GONE);
            return;
        }

        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        //optBottomParentView.setVisibility(maxCount >= 1 ? View.VISIBLE : View.GONE);
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            setOptItemContent(itemGroup,multiSegments.get(i));
        }
        for(int i = maxCount; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setVisibility(View.INVISIBLE);
            //setOptItemContent(itemGroup,null,i,true);
        }
    }

    private void setOptItemContent(ViewGroup itemGroup,MultiSegment multiSegmentBean){
        itemGroup.setVisibility(View.VISIBLE);

        View view = itemGroup.findViewById(R.id.multi_work_item_index);
        view.setVisibility(multiSegmentBean.isCooking ? View.VISIBLE:View.INVISIBLE);

        itemGroup.setTag(DATA_KEY,multiSegmentBean);

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        int textColor = getResources().getColor(multiSegmentBean.isCooking ? R.color.steam_white: R.color.steam_mode_d9);
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

        // itemGroup.findViewById(R.id.multi_item_add).setVisibility(isInit ? View.VISIBLE:View.INVISIBLE);
        //itemGroup.findViewById(R.id.multi_item_del).setVisibility(View.INVISIBLE);
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
        //获取上一个页面传递过来的参数
        //展示
        multiSegments = getIntent().getParcelableArrayListExtra(SteamConstant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            finish();
        }else if(id == R.id.multi_work_pause){//暂停工作
            this.setOptViewsState(false);
        }else if(id==R.id.multi_work_start){//继续工作
            this.setOptViewsState(true);
        }
    }


    /**
     * 是否正在烹饪
     * @return
     */
    private boolean isWorking(){
        return pauseCookView.getVisibility() == View.VISIBLE;
    }


    /**
     * 暂停烹饪
     */
    private void pauseCook(){


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
                if(segment.isCooking){
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




    /**
     * 继续烹饪
     */
    private void ContinueCook(){

    }

    private void showDealDialog(final View view){
        //final int index = ;//被点击的位置，从0开始
        //展示Dialog
        SteamCommonDialog steamCommonDialog = new SteamCommonDialog(MultiWorkActivity.this);
        steamCommonDialog.setContentText(R.string.steam_delete_ok_content);
        steamCommonDialog.setOKText(R.string.steam_delete);
        steamCommonDialog.setListeners(v -> {
            steamCommonDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                dealDataItem(Integer.parseInt(view.getTag()+""));
            }
        },R.id.tv_cancel,R.id.tv_ok);
        steamCommonDialog.show();
    }

    private void dealDataItem(int index){
        multiSegments.remove(index);
        setOptContent(multiSegments);
        setDelBtnItemState(true);
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
        //setDelBtnState(false);//隐藏删除按钮
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
        multiSegments = data.getParcelableExtra(SteamConstant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
    }


}
