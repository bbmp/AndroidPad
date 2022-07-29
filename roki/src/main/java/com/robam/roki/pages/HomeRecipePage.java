package com.robam.roki.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.HeadPage;
import com.robam.common.ui.UIService;
import com.robam.common.utils.ImageUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.NetworkUtils;
import com.robam.common.utils.TimeUtils;
import com.robam.roki.R;
import com.robam.roki.adapter.RecipeTopicAdapter;
import com.robam.roki.adapter.RvRecipeThemeAdapter;
import com.robam.roki.adapter.SelectedTopicsAdapter;
import com.robam.roki.bean.CookBanner;
import com.robam.roki.bean.TopicMultipleItem;
import com.robam.roki.constant.IDeviceType;
import com.robam.roki.response.CookBannerRes;
import com.robam.roki.bean.Recipe;
import com.robam.roki.bean.RecipeTheme;
import com.robam.roki.bean.ThemeRecipeMultipleItem;
import com.robam.roki.http.CloudHelper;
import com.robam.roki.http.CookHelper;
import com.robam.roki.response.PersonalizedRecipeRes;
import com.robam.roki.response.RecipeThemeRes;
import com.robam.roki.response.WeekTopsRes;
import com.robam.roki.ui.activity.helper.BannerIndicator;
import com.robam.roki.ui.view.CustomLoadMoreView;
import com.robam.roki.ui.view.HorizontalItemDecoration;
import com.robam.roki.ui.view.HorizontalItemRecipeDecoration;
import com.robam.roki.utils.PageArgumentKey;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class HomeRecipePage extends HeadPage {

    private EditText etReipeSearch;
    private ImageView ivRecipeVoice;
    private View rl_recipe_search;
    /**
     * 回到顶部
     */
    private View ivToTop;

    /**
     * 下拉刷新
     */
    private SwipeRefreshLayout srlHome;
    /**
     *
     * 灶具
     */
    private View stove;
    private View combiSteamOven;
    private View oven;
    private View steamOven;
    private View more;
    /**
     * 更多
     */
    private View tvThemeMore;
    private View tvWeekTopMore;
    private View tvmore3 ;
    /**
     * 精选专题
     */
    private RecyclerView rvSelectedTopics;
    /**
     * 美食top榜单
     */
    private RecyclerView rvWeekTopics;
    /**
     * 美食主页recycleView
     */
    private RecyclerView rvHomeRecipe ;
    /**
     * 猜你喜欢adapter
     */
    private RvRecipeThemeAdapter rvRecipeThemeAdapter;
    /**
     * 精选专题Adapter
     */
    private SelectedTopicsAdapter mSelectedTopicsAdapter;
    /**
     * Top菜谱adapter
     */
    private RecipeTopicAdapter mRecipeTopicAdapter;
    /**
     * 菜谱和专题数据
     */
    private List<RecipeTheme> recipeThemeList = new ArrayList<>();
    //猜你喜欢
    private RelativeLayout rlSelectTopics;

    private LinearLayout llEmpty;
    /**
     * 菜谱数据 用于排除
     */
    private ArrayList<Long> recipeIds = new ArrayList<>();
    /**
     * 页数
     */
    private int pageNo = 0;
    //美食频道banner
    private Banner banner;
    private List<CookBanner> bannerList = new ArrayList<>();
    private BannerImageAdapter bannerImageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.roki_page_layout_homerecipe;
    }

    @Override
    protected void initView() {
        rvHomeRecipe = findViewById(R.id.rv_home_recipe);
        srlHome = findViewById(R.id.srl_home);
        etReipeSearch = findViewById(R.id.et_recipe_search);
        rl_recipe_search = findViewById(R.id.rl_recipe_search);
        ivRecipeVoice = findViewById(R.id.iv_recipe_voice);
        ivToTop = findViewById(R.id.iv_to_top);
        llEmpty = findViewById(R.id.ll_empty);
        rvHomeRecipe.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        rvHomeRecipe.addItemDecoration(new HorizontalItemRecipeDecoration(12 , getContext()));
        rvRecipeThemeAdapter = new RvRecipeThemeAdapter(this);
        rvHomeRecipe.setAdapter(rvRecipeThemeAdapter);
        rvRecipeThemeAdapter.getLoadMoreModule().setLoadMoreView(new CustomLoadMoreView());
        rvRecipeThemeAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadRecipeData();
            }
        });
        View topView = LayoutInflater.from(getContext()).inflate( R.layout.roki_item_home_recipe_top, null);
        rvRecipeThemeAdapter.addHeaderView(topView);
        rvRecipeThemeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ThemeRecipeMultipleItem themeRecipeMultipleItem = (ThemeRecipeMultipleItem) adapter.getItem(position);
                if (themeRecipeMultipleItem.getItemType() == ThemeRecipeMultipleItem.IMG_RECIPE_MSG_TEXT) {
                    //菜谱详情
//                    RecipeDetailPage.show(themeRecipeMultipleItem.getRecipe().id, themeRecipeMultipleItem.getRecipe().sourceType);
                } else if (themeRecipeMultipleItem.getItemType() == ThemeRecipeMultipleItem.IMG_THEME_RECIPE_MSG_TEXT) {
                    //主题详情
                }
            }
        });
        banner = (Banner)topView.findViewById(R.id.br_home);
        rlSelectTopics = topView.findViewById(R.id.rl_select_theme_list);

        stove = topView.findViewById(R.id.home_recipe_ll_stove);
        combiSteamOven = topView.findViewById(R.id.home_recipe_ll_combi_steam_oven);
        oven = topView.findViewById(R.id.home_recipe_ll_oven);
        steamOven = topView.findViewById(R.id.home_recipe_ll_steam_oven);
        more = topView.findViewById(R.id.home_recipe_more);
        tvmore3 = topView.findViewById(R.id.tv_more_3);
        tvThemeMore = topView.findViewById(R.id.tv_theme_more);
        tvWeekTopMore = topView.findViewById(R.id.tv_week_top_more);

        rvSelectedTopics = topView.findViewById(R.id.rv_selected_topics);
        rvSelectedTopics.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvSelectedTopics.addItemDecoration(new HorizontalItemDecoration(16 , getContext()));
        rvWeekTopics = topView.findViewById(R.id.rv_week_topics);
        rvWeekTopics.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvWeekTopics.addItemDecoration(new HorizontalItemDecoration(16 , getContext()));
        setOnClickListener(rl_recipe_search,etReipeSearch , ivRecipeVoice ,stove , combiSteamOven , oven , steamOven , more ,tvThemeMore ,tvWeekTopMore ,ivToTop , tvmore3);

        findViewById(R.id.home_recipe_img).setOnClickListener(view -> {
//            if (Plat.accountService.isLogon()) {
//                getContext().startActivity(new Intent(getContext(), MessageActivity.class));
//            }else{
//                CmccLoginHelper.getInstance().toLogin();
//            }
        });

        srlHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNo = 0 ;
                rvRecipeThemeAdapter.setNewInstance(new ArrayList<ThemeRecipeMultipleItem>());
                getBannerRes();
                getByTagOtherThemes();
                getWeekRecipeTops();
                getThemeHttpData();
            }
        });
    }

    @Override
    protected void initData() {

        if (!NetworkUtils.isConnect(getContext())){
            llEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkUtils.isConnect(getContext())){
                        // 警告对话框
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                            }
                        } , 1500);
                    }else {
//                        onRefresh();
                        getBannerRes();
//                        getByTagOtherThemes();
//                        getWeekRecipeTops();
//                        getThemeHttpData();
                        rvHomeRecipe.setVisibility(View.VISIBLE);
                        llEmpty.setVisibility(View.GONE);
                    }

                }
            });
            llEmpty.setVisibility(View.VISIBLE);
            rvHomeRecipe.setVisibility(View.GONE);
