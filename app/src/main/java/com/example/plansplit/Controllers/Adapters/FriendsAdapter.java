package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.R;
import com.example.plansplit.Models.Objects.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private static final String TAG = "FriendsAdapter";
    private ArrayList<Friend> friends;
    private Context mCtx;
    private Database database = Database.getInstance();
    private String person_id;
    private RecyclerView m_RecyclerView;

    public FriendsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.friends = new ArrayList<>();
        database.getFriends(person_id, friendsCallBack);
    }

    public void Refresh(){
        friends = new ArrayList<>();
        database.getFriends(person_id, friendsCallBack);
    }

    private Database.FireBaseFriendCallBack friendsCallBack = new Database.FireBaseFriendCallBack() {
        @Override
        public void onFriendRetrieveSuccess(Friend friend) {
            friends.add(friend);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendsAdapter.this);
        }

        @Override
        public void onEmptyListError() {
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendsAdapter.this);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
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

        holder.friend_image.setImageResource(friend.getPerson_image());
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
            friend_image = itemView.findViewById(R.id.friend_image);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_amount = itemView.findViewById(R.id.friend_amount);
            friend_amount_text = itemView.findViewById(R.id.friend_amount_text);
            friend_layout = itemView.findViewById(R.id.friend_background_layout);
            friend_image_balance = itemView.findViewById(R.id.friend_image_balance);

            itemView.findViewById(R.id.friend_card).setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //todo arkadaş detaylar ve arkadaş sil dialog seçeneği
                    }
                    return true;
                }
            });

            itemView.findViewById(R.id.friend_card).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getAdapterPosition();
                    System.out.println("pozisyonn : " + getAdapterPosition());
                    Intent intent = new Intent(mCtx, MyGroupActivity.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(friends.get(p));
                    intent.putExtra("friend", json);
                    mCtx.startActivity(intent);
                }
            });
        }
    }
}
