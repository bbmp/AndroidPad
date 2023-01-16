package com.robam.steamoven.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.UserInfo;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.base.SteamBaseActivity;
import com.robam.steamoven.bean.DeviceConfigurationFunctions;
import com.robam.steamoven.bean.OtherFunc;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.bean.SubViewModelMap;
import com.robam.steamoven.bean.SubViewModelMapSubView;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.http.CloudHelper;
import com.robam.steamoven.manager.RecipeManager;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.request.GetCurveDetailReq;
import com.robam.steamoven.response.GetDeviceParamsRes;
import com.robam.steamoven.ui.pages.RecipeClassifyPage;
import com.robam.steamoven.utils.SkipUtil;
import com.robam.steamoven.utils.SteamDataUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//一体机菜谱
public class RecipeActivity extends SteamBaseActivity {
    private TabLayout tabLayout;
    private ViewPager noScrollViewPager;
    private EditText etSearch;

    private View searchPromptView;
    private TextView searchPromptBtn;
    //分类
    private List<String> classifyList = new ArrayList<>();
    //弱引用，防止内存泄漏
    private List<WeakReference<Fragment>> fragments = new ArrayList<>();
    //菜谱分类
    private List<DeviceConfigurationFunctions> deviceConfigurationFunctionsList = new ArrayList<>();

