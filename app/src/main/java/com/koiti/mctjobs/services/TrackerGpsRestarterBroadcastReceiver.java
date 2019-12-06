package com.koiti.mctjobs.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrackerGpsRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TrackerGpsRestarterBroadcastReceiver.class.getSimpleName(), "Service TrackerGpsService Stops! Oops!!!!");
        context.startService(new Intent(context, TrackerGpsService.class));
    }
}
