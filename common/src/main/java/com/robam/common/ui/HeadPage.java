package com.robam.common.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.robam.common.R;
import com.robam.common.ui.action.ClickAction;

public abstract class HeadPage extends Fragment implements ClickAction {
    protected View mRootView;
    protected View mContentView;
    protected FrameLayout pnlMain;

    /**
     * 获取布局 ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();
    /**
     * 初始化数据
     */
    protected abstract void initData();


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (null == mRootView) {
            mRootView = inflater.inflate(R.layout.common_header_page, container, false);
            pnlMain = mRootView.findViewById(R.id.pnlMain);

            mContentView = inflater.inflate(getLayoutId(), container, false);
            pnlMain.addView(mContentView);
            setStateBarFixer();
            initView();
            initData();
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置状态栏占位
     */
    protected void setStateBarFixer(){
        View mStateBarFixer = mRootView.findViewById(R.id.status_bar_fix);
        if (mStateBarFixer != null){
            ViewGroup.LayoutParams layoutParams = mStateBarFixer.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = getStatusBarHeight(getActivity());
            mStateBarFixer.setLayoutParams(layoutParams);

        }
    }
    /**
     * 设置状态栏颜色
     */
    protected void setStateBarColor(int resid) {
        View mStateBarFixer = mRootView.findViewById(R.id.status_bar_fix);
        if (mStateBarFixer != null){
            mStateBarFixer.setBackgroundResource(resid);
        }
    }
    /**
     * 设置状态栏颜色
     */
    protected void setStateBarDrawable(Drawable drawable) {
        View mStateBarFixer = mRootView.findViewById(R.id.status_bar_fix);
        if (mStateBarFixer != null){
            mStateBarFixer.setBackground(drawable);
        }
    }
    /**
     * 根据资源 id 获取一个 View 对象
     */
    public <V extends View> V findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    private int getStatusBarHeight(Activity a) {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
