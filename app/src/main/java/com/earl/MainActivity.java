package com.earl;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.earl.scan.Apk;
import com.earl.scan.Explorer;
import com.earl.hook.PackageMonitorService;
import com.earl.local.Cache;
import com.earl.scan.Scanner;
import com.earl.local.Settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
                AppStatus appStatus = (AppStatus) parent.getItemAtPosition(position);
                startApkActivity(appStatus);
                return true;
            }
        });

        scanner = new Scanner(getApplicationContext());
        cache = new Cache(getApplicationContext());
        explorer = new Explorer(this.getPackageManager());

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
        if (item.getItemId() == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            this.startActivity(myIntent);
        } else {
            Log.e(TAG, String.format("Unknown menu item %d", item.getItemId()));
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

    private void startApkActivity(AppStatus status) {
        Intent intent = new Intent(this, ApkActivity.class);
        intent.putExtra(getString(R.string.pkg_key), status.getPackageName());
        intent.putExtra(getString(R.string.risk_level_key), status.getRisk());
        intent.putExtra(getString(R.string.verdict_color_key), status.getColor());
        startActivity(intent);
    }

    private void startPackageMonitor() {
        Settings settings = new Settings(this);
        if (settings.getHookOnInstall() && PackageMonitorService.isOff()) {
            startForegroundService(new Intent(this, PackageMonitorService.class));
        }
    }

    private void showApps() {
        new BackgroundScanner(scanner).execute(explorer.getInstalledApps());
    }

    private void showScanResult(List<Pair<Integer, Apk>> pairs) {
        pairs.sort(new Comparator<Pair<Integer, Apk>>() {
            @Override
            public int compare(Pair<Integer, Apk> o1, Pair<Integer, Apk> o2) {
                return o2.first - o1.first;
            }
        });

        int index = 0;
        AppStatus[] status = new AppStatus[pairs.size()];
        for (Pair<Integer, Apk> pair : pairs) {
            Apk apk = pair.second;
            status[index] = new AppStatus(pair.first, apk.getLabel(), apk.getPackageName(),
                    apk.getMd5(), apk.getIcon());
            ++index;
        }
        ListAdapter adapter = new FullScanAdapter(this, status);
        scanResultView.setAdapter(adapter);
    }

    private class BackgroundScanner extends AsyncTask<Apk, Void, Integer[]> {

        private Scanner scanner;
        private List<Pair<Integer, Apk>> pairs;

        BackgroundScanner(Scanner scanner) {
            this.scanner = scanner;
            this.pairs = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            refreshBtn.setVisibility(View.GONE);
            scanResultView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Integer[] doInBackground(Apk... apps) {
            for (Apk apk : apps) {
                pairs.add(Pair.create(50, apk));
            }
            return scanner.scan(apps);
        }

        @Override
        protected void onPostExecute(Integer[] result) {
            progressBar.setVisibility(View.GONE);
            scanResultView.setVisibility(View.VISIBLE);
            refreshBtn.setVisibility(View.VISIBLE);

            for (int index = 0; index < result.length; ++index) {
                Pair<Integer, Apk> pair = pairs.get(index);
                pairs.set(index, Pair.create(result[index], pair.second));
            }
            showScanResult(pairs);

            super.onPostExecute(result);
        }
    }
}
