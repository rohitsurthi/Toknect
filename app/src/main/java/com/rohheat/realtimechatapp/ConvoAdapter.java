package com.rohheat.realtimechatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ConvoAdapter extends FirebaseRecyclerAdapter<ConvoModel,ConvoAdapter.ConvoViewHolder> {

    private DatabaseReference userDatabase;

    public ConvoAdapter(@NonNull FirebaseRecyclerOptions<ConvoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ConvoViewHolder holder, int position, @NonNull final ConvoModel convo) {

        final String list_user_id = getRef(position).getKey();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);

        userDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String userName = dataSnapshot.child("name").getValue().toString();
                final String thumbs = dataSnapshot.child("thumbs").getValue().toString();

                if (dataSnapshot.hasChild("online")){
                    String userOnline = (String)dataSnapshot.child("online").getValue().toString();
                    if(userOnline.equals("true")){
                        Picasso.with(holder.convoOnlineStatus.getContext()).load(R.drawable.online_icon).into(holder.convoOnlineStatus);
                    }else{
                        Picasso.with(holder.convoOnlineStatus.getContext()).load("sh").into(holder.convoOnlineStatus);
                    }
                }

                holder.convoFriendName.setText(userName);
                Picasso.with(holder.convoFriendPic.getContext()).load(thumbs).placeholder(R.drawable.default_user_profile).into(holder.convoFriendPic);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context c = v.getContext();
                        Intent chatIntent = new Intent(c,ChatActivity.class);
                        chatIntent.putExtra("user_id",list_user_id);
                        chatIntent.putExtra("user_name",userName);
                        chatIntent.putExtra("profile_thumb",thumbs);
                        c.startActivity(chatIntent);

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
    public ConvoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.convo_list_layout, parent, false);

        return new ConvoViewHolder(view);

    }

    class ConvoViewHolder extends RecyclerView.ViewHolder{

        CircleImageView convoFriendPic;
        TextView convoFriendName, convoLastChat;
        ImageView convoOnlineStatus;

        public ConvoViewHolder(@NonNull View itemView) {
            super(itemView);

            convoFriendPic = itemView.findViewById(R.id.request_friend_propic);
            convoFriendName = itemView.findViewById(R.id.request_friend_username);
            convoLastChat = itemView.findViewById(R.id.convo_last_message);
            convoOnlineStatus = itemView.findViewById(R.id.online_status_icon);

        }
    }

}
