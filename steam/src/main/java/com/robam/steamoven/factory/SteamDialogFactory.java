package com.robam.steamoven.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.steamoven.constant.DialogConstant;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;

public class SteamDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_STEAM_COMMON:
                iDialog = new SteamCommonDialog(context);
                break;
        }
        return iDialog;
    }
}
