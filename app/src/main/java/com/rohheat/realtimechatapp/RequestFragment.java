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
public class RequestFragment extends Fragment {

    public RequestFragment() {
        // Required empty public constructor
    }

    private RecyclerView requestRecycler;
    private View view;
    private FirebaseUser current_user;
    private RequestAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_request, container, false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        requestRecycler = view.findViewById(R.id.request_fragment_recycle);
        requestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        requestRecycler.setHasFixedSize(true);

        FirebaseRecyclerOptions<RequestModel> options =
                new FirebaseRecyclerOptions.Builder<RequestModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("friend_request").child(current_user.getUid()), RequestModel.class)
                        .build();

        adapter =  new RequestAdapter(options);

        requestRecycler.setAdapter(adapter);

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
