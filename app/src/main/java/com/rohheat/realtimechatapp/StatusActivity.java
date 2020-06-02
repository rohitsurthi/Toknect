package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView statusTV;
    private Button makeChangesBtn;

    private DatabaseReference statusDatabase;
    private FirebaseUser firebaseUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Acount Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");

        statusTV = findViewById(R.id.statusTV);
        makeChangesBtn = findViewById(R.id.changesBtn);

        statusTV.setText(status_value);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = firebaseUser.getUid();

        statusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        makeChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving changes!");
                progressDialog.setMessage("please wait for change..");
                progressDialog.show();

                String status = statusTV.getText().toString();

                statusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                        }else{
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



    }
}
