package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.CurveData;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.response.GetCurveCookbooksRes;
import com.robam.steamoven.ui.adapter.RvCurveAdapter;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.utils.SkipUtil;
import com.robam.steamoven.utils.SteamPageData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurveActivity extends SteamBaseActivity {
    private RecyclerView rvRecipe;
    private RvCurveAdapter rvCurveAdapter;
    private TextView tvRight;
    private ImageView ivRight;
    private List<SteamCurveDetail> steamCurveDetails = new ArrayList<>();
    private TextView tvDelete; //确认删除
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        //showRight();
        //showRightCenter();
        tvRight = findViewById(R.id.tv_right);
        tvRight.setText(R.string.steam_delete);
        ivRight = findViewById(R.id.iv_right);
        ivRight.setImageResource(R.drawable.steam_delete);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvDelete = findViewById(R.id.tv_delete);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvRecipe.setLayoutManager(linearLayoutManager);
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvCurveAdapter = new RvCurveAdapter();
        rvRecipe.setAdapter(rvCurveAdapter);

        rvCurveAdapter.setOnItemClickListener((adapter, view, position) -> {
            //删除状态不响应
            if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE){
                SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
                steamCurveDetail.setSelected(!steamCurveDetail.isSelected());
                if (isAll()) {
                    ivRight.setImageResource(R.drawable.steam_selected);
                    rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
                    ivRight.setImageResource(R.drawable.steam_selected);
                }else{
                    rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    ivRight.setImageResource(R.drawable.steam_unselected);
                }
                rvCurveAdapter.notifyDataSetChanged();
                return;
            }
            if(rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL){
                SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
                steamCurveDetail.setSelected(!steamCurveDetail.isSelected());
                if(!steamCurveDetail.isSelected()){
                    rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    ivRight.setImageResource(R.drawable.steam_unselected);
                }else{
                    ivRight.setImageResource(R.drawable.steam_selected);
                }
                rvCurveAdapter.notifyDataSetChanged();
                return;
            }
            if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK)
                return;
            //曲线选中页
            SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
            Intent intent = new Intent();
            intent.putExtra(SteamConstant.EXTRA_CURVE_ID, steamCurveDetail.curveCookbookId);
            intent.setClass(CurveActivity.this, CurveSelectedActivity.class);
            startActivity(intent);

        });
        rvCurveAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            //某一条菜删除
            if (view.getId() == R.id.iv_select) {
                SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
                if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL) {
                    //全选-》删除
                    steamCurveDetail.setSelected(false);
                    ivRight.setImageResource(R.drawable.steam_unselected);
                    rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    ivRight.setImageResource(R.drawable.steam_unselected);
                } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE) {
                    steamCurveDetail.setSelected(!steamCurveDetail.isSelected());
                    //检测是否全选
                    if (isAll()) {
                        ivRight.setImageResource(R.drawable.steam_selected);
                        rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
                        ivRight.setImageResource(R.drawable.steam_selected);
                    }else{
                        rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                        ivRight.setImageResource(R.drawable.steam_unselected);
                    }
                }
            }
        });

        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.tv_delete);

        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
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
                            SkipUtil.toWorkPage(steamOven,CurveActivity.this);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }


                }
            }
        });

        SteamPageData.getInstance().getBsData().observe(this, bsData->{
            if(bsData == null){
                return;
            }
            if(StringUtils.isNotEmpty(bsData.content) && bsData.bsCode != 0 && rvCurveAdapter != null){
                List<SteamCurveDetail> data = rvCurveAdapter.getData();
                if(data != null){
                    for(SteamCurveDetail curveDetail : data){
                        if(curveDetail.curveCookbookId == bsData.bsCode){
                            curveDetail.name = bsData.content;
                            rvCurveAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        AccountInfo.getInstance().getUser().observe(this, userInfo -> {
            if (null != userInfo)
                getCurveList(userInfo);
            else
                setData(null);
        });
    }

    //获取烹饪曲线列表
    private void getCurveList(UserInfo info) {
        CloudHelper.queryCurveCookbooks(this, (info != null) ? info.id:0, GetCurveCookbooksRes.class,
                new RetrofitCallback<GetCurveCookbooksRes>() {
                    @Override
                    public void onSuccess(GetCurveCookbooksRes getCurveCookbooksRes) {
                        setData(getCurveCookbooksRes);
                    }

                    @Override
                    public void onFaild(String err) {
                        setData(null);
                    }
                });
    }

    //设置烹饪曲线
    private void setData(GetCurveCookbooksRes getCurveCookbooksRes) {
        steamCurveDetails.clear();
        //需过滤掉其他曲线
        if (null != getCurveCookbooksRes && null != getCurveCookbooksRes.data) {
            for (SteamCurveDetail steamCurveDetail : getCurveCookbooksRes.data) {
                if (steamCurveDetail.deviceParams != null && steamCurveDetail.deviceParams.contains(IDeviceType.RZKY) && steamCurveDetail.deviceParams.contains(IDeviceType.SERIES_STEAM)){
                    steamCurveDetails.add(steamCurveDetail);
                }
            }
        }

        rvCurveAdapter.setList(steamCurveDetails);
        //是否显示删除
        if (steamCurveDetails.size() >= 1) {
            showRight();
        }else{
            hideRight();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE)
            {
                //删除-》全选
                allSelect();
                ivRight.setImageResource(R.drawable.steam_selected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
            } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL){
                //全选-》取消全选
                allUnselect();
                ivRight.setImageResource(R.drawable.steam_unselected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);

            } else {
                //返回-》删除
                tvRight.setText(R.string.steam_select_all);
                ivRight.setImageResource(R.drawable.steam_unselected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                tvDelete.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_left) {
            if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK) {
                //设置非删除状态
                tvRight.setText(R.string.steam_delete);
                ivRight.setImageResource(R.drawable.steam_delete);

                allUnselect();
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
                tvDelete.setVisibility(View.INVISIBLE);
            } else
                finish();
        } else if (id == R.id.tv_delete) {
            if(rvCurveAdapter == null){
                ToastUtils.showLong(this,R.string.steam_curve_no_data_prompt);
                return;
            }
            boolean hasSelected = false;
            List<SteamCurveDetail> data = rvCurveAdapter.getData();
            if(data != null){
                for(SteamCurveDetail curveDetail : data){
                    if(curveDetail.isSelected()){
                        hasSelected = true;
                    }
                }
            }
            if(!hasSelected){
                ToastUtils.showLong(this,R.string.steam_curve_del_choice_prompt);
                return;
            }
            showDelDialog();
        }
    }
    //全选
    private void allSelect() {
        for (int i = 0; i< steamCurveDetails.size(); i++) {
            steamCurveDetails.get(i).setSelected(true);
        }
    }
    //取消全选
    private void allUnselect() {
        for (int i = 0; i< steamCurveDetails.size(); i++) {
            steamCurveDetails.get(i).setSelected(false);
        }
    }
    //检查是否全选
    private boolean isAll() {
        for (int i = 0; i< steamCurveDetails.size(); i++) {
            if (!steamCurveDetails.get(i).isSelected())
                return false;
        }
        return true;
    }
    //删除
    private void delete() {
        UserInfo info = AccountInfo.getInstance().getUser().getValue();
        Iterator<SteamCurveDetail> iterator = steamCurveDetails.iterator();
        while (iterator.hasNext()) {
            SteamCurveDetail steamCurveDetail = iterator.next();
            if (steamCurveDetail.isSelected()) {
                iterator.remove();
                //删除
                CloudHelper.delCurve(this, (info != null) ? info.id:0, steamCurveDetail.curveCookbookId, BaseResponse.class,
                        new RetrofitCallback<BaseResponse>() {
                            @Override
                            public void onSuccess(BaseResponse baseResponse) {
                                if (null != baseResponse)
                                    ;
                            }

                            @Override
                            public void onFaild(String err) {

                            }
                        });
            }
        }
        rvCurveAdapter.setList(steamCurveDetails);
        rvCurveAdapter.notifyDataSetChanged();
    }

    SteamCommonDialog delDialog;
    private void showDelDialog(){
        delDialog = new SteamCommonDialog(this);
        delDialog.setContentText(R.string.steam_curve_del_prompt);
        delDialog.setOKText(R.string.steam_title_delete);
        delDialog.setListeners(v -> {
            delDialog.dismiss();
            if(v.getId() == R.id.tv_ok){
                //确认删除
                delete();
                //设置非删除状态
                if (steamCurveDetails.size() < 1)
                    hideRight();
                tvRight.setText(R.string.steam_delete);
                ivRight.setImageResource(R.drawable.steam_delete);

                allUnselect();
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
                tvDelete.setVisibility(View.INVISIBLE);
            }
        },R.id.tv_cancel,R.id.tv_ok);
        delDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(delDialog != null && delDialog.isShow()){
            delDialog.dismiss();
        }
        SteamPageData.getInstance().getBsData().setValue(null);
    }

}
