package com.earl.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.earl.settings.Settings;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Settings settings = new Settings(context);
            if (settings.getHookOnInstall() && PackageMonitorService.isOff()) {
                context.startForegroundService(new Intent(context, PackageMonitorService.class));
            }
        }
    }
}
