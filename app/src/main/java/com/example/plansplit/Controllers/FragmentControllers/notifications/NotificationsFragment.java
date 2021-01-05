package com.example.plansplit.Controllers.FragmentControllers.notifications;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.FriendRequestsAdapter;
import com.example.plansplit.Controllers.Adapters.NotificationsAdapter;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Notification;
import com.example.plansplit.R;
import com.example.plansplit.Controllers.FragmentControllers.notifications.friendRequests.FriendRequestsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    RecyclerView recyclerView;
    NotificationsAdapter adapter;
    ArrayList<Notification> notificationList;
    private Database database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        database = Database.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationsAdapter(this.getContext(), notificationList);
        recyclerView = root.findViewById(R.id.recyclerNotification);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        database.getPersonalNotifications(getContext(), new Database.NotificationCallBack() {
            @Override
            public void onNotificationsRetrieveSuccess(ArrayList<Notification> notifications) {
                Collections.sort(notifications, Notification.getComparator());
                notificationList.addAll(notifications);
                Collections.reverse(notificationList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(error_tag, error);
            }
        });

        adapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                System.out.println(position);

            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.friend_requests_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        FriendRequestsFragment friendRequestsFragment = new FriendRequestsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, friendRequestsFragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
        return super.onOptionsItemSelected(item);
    }
}




