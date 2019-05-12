package com.earl.scan;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Apk {
    private static final String TAG = Apk.class.getName();

    private PackageInfo packageInfo;
    private String md5;
    private Drawable icon;
    private String label;

    Apk(PackageManager packageManager, PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
        this.md5 = computeMd5();

        CharSequence label = packageManager.getApplicationLabel(packageInfo.applicationInfo);
        this.label = label != null ? label.toString() : getPackageName();

        try {
            this.icon = packageManager.getApplicationIcon(getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            this.icon = new ColorDrawable(Color.BLACK);
        }
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public String getMd5() {
        return md5;
    }

    PackageInfo getPackageInfo() {
        return packageInfo;
    }

    private String computeMd5() {
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        if (appInfo == null) {
            Log.e(TAG, String.format("Cannot get ApplicationInfo for %s", packageInfo.packageName));
            return null;
        }

        File apk = new File(appInfo.publicSourceDir);
        int size = (int)apk.length();
        byte[] bytes = new byte[size];
        try {
            FileInputStream stream = new FileInputStream(apk);
            if (stream.read(bytes) != size) {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
        if (messageDigest == null) {
            return null;
        }

        messageDigest.update(bytes);
        return String.format("%032x", new BigInteger(1, messageDigest.digest()));
    }
}
