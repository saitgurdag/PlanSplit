package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ExpensesAdapter;
import com.example.plansplit.Controllers.Adapters.GroupEventsAdapter;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Transfers;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    //Berkay ekleme kısmı//
    RecyclerView recyclerView;
    GroupEventsAdapter adapter;
    Database db;
    ImageView userImage;
    boolean ctrlType;   // friend => true       group=> false
    String personId;
    MyGroupActivity myGroupActivity;
    ArrayList<Transfers>GroupEventsObjectList = new ArrayList<>();

    //Berkay ekleme kısmı bitiş//

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        GroupEventsObjectList = new ArrayList<>();
        db = new Database(this.getContext(), this);
        myGroupActivity=(MyGroupActivity) getContext();
        userImage=root.findViewById(R.id.user_image_groupEvents);
        Picasso.with(getContext()).load(HomeActivity.getPersonPhoto()).into(userImage);
        recyclerView=(RecyclerView) root.findViewById(R.id.RecyclerViewGroupEvents);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);

        if(myGroupActivity.getType().equals("group")){
            db.getExpensesFromGroup(myGroupActivity.getGroup().getGroupKey());
        }else if(myGroupActivity.getType().equals("friend")){
            db.getExpensesFromFriend(myGroupActivity.getFriend().getFriendshipsKey());
        }

        recyclerView = root.findViewById(R.id.RecyclerViewGroupEvents);
        recyclerView.setHasFixedSize(true);
        adapter =new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setArray(ArrayList a){
        GroupEventsObjectList = a;
        adapter = new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}