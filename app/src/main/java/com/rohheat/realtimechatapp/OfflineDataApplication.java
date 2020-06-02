package com.rohheat.realtimechatapp;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class OfflineDataApplication extends Application {

    private DatabaseReference userDatabase;
    private FirebaseAuth userAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



            userAuth = FirebaseAuth.getInstance();
            if (userAuth.getCurrentUser() != null){
                userDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(userAuth.getCurrentUser().getUid());



            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){
                        userDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            }


    }
}
