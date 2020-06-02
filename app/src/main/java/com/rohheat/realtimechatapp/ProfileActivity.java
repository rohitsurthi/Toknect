package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName , profileStatus , profileFriendsCount;
    private ImageView profileImageView;
    private Button sendRequestBtn , declineRequestBtn;
    private DatabaseReference userDatabase;
    private ProgressDialog progressDialog;
    private DatabaseReference friendRequestDatabase;
    private FirebaseUser currentUser;
    private DatabaseReference friendsDatabase;
    private DatabaseReference notificationDatabase;
    private DatabaseReference mRootRef;

    private String current_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        profileName = findViewById(R.id.profile_username);
        profileStatus = findViewById(R.id.profile_status);
       // profileFriendsCount = findViewById(R.id.total_friends_textView);
        profileImageView = findViewById(R.id.profileImageView);
        sendRequestBtn = findViewById(R.id.profile_send_request_btn);
        declineRequestBtn = findViewById(R.id.decline_request);

        current_state = "not_friends";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading user data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                profileName.setText(displayName);
                profileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.userprofile_default_image).into(profileImageView);

                //-----------------------------------------Request  or accepts thing or reject it----------------------------------------

                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(request_type.equals("received")){

                                sendRequestBtn.setEnabled(true);
                                current_state = "request_received";
                                sendRequestBtn.setText("Accept Request");

                                declineRequestBtn.setVisibility(View.VISIBLE);
                                declineRequestBtn.setEnabled(true);

                                progressDialog.dismiss();

                            }else if(request_type.equals("sent")){

                                current_state = "request_sent";
                                sendRequestBtn.setText("cancel request");

                            }

                        }else{

                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){
                                        current_state = "friends";
                                        sendRequestBtn.setText("Unfriend");
                                    }

                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        declineRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_state = "decline_request";

                if(current_state.equals("decline_request")){

                    final Map declineMap = new HashMap();
                    declineMap.put("friend_request/"+currentUser.getUid()+"/"+user_id,null);
                    declineMap.put("friend_request/"+user_id+"/"+currentUser.getUid(),null);

                    mRootRef.updateChildren(declineMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null){

                                sendRequestBtn.setEnabled(true);
                                current_state = "not_friends";
                                sendRequestBtn.setText("send request");
                                declineRequestBtn.setVisibility(View.INVISIBLE);

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
            }
        });


        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--------------------------------Not friends code ------------------------------------------------------------
                sendRequestBtn.setEnabled(false);

                if(current_state.equals("not_friends")){

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",currentUser.getUid());
                    notificationData.put("type","friend request");

                    Map requestMap = new HashMap();
                    requestMap.put("friend_request/"+currentUser.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("friend_request/"+user_id+"/"+currentUser.getUid()+"/request_type","received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null){
                                Toast.makeText(ProfileActivity.this,"something went wrong!",Toast.LENGTH_SHORT).show();
                            }

                            sendRequestBtn.setEnabled(true);
                            current_state = "request_sent";
                            sendRequestBtn.setText("cancel request");


                        }
                    });

                }

                //---------------------------------------end code for not friends-------------------------------------------------------

                //----------------------------------------cancel request state code-------------------------------------------------

                if(current_state.equals("request_sent")){
                    friendRequestDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendRequestDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendRequestBtn.setEnabled(true);
                                    current_state = "not_friends";
                                    sendRequestBtn.setText("Sent Request");

                                }
                            });

                        }
                    });
                }


                //------------------------Making friends code--------------------------------------------

                if (current_state.equals("request_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendMap = new HashMap();
                    friendMap.put("friends/"+currentUser.getUid()+"/"+user_id+ "/date",currentDate);
                    friendMap.put("friends/"+user_id+"/"+currentUser.getUid()+ "/date",currentDate);

                    friendMap.put("friend_request/"+currentUser.getUid()+"/"+user_id,null);
                    friendMap.put("friend_request/"+user_id+"/"+currentUser.getUid(),null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null){

                                sendRequestBtn.setEnabled(true);
                                current_state = "friends";
                                sendRequestBtn.setText("Unfriend");

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }

                //--------------unfriend------------------
                if(current_state.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friends/"+currentUser.getUid()+"/"+user_id,null);
                    unfriendMap.put("friends/"+user_id+"/"+currentUser.getUid(),null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null){

                                sendRequestBtn.setEnabled(true);
                                current_state = "not_friends";
                                sendRequestBtn.setText("send request");

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

                //------------------------ignore or decline friend request---------------------------------



            }
        });


    }
}
