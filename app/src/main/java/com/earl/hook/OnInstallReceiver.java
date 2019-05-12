package com.earl.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.earl.scan.Apk;
import com.earl.scan.Explorer;
import com.earl.scan.Scanner;

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
            
            if (packageName.equals("com.earl")) {
                return;
            }

            Explorer explorer = new Explorer(context.getPackageManager());
            Apk apk = explorer.getApk(packageName);
            if (apk == null) {
                Log.e(TAG, String.format("Cannot retrieve information about %s!", packageName));
                return;
            }

            Scanner scanner = new Scanner(context.getApplicationContext());
            Integer risk = scanner.scan(apk)[0];

            String message = String.format(Locale.US,
                    "%s carries a risk of %d%%", apk.getLabel(), risk);
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
