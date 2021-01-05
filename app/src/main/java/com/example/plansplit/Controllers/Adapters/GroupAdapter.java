package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Groups> groups = new ArrayList<>();
    Context mContx;
    RecyclerViewClickListener mListener;
    private String group_type_option_home = "ev";
    private String group_type_option_work = "iş";
    private String group_type_option_trip = "seyahat";
    private String group_type_option_other = "diğer";
    int homePicture, workPicture, tripPicture, otherPicture;
    SharedPreferences mPrefs;
    String userId;
    float totDebt=0;

    public void setTotDebt(float totDebt) {
        this.totDebt = totDebt;
    }

    Fragment fragment;

    public ArrayList<Groups> getGroups() {
        return groups;
    }

    public GroupAdapter(Context mContx, ArrayList<Groups> groups, RecyclerViewClickListener mListener, String userId, Fragment fragment) {
        mPrefs =mContx.getSharedPreferences("listbell", Context.MODE_PRIVATE);
        this.fragment=fragment;
        this.mContx = mContx;
        this.groups = groups;
        this.mListener = mListener;
        this.userId=userId;
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
        viewHolder0.mTitle.setTextColor(mContx.getResources().getColor(R.color.black));
        viewHolder0.mTitle.setText(groups.get(position).getGroup_name());
        totDebt+=groups.get(position).getTotDebt();
        ((GroupsFragment)fragment).setTotalDebt(totDebt);

        if(groups.get(position).getTotDebt()>0){
            viewHolder0.layout.setBackgroundResource(R.drawable.itemview_bg_border_red);
            viewHolder0.mCost.setTextColor(mContx.getResources().getColor(R.color.red));
            viewHolder0.mGroupIcon.setBackgroundResource(R.drawable.group_itemview_bg_profil_red);

        }else{
            viewHolder0.layout.setBackgroundResource(R.drawable.itemview_bg_border_green);
            viewHolder0.mCost.setTextColor(mContx.getResources().getColor(R.color.brightGreen));
            viewHolder0.mGroupIcon.setBackgroundResource(R.drawable.group_itemview_bg_profil_green);
        }

        homePicture = R.drawable.ic_home_black_radius;
        workPicture = R.drawable.ic_suitcase_radius;
        tripPicture = R.drawable.ic_trip_radius;
        otherPicture = R.drawable.ic_other;
        String k=mPrefs.getString("listbell_key","");
        boolean c=mPrefs.getBoolean("listbell",false);
        if(groups.get(position).getGroupKey().equals(k)){
            if(c==true){
                ((MyViewHolder) holder).mImageView_extra.setVisibility(View.VISIBLE);
            }
            else{
                ((MyViewHolder) holder).mImageView_extra.setVisibility(View.INVISIBLE);
            }

        }
        String group_type = groups.get(position).getGroup_type();
        if(group_type.equals(group_type_option_home)){
            viewHolder0.mGroupIcon.setImageResource(homePicture);
        } else if(group_type.equals(group_type_option_work)){
            viewHolder0.mGroupIcon.setImageResource(workPicture);
        } else if(group_type.equals(group_type_option_trip)){
            viewHolder0.mGroupIcon.setImageResource(tripPicture);
        } else if(group_type.equals(group_type_option_other)){
            viewHolder0.mGroupIcon.setImageResource(otherPicture);
        }

        viewHolder0.mCost.setText(groups.get(position).getTotDebt() + " TL");
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mGroupIcon, mImageView_extra;
        TextView mTitle, mCost;
        RelativeLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mGroupIcon = itemView.findViewById(R.id.imageIv);
            this.layout = itemView.findViewById(R.id.layout);
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
