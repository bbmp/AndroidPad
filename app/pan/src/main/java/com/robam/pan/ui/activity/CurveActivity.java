package com.robam.pan.ui.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.BaseResponse;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.HorizontalSpaceItemDecoration;
import com.robam.pan.bean.PanCurveDetail;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.factory.PanDialogFactory;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetCurveCookbooksRes;
import com.robam.pan.ui.adapter.RvCurveAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//烹饪曲线
public class CurveActivity extends PanBaseActivity {
    private RecyclerView rvRecipe;
    private RvCurveAdapter rvCurveAdapter;
    private TextView tvRight;
    private ImageView ivRight;
    private List<PanCurveDetail> panCurveDetails = new ArrayList<>();
    private TextView tvDelete; //确认删除

    private IDialog selectDialog, openDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_curve;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        tvRight = findViewById(R.id.tv_right);
        //
        tvRight.setText(R.string.pan_delete);
        ivRight = findViewById(R.id.iv_right);
        rvRecipe = findViewById(R.id.rv_recipe);
        tvDelete = findViewById(R.id.tv_delete);
        rvRecipe.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecipe.addItemDecoration(new HorizontalSpaceItemDecoration((int)getResources().getDimension(com.robam.common.R.dimen.dp_8), (int)getResources().getDimension(com.robam.common.R.dimen.dp_32)));
        rvCurveAdapter = new RvCurveAdapter();
        rvRecipe.setAdapter(rvCurveAdapter);

        rvCurveAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                //删除状态不响应
                if (rvCurveAdapter.getStatus() != rvCurveAdapter.STATUS_BACK)
                    return;
                if (position == 0) //曲线创作
                    selectStove();
                else {
                    //曲线选中页
                    PanCurveDetail panCurveDetail = (PanCurveDetail) adapter.getItem(position);
                    Intent intent = new Intent();
                    intent.setClass(CurveActivity.this, RecipeSelectedActivity.class);
                    intent.putExtra(PanConstant.EXTRA_CURVE_ID, panCurveDetail.curveCookbookId);
                    startActivity(intent);
                }
            }
        });
        rvCurveAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                //某一条菜删除
                if (view.getId() == R.id.iv_select) {
                    PanCurveDetail panCurveDetail = (PanCurveDetail) adapter.getItem(position);
                    if (rvCurveAdapter.getStatus() == rvCurveAdapter.STATUS_ALL) {
                        //全选-》删除
                        panCurveDetail.setSelected(false);
                        ivRight.setImageResource(R.drawable.pan_unselected);
                        rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                    } else if (rvCurveAdapter.getStatus() == rvCurveAdapter.STATUS_DELETE) {
                        panCurveDetail.setSelected(!panCurveDetail.isSelected());
                        //检测是否全选
                        if (isAll()) {
                            ivRight.setImageResource(R.drawable.pan_selected);
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
        panCurveDetails.clear();
        panCurveDetails.add(0, new PanCurveDetail("创作烹饪曲线"));
        //需过滤掉其他曲线,锅和灶一起
        if (null != getCurveCookbooksRes && null != getCurveCookbooksRes.payload) {
            for (PanCurveDetail panCurveDetail : getCurveCookbooksRes.payload) {
                if (panCurveDetail.deviceParams.contains(IDeviceType.RRQZ) ||
                        panCurveDetail.deviceParams.contains(IDeviceType.RZNG))
                    panCurveDetails.add(panCurveDetail);
            }
        }

        rvCurveAdapter.setList(panCurveDetails);
        //是否显示删除
        if (panCurveDetails.size() > 1) {
            showRight();
            ivRight.setImageResource(R.drawable.pan_delete);
        }
    }

    //炉头选择
    private void selectStove() {
        //炉头选择提示
        if (null == selectDialog) {
            selectDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_SELECT_STOVE);
            selectDialog.setCancelable(false);
            selectDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.view_left || id == R.id.view_right)
                        openFire();
                }
            }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        }
        selectDialog.show();
    }
    //点火提示
    private void openFire() {
//        IDialog iDialog = PanDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
//        iDialog.setCancelable(false);
//        iDialog.show();
        startActivity(CurveCreateActivity.class);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_right) {
            if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_DELETE)
            {
                //删除-》全选
                allSelect();
                ivRight.setImageResource(R.drawable.pan_selected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_ALL);
            } else if (rvCurveAdapter.getStatus() == RvCurveAdapter.STATUS_ALL){
                //全选-》取消全选
                allUnelect();
                ivRight.setImageResource(R.drawable.pan_unselected);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);

            } else {
                //返回-》删除
                tvRight.setText(R.string.pan_select_all);
                ivRight.setImageResource(R.drawable.pan_unselected);
                panCurveDetails.remove(0);
                rvCurveAdapter.setList(panCurveDetails);
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_DELETE);
                tvDelete.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ll_left) {
            if (rvCurveAdapter.getStatus() != RvCurveAdapter.STATUS_BACK) {
                //设置非删除状态
                tvRight.setText(R.string.pan_delete);
                ivRight.setImageResource(R.drawable.pan_delete);
                panCurveDetails.add(0, new PanCurveDetail("创作烹饪曲线"));
                rvCurveAdapter.setList(panCurveDetails);
                allUnelect();
                rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
                tvDelete.setVisibility(View.INVISIBLE);
            } else
                finish();
        } else if (id == R.id.tv_delete) {
            //确认删除
            delete();
            //设置非删除状态
            if (panCurveDetails.size() <= 1)
                hideRight();
            tvRight.setText(R.string.pan_delete);
            ivRight.setImageResource(R.drawable.pan_delete);
            panCurveDetails.add(0, new PanCurveDetail("创作烹饪曲线"));
            rvCurveAdapter.setList(panCurveDetails);
            allUnelect();
            rvCurveAdapter.setStatus(RvCurveAdapter.STATUS_BACK);
            tvDelete.setVisibility(View.INVISIBLE);
        }
    }

    //全选
    private void allSelect() {
        for (int i=0; i<panCurveDetails.size(); i++) {
            panCurveDetails.get(i).setSelected(true);
        }
    }
    //取消全选
    private void allUnelect() {
        for (int i=0; i<panCurveDetails.size(); i++) {
            panCurveDetails.get(i).setSelected(false);
        }
    }
    //检查是否全选
    private boolean isAll() {
        for (int i=0; i<panCurveDetails.size(); i++) {
            if (!panCurveDetails.get(i).isSelected())
                return false;
        }
        return true;
    }
    //删除曲线
    private void delete() {
        UserInfo info = AccountInfo.getInstance().getUser().getValue();

        Iterator<PanCurveDetail> iterator = panCurveDetails.iterator();
        while (iterator.hasNext()) {
            PanCurveDetail panCurveDetail = iterator.next();
            if (panCurveDetail.isSelected()) {
                iterator.remove();
                //删除
                CloudHelper.delCurve(this, (info != null) ? info.id:0, panCurveDetail.curveCookbookId, BaseResponse.class,
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != selectDialog && selectDialog.isShow())
            selectDialog.dismiss();
        if (null != openDialog && openDialog.isShow())
            openDialog.dismiss();
    }
}