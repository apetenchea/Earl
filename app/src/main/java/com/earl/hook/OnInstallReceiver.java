package com.earl.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.earl.apk.Apk;
import com.earl.apk.Explorer;
import com.earl.scan.Scanner;
import com.earl.scan.Verdict;

import java.util.Locale;

public class OnInstallReceiver extends BroadcastReceiver {
    private static final String TAG = OnInstallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            Uri uri = intent.getData();
            if (uri == null) {
                return;
            }

            String packageName = extractPackageName(uri.toString());
            if (packageName == null) {
                Log.e(TAG, "Cannot extract package name!");
                return;
            }

            Explorer explorer = new Explorer(context);
            Apk apk = explorer.getApk(packageName);
            if (apk == null) {
                Log.e(TAG, String.format("Cannot retrieve information about %s!", packageName));
                return;
            }

            Scanner scanner = new Scanner(context);
            Verdict v = scanner.scan(apk);

            String message = String.format(Locale.US,
                    "%s carries a risk of %d%%", v.getApkName(), v.getRiskAsPercentage());
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, String.format("Unhandled action %s!", intent.getAction()));
        }
    }

    private String extractPackageName(String uri) {
        final String BEGINNING = "package:";
        if (uri.length() > BEGINNING.length()) {
            return uri.substring(BEGINNING.length());
        } else {
            return null;
        }
    }
}
