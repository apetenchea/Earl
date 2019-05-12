package com.earl;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.earl.R;
import com.earl.hook.PackageMonitorService;
import com.earl.local.Settings;

public class SettingsActivity extends AppCompatActivity {
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.settings_title);
        }

        settings = new Settings(this);

        Switch useScanCache = findViewById(R.id.switch_use_scan_cache);
        useScanCache.setChecked(settings.getUseScanCache());
        useScanCache.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setUseScanCache(isChecked);
            }
        });

        final Context context = this;
        Switch hookOnInstall = findViewById(R.id.switch_hook_on_install);
        hookOnInstall.setChecked(settings.getHookOnInstall());
        hookOnInstall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setHookOnInstall(isChecked);
                if (!isChecked) {
                    stopService(new Intent(context, PackageMonitorService.class));
                } else {
                    if (PackageMonitorService.isOff()) {
                        startForegroundService(new Intent(context, PackageMonitorService.class));
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
