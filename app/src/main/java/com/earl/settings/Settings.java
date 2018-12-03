package com.earl.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.earl.R;

public class Settings {
    private static final String TAG = Settings.class.getName();

    private Context context;
    private SharedPreferences settings;

    public Settings(Context context) {
        this.context = context;
        this.settings = context.getSharedPreferences(
                context.getString(R.string.settings_file), Context.MODE_PRIVATE);
    }

    public boolean getShowSystemApps() {
        return settings.getBoolean(context.getString(R.string.settings_show_system_apps), false);
    }

    void setShowSystemApps(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(context.getString(R.string.settings_show_system_apps), value);
        editor.apply();
    }

    public boolean getUseScanCache() {
        return settings.getBoolean(context.getString(R.string.settings_use_scan_cache), true);
    }

    void setUseScanCache(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(context.getString(R.string.settings_use_scan_cache), value);
        editor.apply();
    }

    public boolean getUseVerdictsDb() {
        return settings.getBoolean(context.getString(R.string.settings_use_verdicts_db), true);
    }

    void setUseVerdictsDb(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(context.getString(R.string.settings_use_verdicts_db), value);
        editor.apply();
    }

    public boolean getHookOnInstall() {
        return settings.getBoolean(context.getString(R.string.settings_hook_on_install), true);
    }

    void setHookOnInstall(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(context.getString(R.string.settings_hook_on_install), value);
        editor.apply();
    }
}
