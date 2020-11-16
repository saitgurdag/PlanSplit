package com.example.plansplit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.R;
import com.example.plansplit.Objects.Friend;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private ArrayList<Friend> m_friend_ArrayList;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
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
        }
    }

    public FriendsAdapter(ArrayList<Friend> friend_ArrayList){
        m_friend_ArrayList = friend_ArrayList;
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendsViewHolder fvh = new FriendsViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position){
        Friend friend = m_friend_ArrayList.get(position);

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
        return m_friend_ArrayList.size();
    }
}
