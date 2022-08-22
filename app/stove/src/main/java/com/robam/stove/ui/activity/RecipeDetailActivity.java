package com.robam.stove.ui.activity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.RecipeMaterial;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.ui.adapter.RvMaterialAdapter;
import com.robam.stove.ui.adapter.RvStepAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends StoveBaseActivity {

    private RecyclerView rvMaterial;
    private Group group1, group2, group3;
    private TextView tvQrcode, tvMaterial, tvStep;
    private ImageView ivRecipe;
    //食材
    private RvMaterialAdapter rvMaterialAdapter;
    //步骤
    private RecyclerView rvStep;
    private RvStepAdapter rvStepAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.stove_activity_layout_recipe_detail;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.stove_group1);  //二维码
        group2 = findViewById(R.id.stove_group2);  //食材
        group3 = findViewById(R.id.stove_group3);  //步骤
        tvQrcode = findViewById(R.id.tv_qrcode);
        tvMaterial = findViewById(R.id.tv_material);
        tvStep = findViewById(R.id.tv_step);
        rvStep = findViewById(R.id.rv_step);
        ivRecipe = findViewById(R.id.iv_recipe_img);
        //食材
        rvMaterial.setLayoutManager(new GridLayoutManager(this, 2));
        rvMaterial.addItemDecoration(new GridSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_126)));
        rvMaterialAdapter = new RvMaterialAdapter();
        rvMaterial.setAdapter(rvMaterialAdapter);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStepAdapter = new RvStepAdapter();
        rvStep.setAdapter(rvStepAdapter);
        setOnClickListener(R.id.tv_qrcode, R.id.tv_material, R.id.tv_step);

    }

    @Override
    protected void initData() {
//初始值
        tvQrcode.setSelected(true);
        RequestOptions maskOption = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.stove_main_item_bg) //预加载图片
                .error(R.drawable.stove_main_item_bg) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
                .override((int) (370), (int) (370));
        ImageUtils.loadImage(getContext(), R.drawable.stove_main_item_bg, maskOption, ivRecipe);

        //test
        List<RecipeMaterial> materialList = new ArrayList<>();
        materialList.add(new RecipeMaterial("排骨", 800, "克"));
        materialList.add(new RecipeMaterial("枸杞", 2, "克"));
        materialList.add(new RecipeMaterial("姜", 5, "克"));
        materialList.add(new RecipeMaterial("盐", 2, "克"));
        materialList.add(new RecipeMaterial("排骨", 800, "克"));
        materialList.add(new RecipeMaterial("排骨", 800, "克"));
        materialList.add(new RecipeMaterial("排骨", 800, "克"));
        rvMaterialAdapter.setList(materialList);

        List<RecipeStep> stepList = new ArrayList<>();
        stepList.add(new RecipeStep("sdfs", 1, "20min"));
        stepList.add(new RecipeStep("sdfs", 2, "20min"));
        stepList.add(new RecipeStep("sdfs", 3, "20min"));
        rvStepAdapter.setList(stepList);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_material) {
            if (!tvMaterial.isSelected()){
                tvMaterial.setSelected(true);
                tvQrcode.setSelected(false);
                tvStep.setSelected(false);
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.VISIBLE);
                group3.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_qrcode) {
            if (!tvQrcode.isSelected()) {
                tvQrcode.setSelected(true);
                tvMaterial.setSelected(false);
                tvStep.setSelected(false);
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                group3.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_step) {
            if (!tvStep.isSelected()) {
                tvStep.setSelected(true);
                tvQrcode.setSelected(false);
                tvMaterial.setSelected(false);
                group3.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
                group1.setVisibility(View.GONE);
            }
        }
    }
}