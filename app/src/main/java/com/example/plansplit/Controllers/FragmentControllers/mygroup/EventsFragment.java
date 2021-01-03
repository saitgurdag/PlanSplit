package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.GroupEventsAdapter;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Transfers;
import com.example.plansplit.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class EventsFragment extends Fragment {

    RecyclerView recyclerView;
    GroupEventsAdapter adapter;
    Database db;
    ImageView userImage;
    TextView userDeptText;
    ImageButton payButton;
    boolean ctrlType;   // friend => true       group=> false
    String personId;
    MyGroupActivity myGroupActivity;
    ArrayList<Transfers>GroupEventsObjectList = new ArrayList<>();
    ImageView backCircle;
    float totDept;
    Groups group;
    Friend friend;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        GroupEventsObjectList = new ArrayList<>();
        db = Database.getInstance();
        myGroupActivity=(MyGroupActivity) getContext();
        userImage=root.findViewById(R.id.user_image_groupEvents);
        payButton=root.findViewById(R.id.pay_IButton);
        backCircle = root.findViewById(R.id.user_image_balance_groupEvents);
        userDeptText = root.findViewById(R.id.user_debt_groupEvents_text);
        userDeptText.setText(getString(R.string.total_dept)+String.valueOf(0)+"TL");
        Picasso.with(getContext()).load(db.getPerson().getImage()).into(userImage);
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

        Bundle extras = getArguments();

        if(extras != null && extras.keySet().contains("group")){
            Gson gson = new Gson();
            String json = extras.getString("group");
            group = gson.fromJson(json, Groups.class);
            db.getExpensesFromGroup(group.getKey(), transferCallBack);
            ArrayList<Friend> members = new ArrayList<>();
            db.getGroupMembersInfo(((MyGroupActivity) getContext()).getGroup().getGroup_members(),members, memberCallBack );
        }else if(extras != null && extras.keySet().contains("friend")){
            Gson gson = new Gson();
            String json = extras.getString("friend");
            friend = gson.fromJson(json, Friend.class);
            db.getExpensesFromFriend(friend.getFriendshipsKey(), transferCallBack);
            db.getDebtFromFriend(db.getPerson().getKey(), ((MyGroupActivity) getContext()).getFriend(), friendCallBack);
        }

        adapter =new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();

        recyclerView.setAdapter(adapter);
        return root;
    }

    public final Database.TransferCallBack transferCallBack = new Database.TransferCallBack() {
        @Override
        public void onTransferRetrieveSuccess(ArrayList<Transfers> transfers) {
            setArray(transfers);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    }

    public void openPayFragment(){
        myGroupActivity.setNaviPay(R.id.navi_pay);
    }

    public void setArray(ArrayList a){
        GroupEventsObjectList = a;
        adapter = new GroupEventsAdapter(GroupEventsObjectList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private  final Database.getDebtFromFriendCallBack friendCallBack = new Database.getDebtFromFriendCallBack() {
        @Override
        public void onGetDebtFromFriendRetrieveSuccess(float debt) {
            userDeptText.setText(getString(R.string.personal_debt) + " : " +String.valueOf(debt)+" TL");
            if(debt>0){
                backCircle.setImageResource(R.drawable.circle_background_red);
                userDeptText.setTextColor(getResources().getColor(R.color.red));
            }else{
                backCircle.setImageResource(R.drawable.circle_background_green);
                userDeptText.setTextColor(getResources().getColor(R.color.brightGreen));
            }
        }

        @Override
        public void onError(String error_tag, String error) {

        }
    };

    Database.GetMemberInfoCallBack memberCallBack = new Database.GetMemberInfoCallBack() {
        @Override
        public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
            totDept=0;
            for (Friend f : members){
                if(!db.getPerson().getKey().equals(f.getKey())) {
                    f.setFriendshipsKey(((MyGroupActivity)getContext()).getGroup().getKey());
                    db.getDebtFromGroups(db.getPerson().getKey(), f, groupCallBack);
                }
            }
        }

        @Override
        public void onError(String error_tag, String error) {

        }
    };

    Database.getDebtFromGroupCallBack groupCallBack = new Database.getDebtFromGroupCallBack() {
        @Override
        public void onGetDebtFromGroupRetrieveSuccess(float debt) {
            totDept+=debt;
            userDeptText.setText(getString(R.string.personal_debt) + " : " +String.valueOf(totDept)+" TL");
            if(debt>0){
                backCircle.setImageResource(R.drawable.circle_background_red);
                userDeptText.setTextColor(getResources().getColor(R.color.red));
            }else{
                backCircle.setImageResource(R.drawable.circle_background_green);
                userDeptText.setTextColor(getResources().getColor(R.color.brightGreen));
            }

        }

        @Override
        public void onError(String error_tag, String error) {

        }
    };

}