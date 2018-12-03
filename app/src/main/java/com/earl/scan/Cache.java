package com.earl.scan;

import android.content.Context;
import android.content.SharedPreferences;

import com.earl.R;

public class Cache {
    private SharedPreferences cache;

    public Cache(Context context) {
        this.cache = context.getSharedPreferences(context.getString(R.string.scan_cache_file), Context.MODE_PRIVATE);
    }

    float get(String key) {
        if (key == null) {
            return Float.NaN;
        }
        return cache.getFloat(key, Float.NaN);
    }

    void set(String key, float value) {
        SharedPreferences.Editor editor = cache.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = cache.edit();
        editor.clear();
        editor.apply();
    }
}
