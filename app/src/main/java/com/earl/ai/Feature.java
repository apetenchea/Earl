package com.earl.ai;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.util.Log;

import java.io.File;

class Feature {
    private static final String TAG = Feature.class.getName();

    private String name;
    private String type;
    private Float mean;
    private Float scale;
    private Float clipUpper;
    private Float clipLower;

    float getValue(PackageInfo packageInfo) {
        float value = 0f;
        switch (type) {
            case "count":
                value = extractCount(packageInfo);
                break;
            case "number":
                value = extractNumber(packageInfo);
                break;
            case "permission":
                value = extractPermission(packageInfo);
                break;
            default:
                Log.e(TAG, String.format("Unknown feature type %s", type));
                break;
        }
        return transform(value);
    }

    private float transform(float value) {
        if (clipLower != null) {
            value = Math.max(value, clipLower);
        }
        if (clipUpper != null) {
            value = Math.min(value, clipUpper);
        }
        if (mean != null && scale != null) {
            value = (value - mean) / scale;
        }
        return value;
    }

    private float extractPermission(PackageInfo packageInfo) {
        String[] requestedPermissions = packageInfo.requestedPermissions;
        if (requestedPermissions != null) {
            for (String permission : requestedPermissions) {
                int lastDot = permission.lastIndexOf('.');
                if (lastDot >= 0 && lastDot + 1 < permission.length()) {
                    if (name.equals(permission.substring(lastDot + 1))) {
                        return 1f;
                    }
                }
            }
        }
        return 0f;
    }

    private float extractNumber(PackageInfo packageInfo) {
        float result = 0f;
        switch (name) {
            case "APK_SIZE":
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if (appInfo == null) {
                    Log.e(TAG, String.format("Cannot extract APK_SIZE for %s", packageInfo.packageName));
                    break;
                }
                result = (float)new File(appInfo.publicSourceDir).length();
                break;
            case "VERSION_CODE":
                result = (float)(packageInfo.getLongVersionCode() & 0xFFFFFFFFL);
                break;
            default:
                Log.e(TAG, String.format("Unknown feature name %s", name));
                break;
        }
        return result;
    }

    private float extractCount(PackageInfo packageInfo) {
        float result = 0f;
        switch (name) {
            case "ACTIVITIES":
                ActivityInfo[] activities = packageInfo.activities;
                if (activities != null) {
                    result = (float)activities.length;
                }
                break;
            case "PROVIDERS":
                ProviderInfo[] providers = packageInfo.providers;
                if (providers != null) {
                    result = (float)providers.length;
                }
                break;
            case "RECEIVERS":
                ActivityInfo[] receivers = packageInfo.receivers;
                if (receivers != null) {
                    result = (float)receivers.length;
                }
                break;
            case "SERVICES":
                ServiceInfo[] services = packageInfo.services;
                if (services != null) {
                    return (float)services.length;
                }
                break;
            default:
                Log.e(TAG, String.format("Unknown feature name %s", name));
                break;
        }
        return result;
    }
}
