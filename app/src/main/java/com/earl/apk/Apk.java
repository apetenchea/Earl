package com.earl.apk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.earl.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Apk {
    private static final String TAG = Apk.class.getName();

    private Context context;
    private PackageInfo packageInfo;
    private String md5;
    private Drawable icon;
    private String label;

    Apk(Context context, PackageInfo packageInfo) {
        this.context = context;
        this.packageInfo = packageInfo;
        this.md5 = null;
        this.icon = null;
        this.label = null;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getLabel() {
        if (label == null) {
            PackageManager pm = context.getPackageManager();
            CharSequence lbl = pm.getApplicationLabel(packageInfo.applicationInfo);
            if (lbl != null) {
                label = lbl.toString();
            }
        }
        return label;
    }

    public Drawable getIcon() {
        if (icon == null) {
            try {
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    icon = pm.getApplicationIcon(getPackageName());
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
            if (icon == null) {
                icon = ContextCompat.getDrawable(
                        context.getApplicationContext(), R.drawable.baseline_android_black_48dp);
            }
        }

        if (icon == null) {
            return new ColorDrawable(Color.BLACK);
        }
        return icon;
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public String getMd5() {
        if (md5 == null) {
            md5 = this.computeMd5();
        }
        return md5;
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
