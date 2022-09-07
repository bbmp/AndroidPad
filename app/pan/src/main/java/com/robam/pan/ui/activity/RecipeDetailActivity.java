package com.robam.pan.ui.activity;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.QrUtils;
import com.robam.pan.R;
import com.robam.pan.bean.Material;
import com.robam.pan.base.PanBaseActivity;
import com.robam.pan.bean.PanRecipeDetail;
import com.robam.pan.constant.PanConstant;
import com.robam.pan.http.CloudHelper;
import com.robam.pan.response.GetRecipeDetailRes;
import com.robam.pan.ui.adapter.RvMaterialAdapter;

import java.util.ArrayList;
import java.util.List;


public class RecipeDetailActivity extends PanBaseActivity {
    private RecyclerView rvMaterial;
    private Group group1, group2;
    private TextView tvQrcode, tvMaterial;
    //菜谱图片
    private ImageView ivRecipe;
    //菜谱名字
    private TextView tvRecipeName;
    //时间
    private ImageView ivTime;
    //时长
    private TextView tvTime;
    private RvMaterialAdapter rvMaterialAdapter;
    private RequestOptions maskOption = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.pan_main_item_bg) //预加载图片
            .error(R.drawable.pan_main_item_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级
            .skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .override((int) (370), (int) (370));
    //菜谱id
    private long recipeId;
    //二维码
    private ImageView ivQrcode;
    //二维码url
    private String url = "https://h5.myroki.com/dist/index.html#/recipeDetail?cookbookId=" + "%d&entranceCode=code1&isFromWx=true&userId=%d";

    @Override
    protected int getLayoutId() {
        return R.layout.pan_activity_layout_recipe_detail;
    }

    @Override
    protected void initView() {
        showLeft();
        showLeftCenter();
        showCenter();

        if (null != getIntent())
            recipeId = getIntent().getLongExtra(PanConstant.EXTRA_RECIPE_ID, 0);
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.pan_group1);
        group2 = findViewById(R.id.pan_group2);
        tvRecipeName = findViewById(R.id.tv_recipe_name);
        tvTime = findViewById(R.id.tv_time);
        ivTime = findViewById(R.id.iv_time);
        tvQrcode = findViewById(R.id.tv_qrcode);
        tvMaterial = findViewById(R.id.tv_material);
        ivQrcode = findViewById(R.id.iv_qrcode);// 二维码
        ivRecipe = findViewById(R.id.iv_recipe_img);
        rvMaterial.setLayoutManager(new GridLayoutManager(this, 2));
        rvMaterial.addItemDecoration(new GridSpaceItemDecoration((int) getResources().getDimension(com.robam.common.R.dimen.dp_126)));
        rvMaterialAdapter = new RvMaterialAdapter();
        rvMaterial.setAdapter(rvMaterialAdapter);
        setOnClickListener(R.id.ll_left_center, R.id.tv_qrcode, R.id.tv_material);
    }

    @Override
    protected void initData() {
        //初始值
        tvQrcode.setSelected(true);
        getRecipeDetail();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_material) { //食材
            if (!tvMaterial.isSelected()){
                tvMaterial.setSelected(true);
                tvQrcode.setSelected(false);
                group1.setVisibility(View.GONE);
                group2.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.tv_qrcode) { //二维码
            if (!tvQrcode.isSelected()) {
                tvQrcode.setSelected(true);
                tvMaterial.setSelected(false);
                group1.setVisibility(View.VISIBLE);
                group2.setVisibility(View.GONE);
            }
        } else if (id == R.id.ll_left_center) { //回首页
            startActivity(MainActivity.class);
            finish();
        }
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
    private void setData(PanRecipeDetail panRecipeDetail) {
        //二维码
        UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
        String qrUrl = String.format(url, panRecipeDetail.id, (userInfo != null) ? userInfo.id:0);
        Bitmap imgBit = QrUtils.create2DCode(qrUrl, (int)getResources().getDimension(com.robam.common.R.dimen.dp_156),
                (int)getResources().getDimension(com.robam.common.R.dimen.dp_156), Color.WHITE);
        if (null != imgBit)
            ivQrcode.setImageBitmap(imgBit);
        //时间
        ivTime.setImageResource(R.drawable.pan_time);
        //图片
        ImageUtils.loadImage(this, panRecipeDetail.imgSmall, maskOption, ivRecipe);
        //名字
        tvRecipeName.setText(panRecipeDetail.name);
        //时长
        tvTime.setText("时间   " + panRecipeDetail.needTime / 60 + "min");
        //食材
        List<Material> materials = new ArrayList<>();
        if (null != panRecipeDetail.materials && null != panRecipeDetail.materials.main)
            materials.addAll(panRecipeDetail.materials.main);

        if (null != panRecipeDetail.materials && null != panRecipeDetail.materials.accessory)
            materials.addAll(panRecipeDetail.materials.accessory);
        rvMaterialAdapter.setList(materials);

    }
}