package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatUser,chatThumb;
    private Toolbar chatToolbar;
    private DatabaseReference rootRef;

    private TextView username,lastseen;
    private CircleImageView propicView;
    private FirebaseAuth firebaseAuth;
    private String currentUser;

    private ImageButton addBtn , sendBtn;
    private EditText chatMessageEditText;

    private RecyclerView messageList;
    private SwipeRefreshLayout refreshLayout;

    private final List<Messages> arrayMessageList = new ArrayList<>();
    private LinearLayoutManager linearLayout;
    private MessageAdapter adapter;

    private static final int TOTAL_MESG_TO_LOAD = 10;
    private int currentPage = 1;

    //testing solution for pagination
    private int itemPosition = 0;
    private String mLastKey = "";
    private String mPrevKey = "";




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolbar = findViewById(R.id.chat_appBar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference();

        addBtn = findViewById(R.id.chat_add_imagebtn);
        sendBtn = findViewById(R.id.chat_send_imagebtn);
        chatMessageEditText = findViewById(R.id.chat_message_edittext);

        refreshLayout = findViewById(R.id.swipe_refresh_layout);

        chatUser = getIntent().getStringExtra("user_id");
        String user_name = getIntent().getStringExtra("user_name");
        chatThumb = getIntent().getStringExtra("profile_thumb");
        getSupportActionBar().setTitle(null);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View barView = inflater.inflate(R.layout.custom_chat_bar,null);

        actionBar.setCustomView(barView);

        //-----------------adapter-------------------

        adapter = new MessageAdapter(arrayMessageList);

        //------------------custom bar items---------------

        username = findViewById(R.id.chat_bar_username);
        lastseen = findViewById(R.id.chat_bar_lastseen);
        propicView = findViewById(R.id.chat_bar_propic);

        username.setText(user_name);
        Picasso.with(this).load(chatThumb).placeholder(R.drawable.userprofile_default_image).into(propicView);
        //------------------------------message recycler things--------------------------
        messageList = findViewById(R.id.message_list_recycler);
        linearLayout = new LinearLayoutManager(this);
        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(linearLayout);

        messageList.setAdapter(adapter);
        loadMessages();

        //--------------------------------------------------------------------------


        //----------------setting last seen feature------------------------------------------
        rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String isOnline = dataSnapshot.child("online").getValue().toString();
                String userImage = dataSnapshot.child("image").getValue().toString();

                if (isOnline.equals("true")){

                    lastseen.setText("Online");

                }else{

                    GetTime getTime = new GetTime();

                    long lastTime = Long.parseLong(isOnline);

                    String lastSeenTime = getTime.getTimeAgo(lastTime,getApplication());

                    lastseen.setText(lastSeenTime);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //--------------------------------------------------------------------------

        //---------------------------chat object to database----------------------------------------
        rootRef.child("chat").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatUser)){

                    Map chatMap = new HashMap();
                    chatMap.put("seen",false);
                    chatMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("chat/"+currentUser+"/"+chatUser,chatMap);
                    chatUserMap.put("chat/"+chatUser+"/"+currentUser,chatMap);

                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Log.d("chat_log",databaseError.getMessage().toString());
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-----------------------------------------------------------------------------------------------------------------

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                sendMessage();

            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                currentPage++;
                itemPosition = 0;
                loadMoreMessages();

            }
        });

    }

    //testing.....
    private void loadMoreMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(currentUser).child(chatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10); // made a change

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)){
                    arrayMessageList.add(itemPosition++ ,message);
                }else {
                    mPrevKey = mLastKey;
                }


                if (itemPosition == 1){

                    mLastKey = messageKey;
                }




                Log.d("keys","last key \n"+mLastKey+"prev key \n"+mPrevKey+"current key \n"+messageKey);

                adapter.notifyDataSetChanged();
               //messageList.scrollToPosition(arrayMessageList.size() - 1);
                refreshLayout.setRefreshing(false);
              linearLayout.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadMessages() {

        DatabaseReference messageRef = rootRef.child("messages").child(currentUser).child(chatUser);

        Query messageQuery = messageRef.limitToLast( currentPage *  TOTAL_MESG_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPosition++;

                if (itemPosition == 1){

                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                arrayMessageList.add(message);
                adapter.notifyDataSetChanged();
                messageList.scrollToPosition(arrayMessageList.size() - 1);
                refreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //----------------------------------------------storing message in database!  -------------------------------------------------------
    private void sendMessage() {

        String message = chatMessageEditText.getText().toString();

        if (!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/"+currentUser+"/"+chatUser;
            String chat_user_ref = "messages/"+chatUser+"/"+currentUser;

            DatabaseReference user_message_push = rootRef.child("messages")
                    .child(currentUser).child(chatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",currentUser);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

            chatMessageEditText.setText("");

            rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("chat_log",databaseError.getMessage().toString());
                    }
                }
            });

        }

    }

    //--------------------------------------------------------------------------------------------------------------


}