package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText fullName , email , password;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.reg_full_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        registerBtn = findViewById(R.id.reg_regBtn);
        toolbar = findViewById(R.id.register_toolBar);

        progressDialog = new ProgressDialog(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerBtn.setEnabled(true);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailStr = email.getText().toString();
                String fullNameStr = fullName.getText().toString();
                String passwordStr = password.getText().toString();


                if(!TextUtils.isEmpty(fullNameStr)){
                    if(!TextUtils.isEmpty(emailStr)){
                        if(!TextUtils.isEmpty(passwordStr)){

                            progressDialog.show();
                            progressDialog.setTitle("Registering User");
                            progressDialog.setMessage("Please wait! We are creating your name");
                            progressDialog.setCanceledOnTouchOutside(false);

                            firebaseAuth = FirebaseAuth.getInstance();
                            registerUser(fullNameStr,emailStr,passwordStr);

                        }else{
                            password.setError("Enter password");
                        }
                    }else{
                        email.setError("Enter email");
                    }
                }else{
                    fullName.setError("Enter your full name");
                }

            }
        });

    }

    private void registerUser(String fullNameStr,String emailStr,String passwordStr){

        firebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    database = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",fullName.getText().toString());
                    userMap.put("status","im new to this!");
                    userMap.put("image","default");
                    userMap.put("thumbs","dafault");
                    userMap.put("device_token",device_token);

                    database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressDialog.dismiss();

                            Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(RegisterActivity.this,"Register success",Toast.LENGTH_LONG).show();

                        }
                    });

                }else{

                    progressDialog.hide();

                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this,error,Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
