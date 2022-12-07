package com.robam.pan.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.ui.dialog.CurveEidtDialog;
import com.robam.pan.ui.dialog.ElectricQuantityDialog;
import com.robam.pan.ui.dialog.OpenFireDialog;
import com.robam.pan.ui.dialog.PanCommonDialog;
import com.robam.pan.ui.dialog.SelectStoveDialog;
import com.robam.pan.ui.dialog.ShareDialog;

public class PanDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_OPEN_FIRE:
                iDialog = new OpenFireDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_PAN_COMMON:
                iDialog = new PanCommonDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_SELECT_STOVE:
                iDialog = new SelectStoveDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_ELECTRIC_QUANTITY:
                iDialog = new ElectricQuantityDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_SHARE:
                iDialog = new ShareDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_CURVE_EDIT:
                iDialog = new CurveEidtDialog(context);
                break;
        }

        return iDialog;
    }
}
