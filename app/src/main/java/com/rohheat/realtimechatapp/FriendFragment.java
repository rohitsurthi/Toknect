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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    public FriendFragment() {
        // Required empty public constructor
    }


    private RecyclerView friendsRecyclerView;
    private FriendsAdapter adapter;
    private View view;
    private FirebaseUser current_user;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_friend, container, false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        friendsRecyclerView = view.findViewById(R.id.friendsFragmentRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setHasFixedSize(true);


        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("friends").child(current_user.getUid()), Friends.class)
                        .build();

        adapter =  new FriendsAdapter(options);

        friendsRecyclerView.setAdapter(adapter);

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
