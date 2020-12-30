package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddFriendToGroupAdapter extends RecyclerView.Adapter<AddFriendToGroupAdapter.AddFriendToGroupViewHolder>{

    Context mCtx;
    ArrayList<Friend> friendsList;
    public static ArrayList<Friend> checked_personList = new ArrayList<>();
    private Database database = Database.getInstance();


    public AddFriendToGroupAdapter(Context mCtx, ArrayList<Friend> friendsList) {
        this.mCtx = mCtx;
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public AddFriendToGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addgroups_person, parent, false);
        AddFriendToGroupAdapter.AddFriendToGroupViewHolder viewHolder = new AddFriendToGroupAdapter.AddFriendToGroupViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendToGroupViewHolder holder, int position) {
        final Friend friend = friendsList.get(position);
        holder.Name.setText(friend.getName());
        holder.Mail.setText(friend.getMail());
        Picasso.with(mCtx).load(friend.getPerson_image()).into( holder.addFriendImage);

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class AddFriendToGroupViewHolder extends RecyclerView.ViewHolder {
        public TextView Name;
        public TextView Mail;
        public ImageView addFriendImage;
        public CheckBox checkBox_selected_friends;

        public AddFriendToGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            this.Name = (TextView) itemView.findViewById(R.id.textViewName);
            this.Mail = (TextView) itemView.findViewById(R.id.textViewMail);
            this.addFriendImage = (ImageView) itemView.findViewById(R.id.imageViewAddFriendToGroup);
            this.checkBox_selected_friends = (CheckBox) itemView.findViewById(R.id.checkBox_selected_friend);

        }
    }
}
