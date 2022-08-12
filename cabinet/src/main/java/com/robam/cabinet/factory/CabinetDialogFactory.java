package com.robam.cabinet.factory;

import android.content.Context;

import com.robam.cabinet.constant.DialogConstant;
import com.robam.cabinet.ui.dialog.ScreenLockDialog;
import com.robam.cabinet.ui.dialog.WorkCompleteDialog;
import com.robam.cabinet.ui.dialog.WorkStopDialog;
import com.robam.common.ui.dialog.IDialog;

public class CabinetDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_WORK_STOP:
                iDialog = new WorkStopDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_WORK_COMPLETE:
                iDialog = new WorkCompleteDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_SCREEN_LOCK:
                iDialog = new ScreenLockDialog(context);
                break;
        }
        return iDialog;
    }
}
