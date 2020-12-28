package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ExpensesAdapter;
import com.example.plansplit.Controllers.Adapters.GroupEventsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Controllers.FragmentControllers.PayFragment;
import com.example.plansplit.Controllers.FragmentControllers.notifications.NotificationsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Transfers;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    RecyclerView recyclerView;
    GroupEventsAdapter adapter;
    Database db;
    ImageView userImage;
    TextView userDeptText;
    int userTotalDept;
    ImageButton payButton;
    boolean ctrlType;   // friend => true       group=> false
    String personId;
    MyGroupActivity myGroupActivity;
    ArrayList<Transfers>GroupEventsObjectList = new ArrayList<>();

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        GroupEventsObjectList = new ArrayList<>();
        db = new Database(this.getContext(), this);
        myGroupActivity=(MyGroupActivity) getContext();
        userImage=root.findViewById(R.id.user_image_groupEvents);
        payButton=root.findViewById(R.id.pay_IButton);
        Picasso.with(getContext()).load(HomeActivity.getPersonPhoto()).into(userImage);
        userDeptText = root.findViewById(R.id.user_debt_groupEvents_text);
        userTotalDept = 15;
        userDeptText.setText(getString(R.string.total_dept)+String.valueOf(userTotalDept)+"TL");
        recyclerView=(RecyclerView) root.findViewById(R.id.RecyclerViewGroupEvents);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openPayFragment();
            }
        });
        if(myGroupActivity.getType().equals("group")){
            db.getExpensesFromGroup(myGroupActivity.getGroup().getGroupKey());
        }else if(myGroupActivity.getType().equals("friend")){
            db.getExpensesFromFriend(myGroupActivity.getFriend().getFriendshipsKey());
        }

        adapter =new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        return root;
    }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        }

        public void openPayFragment(){
        PayFragment payFragment = new PayFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place_mygroup,payFragment);
        fragmentTransaction.commit();
    }

    public void setArray(ArrayList a){
        GroupEventsObjectList = a;
        adapter = new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}