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
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddGroupsAdapter extends RecyclerView.Adapter<AddGroupsAdapter.AddGroupViewHolder> {

    public static ArrayList<Friend> addgroups_personList;
    public static ArrayList<Friend> checked_personList = new ArrayList<>();
    ArrayList<Friend> group_members_objects;
    Context mCtx;
    AddGroupsAdapter.OnItemClickListener nListener;
    ItemClickListener mListener;
    private Database database = Database.getInstance();
    private String person_id;
    private RecyclerView m_RecyclerView;
    private boolean addGroup;

    public interface ItemClickListener {
        void OnItemClick(View v, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AddGroupsAdapter.OnItemClickListener listener) {
        nListener = listener;
    }

    public AddGroupsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView) {
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.addgroups_personList = new ArrayList<>();
        database.getFriends(person_id, friendsCallBack);
        checked_personList = new ArrayList<>();
        addGroup = true;
    }

    public AddGroupsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView, ArrayList<String> group_members, ItemClickListener mListener) {
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.addgroups_personList = new ArrayList<>();
        getGroupMembers(group_members);
        this.mListener = mListener;
        addGroup = false;
    }

    public AddGroupsAdapter(Context mCtx, String person_id, ArrayList<String> group_members, RecyclerView m_RecyclerView) { //Gruba yeni arkadaş eklemek için
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.addgroups_personList = new ArrayList<>();
        getOtherFriends(group_members, person_id);
        addGroup = true;
    }

    private void getOtherFriends(final ArrayList<String> group_members, String person_id) {
        Database.FriendCallBack friendsCallback_toadd = new Database.FriendCallBack() {
            @Override
            public void onFriendRetrieveSuccess(Friend friend) {
                String friendkey = friend.getKey();
                if (!group_members.contains(friendkey)) {
                    addgroups_personList.add(friend);
                }

                notifyDataSetChanged();
                m_RecyclerView.setAdapter(AddGroupsAdapter.this);
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(error_tag, error);
            }
        };

        database.getFriends(person_id, friendsCallback_toadd);

    }

    private void getGroupMembers(ArrayList<String> group_members) {
        for (String id : group_members) {
            database.getFriend(id, friendsCallBack);
        }
    }


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
        AddGroupsAdapter.AddGroupViewHolder fvh = new AddGroupsAdapter.AddGroupViewHolder(v, nListener);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        final Friend addgroups_person = addgroups_personList.get(position);
        holder.Name.setText(addgroups_person.getName());
        holder.Mail.setText(addgroups_person.getMail());
        Picasso.with(mCtx).load(addgroups_person.getPerson_image()).into(holder.addFriendImage);
        //holder.addFriendImage.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.alacak));
        if (!addGroup) {
            holder.checkBox_selected_friends.setVisibility(View.INVISIBLE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                if (addGroup) {
                    CheckBox checkBox_selected_friends = (CheckBox) v;
                    if (checkBox_selected_friends.isChecked()) {
                        checked_personList.add(addgroups_personList.get(position));
                    } else {
                        checked_personList.remove(addgroups_personList.get(position));
                    }
                }
            }
        });


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
        ItemClickListener itemClickListener;

        public AddGroupViewHolder(@NonNull View itemView, final AddGroupsAdapter.OnItemClickListener nListener) {
            super(itemView);
            this.Name = (TextView) itemView.findViewById(R.id.textViewName);
            this.Mail = (TextView) itemView.findViewById(R.id.textViewMail);
            this.addFriendImage = (ImageView) itemView.findViewById(R.id.imageViewAddFriendToGroup);
            this.checkBox_selected_friends = (CheckBox) itemView.findViewById(R.id.checkBox_selected_friend);
            checkBox_selected_friends.setOnClickListener(this);
            if (!addGroup) {
                itemView.setOnClickListener(this);
            }

            itemView.findViewById(R.id.cardView_addgroupsPicture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            nListener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.OnItemClick(view, getLayoutPosition());
            if (!addGroup) {
                mListener.OnItemClick(itemView, getAdapterPosition());
            }

        }


    }
}
