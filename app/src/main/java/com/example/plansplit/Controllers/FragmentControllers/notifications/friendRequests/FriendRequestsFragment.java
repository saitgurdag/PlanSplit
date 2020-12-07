package com.example.plansplit.Controllers.FragmentControllers.notifications.friendRequests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.FriendRequestsAdapter;
import com.example.plansplit.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendRequestsFragment extends Fragment{
    private static final String TAG = "FriendRequestsFragment";
    private RecyclerView m_RecyclerView;
    private FriendRequestsAdapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_ref = db_ref.child("users");
    private String person_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View root = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        Log.d(TAG, "BURADA");
        person_id = getArguments().get("person_id").toString();

        m_RecyclerView = root.findViewById(R.id.recycler_friends_requests);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_Adapter = new FriendRequestsAdapter(this.getContext(), person_id, m_RecyclerView);
        m_RecyclerView.setAdapter(m_Adapter);

        return root;
    }
}
