package com.robam.stove.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.stove.constant.DialogConstant;
import com.robam.stove.ui.dialog.CompleteDialog;
import com.robam.stove.ui.dialog.CurveEditDialog;
import com.robam.stove.ui.dialog.HomeLockDialog;
import com.robam.stove.ui.dialog.OpenFireDialog;
import com.robam.stove.ui.dialog.SelectStoveDialog;
import com.robam.stove.ui.dialog.StoveCommonDialog;
import com.robam.stove.ui.dialog.WaitingDialog;

public class StoveDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_OPEN_FIRE:
                iDialog = new OpenFireDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_STOVE_COMMON:
                iDialog = new StoveCommonDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_SELECT_STOVE:
                iDialog = new SelectStoveDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_CURVE_EDIT:
                iDialog = new CurveEditDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_HOME_LOCK:
                iDialog = new HomeLockDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_WAITING:
                iDialog = new WaitingDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_COMPLETE:
                iDialog = new CompleteDialog(context);
                break;
        }
        return iDialog;
    }
}
