package com.earl.db;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.earl.R;

public class TicketActivity extends AppCompatActivity {
    public static final int RESULT_CODE_BACK = -2;
    private static final String TAG = TicketActivity.class.getName();

    private String packageName;
    private String md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.apk_title);
        }

        Intent intent = getIntent();
        packageName = intent.getStringExtra(getString(R.string.pkg_key));
        md5 = intent.getStringExtra(getString(R.string.md5_key));

        if (packageName == null || md5 == null) {
            Log.e(TAG, "One of the expected intent extras is null!");
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CODE_BACK, new Intent());
        onBackPressed();
        return true;
    }

    public void submitTicket(View view) {
        EditText email = findViewById(R.id.edit_text_email);
        EditText msg = findViewById(R.id.edit_text_msg);
        TicketCollection collection = new TicketCollection();
        collection.submit(md5,
                new Ticket(packageName, email.getText().toString(), msg.getText().toString()));
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}
