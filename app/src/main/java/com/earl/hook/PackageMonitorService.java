package com.earl.hook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.earl.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class PackageMonitorService extends Service {
    private static final String TAG = PackageMonitorService.class.getName();
    public static AtomicBoolean status = new AtomicBoolean();

    private static final int FOREGROUND_ID = 3421;
    private BroadcastReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (status.get()) {
            Log.e(TAG, "Attempting to start service when it is already running!");
            return START_NOT_STICKY;
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.pkg_monitor_channel))
                .setContentTitle("Scan")
                .setContentText("See application risk level")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
        status.set(true);
        startForeground(FOREGROUND_ID, notificationBuilder.build());
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager.getNotificationChannel(getString(R.string.pkg_monitor_channel)) == null) {
            createNotificationChannel();
        }
        receiver = new OnInstallReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;
        status.set(false);
    }

    public static boolean isOff() {
        return !status.get();
    }

    private void createNotificationChannel() {
        CharSequence name = "Scan notifications";
        String description = "Show notifications when an application is installed.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(getString(R.string.pkg_monitor_channel), name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}

