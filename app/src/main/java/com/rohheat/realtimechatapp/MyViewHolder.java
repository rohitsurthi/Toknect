package com.rohheat.realtimechatapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView userName,userStatus;
    public CircleImageView userPic;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.users_single_name);
        userPic = itemView.findViewById(R.id.users_single_propic);
        userStatus = itemView.findViewById(R.id.users_single_status);


    }
}