//            DialogHelper.notNetDialog(getContext());
        }else {
            getBannerRes();
            getByTagOtherThemes();
            getWeekRecipeTops();
            getThemeHttpData();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == stove){
            recipeCategoryClick(view, IDeviceType.RRQZ);
        }else if (view == combiSteamOven){
            recipeCategoryClick(view, IDeviceType.RZKY);
        }else if (view == oven){
            recipeCategoryClick(view, IDeviceType.RDKX);
        }else if (view == steamOven) {
            recipeCategoryClick(view, IDeviceType.RZQL);
        }
    }

    /**
     * 设备菜谱点击调用(可提供外部调用)
     */
    public void recipeCategoryClick(View view, String category) {
        Bundle bundle = new Bundle();
        bundle.putString(PageArgumentKey.RecipeId, category);
        UIService.postPage(view, R.id.action_recipecategory, bundle);
    }

    /**
     * 加载菜谱数据
     */
    private void loadRecipeData() {
        if (pageNo >= 10){
            rvRecipeThemeAdapter.getLoadMoreModule().loadMoreEnd();
            return;
        }
        CloudHelper.getbyTagOtherCooks(this, null , true, pageNo, 10, -1 , recipeIds, PersonalizedRecipeRes.class, new RetrofitCallback<PersonalizedRecipeRes>() {
            @Override
            public void onSuccess(PersonalizedRecipeRes personalizedRecipeRes) {
                List<Recipe> recipes = personalizedRecipeRes.cookbooks;
                if (recipes == null || recipes.size() == 0 ) {
                    LogUtils.i(" recipes isEmpty!");
                    rvRecipeThemeAdapter.getLoadMoreModule().loadMoreEnd();
                }else {
                    setIds(recipes);
                    List<ThemeRecipeMultipleItem> themeRecipeMultipleItemList = new ArrayList<>();
                    for (Recipe recipe : recipes) {
                        themeRecipeMultipleItemList.add(new ThemeRecipeMultipleItem(ThemeRecipeMultipleItem.IMG_RECIPE_MSG_TEXT, recipe));
                    }
                    settingData(themeRecipeMultipleItemList);
                }
                srlHome.setRefreshing(false);
            }

            @Override
            public void onFaild(String err) {
                rvRecipeThemeAdapter.getLoadMoreModule().loadMoreFail();
                srlHome.setRefreshing(false);
            }
        });
    }
    //用于请求时排除
    private void setIds(List<Recipe> recipes){

        for (Recipe recipe : recipes) {
            recipeIds.add(recipe.getId());
        }

    }

    /**
     * 设置菜谱数据
     * @param themeRecipeMultipleItemList
     */
    private void settingData(List<ThemeRecipeMultipleItem> themeRecipeMultipleItemList){
        if (recipeThemeList == null || recipeThemeList.size() == 0) {
            rvRecipeThemeAdapter.addData(themeRecipeMultipleItemList);
        }else {

            int i1 = (int)((9) );
            if (i1 < themeRecipeMultipleItemList.size()){
                themeRecipeMultipleItemList.add(i1 ,new ThemeRecipeMultipleItem(ThemeRecipeMultipleItem.IMG_THEME_RECIPE_MSG_TEXT, recipeThemeList.get(0)));
                recipeThemeList.remove(0);
            }

            rvRecipeThemeAdapter.addData(themeRecipeMultipleItemList);
        }
        pageNo++ ;
        rvRecipeThemeAdapter.getLoadMoreModule().loadMoreComplete();
    }
    /**
     * 获取banner资源图片
     */
    private void getBannerRes(){
        CookHelper.getBanner(CookBannerRes.class, new RetrofitCallback<CookBannerRes>() {
            @Override
            public void onSuccess(CookBannerRes bannerBean) {
                if (bannerBean != null && bannerBean.data != null && bannerBean.data.size() != 0) {
                    bannerList.clear();
                    bannerList.addAll(bannerBean.data);
                    refreshBanner();

                }else {
                    banner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("banner err:" + err);
            }

        });
    }

    private void refreshBanner() {
        if (null == bannerImageAdapter) {
            bannerImageAdapter = new BannerImageAdapter<CookBanner>(bannerList) {

                @Override
                public void onBindView(BannerImageHolder holder, CookBanner data, int position, int size) {
                    ImageUtils.loadImage(getContext(),
                            data.imageUrl,
                            R.mipmap.roki_banner_default,
                            R.mipmap.roki_banner_default,
                            350*2, 131*2,
                            holder.imageView);
                }
            };
            bannerImageAdapter.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(Object o, int i) {
                    CookBanner item = bannerList.get(i);
                    int linkAction = item.linkAction;
                    switch (linkAction){//1小游戏  2精选专题  3动态菜谱  4静态菜谱  5H5页面 6小程序  7 抽奖H5
                        case 1:
//                            UIService.getInstance().postPage(PageKey.RandomRecipe);
                            break;
                        case 2:
//                            try {
//                                if (item.resource != null){
//                                    Bundle bd = new Bundle();
//                                    bd.putLong(PageArgumentKey.Id, Long.parseLong(item.resource));
//                                    bd.putInt(PageArgumentKey.ThemeType, 3);
//                                    UIService.getInstance().postPage(PageKey.SelectThemeDetailPage , bd);
//                                }
//                            }catch (Exception e){
//                                e.getMessage();
//                                ToastUtils.showShort(getContext(), "专题ID错误");
//                            }
                            break;
                        case 3:
                        case 4:
//                            try {
//                                if (item.resource != null){
//                                    RecipeDetailPage.show( Long.parseLong(item.resource), 1);
//                                }
//                            }catch (Exception e){
//                                ToastUtils.showShort(getContext(), "菜谱ID错误");
//                            }
                            break;
                        case 5:
//                            if (item.resource != null){
//                                if(item.resource.equals(kitchenKnowledge_url)){
//                                    RWebActivity.start(activity , item.resource);
//                                }else {
//                                    Bundle bd = new Bundle();
//                                    bd.putString(PageArgumentKey.title, item.title);
//                                    bd.putString(PageArgumentKey.Url, item.resource);
//                                    bd.putString("Share_Img",item.forwardImageUrl);
//                                    bd.putString(PageArgumentKey.H5Key, "common_act");
//                                    bd.putString("shareContText", item.secondTitle);
//                                    UIService.getInstance().postPage(PageKey.ActivityWebViewPage,bd);
//                                }
//
//                            }
                            //跳转小程序
                        case 6:
                            if (item.resource != null){

                            }
                            break;
                        //awe抽奖活动
                        case 7:
//                            if (item.resource != null){
//                                Bundle bd = new Bundle();
//                                bd.putString(PageArgumentKey.title, item.title);
//                                bd.putString(PageArgumentKey.Url, item.resource);
//                                bd.putString("Share_Img",item.forwardImageUrl);
//                                bd.putString(PageArgumentKey.H5Key, "special_act");
//                                bd.putString("shareContText", item.secondTitle);
//                                UIService.getInstance().postPage(PageKey.ActivityWebViewPage,bd);
//                            }

                            break;
                        default:
                            break;
                    }
                }
            });
            banner.setAdapter(bannerImageAdapter)
                    .setIndicator(new BannerIndicator(getContext()));
        } else {
            bannerImageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取某个标签或推荐或周上新的主题
     */
    public void getByTagOtherThemes() {
        CloudHelper.getByTagOtherThemes(this, null, false, pageNo, 10, -1, RecipeThemeRes.class,
                new RetrofitCallback<RecipeThemeRes>() {
                    @Override
                    public void onSuccess(RecipeThemeRes recipeThemeRes) {
                        if (null != recipeThemeRes) {
                            List<RecipeTheme> recipeThemes = recipeThemeRes.items;
                            recipeThemeList.clear();
                            recipeIds.clear();
                            recipeThemeList.addAll(recipeThemes) ;
                            loadRecipeData();
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        rvRecipeThemeAdapter.getLoadMoreModule().loadMoreFail();
                        srlHome.setRefreshing(false);
                    }
        });
    }

    /**
     * 获取上周榜单菜谱数据
     */
    private void getWeekRecipeTops() {
        String weekTime = TimeUtils.getlastWeekTime();
        LogUtils.i("weekTime:" + weekTime);
        CloudHelper.getWeekTops(this, weekTime, 0, 5, WeekTopsRes.class, new RetrofitCallback<WeekTopsRes>() {
            @Override
            public void onSuccess(WeekTopsRes weekTopsRes) {
                if (null != weekTopsRes) {
                    List<Recipe> recipes = weekTopsRes.payload;
                    LogUtils.i( "getWeekTops onSuccess");

                    for (Recipe recipe : recipes) {
                        LogUtils.i( "imgLarge:" + recipe.imgLarge);
                        recipe.setItemType(Recipe.IMG);
                    }
                    recipes.add(new Recipe(Recipe.TEXT));
                    mRecipeTopicAdapter = new RecipeTopicAdapter(recipes);
                    rvWeekTopics.setAdapter(mRecipeTopicAdapter);
                    mRecipeTopicAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                            LogUtils.i("recipes.size:" + recipes.size() + " position:" + position);
//                            if (recipes.size() == position + 1) {
//                                UIService.getInstance().postPage(PageKey.TopWeekPage);
//                            } else {
//                                RecipeDetailPage.show(recipes.get(position).id, recipes.get(position).sourceType);
//                            }
                        }
                    });
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.i("onFailure " + err);

            }

        });
    }
    /**
     * 精选专题列表
     */
    private void getThemeHttpData() {
        CloudHelper.getThemeRecipeList(this, RecipeThemeRes.class, new RetrofitCallback<RecipeThemeRes>() {
            @Override
            public void onSuccess(RecipeThemeRes recipeThemeRes) {
                if (null != recipeThemeRes) {
                    List<RecipeTheme> recipeThemes = recipeThemeRes.items;
//                    if (Plat.accountService.isLogon()) {
//                        getThemeCollection(recipeThemes);
//                        return;
//                    }
                    //设置精选专题
                    setTheme(recipeThemes);
                }
            }

            @Override
            public void onFaild(String err) {

            }

        });
    }
    /**
     * 设置精选专题
     * @param recipeThemes
     */
    private void  setTheme(List<RecipeTheme> recipeThemes){

        rlSelectTopics.setVisibility(View.VISIBLE);
        List<TopicMultipleItem> topicMultipleItemList = new ArrayList<>() ;
        for (RecipeTheme recipeTheme : recipeThemes) {
            if (topicMultipleItemList.size() < 5) {
                topicMultipleItemList.add(new TopicMultipleItem(TopicMultipleItem.IMG, TopicMultipleItem.IMG_SPAN_SIZE, recipeTheme.imageUrl));
            } else {
                break;
            }
        }

        topicMultipleItemList.add(new TopicMultipleItem(TopicMultipleItem.TEXT, TopicMultipleItem.IMG_SPAN_SIZE, "加载更多"));
        mSelectedTopicsAdapter = new SelectedTopicsAdapter(topicMultipleItemList);
        rvSelectedTopics.setAdapter(mSelectedTopicsAdapter);
        mSelectedTopicsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
//                if (position == topicMultipleItemList.size() - 1) {
//                    UIService.getInstance().postPage(PageKey.ThemeRecipeListPage);
//                } else {
//                    SelectThemeDetailPage.show(recipeThemes.get(position), SelectThemeDetailPage.TYPE_THEME_RECIPE);
//                }
            }
        });
    }
}
