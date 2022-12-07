package com.robam.steamoven.factory;

import android.content.Context;

import com.robam.common.ui.dialog.IDialog;
import com.robam.steamoven.constant.DialogConstant;
import com.robam.steamoven.ui.dialog.SteamCommonDialog;
import com.robam.steamoven.ui.dialog.SteamErrorDialog;

public class SteamDialogFactory {
    public static IDialog createDialogByType(Context context, int dialogType) {
        IDialog iDialog = null;
        switch (dialogType) {
            case DialogConstant.DIALOG_TYPE_STEAM_COMMON:
                iDialog = new SteamCommonDialog(context);
                break;
            case DialogConstant.DIALOG_TYPE_STEAM_ERROR:
                iDialog = new SteamErrorDialog(context);
                break;
        }
        return iDialog;
    }
}
