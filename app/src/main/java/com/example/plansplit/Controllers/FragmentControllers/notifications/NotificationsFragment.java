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
import com.example.plansplit.Models.Objects.Notification;
import com.example.plansplit.R;
import com.example.plansplit.Controllers.FragmentControllers.notifications.friendRequests.FriendRequestsFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    RecyclerView recyclerView;
    NotificationsAdapter adapter;
    List<Notification> notificationList;
    private String person_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
         person_id = getArguments().get("person_id").toString();

        recyclerView = root.findViewById(R.id.recyclerNotification);
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
        Bundle bundle = new Bundle();
        bundle.putString("person_id", person_id);
        FriendRequestsFragment friendRequestsFragment = new FriendRequestsFragment();
        friendRequestsFragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, friendRequestsFragment);
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




