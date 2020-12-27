package com.example.plansplit.Controllers.FragmentControllers.notifications.friendRequests;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.FriendRequestsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.notifications.NotificationsFragment;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.friend_requests_back_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        Bundle bundle = new Bundle();
        bundle.putString("person_id", person_id);
        NotificationsFragment notificationsFragment = new NotificationsFragment();
        notificationsFragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, notificationsFragment);
        /*fixme:
           bir fragmenttan bildirimlere, ordan da isteklere geçildikten sonra
            geri tuşuna basılınca bildirimlere değil önceki fragmenta döndürüyor -arda
         */
        // belki geri tuşu overridelanabilir ama geri tuşunu değiştirmek
        // tavsiye edilen tasarım normlarının dışında sayılıyor genelde
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
        return super.onOptionsItemSelected(item);
    }
}
