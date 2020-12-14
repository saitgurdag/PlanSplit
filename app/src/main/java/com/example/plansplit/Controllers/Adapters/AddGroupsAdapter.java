package com.example.plansplit.Controllers.Adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.example.plansplit.Models.Objects.Person;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsAdapter extends RecyclerView.Adapter<AddGroupsAdapter.AddGroupViewHolder> {

    private List<Friend> addgroups_personList;
    Context mCtx;
    AddGroupsAdapter.OnItemClickListener nListener;
    private static final String TAG = "FriendsAdapter";
    private Database database = Database.getInstance();
    private String person_id;
    private RecyclerView m_RecyclerView;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(AddGroupsAdapter.OnItemClickListener listener){
        nListener = listener;
    }

    public AddGroupsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.addgroups_personList = new ArrayList<>();
        database.getFriends(person_id, friendsCallBack);
    }

    private Database.FireBaseFriendCallBack friendsCallBack = new Database.FireBaseFriendCallBack() {
        @Override
        public void onFriendRetrieveSuccess(Friend friend) {
            addgroups_personList.add(friend);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(AddGroupsAdapter.this);
        }

        @Override
        public void onEmptyListError() {
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(AddGroupsAdapter.this);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
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
        Friend addgroups_person = addgroups_personList.get(position);
        holder.Name.setText(addgroups_person.getName());
        holder.Mail.setText(addgroups_person.getMail());
        holder.cardView_addgroups.setForeground(mCtx.getResources().getDrawable(R.drawable.denemeresim));
    }

    @Override
    public int getItemCount() {
        return addgroups_personList.size();
    }

    public class AddGroupViewHolder extends RecyclerView.ViewHolder {
        public TextView Name;
        public TextView Mail;
        public CardView cardView_addgroups;

        public AddGroupViewHolder(@NonNull View itemView, final AddGroupsAdapter.OnItemClickListener nListener) {
            super(itemView);
            this.Name = (TextView) itemView.findViewById(R.id.textViewName);
            this.Mail = (TextView) itemView.findViewById(R.id.textViewMail);
            this.cardView_addgroups = (CardView) itemView.findViewById(R.id.cardView_addgroupsPicture);
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
    }
}
