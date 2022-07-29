package com.robam.roki.pages;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.HeadPage;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;
import com.robam.roki.R;
import com.robam.roki.ui.activity.RWebActivity;
import com.robam.roki.ui.activity.helper.BannerIndicator;
import com.robam.roki.bean.CookingKnowledge;
import com.robam.roki.http.CloudHelper;
import com.robam.roki.response.CookingKnowledgeRes;
import com.robam.roki.utils.PageArgumentKey;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class HomeDevicePage extends HeadPage {
    private List<CookingKnowledge> mCookingKnowledges = new ArrayList<>();
    private BannerImageAdapter bannerImageAdapter;
    private Banner bannerKichen;
    private LinearLayout llEmpty;

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_homedevice;
    }

    @Override
    protected void initView() {
        bannerKichen = findViewById(R.id.br_kichen_home);
        llEmpty = findViewById(R.id.ll_empty);

        findViewById(R.id.iv_kitchen_knowledge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(PageArgumentKey.Url, "https://h5.myroki.com/#/kitchenKnowledge");
                intent.setClass(getContext(), RWebActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        CloudHelper.getCookingKnowledge(this, "cookingSkill", 1, null, 0, 3, CookingKnowledgeRes.class,
                new RetrofitCallback<CookingKnowledgeRes>() {
                    @Override
                    public void onSuccess(CookingKnowledgeRes cookingKnowledgeRes) {
                        LogUtils.e("cookingKnowledgeRes");
                        if (cookingKnowledgeRes != null) {
                            mCookingKnowledges.clear();
                            mCookingKnowledges.addAll(cookingKnowledgeRes.getCookingKnowledges());
                            refreshKitchenKnowledge();
//                    rlKitchenknowledge.setVisibility(View.VISIBLE);
                            llEmpty.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("cookingKnowledgeRes" + err);
                    }
                });
    }

    private void refreshKitchenKnowledge() {
        if (null == bannerImageAdapter) {
            bannerImageAdapter = new BannerImageAdapter<CookingKnowledge>(mCookingKnowledges) {

                @Override
                public void onBindView(BannerImageHolder bannerImageHolder, CookingKnowledge cookingKnowledge, int position, int size) {
                    ImageUtils.loadImage(getContext(),
                            cookingKnowledge.pictureCoverUrl,
                            R.mipmap.roki_banner_default,
                            R.mipmap.roki_banner_default,
                            350*2, 131*2,
                            bannerImageHolder.imageView);
                }
            };
            bannerImageAdapter.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(Object o, int i) {
                    CookingKnowledge tag = mCookingKnowledges.get(i);
                    startPage(tag);
                }
            });
            bannerKichen.setAdapter(bannerImageAdapter)
                    .setIndicator(new BannerIndicator(getContext()));
        } else {
            bannerImageAdapter.notifyDataSetChanged();
        }
    }

    private void startPage(CookingKnowledge tag) {
//        Bundle bd = new Bundle();
//        bd.putLong(PageArgumentKey.Id, tag.id);
//        bd.putInt(PageArgumentKey.contentType, tag.contentType);
//        bd.putString(PageArgumentKey.Url, tag.videoId);
//        UIService.getInstance().postPage(PageKey.KitchenKnowledgeArticle, bd);
    }
}
