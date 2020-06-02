package com.rohheat.realtimechatapp;

import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessagesList;
    private FirebaseAuth auth;
    private DatabaseReference userDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessagesList = mMessageList;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_custom_layout,parent,false);



        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText,userNameOnChat , chatSentTime;
        public CircleImageView profileImage;
        public RelativeLayout chatLayout;
        public View sideColorBar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.message_text);
            //profileImage = itemView.findViewById(R.id.message_propric);
            userNameOnChat = itemView.findViewById(R.id.chat_username);
            chatSentTime = itemView.findViewById(R.id.chat_sent_time);
            chatLayout = itemView.findViewById(R.id.chat_layout);
            sideColorBar = itemView.findViewById(R.id.sideColorBar);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder, int position) {

        auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        Messages c = mMessagesList.get(position);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabase.keepSynced(true);


        String fromUser = c.getFrom();

        //------------checking who is sending the message---------

        if (fromUser.equals(currentUserId)) {

            //holder.messageText.setBackgroundColor(Color.parseColor("#333333"));
            holder.userNameOnChat.setText("Me");
           // holder.profileImage.setImageDrawable(null);
            //holder.chatLayout.setBackgroundColor(Color.parseColor("#1f1f1f"));
            holder.userNameOnChat.setTextColor(Color.parseColor("#FAC42F"));
            holder.sideColorBar.setBackgroundColor(Color.parseColor("#FAC42F"));

        }else{

           // holder.messageText.setBackgroundColor(Color.parseColor("#0a0a0a"));
            //Picasso.with(this).load().placeholder().into();

            holder.userNameOnChat.setText("username");
            holder.userNameOnChat.setTextColor(Color.parseColor("#53E0BC"));
            holder.sideColorBar.setBackgroundColor(Color.parseColor("#53E0BC"));

            //holder.chatLayout.setBackgroundColor(Color.parseColor("#383838"));

            userDatabase.child(fromUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final String userName = dataSnapshot.child("name").getValue().toString();
                    final String thumbs = dataSnapshot.child("thumbs").getValue().toString();

                    holder.userNameOnChat.setText(userName);
                   // Picasso.with(holder.profileImage.getContext()).load(thumbs).placeholder(R.drawable.default_user_profile).into(holder.profileImage);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //TODO:  setting thumbs for chat!

        }

        holder.messageText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
