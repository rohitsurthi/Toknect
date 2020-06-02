package com.rohheat.realtimechatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    public ChatFragment() {
        // Required empty public constructor
    }

    private RecyclerView chatRecyclerView;
    private View view;
    private FirebaseUser current_user;
    private ConvoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_chat, container, false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        chatRecyclerView = view.findViewById(R.id.chat_fragment_recycler);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setHasFixedSize(true);

        FirebaseRecyclerOptions<ConvoModel> options =
                new FirebaseRecyclerOptions.Builder<ConvoModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("chat").child(current_user.getUid()), ConvoModel.class)
                        .build();

        adapter =  new ConvoAdapter(options);

        chatRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
