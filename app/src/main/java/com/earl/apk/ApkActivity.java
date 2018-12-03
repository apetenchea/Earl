package com.earl.apk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.earl.R;
import com.earl.db.TicketActivity;

public class ApkActivity extends AppCompatActivity {
    private static final String TAG = ApkActivity.class.getName();
    private static final int REQUEST_CODE_TICKET = 1;

    private String packageName;
    private String md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.apk_title);
        }

        Intent intent = getIntent();
        String packageName = intent.getStringExtra(getString(R.string.pkg_key));
        if (packageName == null) {
            Log.e(TAG, "Intent does not contain package name!");
            finish();
        }
        this.packageName = packageName;

        int risk = intent.getIntExtra(getString(R.string.risk_level_key), -1);
        if (risk < 0) {
            Log.e(TAG, "Invalid risk level!");
            finish();
        }

        int verdictColor = intent.getIntExtra(getString(R.string.verdict_color_key), 0);

        Explorer explorer = new Explorer(this);
        Apk apk = explorer.getApk(packageName);
        if (apk == null) {
            Log.e(TAG, String.format("Cannot find %s", packageName));
            finish();
        }

        ImageView icon = findViewById(R.id.image_view_icon);
        icon.setImageDrawable(apk.getIcon());

        TextView appNameView = findViewById(R.id.text_view_app_name);
        appNameView.setText(apk.getLabel());
        appNameView.setTextColor(verdictColor);

        TextView pkgNameView = findViewById(R.id.text_view_pkg_name);
        pkgNameView.setText(apk.getPackageName());
        pkgNameView.setTextColor(verdictColor);

        String md5 = apk.getMd5();
        this.md5 = md5;
        TextView md5View = findViewById(R.id.text_view_apk_md5);
        md5View.setText(md5);
        md5View.setTextColor(verdictColor);

        TextView riskView = findViewById(R.id.text_view_risk);
        riskView.setText(String.valueOf(risk));
        riskView.setTextColor(verdictColor);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TICKET) {
            if (resultCode == TicketActivity.RESULT_CODE_BACK) {
                return;
            }
            String msg = "Cannot create ticket!";
            if (resultCode == Activity.RESULT_OK){
                msg = "Ticket created! Thanks!";
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void createTicket(View view) {
        Intent intent = new Intent(this, TicketActivity.class);
        intent.putExtra(getString(R.string.pkg_key), packageName);
        intent.putExtra(getString(R.string.md5_key), md5);
        startActivityForResult(intent, REQUEST_CODE_TICKET);
    }

    public void uninstallPackage(View view) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse(String.format("package:%s", packageName)));
        startActivity(intent);
    }
}
