package com.robam.steamoven.ui.activity;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.ui.adapter.RvMaterialAdapter;
import com.robam.steamoven.ui.adapter.RvStepAdapter;

public class recipe3DetailActivityOld extends SteamBaseActivity {
    private RecyclerView rvMaterial;
    private Group group1, group2, group3;
    private TextView tvQrcode, tvMaterial, tvStep;
    //菜谱图片
    private ImageView ivRecipe;
    //菜谱名字
    private TextView tvRecipeName;
    //时间
    private ImageView ivTime;
    //时长
    private TextView tvTime;
    //食材
    private RvMaterialAdapter rvMaterialAdapter;
    //步骤
    private RecyclerView rvStep;
    private RvStepAdapter rvStepAdapter;
    //二维码
    private ImageView ivQrcode;
    //菜谱详情
    private SteamCurveDetail steamCurveDetail;
    private long recipeId;
    //二维码url
    private String url = "https://h5.myroki.com/dist/index.html#/recipeDetail?cookbookId=" + "%d&entranceCode=code1&isFromWx=true&userId=%d";

    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.steam_main_item_bg) //预加载图片
            .error(R.drawable.steam_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (370), (int) (370));

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_recipe_detail_3;
    }

    @Override
    protected void initView() {
        showLeft();
        showLeftCenter();
        showCenter();
        showRightCenter();

        if (null != getIntent())
            recipeId = getIntent().getLongExtra(SteamConstant.EXTRA_RECIPE_ID, 0);
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.steam_group1);  //二维码
        group2 = findViewById(R.id.steam_group2);  //食材
        group3 = findViewById(R.id.steam_group3);  //步骤
        tvQrcode = findViewById(R.id.tv_qrcode);
        tvMaterial = findViewById(R.id.tv_material);
        ivQrcode = findViewById(R.id.iv_qrcode);// 二维码
        tvStep = findViewById(R.id.tv_step);
        rvStep = findViewById(R.id.rv_step);
        ivRecipe = findViewById(R.id.iv_recipe_img);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvTime = findViewById(R.id.tv_time);
        ivTime = findViewById(R.id.iv_time);
        //食材
        rvMaterial.setLayoutManager(new GridLayoutManager(this, 2));
        rvMaterial.addItemDecoration(new GridSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_126)));
        rvMaterialAdapter = new RvMaterialAdapter();
        rvMaterial.setAdapter(rvMaterialAdapter);
        //步骤
        rvStep.setLayoutManager(new LinearLayoutManager(this));
        rvStepAdapter = new RvStepAdapter();
        rvStep.setAdapter(rvStepAdapter);
        setOnClickListener(R.id.ll_left_center, R.id.tv_qrcode, R.id.tv_material, R.id.tv_step, R.id.btn_start);
    }

    @Override
    protected void initData() {

    }
}