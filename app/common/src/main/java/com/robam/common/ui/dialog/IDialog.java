package com.robam.common.ui.dialog;

import android.view.View;

public interface IDialog {
    /**
     * 设置对话框文本内容
     * @param contentStrId 资源文件中的id
     */
    void setContentText(int contentStrId);
    /**
     * 设置对话框文本内容
     * @param contentStr 字符串类型要展示的文本内容
     */
    void setContentText(CharSequence contentStr);

    /**
     * 取消文字
     * @param res
     */
    void setCancelText(int res);

    /**
     * 确认文字
     * @param res
     */
    void setOKText(int res);
    /**
     *
     * @param onClickListener
     * @param viewIds
     */
    void setListeners(DialogOnClickListener onClickListener, int... viewIds);

    /**
     * 设置对话框是否可取消
     * @param b
     */
    void setCancelable(boolean b);

    /**
     * true 显示 false 不显示
     * @return
     */
    boolean isShow();

    /**
     * 对话框显示功能
     */
    void show();

    /**
     * 对话框关闭功能
     */
    void dismiss();

    /**
     * 根布局
     * @return
     */
    View getRootView();
    //
    interface DialogOnClickListener{
        void onClick(View v);
    }
}
