package com.robam.steamoven.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.response.GetCurveCookbooksRes;
import com.robam.steamoven.ui.adapter.RvCurveAdapter;

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
        showRightCenter();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.steam_delete);
        ivRight = findViewById(R.id.iv_right);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvDelete = findViewById(R.id.tv_delete);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvRecipe.setLayoutManager(linearLayoutManager);
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvCurveAdapter = new RvCurveAdapter();
        rvRecipe.setAdapter(rvCurveAdapter);

        rvCurveAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //删除状态不响应
                if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK)
                    return;
                //曲线选中页
                SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(SteamConstant.EXTRA_CURVE_ID, steamCurveDetail.curveCookbookId);
                intent.setClass(CurveActivity.this, CurveSelectedActivity.class);
                startActivity(intent);

            }
        });
        rvCurveAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                //某一条菜删除
                if (view.getId() == R.id.iv_select) {
                    SteamCurveDetail steamCurveDetail = (SteamCurveDetail) adapter.getItem(position);
                    if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL) {
                        //全选-》删除
                        steamCurveDetail.setSelected(false);
                        ivRight.setImageResource(R.drawable.steam_unselected);
                        rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE) {
                        steamCurveDetail.setSelected(!steamCurveDetail.isSelected());
                        //检测是否全选
                        if (isAll()) {
                            ivRight.setImageResource(R.drawable.steam_selected);
                            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
                        }
                        else
                            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    }
                }
            }
        });

        setOnClickListener(R.id.ll_left, R.id.ll_right, R.id.tv_delete);
    }

    @Override
    protected void initData() {
        getCurveList();
    }

    //获取烹饪曲线列表
    private void getCurveList() {
        UserInfo info = AccountInfo.getInstance().getUser().getValue();

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
        if (null != getCurveCookbooksRes && null != getCurveCookbooksRes.payload) {
            for (SteamCurveDetail steamCurveDetail : getCurveCookbooksRes.payload) {
                if (steamCurveDetail.deviceParams.contains(IDeviceType.RZKY))
                    steamCurveDetails.add(steamCurveDetail);
            }
        }

        rvCurveAdapter.setList(steamCurveDetails);
        //是否显示删除
        if (steamCurveDetails.size() > 1) {
            showRight();
            ivRight.setImageResource(R.drawable.steam_delete);
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
            //确认删除
            delete();
            //设置非删除状态
            if (steamCurveDetails.size() <= 1)
                hideRight();
            tvRight.setText(R.string.steam_delete);
            ivRight.setImageResource(R.drawable.steam_delete);

            allUnselect();
            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
            tvDelete.setVisibility(View.INVISIBLE);
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
    }
}
