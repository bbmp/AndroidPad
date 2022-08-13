package com.robam.dishwasher.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.dishwasher.constant.DialogConstant;
import com.robam.dishwasher.ui.dialog.DiashWasherCommonDialog;
import com.robam.dishwasher.ui.dialog.WorkCompleteDialog;

public class DishWasherDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_COMMON_DIALOG:
                iDialog = new DiashWasherCommonDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_WORK_COMPLETE:
                iDialog = new WorkCompleteDialog(context);
                break;
        }
        return iDialog;
    }
}
