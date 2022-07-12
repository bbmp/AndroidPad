package com.robam.roki.pages;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.ui.HeadPage;
import com.robam.roki.R;
import com.robam.roki.ui.UIService;

public class HomeMinePage extends HeadPage {
    /**
     * 头像
     */
    private ImageView ivPhoto;
    /**
     * 用户名
     */
    private TextView tvUserName;
    /**
     * 手机号
     */
    private TextView tvUserPhone;
    /**
     * 我的收藏
     */
    private LinearLayout llCollection;
    /**
     * 我的作品
     */
    private LinearLayout llWork;
    /**
     * 烹饪曲线
     */
    private LinearLayout stbCookLine;

    /**
     * 厨电管理
     */
    private TextView stbDevice;
    /**
     * 售后服务
     */
    private TextView stbSaleService;
    /**
     * 关于
     */
    private TextView stbAbout;
    /**
     * 设置
     */
    private TextView stbSetting;

    //产品手册
    private TextView stbCpsc;
    //服务预约
    private TextView stbFwyy;
    //服务商城
    private TextView stbFwsc;
    private TextView tvCollectionNum;
    private TextView tvWorkNum;
    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_homemine;
    }

    @Override
    protected void initView() {
        setStateBarColor(R.color.roki_white);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        llCollection = findViewById(R.id.ll_collection);
        llWork = findViewById(R.id.ll_work);
        stbCookLine = findViewById(R.id.ll_curve);
        stbDevice = findViewById(R.id.tv_device_manage);
        stbSaleService = findViewById(R.id.tv_sale_service);
        stbAbout =  findViewById(R.id.tv_about);
        stbSetting =  findViewById(R.id.tv_set);
        stbCpsc = findViewById(R.id.tv_product);
        stbFwyy = findViewById(R.id.tv_service);
        stbFwsc = findViewById(R.id.tv_sercie_shop);
        tvCollectionNum = findViewById(R.id.tv_collection_num);
        tvWorkNum = findViewById(R.id.tv_work_num);
        setOnClickListener(
                llCollection, llWork, stbCookLine, stbDevice, stbSaleService, stbAbout, stbSetting
                , ivPhoto, tvUserName, tvUserPhone, stbCpsc, stbFwsc, stbFwyy);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        if (view.equals(stbSaleService)) {
            UIService.postPage(view, R.id.action_saleservice);
        } else if (view.equals(stbAbout)) {
//            UIService.getInstance().postPage(PageKey.MineAboutPage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
