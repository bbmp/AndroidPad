package com.robam.ventilator.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.robam.ventilator.ui.service.AlarmVentilatorService;

public class AlarmSerialReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AlarmVentilatorService.class));
    }
}