    private List<WeakReference<Fragment>> searchFragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.steam_activity_layout_recipe;
    }

    @Override
    protected void initView() {
        showLeft();
        showCenter();

        etSearch = findViewById(R.id.tv_search);
        tabLayout = findViewById(R.id.tablayout);
        noScrollViewPager = findViewById(R.id.pager);
        tabLayout.setSelectedTabIndicatorHeight(0);
        searchPromptView = findViewById(R.id.recipe_search_prompt);
        searchPromptBtn = findViewById(R.id.recipe_search_prompt_btn);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                noScrollViewPager.setCurrentItem(tab.getId(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                String text = etSearch.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    //处理搜索
                    searchResult(text);
                } else {
                    showRecipeCategory();
                }
                return true;
            }
            return false;
        });

        setOnClickListener(R.id.recipe_search_prompt_btn);
        AccountInfo.getInstance().getGuid().observe(this, s -> {
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(s) && device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                    SteamOven steamOven = (SteamOven) device;
                    if(!SteamCommandHelper.getInstance().isSafe()){
                        return;
                    }
                    if(toWaringPage(steamOven)){
                        return;
                    }
                    if(toOffLinePage(steamOven)){
                        return;
                    }
                    switch (steamOven.powerState){
                        case SteamStateConstant.POWER_STATE_AWAIT:
                        case SteamStateConstant.POWER_STATE_ON:
                        case SteamStateConstant.POWER_STATE_TROUBLE:
                            SkipUtil.toWorkPage(steamOven,RecipeActivity.this);
                            break;
                        case SteamStateConstant.POWER_STATE_OFF:
                            break;
                    }


                }
            }
        });
    }

    private void performSearch() {
        etSearch.clearFocus();
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    @Override
    protected void initData() {
        String recipeData = RecipeManager.getInstance().getRecipeData();
        if(StringUtils.isNotBlank(recipeData)){
            showRecipe(recipeData);
        }else{
            loadRecipe();
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_left) {
            if(tabLayout.getVisibility() != View.VISIBLE){
                showRecipeCategory();
                return;
            }
            finish();
        }else if(id == R.id.recipe_search_prompt_btn){
            etSearch.setText("");
            showRecipeCategory();
            setSearchState(false,false);
        }
    }

    private void loadRecipe() {
        SteamOven steamOven = getSteamOven();
        if(steamOven != null){//获取设备数据，主要用于展示菜谱
            UserInfo info = AccountInfo.getInstance().getUser().getValue();
            String guidType = DeviceUtils.getDeviceTypeId(steamOven.guid);
            CloudHelper.getDeviceParams(this, (info != null) ? info.id : 0, guidType, IDeviceType.RZKY, GetDeviceParamsRes.class,
                    new RetrofitCallback<GetDeviceParamsRes>() {
                        @Override
                        public void onSuccess(GetDeviceParamsRes getDeviceParamsRes) {
                            if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap) {
                                new Thread(() -> RecipeManager.getInstance().setRecipeInfo(guidType, getDeviceParamsRes)).start();
                                setRecipeData(getDeviceParamsRes);
                            }
                        }

                        @Override
                        public void onFaild(String err) {
                            //showRecipeCategory();
                        }
                    });
        }
    }

    private void showRecipe(String recipeData){
        if(StringUtils.isNotBlank(recipeData)){
            GetDeviceParamsRes getDeviceParamsRes = new Gson().fromJson(recipeData, GetDeviceParamsRes.class);
            if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
                setRecipeData(getDeviceParamsRes);
            }else{
                this.setSearchState(true,false);
            }
        }else{
            this.setSearchState(true,false);
        }
    }

    //本地菜谱
    private void getLocalRecipe() {
        SteamOven steamOven = getSteamOven();
        if(steamOven == null){
            return;
        }
        String deviceTypeId = DeviceUtils.getDeviceTypeId(steamOven.guid);
        String steamContent = SteamDataUtil.getSteamContent(deviceTypeId);
        if(StringUtils.isNotBlank(steamContent)){
            GetDeviceParamsRes getDeviceParamsRes = new Gson().fromJson(steamContent, GetDeviceParamsRes.class);
            if (null != getDeviceParamsRes && null != getDeviceParamsRes.modelMap){
                setRecipeData(getDeviceParamsRes);
            }
        }
    }
    //菜谱数据
    private void setRecipeData(GetDeviceParamsRes getDeviceParamsRes) {
        if(getDeviceParamsRes == null || getDeviceParamsRes.modelMap == null || getDeviceParamsRes.modelMap.otherFunc == null){
            this.setSearchState(true,false);
            return;
        }
        OtherFunc otherFunc = getDeviceParamsRes.modelMap.otherFunc;
        for (DeviceConfigurationFunctions deviceConfigurationFunctions: otherFunc.deviceConfigurationFunctions) {
            if ("localCookbook".equals(deviceConfigurationFunctions.functionCode)) {
                if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                    SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                    if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                        for (DeviceConfigurationFunctions deviceConfigurationFunctions1: subViewModelMapSubView.deviceConfigurationFunctions) {
                            if (!"ckno".equals(deviceConfigurationFunctions1.functionCode))
                                deviceConfigurationFunctionsList.add(deviceConfigurationFunctions1);
                        }
                    }
                }
                break;
            }
        }
        if(deviceConfigurationFunctionsList.size() > 0){
            Collections.sort(deviceConfigurationFunctionsList, (t1, t2) -> {
                String order = t1.functionParams;
                String order2 = t2.functionParams;
                if(StringUtils.isBlank(order) || StringUtils.isBlank(order2)){
                    return 0;
                }
                try {
                    JSONObject jObj1 = new JSONObject(order);
                    JSONObject jObj2 = new JSONObject(order2);
                    String order1Str = jObj1.optString("sort");
                    String order2Str = jObj2.optString("sort");
                    return Integer.parseInt(order1Str) - Integer.parseInt(order2Str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                return 0;
            });
        }
        //添加分类
        for (int i =0; i<deviceConfigurationFunctionsList.size(); i++) {
            DeviceConfigurationFunctions deviceConfigurationFunctions = deviceConfigurationFunctionsList.get(i);
            if(deviceConfigurationFunctions.functionName != null){
                if(deviceConfigurationFunctions.functionName.contains("水产品")){
                    classifyList.add("水产");
                }else{
                    classifyList.add(deviceConfigurationFunctions.functionName.replace("菜谱",""));
                }
            }else{
                continue;
            }
            //菜谱信息
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setId(i);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.steam_view_layout_tab_classify, null);
            TextView classify = view.findViewById(R.id.tv_classify);
            classify.setText(classifyList.get(i));
            tab.setCustomView(view);
            tabLayout.addTab(tab);

            Fragment recipeClassifyPage = new RecipeClassifyPage();
            Bundle bundle = new Bundle();
            if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                    bundle.putInt("classify", i);
                    bundle.putParcelableArrayList(Constant.RECIPE_LIST_FLAG, (ArrayList<? extends Parcelable>) subViewModelMapSubView.deviceConfigurationFunctions);
                    recipeClassifyPage.setArguments(bundle);
                }
            }

            fragments.add(new WeakReference<>(recipeClassifyPage));
        }

        //添加设置适配器
        noScrollViewPager.setAdapter(new RecipeClassifyPagerAdapter(getSupportFragmentManager(),fragments));

        noScrollViewPager.setOffscreenPageLimit(classifyList.size());
    }

    class RecipeClassifyPagerAdapter extends FragmentStatePagerAdapter {



        List<WeakReference<Fragment>> curFragments;

        public RecipeClassifyPagerAdapter(@NonNull FragmentManager fm,List<WeakReference<Fragment>> fragments) {
            super(fm);
            this.curFragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = curFragments.get(position).get();
            LogUtils.e("getItem : "+position + " fragment "+(fragment == null));
            return fragment;
        }

        @Override
        public int getCount() {
            return curFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }

    //搜索结果
    private void searchResult(String text) {
        searchFragments.clear();
        List<DeviceConfigurationFunctions> resultList = new ArrayList<>();
        for (int i =0; i<deviceConfigurationFunctionsList.size(); i++) {
            DeviceConfigurationFunctions deviceConfigurationFunctions = deviceConfigurationFunctionsList.get(i);
            if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                    for(DeviceConfigurationFunctions item : subViewModelMapSubView.deviceConfigurationFunctions){
                        if(item.functionName.contains(text)){
                            resultList.add(item);
                        }
                    }
                }
            }
        }
        Fragment recipeClassifyPage = new RecipeClassifyPage();
        Bundle bundle = new Bundle();
        bundle.putInt("classify", 0);
        bundle.putParcelableArrayList(Constant.RECIPE_LIST_FLAG, (ArrayList<? extends Parcelable>) resultList);
        recipeClassifyPage.setArguments(bundle);
        searchFragments.add(new WeakReference<>(recipeClassifyPage));
        noScrollViewPager.setAdapter(new RecipeClassifyPagerAdapter(getSupportFragmentManager(),searchFragments));
        noScrollViewPager.setOffscreenPageLimit(1);
        this.setSearchState(resultList.size() == 0,true);
        tabLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 展示全部菜谱
     */
    private void showRecipeCategory(){
        tabLayout.setVisibility(View.VISIBLE);
        fragments.clear();
        for (int i =0; i<deviceConfigurationFunctionsList.size(); i++) {
            DeviceConfigurationFunctions deviceConfigurationFunctions = deviceConfigurationFunctionsList.get(i);
            Fragment recipeClassifyPage = new RecipeClassifyPage();
            Bundle bundle = new Bundle();
            if (null != deviceConfigurationFunctions.subView && null != deviceConfigurationFunctions.subView.modelMap) {
                SubViewModelMapSubView subViewModelMapSubView = deviceConfigurationFunctions.subView.modelMap.subView;
                if (null != subViewModelMapSubView && null != subViewModelMapSubView.deviceConfigurationFunctions) {
                    bundle.putInt("classify", i);
                    bundle.putParcelableArrayList(Constant.RECIPE_LIST_FLAG, (ArrayList<? extends Parcelable>) subViewModelMapSubView.deviceConfigurationFunctions);
                    recipeClassifyPage.setArguments(bundle);
                }
            }
            fragments.add(new WeakReference<>(recipeClassifyPage));
        }
        //添加设置适配器
        if(tabLayout.getTabCount() <= 0){
            return;
        }
        noScrollViewPager.setAdapter(new RecipeClassifyPagerAdapter(getSupportFragmentManager(),fragments));
        noScrollViewPager.setOffscreenPageLimit(classifyList.size());
        noScrollViewPager.setCurrentItem(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getId(), false);
    }



    private void setSearchState(boolean showSearchPrompt,boolean showBt){
        if(showSearchPrompt){
            searchPromptView.setVisibility(View.VISIBLE);
            searchPromptBtn.setVisibility(showBt?View.VISIBLE:View.INVISIBLE);
            tabLayout.setVisibility(View.INVISIBLE);
            noScrollViewPager.setVisibility(View.INVISIBLE);
        }else{
            searchPromptView.setVisibility(View.INVISIBLE);
            searchPromptBtn.setVisibility(View.INVISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            noScrollViewPager.setVisibility(View.VISIBLE);
        }
    }


}
