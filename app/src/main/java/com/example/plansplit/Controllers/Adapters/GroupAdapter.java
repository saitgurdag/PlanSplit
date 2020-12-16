package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Groups> groups;
    Context mContx;
    RecyclerViewClickListener mListener;

    public GroupAdapter(Context mContx, ArrayList<Groups> groups, RecyclerViewClickListener mListener) {
        this.mContx = mContx;
        this.groups = groups;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContx).inflate(R.layout.item_groups_red, parent, false);

        GroupAdapter.MyViewHolder myViewHolder0 = new GroupAdapter.MyViewHolder(view);
        return myViewHolder0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder0 = (MyViewHolder) holder;
        viewHolder0.mTitle.setText(groups.get(position).getGroup_name());

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView, mImageView_extra;
        TextView mTitle, mCost;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            this.mImageView = itemView.findViewById(R.id.imageIv);
            this.mImageView_extra = itemView.findViewById(R.id.imageIv_extra);
            this.mTitle = itemView.findViewById(R.id.group_titleTv);
            this.mCost = itemView.findViewById(R.id.costTv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(itemView, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);

    }

}
