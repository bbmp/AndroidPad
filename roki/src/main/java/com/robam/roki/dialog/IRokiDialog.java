package com.robam.roki.dialog;
import android.content.DialogInterface;
import android.view.View;

import java.util.List;

public interface IRokiDialog {
    /**
     * 设置标题
     * @param titleStrId 资源文件中的id
     */
    void setTitle(int titleStrId);

    /**
     * 设置标题
     * @param titleStr 字符串类型要展示的标题
     */
    void setTitle(CharSequence titleStr);

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
     *
     * @param list
     * @param <T>   内容列表
     */
    void setContentList(List<?> list);

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

    //
    interface DialogOnClickListener{
        void onClick(View v, int position);
    }
}