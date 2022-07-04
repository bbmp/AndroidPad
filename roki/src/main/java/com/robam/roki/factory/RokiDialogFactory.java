package com.robam.roki.factory;

import android.content.Context;

import com.robam.roki.dialog.DialogUtils;
import com.robam.roki.dialog.IRokiDialog;
import com.robam.roki.dialog.type.DialogType_23;
import com.robam.roki.dialog.type.DialogType_24;

public class RokiDialogFactory {
    public static IRokiDialog createDialogByType(Context context, int dialogType) {
        IRokiDialog rokiDialog = null;
        switch (dialogType) {
            case DialogUtils.DIALOG_TYPE_23:
                rokiDialog = new DialogType_23(context);
                break;
            case DialogUtils.DIALOG_TYPE_24:
                rokiDialog = new DialogType_24(context);
                break;
        }
        return rokiDialog;
    }
}
