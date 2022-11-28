package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.UiAutomation;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.robam.common.ui.dialog.IDialog;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;

import java.io.File;

public class ResetActivity extends VentilatorBaseActivity {
    private IDialog resetDialog, progressDialog;
    int curProgress = 0;
    private Handler mHandler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_reset;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_reset);

        setOnClickListener(R.id.tv_reset);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_reset)
            resetDialog();
    }
    //恢复确认
    private void resetDialog() {
        if (null == resetDialog) {
            resetDialog = VentilatorDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON);
            resetDialog.setCancelable(false);
            resetDialog.setContentText(R.string.ventilator_reset_ok);
            resetDialog.setOKText(R.string.ventilator_reset);
            resetDialog.setListeners(new IDialog.DialogOnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.tv_ok) {
                        showProressDialog();

                        clearPublic(); //恢复中
                        clearPrivate();

                        mHandler.postDelayed(updateProgress, 100);
                    }
                }
            }, R.id.tv_cancel, R.id.tv_ok);
        }
        resetDialog.show();
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (null != progressDialog) {
                ProgressBar progressBar = progressDialog.getRootView().findViewById(R.id.sbr_progress);
                curProgress++;
                progressBar.setProgress(curProgress);
                if (curProgress >= 100) {
                    closeProgressDialog();
                    return;
                }
                mHandler.postDelayed(updateProgress, 100);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != resetDialog && resetDialog.isShow())
            resetDialog.dismiss();
        closeProgressDialog();
        mHandler.removeCallbacks(updateProgress);

        mHandler.removeCallbacksAndMessages(null);
    }

    private void showProressDialog() {
        if (null == progressDialog) {
            progressDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_UPDATE_VERSION);
            progressDialog.setCancelable(false);
        }
        progressDialog.setContentText(R.string.ventilator_reset_ing);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (null != progressDialog && progressDialog.isShow())
            progressDialog.dismiss();
    }
    /**
     * 清空公有目录
     */
    public void clearPublic() {

        String publicFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + getPackageName();
        File dir = new File(publicFilePath);
        File[] files = dir.listFiles();
        if (null != files) {
            for (File file : files) {
                deleteFolder(file.getAbsolutePath());
            }
        }
    }

    /**
     * 清空私有目录
     */
    public  void clearPrivate() {

        //清空文件夹
        File dir = new File(getApplication().getFilesDir().getParent());
        File[] files = dir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (!file.getName().contains("lib")) {
                    deleteFolder(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     */
    private boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteSingleFile(filePath);
            } else {
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 删除指定文件
     */
    private  boolean deleteDirectory(String filePath) {
        boolean flag = false;
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}