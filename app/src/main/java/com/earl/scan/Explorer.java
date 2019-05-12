package com.earl.scan;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.GET_ACTIVITIES;
import static android.content.pm.PackageManager.GET_PERMISSIONS;
import static android.content.pm.PackageManager.GET_PROVIDERS;
import static android.content.pm.PackageManager.GET_RECEIVERS;
import static android.content.pm.PackageManager.GET_SERVICES;

public class Explorer {
    private static final String TAG = Explorer.class.getName();
    private static final Integer FLAGS = GET_ACTIVITIES | GET_PERMISSIONS | GET_PROVIDERS |
            GET_RECEIVERS | GET_SERVICES;

    private PackageManager packageManager;

    public Explorer(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public Apk[] getInstalledApps() {
        List<PackageInfo> packages = packageManager.getInstalledPackages(FLAGS);

        final int SYSTEM_FILTER = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        List<PackageInfo> filteredPackages = new ArrayList<>(packages.size());
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if ((appInfo.flags & SYSTEM_FILTER) == 0 && !appInfo.packageName.equals("com.earl")) {
                filteredPackages.add(packageInfo);
            }
        }

        Apk[] apks = new Apk[filteredPackages.size()];
        int index = 0;
        for (PackageInfo packageInfo : filteredPackages) {
            apks[index] = new Apk(packageManager, packageInfo);
            ++index;
        }

        return apks;
    }

    public Apk getApk(String packageName) {
        try {
            return new Apk(packageManager, packageManager.getPackageInfo(packageName, FLAGS));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
