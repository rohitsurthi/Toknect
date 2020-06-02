package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private SectionPagerAdapter sectionPagerAdapter;
    private TabLayout tabLayout;
    private DatabaseReference userOnlineRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.mainPageToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TOKNECT");

        viewPager = findViewById(R.id.viewPager);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(sectionPagerAdapter);

        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //if statement added later *
        if(mAuth.getCurrentUser() != null){
            userOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            sendToStart();

        }else{

            userOnlineRef.child("online").setValue("true");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.main_menu_logout){

             mAuth.getInstance().signOut();
             sendToStart();
         }

         if(item.getItemId() == R.id.main_account_btn){

             Intent settingsIntent = new Intent(MainActivity.this,AccountSettingsActivity.class);
             startActivity(settingsIntent);

         }

         if(item.getItemId() == R.id.main_alluser_btn){

             Intent allUsersIntent = new Intent(MainActivity.this,AllUsersActivity.class);
             startActivity(allUsersIntent);

         }

        return true;
    }

    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            userOnlineRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
