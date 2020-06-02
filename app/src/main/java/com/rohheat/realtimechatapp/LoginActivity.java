package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText email , password;
    private TextView forgotPassword;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_button);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.login_forgotPass_textView);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.login_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();

                if(!TextUtils.isEmpty(emailStr)){
                    if(!TextUtils.isEmpty(passwordStr)){

                        progressDialog.setTitle("Logging In ");
                        progressDialog.setMessage("Please Wait! While we Login");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        loginUser(emailStr,passwordStr);
                    }else{
                        password.setError("Enter Password");
                    }
                }else{
                    email.setError("Enter email");
                }

            }
        });

    }

    private void loginUser(String emailStr, String passwordStr) {

        firebaseAuth.signInWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){



                    String current_user_id = firebaseAuth.getCurrentUser().getUid();

                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    databaseReference.child(current_user_id).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }
                    });

                }else{

                    progressDialog.hide();

                    String error = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this,error,Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
