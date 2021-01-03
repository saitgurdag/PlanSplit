package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.android.gms.common.util.JsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsAdapter extends RecyclerView.Adapter<AddGroupsAdapter.AddGroupViewHolder> {

    private static final String TAG = "AddGroupsAdapter";
    public static ArrayList<Friend> addgroups_personList = new ArrayList<>();
    private ArrayList<Friend> bufferList = new ArrayList<>();
    public static ArrayList<Friend> checked_personList = new ArrayList<>();
    Context mCtx;
    private Database database = Database.getInstance();
    private RecyclerView m_RecyclerView;
    private String id0;
    private String id1;
    private String type;
    private Groups group;
    private static ClickListener clickListener;


    public AddGroupsAdapter(Context mCtx, RecyclerView m_RecyclerView, String type, String... ids) {
        this.mCtx = mCtx;
        this.type = type;
        this.m_RecyclerView = m_RecyclerView;
        this.addgroups_personList = new ArrayList<>();
        addgroups_personList.clear();
        checked_personList.clear();
        bufferList.clear();
        this.id0 = ids[0];
        switch (type) {
            case "add_members":
                this.id1 = ids[1];
                database.getSelectedGroup(null, ids[0], groupCallBack2); // ids[0] groupkey, ids[1] user_key
                break;
            case "new_group":
                database.getFriends(ids[0], friendsCallBack); // ids[0] user_key
                break;
            case "group_members":
                database.getSelectedGroup(null, ids[0], groupCallBack); // ids[0] groupkey
                break;
        }
    }

    private final Database.FriendCallBack friendsCallBack2 = new Database.FriendCallBack() {
        @Override
        public void onFriendRetrieveSuccess(Friend friend) {
            boolean already_exists = false;
            for (Friend friend_obj : bufferList) {
                if (friend_obj.getKey().equals(friend.getKey())) {
                    already_exists = true;
                }
            }
            if (!already_exists) {
                addgroups_personList.add(friend);
                notifyDataSetChanged();
                m_RecyclerView.setAdapter(AddGroupsAdapter.this);
            }


        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    private final Database.GroupCallBack groupCallBack2 = new Database.GroupCallBack() {
        @Override
        public void onGroupRetrieveSuccess(Groups selected_group) {
            group = selected_group;
            ArrayList<Friend> members = new ArrayList<>();
            database.getGroupMembersInfo(selected_group.getGroup_members(), members, MembersCallBack2);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    private final Database.GetMemberInfoCallBack MembersCallBack2 = new Database.GetMemberInfoCallBack() {
        @Override
        public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
            bufferList.clear();
            bufferList.addAll(members);
            database.getFriends(id1, friendsCallBack2);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

//***************

    private final Database.GroupCallBack groupCallBack = new Database.GroupCallBack() {
        @Override
        public void onGroupRetrieveSuccess(Groups selected_group) {
            group = selected_group;
            ArrayList<Friend> members = new ArrayList<>();
            database.getGroupMembersInfo(selected_group.getGroup_members(), members, MembersCallBack);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    private final Database.GetMemberInfoCallBack MembersCallBack = new Database.GetMemberInfoCallBack() {
        @Override
        public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
            addgroups_personList.addAll(members);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(AddGroupsAdapter.this);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    private Database.FriendCallBack friendsCallBack = new Database.FriendCallBack() {
        @Override
        public void onFriendRetrieveSuccess(Friend friend) {
            addgroups_personList.add(friend);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(AddGroupsAdapter.this);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }


    };

    @NonNull
    @Override
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addgroups_person, parent, false);
        AddGroupsAdapter.AddGroupViewHolder fvh = new AddGroupsAdapter.AddGroupViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        final Friend addgroups_person = addgroups_personList.get(position);
        holder.Name.setText(addgroups_person.getName());
        holder.Mail.setText(addgroups_person.getMail());
        Picasso.with(mCtx).load(addgroups_person.getPerson_image()).into(holder.addFriendImage);
    }

    @Override
    public int getItemCount() {
        return addgroups_personList.size();
    }

    public class AddGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView Name;
        public TextView Mail;
        public ImageView addFriendImage;
        public CheckBox checkBox_selected_friends;

        public AddGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.Name = (TextView) itemView.findViewById(R.id.textViewName);
            this.Mail = (TextView) itemView.findViewById(R.id.textViewMail);
            this.addFriendImage = (ImageView) itemView.findViewById(R.id.imageViewAddFriendToGroup);
            this.checkBox_selected_friends = (CheckBox) itemView.findViewById(R.id.checkBox_selected_friend);

            switch (type) {
                case "add_members":
                    checkBox_selected_friends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox checkBox_selected_friends = (CheckBox) view;
                            if (checkBox_selected_friends.isChecked()) {
                                checked_personList.add(addgroups_personList.get(getAdapterPosition()));
                            } else {
                                checked_personList.remove(addgroups_personList.get(getAdapterPosition()));
                            }
                        }
                    });
                    break;
                case "new_group":
                    System.out.println();
                    checkBox_selected_friends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CheckBox checkBox_selected_friends = (CheckBox) view;
                            if (checkBox_selected_friends.isChecked()) {
                                checked_personList.add(addgroups_personList.get(getAdapterPosition()));
                            } else {
                                checked_personList.remove(addgroups_personList.get(getAdapterPosition()));
                            }
                        }
                    });
                    break;
                case "group_members":
                    checkBox_selected_friends.setVisibility(View.INVISIBLE);
                    itemView.setOnClickListener(this);
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AddGroupsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}

