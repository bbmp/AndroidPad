package com.robam.stove.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.VerticalSpaceItemDecoration;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.CurveStep;
import com.robam.stove.bean.StoveCurveDetail;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetCurveCookbooksRes;
import com.robam.stove.response.GetCurveDetailRes;
import com.robam.stove.ui.adapter.RvStep3Adapter;

import java.util.ArrayList;
import java.util.List;

//曲线选中，烹饪曲线其他进入
public class CurveSelectedActivity extends StoveBaseActivity {
    private long curveid;
    //曲线步骤
    private RvStep3Adapter rvStep3Adapter;
    private RecyclerView rvStep;
    //曲线名字
    private TextView tvCurveName;
    //开始烹饪
    private TextView tvStartCook;
    //曲线详情
    private StoveCurveDetail stoveCurveDetail;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_curve_selected;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        if (null != getIntent())
            curveid = getIntent().getLongExtra(StoveConstant.EXTRA_CURVE_ID, -1);
        rvStep = findViewById(R.id.rv_step);
        tvCurveName = findViewById(R.id.tv_recipe_name);
        tvStartCook = findViewById(R.id.tv_start_cook);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStep.addItemDecoration(new VerticalSpaceItemDecoration((int) getContext().getResources().getDimension(com.robam.common.R.dimen.dp_15)));
        rvStep3Adapter = new RvStep3Adapter();
        rvStep.setAdapter(rvStep3Adapter);

        setOnClickListener(R.id.tv_start_cook);
    }

    @Override
    protected void initData() {
        getCuveDetail();

    }
    //曲线详情
    private void getCuveDetail() {
        CloudHelper.getCurvebookDetail(this, curveid, GetCurveDetailRes.class, new RetrofitCallback<GetCurveDetailRes>() {
            @Override
            public void onSuccess(GetCurveDetailRes getCurveDetailRes) {
                if (null != getCurveDetailRes && null != getCurveDetailRes.payload) {
                    stoveCurveDetail = getCurveDetailRes.payload;
                    tvCurveName.setText(stoveCurveDetail.name);

                    List<CurveStep> curveSteps = new ArrayList<>();
                    if (null != stoveCurveDetail.stepList) {
                        curveSteps.addAll(stoveCurveDetail.stepList);
                        tvStartCook.setVisibility(View.VISIBLE);
                    }
                    rvStep3Adapter.setList(curveSteps);
                }
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_start_cook) {
            //曲线还原
            Intent intent = new Intent();
            if (null != stoveCurveDetail)
                intent.putExtra(StoveConstant.EXTRA_CURVE_DETAIL, stoveCurveDetail);
            intent.setClass(this, CurveRestoreActivity.class);
            startActivity(intent);
            finish();
        }

    }
}