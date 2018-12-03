package com.earl.apk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.earl.settings.Settings;

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

    private Context context;
    private PackageManager packageManager;
    private Settings settings;

    public Explorer(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();
        settings = new Settings(context);
    }

    public Apk[] getApps() {
        List<PackageInfo> packages = packageManager.getInstalledPackages(FLAGS);
        if (settings.getShowSystemApps()) {
            return packageInfoToApkArray(packages);
        }

        final int SYSTEM_FILTER = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        List<PackageInfo> filteredPackages = new ArrayList<>(packages.size());
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if ((appInfo.flags & SYSTEM_FILTER) == 0) {
                filteredPackages.add(packageInfo);
            }
        }

        return packageInfoToApkArray(filteredPackages);
    }

    public Apk getApk(String packageName) {
        try {
            return new Apk(context, packageManager.getPackageInfo(packageName, FLAGS));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private Apk[] packageInfoToApkArray(List<PackageInfo> packageInfos) {
        Apk[] apks = new Apk[packageInfos.size()];
        for (int index = 0; index < apks.length; ++index) {
            apks[index] = new Apk(context, packageInfos.get(index));
        }
        return apks;
    }
}
