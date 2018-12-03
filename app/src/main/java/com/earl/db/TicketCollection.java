package com.earl.db;

import android.support.annotation.NonNull;
import android.util.Log;

import com.earl.apk.Apk;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketCollection {
    private static final String TAG = TicketCollection.class.getName();

    private DatabaseReference tickets;

    public TicketCollection() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        tickets = rootRef.child("ticket");
    }

    public void submit(@NonNull String md5, Ticket ticket) {
        DatabaseReference child = tickets.child(md5);
        child.setValue(ticket);
    }
}
