package com.earl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.earl.apk.Apk;
import com.earl.apk.ApkActivity;
import com.earl.apk.Explorer;
import com.earl.hook.PackageMonitorService;
import com.earl.scan.Cache;
import com.earl.scan.Scanner;
import com.earl.scan.Verdict;
import com.earl.settings.Settings;
import com.earl.settings.SettingsActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private Scanner scanner;
    private Explorer explorer;
    private Cache cache;
    private ProgressBar progressBar;
    private Button refreshBtn;
    private ListView scanResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.main_title);
        }

        progressBar = findViewById(R.id.scanProgressBar);
        refreshBtn = findViewById(R.id.buttonFullScan);
        scanResultView = findViewById(R.id.listViewScanResult);
        scanResultView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Verdict verdict = (Verdict) parent.getItemAtPosition(position);
                startApkActivity(verdict);
                return true;
            }
        });

        scanner = new Scanner(this);
        explorer = new Explorer(this);
        cache = new Cache(this);

        startPackageMonitor();
    }

    @Override
    public void onResume() {
        super.onResume();
        showApps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                this.startActivity(myIntent);
                break;
            default:
                Log.e(TAG, String.format("Unknown menu item %d", item.getItemId()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void refresh(View view) {
        cache.clear();
        showApps();
    }

    private void startApkActivity(Verdict verdict) {
        Intent intent = new Intent(this, ApkActivity.class);
        intent.putExtra(getString(R.string.pkg_key), verdict.getApkPkgName());
        intent.putExtra(getString(R.string.risk_level_key), verdict.getRiskAsPercentage());
        intent.putExtra(getString(R.string.verdict_color_key), verdict.getColor());
        startActivity(intent);
    }

    private void startPackageMonitor() {
        Settings settings = new Settings(this);
        if (settings.getHookOnInstall() && PackageMonitorService.isOff()) {
            startForegroundService(new Intent(this, PackageMonitorService.class));
        }
    }

    private void showApps() {
        new BackgroundScanner(scanner).execute(explorer.getApps());
    }

    private void showScanResult(Verdict[] result) {
        Arrays.sort(result);
        ListAdapter adapter = new FullScanAdapter(this, result);
        scanResultView.setAdapter(adapter);
    }

    private class BackgroundScanner extends AsyncTask<Apk, Void, Verdict[]> {

        private Scanner scanner;

        BackgroundScanner(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            refreshBtn.setVisibility(View.GONE);
            scanResultView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Verdict[] doInBackground(Apk... apps) {
            return scanner.scan(apps);
        }

        @Override
        protected void onPostExecute(Verdict[] result) {
            progressBar.setVisibility(View.GONE);
            scanResultView.setVisibility(View.VISIBLE);
            refreshBtn.setVisibility(View.VISIBLE);
            showScanResult(result);
            super.onPostExecute(result);
        }
    }
}
