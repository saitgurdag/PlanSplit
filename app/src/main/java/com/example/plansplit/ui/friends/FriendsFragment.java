package com.example.plansplit.ui.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.FriendsAdapter;
import com.example.plansplit.Objects.Friend;
import com.example.plansplit.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        ArrayList<Friend> friends_list = new ArrayList<>();
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie", 30));
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie", -50));
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie",40));

        m_RecyclerView = root.findViewById(R.id.recycler_friends);
        m_RecyclerView.setHasFixedSize(true);
        m_Adapter = new FriendsAdapter(friends_list);
        m_LayoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_RecyclerView.setAdapter(m_Adapter);

        return root;
    }
}