package com.robam.stove.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.Material;
import com.robam.stove.bean.MaterialClassify;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.bean.StoveRecipeDetail;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetRecipeDetailRes;
import com.robam.stove.ui.adapter.RvMaterialAdapter;
import com.robam.stove.ui.adapter.RvStepAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends StoveBaseActivity {

    private RecyclerView rvMaterial;
    private Group group1, group2, group3;
    private TextView tvQrcode, tvMaterial, tvStep;
    //菜谱图片
    private ImageView ivRecipe;
    //菜谱名字
    private TextView tvRecipeName;
    //时长
    private TextView tvTime;
    //食材
    private RvMaterialAdapter rvMaterialAdapter;
    //步骤
    private RecyclerView rvStep;
    private RvStepAdapter rvStepAdapter;
    private long recipeId;

    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.stove_main_item_bg) //预加载图片
            .error(R.drawable.stove_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (370), (int) (370));

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_recipe_detail;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        if (null != getIntent())
            recipeId = getIntent().getLongExtra(StoveConstant.EXTRA_RECIPE_ID, 0);
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.stove_group1);  //二维码
        group2 = findViewById(R.id.stove_group2);  //食材
        group3 = findViewById(R.id.stove_group3);  //步骤
        tvQrcode = findViewById(R.id.tv_qrcode);
        tvMaterial = findViewById(R.id.tv_material);
        tvStep = findViewById(R.id.tv_step);
        rvStep = findViewById(R.id.rv_step);
        ivRecipe = findViewById(R.id.iv_recipe_img);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvTime = findViewById(R.id.tv_time);
        //食材
        rvMaterial.setLayoutManager(new GridLayoutManager(this, 2));
        rvMaterial.addItemDecoration(new GridSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_126)));
        rvMaterialAdapter = new RvMaterialAdapter();
        rvMaterial.setAdapter(rvMaterialAdapter);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStepAdapter = new RvStepAdapter();
        rvStep.setAdapter(rvStepAdapter);
        setOnClickListener(R.id.tv_qrcode, R.id.tv_material, R.id.tv_step, R.id.btn_start);

    }

    @Override
    protected void initData() {
//初始值
        tvQrcode.setSelected(true);
        getRecipeDetail();

    }

    //获取菜谱详情
    private void getRecipeDetail() {
        CloudHelper.getRecipeDetail(this, recipeId, "1", "1", GetRecipeDetailRes.class, new RetrofitCallback<GetRecipeDetailRes>() {
            @Override
            public void onSuccess(GetRecipeDetailRes getRecipeDetailRes) {
                if (null != getRecipeDetailRes && null != getRecipeDetailRes.cookbook)
                    setData(getRecipeDetailRes.cookbook);
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }
    //获取到的数据
    private void setData(StoveRecipeDetail stoveRecipeDetail) {
        //图片
        ImageUtils.loadImage(this, stoveRecipeDetail.imgSmall, maskOption, ivRecipe);
        //名字
        tvRecipeName.setText(stoveRecipeDetail.name);
        //时长
        tvTime.setText("时间   " + stoveRecipeDetail.needTime / 60 + "min");
        //食材
        List<Material> materials = new ArrayList<>();
        if (null != stoveRecipeDetail.materials && null != stoveRecipeDetail.materials.main)
            materials.addAll(stoveRecipeDetail.materials.main);

        if (null != stoveRecipeDetail.materials && null != stoveRecipeDetail.materials.accessory)
            materials.addAll(stoveRecipeDetail.materials.accessory);
        rvMaterialAdapter.setList(materials);
        //步骤
        List<RecipeStep> recipeSteps = new ArrayList<>();
        if (null != stoveRecipeDetail.steps)
            recipeSteps.addAll(stoveRecipeDetail.steps);
        rvStepAdapter.setList(recipeSteps);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_material) {   //食材
            if (!tvMaterial.isSelected()){
                tvMaterial.setSelected(true);
                tvQrcode.setSelected(false);
                tvStep.setSelected(false);
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.VISIBLE);
                group3.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_qrcode) {  //二维码
            if (!tvQrcode.isSelected()) {
                tvQrcode.setSelected(true);
                tvMaterial.setSelected(false);
                tvStep.setSelected(false);
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                group3.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_step) {  //步骤
            if (!tvStep.isSelected()) {
                tvStep.setSelected(true);
                tvQrcode.setSelected(false);
                tvMaterial.setSelected(false);
                group3.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                group1.setVisibility(View.GONE);
            }
        } else if (id == R.id.btn_start) {//开始烹饪

        }
    }
}