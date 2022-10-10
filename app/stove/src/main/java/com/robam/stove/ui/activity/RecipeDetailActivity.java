package com.robam.stove.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.helper.GridSpaceItemDecoration;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.QrUtils;
import com.robam.stove.R;
import com.robam.stove.base.StoveBaseActivity;
import com.robam.stove.bean.Material;
import com.robam.stove.bean.RecipeStep;
import com.robam.stove.bean.Stove;
import com.robam.stove.bean.StoveRecipeDetail;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.factory.StoveDialogFactory;
import com.robam.stove.http.CloudHelper;
import com.robam.stove.response.GetRecipeDetailRes;
import com.robam.stove.ui.adapter.RvMaterialAdapter;
import com.robam.stove.ui.adapter.RvStepAdapter;
import com.robam.stove.ui.dialog.SelectStoveDialog;

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
    private StoveRecipeDetail stoveRecipeDetail;
    private long recipeId;
    //二维码url
    private String url = "https://h5.myroki.com/dist/index.html#/recipeDetail?cookbookId=" + "%d&entranceCode=code1&isFromWx=true&userId=%d";
    private SelectStoveDialog selectStoveDialog;

    private IDialog openDialog;
    //炉头id
    private int stoveId;

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
        showLeftCenter();
        showCenter();
        showRightCenter();

        if (null != getIntent())
            recipeId = getIntent().getLongExtra(StoveConstant.EXTRA_RECIPE_ID, 0);
        rvMaterial = findViewById(R.id.rv_material);
        group1 = findViewById(R.id.stove_group1);  //二维码
        group2 = findViewById(R.id.stove_group2);  //食材
        group3 = findViewById(R.id.stove_group3);  //步骤
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

        //监听开火状态
        AccountInfo.getInstance().getGuid().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                for (Device device: AccountInfo.getInstance().deviceList) {
                    if (device.guid.equals(s) && device.guid.equals(HomeStove.getInstance().guid) && device instanceof Stove) { //当前灶
                        Stove stove = (Stove) device;
                        //开火提示状态
                        if (null != openDialog && openDialog.isShow()) {

                            if (stoveId == IPublicStoveApi.STOVE_LEFT && stove.leftStatus == StoveConstant.WORK_WORKING) { //左灶已点火
                                openDialog.dismiss();
                                //开始工作
                                Intent intent = new Intent();
                                intent.setClass(RecipeDetailActivity.this, RecipeCookActivity.class);
                                if (null != stoveRecipeDetail)
                                    intent.putExtra(StoveConstant.EXTRA_RECIPE_DETAIL, stoveRecipeDetail);
                                intent.putExtra(StoveConstant.stoveId, stoveId);
                                startActivity(intent);
                            } else if (stoveId == IPublicStoveApi.STOVE_RIGHT && stove.rightStatus == StoveConstant.WORK_WORKING) { //右灶已点火
                                openDialog.dismiss();
                                //开始工作
                                Intent intent = new Intent();
                                intent.setClass(RecipeDetailActivity.this, RecipeCookActivity.class);
                                if (null != stoveRecipeDetail)
                                    intent.putExtra(StoveConstant.EXTRA_RECIPE_DETAIL, stoveRecipeDetail);
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                }
            }
        });
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
                    stoveRecipeDetail = getRecipeDetailRes.cookbook;
                    setData(getRecipeDetailRes.cookbook);
            }

            @Override
            public void onFaild(String err) {

            }
        });
    }
    //获取到的数据
    private void setData(StoveRecipeDetail stoveRecipeDetail) {
        //二维码
        UserInfo userInfo = AccountInfo.getInstance().getUser().getValue();
        String qrUrl = String.format(url, stoveRecipeDetail.id, (userInfo != null) ? userInfo.id:0);
        Bitmap imgBit = QrUtils.create2DCode(qrUrl, (int)getResources().getDimension(com.robam.common.R.dimen.dp_156),
                (int)getResources().getDimension(com.robam.common.R.dimen.dp_156), Color.WHITE);
        if (null != imgBit)
            ivQrcode.setImageBitmap(imgBit);
        //时间
        ivTime.setImageResource(R.drawable.stove_time);
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
        ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
        if (null != stoveRecipeDetail.steps) {
            recipeSteps.addAll(stoveRecipeDetail.steps);
        }
        rvStepAdapter.setList(recipeSteps);
    }

    //炉头选择
    private void selectStove() {
        //炉头选择提示
        if (null == selectStoveDialog) {
            selectStoveDialog = new SelectStoveDialog(this);
            selectStoveDialog.setCancelable(false);

            selectStoveDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.view_left)
                        openFire(IPublicStoveApi.STOVE_LEFT); //左灶
                    else if (id == R.id.view_right)
                        openFire(IPublicStoveApi.STOVE_RIGHT); //右灶
                }
            }, R.id.select_stove_dialog, R.id.view_left, R.id.view_right);
        }
        //检查炉头状态
        selectStoveDialog.checkStoveStatus();
        selectStoveDialog.show();
    }

    //点火提示
    private void openFire(int stove) {

        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && device.guid.equals(HomeStove.getInstance().guid)) {
                if (null == openDialog) {
                    openDialog = StoveDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_OPEN_FIRE);
                    openDialog.setCancelable(false);
                }

                if (stove == IPublicStoveApi.STOVE_LEFT) {
                    openDialog.setContentText(R.string.stove_open_left_hint);
                    //进入工作状态
                    //选择左灶
                    stoveId = IPublicStoveApi.STOVE_LEFT;

                } else {
                    openDialog.setContentText(R.string.stove_open_right_hint);
                    //选择右灶
                    stoveId = IPublicStoveApi.STOVE_RIGHT;

                }
                openDialog.show();
                break;
            }
        }
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
            //选择炉头
            selectStove();

        } else if (id == R.id.ll_left_center) { //回主页
            startActivity(MainActivity.class);
        } else if (id == R.id.ll_left) {  //返回
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != selectStoveDialog && selectStoveDialog.isShow())
            selectStoveDialog.dismiss();
        if (null != openDialog && openDialog.isShow())
            openDialog.dismiss();
    }
}