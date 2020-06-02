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

public class RequestAdapter extends FirebaseRecyclerAdapter<RequestModel, RequestAdapter.RequestViewHolder> {

    private DatabaseReference userDatabase;

    public RequestAdapter(@NonNull FirebaseRecyclerOptions<RequestModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull final RequestModel model) {

        final String list_user_id = getRef(position).getKey();
       // userDatabase.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        userDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String userName = dataSnapshot.child("name").getValue().toString();
                final String thumbs = dataSnapshot.child("thumbs").getValue().toString();

                holder.friendName.setText(userName);
                Picasso.with(holder.friendProPic.getContext()).load(thumbs).placeholder(R.drawable.default_user_profile).into(holder.friendProPic);

                if (model.getRequest_type().equals("sent")){

                    holder.requestDetails.setText("My request to");
                    holder.acceptDetails.setText("Tap to cancel the request!");
                    holder.imageIndicator.setImageResource(R.drawable.sent_icon);

                }else{

                    holder.requestDetails.setText("Request received from");
                    holder.acceptDetails.setText("Tap to accept or reject");
                    holder.imageIndicator.setImageResource(R.drawable.received_icon);

                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Context c = v.getContext();
                        Intent profileIntent = new Intent(c,ProfileActivity.class);
                        profileIntent.putExtra("user_id",list_user_id);
                        c.startActivity(profileIntent);

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
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_made_layout, parent, false);

        return new RequestViewHolder(view);

    }

    class RequestViewHolder extends RecyclerView.ViewHolder{

        CircleImageView friendProPic;
        TextView friendName,requestDetails,acceptDetails;
        ImageView imageIndicator;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            friendName = itemView.findViewById(R.id.request_friend_username);
            friendProPic = itemView.findViewById(R.id.request_friend_propic);
            requestDetails = itemView.findViewById(R.id.request_details);
            acceptDetails = itemView.findViewById(R.id.accept_details);
            imageIndicator = itemView.findViewById(R.id.image_indicator);

        }
    }

}
