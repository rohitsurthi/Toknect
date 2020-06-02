package com.rohheat.realtimechatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends FirebaseRecyclerAdapter<Friends, FriendsAdapter.FriendsViewHolder> {

    private DatabaseReference userDatabase;

    public FriendsAdapter(@NonNull FirebaseRecyclerOptions<Friends> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends friend) {

        holder.friendDate.setText(friend.getDate());

        final String list_user_id = getRef(position).getKey();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);
        userDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final String userName = dataSnapshot.child("name").getValue().toString();
                final String thumbs = dataSnapshot.child("thumbs").getValue().toString();

//                if (dataSnapshot.hasChild("online")){
//                    String userOnline = (String)dataSnapshot.child("online").getValue().toString();
//                    if(userOnline.equals("true")){
//                        Picasso.with(holder.friendOnline.getContext()).load(R.drawable.online_icon).into(holder.friendOnline);
//                    }else{
//                        Picasso.with(holder.friendOnline.getContext()).load("sh").into(holder.friendOnline);
//                    }
//                }

                holder.directUnfriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Context c = v.getContext();
                        Intent profileIntent = new Intent(c,ProfileActivity.class);
                        profileIntent.putExtra("user_id",list_user_id);
                        c.startActivity(profileIntent);

                    }
                });

                holder.friendName.setText(userName);
                Picasso.with(holder.friendProPic.getContext()).load(thumbs).placeholder(R.drawable.default_user_profile).into(holder.friendProPic);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        CharSequence options[] = new CharSequence[]{
                          "Profile",
                          "Send message"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0){
                                    Context c = v.getContext();
                                    Intent profileIntent = new Intent(c,ProfileActivity.class);
                                    profileIntent.putExtra("user_id",list_user_id);
                                    c.startActivity(profileIntent);
                                }

                                if (which == 1){
                                    Context c = v.getContext();
                                    Intent chatIntent = new Intent(c,ChatActivity.class);
                                    chatIntent.putExtra("user_id",list_user_id);
                                    chatIntent.putExtra("user_name",userName);
                                    chatIntent.putExtra("profile_thumb",thumbs);
                                    c.startActivity(chatIntent);

                                }

                            }
                        });

                        builder.show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_layout, parent, false);

        return new FriendsViewHolder(view);

    }

    class FriendsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView friendProPic;
        TextView friendName, friendDate;
        ImageView friendOnline;
        Button directUnfriendBtn;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            friendProPic = itemView.findViewById(R.id.request_friend_propic);
            friendName = itemView.findViewById(R.id.request_friend_username);
            friendDate = itemView.findViewById(R.id.accept_details);
            friendOnline = itemView.findViewById(R.id.online_status_icon);
            directUnfriendBtn = itemView.findViewById(R.id.direct_unfriend_btn);

        }

    }

}
