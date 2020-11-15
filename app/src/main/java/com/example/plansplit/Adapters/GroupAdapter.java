package com.example.plansplit.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Groups;
import com.example.plansplit.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int type_red = 0;
    final int type_green = 2;

    private ArrayList<Groups> groups;
    OnItemClickListener mListener;

    public GroupAdapter(ArrayList<Groups> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups_red, parent, false);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groups_green, parent, false);

        switch (viewType) {
            case 0:
                GroupAdapter.MyViewHolder myViewHolder0 = new GroupAdapter.MyViewHolder(view, mListener);
                return myViewHolder0;
            case 2:
                GroupAdapter.MyViewHolder2 myViewHolder2 = new GroupAdapter.MyViewHolder2(view2, mListener);
                return myViewHolder2;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                MyViewHolder viewHolder0 = (MyViewHolder) holder;
                viewHolder0.mTitle.setText(groups.get(position).getGroup_name());
                viewHolder0.mCost.setText(groups.get(position).getGroup_balance_sheet());
                viewHolder0.mImageView.setImageResource(groups.get(position).getGroup_photo());
                viewHolder0.mImageView_extra.setImageResource(groups.get(position).getGroup_notification_symbol());
                break;

            case 2:
                MyViewHolder2 viewHolder2 = (MyViewHolder2) holder;
                viewHolder2.mTitle.setText(groups.get(position).getGroup_name());
                viewHolder2.mCost.setText(groups.get(position).getGroup_balance_sheet());
                viewHolder2.mImageView.setImageResource(groups.get(position).getGroup_photo());
                viewHolder2.mImageView_extra.setImageResource(groups.get(position).getGroup_notification_symbol());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(GroupAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if(groups.get(position).getGroup_id() % 2 == 0){
            return type_red;
        }
        return type_green;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView, mImageView_extra;
        TextView mTitle, mCost;

        public MyViewHolder(@NonNull View itemView, final GroupAdapter.OnItemClickListener listener){

            super(itemView);

            this.mImageView = itemView.findViewById(R.id.imageIv);
            this.mImageView_extra = itemView.findViewById(R.id.imageIv_extra);
            this.mTitle = itemView.findViewById(R.id.group_titleTv);
            this.mCost = itemView.findViewById(R.id.costTv);

            itemView.findViewById(R.id.card_group).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public class MyViewHolder2 extends RecyclerView.ViewHolder{

        ImageView mImageView, mImageView_extra;
        TextView mTitle, mCost;

        public MyViewHolder2(@NonNull View itemView, final GroupAdapter.OnItemClickListener listener) {

            super(itemView);

            this.mImageView = itemView.findViewById(R.id.imageIv);
            this.mImageView_extra = itemView.findViewById(R.id.imageIv_extra);
            this.mTitle = itemView.findViewById(R.id.group_titleTv);
            this.mCost = itemView.findViewById(R.id.costTv);

            itemView.findViewById(R.id.card_group_green).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

}
