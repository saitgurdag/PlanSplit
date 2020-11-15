package com.example.plansplit.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.NotificationsAdapter;
import com.example.plansplit.Notification;
import com.example.plansplit.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private NotificationsViewModel notificationsViewModel;
    RecyclerView recyclerView;
    NotificationsAdapter adapter;
    List<Notification> notificationList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerNotification);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        notificationList = new ArrayList<>();

        notificationList.add(new Notification("Curie, İŞ grubuna harcama ekledi: Kartuş, 150 TL", "50 TL borçlusun", "20.10.2020", "20.50", R.drawable.denemeresim));
        notificationList.add(new Notification("Curie, İŞ grubuna harcama ekledi: Kartuş, 150 TL", "50 TL borçlusun", "20.10.2020", "20.50", R.drawable.denemeresim));
        notificationList.add(new Notification("Curie, İŞ grubuna harcama ekledi: Kartuş, 150 TL", "50 TL borçlusun", "20.10.2020", "20.50", R.drawable.denemeresim));

        Log.d(TAG, "BURADA");

        adapter = new NotificationsAdapter(this.getContext(), notificationList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        return root;
    }
}




