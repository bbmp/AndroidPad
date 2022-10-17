package com.robam.pan.module;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.module.IPublicPanApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.ui.dialog.IDialog;
import com.robam.pan.R;
import com.robam.pan.constant.DialogConstant;
import com.robam.pan.device.HomePan;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.factory.PanDialogFactory;

import java.util.Map;

//锅
public class PublicPanApi implements IPublicPanApi {

    @Override
    public void setInteractionParams(String targetGuid, Map params) {
        PanAbstractControl.getInstance().setInteractionParams(targetGuid, params);
    }

    @Override
    public void lowBatteryHint(Context context) {
        IDialog iDialog = PanDialogFactory.createDialogByType(context, DialogConstant.DIALOG_TYPE_ELECTRIC_QUANTITY);
        iDialog.setCancelable(false);
        iDialog.setListeners(new IDialog.DialogOnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, R.id.tv_ok);
        iDialog.show();
    }
}
