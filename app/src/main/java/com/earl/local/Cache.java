package com.earl.local;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {
    private SharedPreferences cache;

    public Cache(Context context) {
        this.cache = context.getSharedPreferences("scan_cache", Context.MODE_PRIVATE);
    }

    public int get(String key) {
        return cache.getInt(key, -1);
    }

    public void set(String key, int value) {
        SharedPreferences.Editor editor = cache.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = cache.edit();
        editor.clear();
        editor.apply();
    }
}
