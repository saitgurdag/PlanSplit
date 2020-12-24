package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.FragmentControllers.friends.FriendsFragment;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.R;
import com.example.plansplit.Models.Objects.Friend;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private static final String TAG = "FriendsAdapter";
    private  ArrayList<Friend> friends=new ArrayList<>();
    private Context mCtx;
    private Database database = Database.getInstance();
    private String person_id;
    private RecyclerView m_RecyclerView;
    private ArrayList<Friend> friendsbuffer=new ArrayList<>();

    public FriendsAdapter(Context mCtx, String person_id, final RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        loadFriends();
        FriendsFragment.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                friends=new ArrayList<>();
                friends.addAll(friendsbuffer);
                ArrayList<Friend> friendstodelete=new ArrayList<>();
                if(!s.isEmpty()){
                    for(Friend friend:friendsbuffer){
                        if(!friend.getName().toLowerCase().contains(s.toLowerCase())){
                            friendstodelete.add(friend);
                        }
                    }
                    friends.removeAll(friendstodelete);
                }
                notifyDataSetChanged();
                m_RecyclerView.setAdapter(FriendsAdapter.this);

                return true;
            }
        });

    }

    public void loadFriends(){
        this.friendsbuffer = new ArrayList<>();
        database.getFriends(person_id, friendsCallBack);
    }

    private final Database.FriendCallBack friendsCallBack = new Database.FriendCallBack() {
        @Override
        public void onFriendRetrieveSuccess(final Friend friend) {

            friendsbuffer.add(friend);
            friends.clear();
            friends.addAll(friendsbuffer);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendsAdapter.this);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(TAG, error_tag + ": " + error);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendsAdapter.this);
        }
    };


    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendsViewHolder fvh = new FriendsViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position){
        Friend friend = friends.get(position);
        if(friend.getPerson_image().toString().isEmpty()){
            holder.friend_image.setImageResource(R.drawable.denemeresim);
        }
        if(!friend.getPerson_image().toString().isEmpty()){
            Picasso.with(mCtx).load(friend.getPerson_image()).into(holder.friend_image);
        }
        //holder.friend_image.setImageResource(friend.getPerson_image());

        holder.friend_name.setText(friend.getName());
        holder.friend_amount.setText(friend.getAmount());
        holder.friend_amount.setTextColor(ContextCompat.
                getColor(holder.itemView.getContext(), friend.getColor()));
        holder.friend_amount_text.setText(friend.getAmount_text());
        holder.friend_amount_text.setTextColor(ContextCompat.
                getColor(holder.itemView.getContext(), friend.getColor()));
        holder.friend_layout.setBackgroundResource(friend.getLayout_background());
        holder.friend_image_balance.setImageResource(friend.getImage_background());


    }

    @Override
    public int getItemCount(){
        return friends.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        public ImageView friend_image;
        public TextView friend_name;
        public TextView friend_amount;
        public TextView friend_amount_text;
        public RelativeLayout friend_layout;
        public ImageView friend_image_balance;

        public FriendsViewHolder(@NonNull View itemView){
            super(itemView);
            friend_image = itemView.findViewById(R.id.personalOperations_imagePerson);
            friend_name = itemView.findViewById(R.id.groupOperations_text);
            friend_amount = itemView.findViewById(R.id.groupOperations_amount);
            friend_amount_text = itemView.findViewById(R.id.friend_amount_text);
            friend_layout = itemView.findViewById(R.id.friend_background_layout);
            friend_image_balance = itemView.findViewById(R.id.personalOperations_PersonBackGround);

            itemView.findViewById(R.id.groupOperations_card).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        database.searchInFriends(person_id, friends.get(position).getKey(), new Database.DatabaseCallBack() {
                            @Override
                            public void onSuccess(String success) {
                                Log.i(TAG, success);
                                Intent intent = new Intent(mCtx, MyGroupActivity.class);
                                Gson gson = new Gson();
                                String json = gson.toJson(friends.get(position));
                                intent.putExtra("friend", json);
                                intent.putExtra("person_id", person_id);
                                mCtx.startActivity(intent);
                            }

                            @Override
                            public void onError(String error_tag, String error) {
                                Log.e(TAG, error_tag + ": " + error);
                                loadFriends();
                            }
                        });
                    }else{
                        Log.e(TAG, "RecyclerView: NO_POSITION");
                    }
                }
            });
        }
    }
}
