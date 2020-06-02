package com.rohheat.realtimechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView usersList;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerOptions<Users> options;
    private FirebaseRecyclerAdapter<Users,MyViewHolder> adapter;
    private EditText searchUser;
    private ImageButton searchUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        searchUser = findViewById(R.id.search_user_edittext);
        searchUserBtn = findViewById(R.id.search_user_imagebtn);

        //toolbar setup
        toolbar = findViewById(R.id.users_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase database setup
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        usersList = findViewById(R.id.users_list);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));

        loadData("");

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString() != null){

                    loadData(s.toString());

                }else{

                    loadData("");

                }

            }
        });


    }

    private void loadData(String data){

        Query query = mUsersDatabase.orderByChild("name").startAt(data).endAt(data+"\uf8ff");


        options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();

        adapter = new FirebaseRecyclerAdapter<Users, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Users model) {

                holder.userName.setText(model.getName());
                Picasso.with(holder.userPic.getContext()).load(model.getThumb_image()).placeholder(R.drawable.default_user_profile).into(holder.userPic);

                holder.userStatus.setText(model.getStatus());

                final String user_id = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context c = v.getContext();
                        Intent profileIntent = new Intent(c,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        c.startActivity(profileIntent);

                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout,parent,false);

                return new MyViewHolder(v);
            }
        };

        adapter.startListening();
        usersList.setAdapter(adapter);

    }

}
