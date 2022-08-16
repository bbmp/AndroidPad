package com.robam.ventilator.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.ui.dialog.DelayCloseDialog;
import com.robam.ventilator.ui.dialog.VentilatorCommonDialog;

public class VentilatorDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_VENTILATOR_COMMON:
                iDialog = new VentilatorCommonDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_DELAY_CLOSE:
                iDialog = new DelayCloseDialog(context);
                break;
        }
        return iDialog;
    }
}
