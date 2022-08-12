package com.robam.pan.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.pan.R;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.RecipeMaterial;
import com.robam.pan.ui.adapter.RvMaterialAdapter;

import java.util.ArrayList;
import java.util.List;


public class RecipeDetailActivity extends PanBaseActivity {
    private RecyclerView rvMaterial;
    private Group group1, group2;
    private TextView tvQrcode, tvMaterial;
    private ImageView ivRecipe;
    private RvMaterialAdapter rvMaterialAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe_detail;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.pan_group1);
        group2 = findViewById(R.id.pan_group2);
        tvQrcode = findViewById(R.id.tv_qrcode);
        tvMaterial = findViewById(R.id.tv_material);
        ivRecipe = findViewById(R.id.iv_recipe_img);
        rvMaterial.setLayoutManager(new GridLayoutManager(this, 2));
        rvMaterial.addItemDecoration(new GridSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_126)));
        rvMaterialAdapter = new RvMaterialAdapter();
        rvMaterial.setAdapter(rvMaterialAdapter);
        setOnClickListener(R.id.tv_qrcode, R.id.tv_material);
    }

    @Override
    protected void initData() {
        //初始值
        tvQrcode.setSelected(true);
        RequestOptions maskOption = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.pan_main_item_bg) //预加载图片
                .error(R.drawable.pan_main_item_bg) //加载失败图片
                .priority(Priority.HIGH) //优先级
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
                .override((int) (370), (int) (370));
        ImageUtils.loadImage(getContext(), R.drawable.pan_main_item_bg, maskOption, ivRecipe);

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
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_material) {
            if (!tvMaterial.isSelected()){
                tvMaterial.setSelected(true);
                tvQrcode.setSelected(false);
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.tv_qrcode) {
            if (!tvQrcode.isSelected()) {
                tvQrcode.setSelected(true);
                tvMaterial.setSelected(false);
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
            }
        }
    }
}