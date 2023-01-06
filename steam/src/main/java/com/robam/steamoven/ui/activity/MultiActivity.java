package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.FuntionBean;
import com.robam.steamoven.bean.ModeBean;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.MultiSegmentEnum;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamEnum;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.constant.SteamOvenSteamEnum;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.utils.SkipUtil;

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

    private TextView startCookBtn;

    //设置段数据
    private List<MultiSegment> multiSegments = new ArrayList<>();
    //当前段数（3段）
    public static final int  CUR_ITEM_VIEW_COUNT = 3;
    public static final int  DATA_KEY = R.id.multi_opt;
    public static final int  START_WORK_CODE = 350;
    //跳转集合
    //private List<FuntionBean> funtionBeans;
    private FuntionBean funtionBean;
    //private List<ModeBean> modeBeanList;
    //多段是否已启动
    private boolean isStart = false;

    private int directive_offset = 16000000;


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
        totalDurationView = findViewById(R.id.steam_total_duration_value);
        startCookBtn = findViewById(R.id.btn_start);
        initOptViewTag();
        initOptContent();
        initDelBtnView();
        setOnClickListener(R.id.btn_start);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            toWorkPage(steamOven);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }
                }
            }
        });
    }

    /**
     * 去往工作页面
     * @param steamOven
     */
    private void toWorkPage(SteamOven steamOven){
        if(steamOven.mode == 0){
            return;
        }
        SkipUtil.toWorkPage(steamOven,this);
    }

    private void showLight(){
        showRightCenter();
        ((TextView)findViewById(R.id.tv_right_center)).setText(R.string.steam_light);
        ((ImageView)findViewById(R.id.iv_right_center)).setImageResource(R.drawable.steam_ic_light_max);
    }

    /**
     * 设置删除文本与删除按钮图标
     */
    private void initDelBtnView(){
        setOnClickListener(R.id.ll_right);
        ((ImageView)findViewById(R.id.iv_right)).setImageResource(R.drawable.steam_ic_title_del);
        ((TextView)findViewById(R.id.tv_right)).setText(R.string.steam_title_delete);
    }


    private void setDelBtnState(boolean isShow){
        findViewById(R.id.ll_right).setVisibility(isShow? View.VISIBLE:View.INVISIBLE);
        float paddingBottom;
        if(isShow){
            paddingBottom = getResources().getDimension(com.robam.common.R.dimen.dp_22);
        }else{
            paddingBottom = getResources().getDimension(com.robam.common.R.dimen.dp_36);
        }
        optContentParentView.setPadding(optContentParentView.getPaddingLeft(),optContentParentView.getPaddingTop(),optBottomParentView.getPaddingRight(), (int) paddingBottom);
        optContentParentView.requestLayout();
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
                optContentParentView.getChildAt(i).setOnClickListener(view -> {
                    if(!checkSegmentState(view) && !isStart){
                        Toast.makeText(getContext(),R.string.steam_work_multi_check_message,Toast.LENGTH_LONG).show();
                        return;
                    }
                    int index = Integer.parseInt(view.getTag()+"");
                    if(isStart && index <= multiSegments.size() -1 && multiSegments.get(index).isFinish()){//该段以工作完成
                        return;
                    }
                    //调整到模式设置页面 ： 第一段 - 默认调整到专业模式下的 - 营养蒸； 第二段 - 默认跳转到考模式下的 - 烘焙 ； 第三段 - 默认调整到炸模式 - 空气炸
                    MultiActivity.this.toTagPageModel(view);
                });
            }
        }
    }

    private void toTagPageModel(View view){
        this.toModelPage(funtionBean,Integer.parseInt(view.getTag()+""),getSelectCode(view));
    }

    private MultiSegment getSelectCode(View view){
        Object data = view.getTag(DATA_KEY);
        if(data == null){//去往默认规则页面
            int modeCode = MultiSegmentEnum.matchIndex(Integer.parseInt(view.getTag()+""));
            if(modeCode == -1){
                return null;
            }
            for(int i = 0;i < funtionBean.mode.size();i++){
                if(modeCode == funtionBean.mode.get(i).code){
                    MultiSegment emptySegment = new MultiSegment();
                    emptySegment.code = funtionBean.mode.get(i).code;
                    emptySegment.defTemp = funtionBean.mode.get(i).defTemp;
                    emptySegment.downTemp = funtionBean.mode.get(i).defTemp;
                    emptySegment.duration = funtionBean.mode.get(i).defTime;
                    emptySegment.steam = funtionBean.mode.get(i).defSteam;
                    return emptySegment;
                }
            }
            return null;
        }else{//去往当前选择模式页面
            return (MultiSegment) data;
        }
    }





    private void toModelPage(FuntionBean funtionBean,int index,MultiSegment curSegment){
        if (funtionBean == null || funtionBean.into == null || funtionBean.into.length() == 0) {
            ToastUtils.showShort(getContext(), "功能还未实现，请等待版本更新");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(SteamConstant.EXTRA_MODE_LIST, funtionBean.mode);
        intent.setClassName(getContext(), funtionBean.into);
        intent.putExtra(Constant.NEED_SET_RESULT,true);
        intent.putExtra(Constant.SEGMENT_DATA_FLAG,curSegment);
        intent.putExtra(Constant.SEGMENT_SECTION,getSectionResId(index));
        startActivityForResult(intent,index);
    }

    private int getSectionResId(int index){
        switch (index){
            case 0:
                return R.string.steam_section_1;
            case 1:
                return R.string.steam_section_2;
            case 2:
                return R.string.steam_section_3;
            default:
               return  -1;
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
        int enableColor = getResources().getColor(R.color.steam_white);
        int disableColor = getResources().getColor(R.color.steam_white_72);
        for(int i = 0; i < CUR_ITEM_VIEW_COUNT;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");
            itemGroup.setTag(DATA_KEY,null);

            TextView segmentView = itemGroup.findViewById(R.id.multi_item_name);
            segmentView.setTextColor(i == 0 ? enableColor:disableColor);
            segmentView.setText(this.getSegmentName(i));

            TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
            modelView.setTextColor(i == 0 ? enableColor:disableColor);
            modelView.setText("");

            TextView steamView = itemGroup.findViewById(R.id.multi_item_model);
            steamView.setTextColor(i == 0 ? enableColor:disableColor);
            steamView.setText("");

            TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
            temperatureView.setTextColor(i == 0 ? enableColor:disableColor);
            temperatureView.setText("");

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
        int disableColor = getResources().getColor(R.color.steam_white_72);
        int maxCount = multiSegments.size() >= CUR_ITEM_VIEW_COUNT ? CUR_ITEM_VIEW_COUNT : multiSegments.size();
        optBottomParentView.setVisibility(maxCount >= 1 ? View.VISIBLE : View.GONE);
        for(int i = 0; i < maxCount;i++){
            ViewGroup itemGroup = optContentParentView.findViewWithTag(i+"");

            if(multiSegments.get(i).isFinish()){
                setOptItemContent(itemGroup,multiSegments.get(i),i,false,disableColor,R.drawable.steam_ic_multi_item_add);
            }else{
                setOptItemContent(itemGroup,multiSegments.get(i),i,false,enableColor,R.drawable.steam_ic_multi_item_add);
            }
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

        if(!isInit && SteamModeEnum.isAddSteam(multiSegmentBean.code)){
            TextView steamView = itemGroup.findViewById(R.id.multi_item_steam);
            steamView.setVisibility(View.VISIBLE);
            steamView.setTextColor(textColor);
            String streamValue = isInit ? "" : SteamOvenSteamEnum.match(multiSegmentBean.steam) +"蒸汽";
            steamView.setText(streamValue);
        }else{
            itemGroup.findViewById(R.id.multi_item_steam).setVisibility(View.GONE);
        }

        TextView temperatureView = itemGroup.findViewById(R.id.multi_item_temperature);
        temperatureView.setTextColor(textColor);
        String temperature = isInit ? "" : (multiSegmentBean.defTemp + "°c");
        temperatureView.setText(temperature);


        TextView modelView = itemGroup.findViewById(R.id.multi_item_model);
        modelView.setTextColor(textColor);
        String model = isInit ? "" : multiSegmentBean.model+"";
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
                return "第一段";
            case 1:
                return "第二段";
            case 2:
                return "第三段";
            default:
                return "";
        }
    }


    @Override
    protected void initData() {
        //funtionBeans = FunctionManager.getFuntionList(getContext(), FuntionBean.class, R.raw.steam);
        //funtionBeans = FunctionManager.getFuntionList(getContext(), FuntionBean.class, R.raw.steam);
        List<ModeBean> modeBeanList = FunctionManager.getFuntionList(getContext(),ModeBean.class,R.raw.steammode);
        funtionBean = new FuntionBean();
        funtionBean.mode = (ArrayList<ModeBean>) modeBeanList;
        funtionBean.into = "com.robam.steamoven.ui.activity.ModeSelectActivity";
        isStart = getIntent().getBooleanExtra(Constant.SEGMENT_WORK_FLAG,false);
        if(isStart){
            multiSegments = getIntent().getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
            setOptContent(multiSegments);
            setTotalDuration();
            initStartBtnState();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            if(isDelState()){
                setDelBtnItemState(false);
                setDelBtnState(true);
                return;
            }
            finish();
        }else if(id == R.id.ll_right){//显示删除按钮
            showItemDelView();
        }else if(id == R.id.multi_item_del){//删除按钮被点击
            showDealDialog(view);
        }else if(id == R.id.btn_start){
            //toWorkAc();
            startWork();
        }
    }



    private void toWorkAc(){
        if(!multiSegments.get(0).isStart() && multiSegments.size() < 2){
            Toast.makeText(this, R.string.steam_cook_start_prompt,Toast.LENGTH_LONG).show();
            return;
        }

        if(!isStart){
            isStart = true;
            multiSegments.get(0).setCookState(MultiSegment.COOK_STATE_START);
        }
        for(int i = 0;i < multiSegments.size();i++){
            multiSegments.get(i).workRemaining = multiSegments.get(i).duration * 60;
        }
        Intent intent = new Intent(this,MultiWorkActivity.class);
        intent.putParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG, (ArrayList<? extends Parcelable>) multiSegments);
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
        initStartBtnState();
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
                dealWorkBack(data);
            }else{
                dealResult(requestCode,data);
            }

        }
    }

    private void dealWorkBack(Intent data){
        isStart = true;
        multiSegments = data.getParcelableArrayListExtra(Constant.SEGMENT_DATA_FLAG);
        setOptContent(multiSegments);
        setTotalDuration();
        initStartBtnState();
    }



    private void dealResult(int requestCode, Intent data){
        if(multiSegments.size() > requestCode){//修改当前历史
            MultiSegment resultData  = data.getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
            multiSegments.remove(requestCode);
            multiSegments.add(requestCode,resultData);
        }else{//添加新对象
            MultiSegment resultData  = data.getParcelableExtra(Constant.SEGMENT_DATA_FLAG);
            multiSegments.add(resultData);
        }
        setDelBtnState(multiSegments.size() > 0 ? true:false);
        setOptContent(multiSegments);
        setTotalDuration();
        initStartBtnState();
    }

    private void setTotalDuration(){
        int totalDuration = 0;
        for(int i = 0;i < multiSegments.size();i++){
            totalDuration += multiSegments.get(i).duration;
        }
        totalDurationView.setText(totalDuration+"min");
    }

    private void initStartBtnState(){
        if(multiSegments.size() == 0){
            return;
        }
        //startCookBtn.setVisibility(isDelState() ?View.INVISIBLE:View.VISIBLE);
        if(multiSegments.get(0).isStart()){
            startCookBtn.setText(R.string.steam_work_continue);
            startCookBtn.setTextColor(getResources().getColor(R.color.steam_white));
            startCookBtn.setBackgroundResource(R.drawable.steam_shape_button_selected);
        }else{
            if(multiSegments.size() == 1){
                startCookBtn.setText(R.string.steam_start);
                startCookBtn.setTextColor(getResources().getColor(R.color.steam_white_72));
                startCookBtn.setBackgroundResource(R.drawable.steam_shape_button_unselected);
            }else if(multiSegments.size() >= 2){
                startCookBtn.setText(R.string.steam_start);
                startCookBtn.setTextColor(getResources().getColor(R.color.steam_white));
                startCookBtn.setBackgroundResource(R.drawable.steam_shape_button_selected);
            }
        }
    }

    private void startWork(){
        for(int i = 0;i < multiSegments.size();i++){
            if(!SteamCommandHelper.checkSteamState(this,getSteamOven(),multiSegments.get(i).code)){
                return;
            }
        }
        SteamCommandHelper.sendMultiWork(this,multiSegments,directive_offset+MsgKeys.setDeviceAttribute_Req);
    }


}
