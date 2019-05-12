package com.earl.local;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    private static final String TAG = Settings.class.getName();

    private SharedPreferences settings;

    public Settings(Context context) {
        this.settings = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE);
    }

    public boolean getUseScanCache() {
        return settings.getBoolean("USE_SCAN_CACHE", true);
    }

    public void setUseScanCache(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("USE_SCAN_CACHE", value);
        editor.apply();
    }

    public boolean getHookOnInstall() {
        return settings.getBoolean("HOOK_ON_INSTALL", true);
    }

    public void setHookOnInstall(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("HOOK_ON_INSTALL", value);
        editor.apply();
    }
}
