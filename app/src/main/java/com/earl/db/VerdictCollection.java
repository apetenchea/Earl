package com.earl.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.earl.apk.Apk;
import com.earl.scan.Verdict;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class VerdictCollection {
    private static final String TAG = VerdictCollection.class.getName();
    private static final long TRANSFER_TIMEOUT_MS = 500;

    private Query verdictQuery;
    private BlockingQueue<Verdict> queue;
    private int queryCounter;

    public VerdictCollection() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference verdictRef = rootRef.child("verdict");
        verdictQuery = verdictRef.orderByKey();
        queue = new LinkedBlockingQueue<>();
        queryCounter = 0;
    }

    public void submit(final Apk apk) {
        final String md5 = apk.getMd5();
        if (md5 == null) {
            return;
        }
        ++queryCounter;

        verdictQuery.equalTo(md5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Float>> genericTypeIndicator =
                        new GenericTypeIndicator<Map<String, Float>>() {};
                Map<String, Float> verdict = dataSnapshot.getValue(genericTypeIndicator);
                Log.d(TAG, "Got verdict for apk " + md5);
                if (verdict != null) {
                    Float result = verdict.get(md5);
                    if (result != null) {
                        Verdict pair = new Verdict(apk, result);
                        try {
                            if (!queue.offer(pair, TRANSFER_TIMEOUT_MS, MILLISECONDS)) {
                                Log.e(TAG, String.format("Transfer failed for %s", md5));
                            }
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    public Verdict retrieve() {
        Verdict result = null;
        try {
            result = queue.poll(TRANSFER_TIMEOUT_MS, MILLISECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        if (result != null) {
            --queryCounter;
        }
        return result;
    }

    public List<Verdict> drain() {
        List<Verdict> list = new ArrayList<>(queue.size());
        queue.drainTo(list);
        queryCounter -= list.size();
        return list;
    }

    public void clear() {
        queue.clear();
        queryCounter = 0;
    }

    public int getQueryCounter() {
        return queryCounter;
    }
}
