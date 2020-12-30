package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.FragmentControllers.mygroup.GroupOperationsFragment;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupOperationsAdapter extends RecyclerView.Adapter<GroupOperationsAdapter.GroupOperationsViewHolder> {

    private ArrayList<Person> groupOperationsPersonList;
    Context mCtx;

    public GroupOperationsAdapter(ArrayList<Person> operationsPersonList){
        groupOperationsPersonList = operationsPersonList;
    }

    @NonNull
    @Override
    public GroupOperationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View k = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupoperations_person,parent,false);
        GroupOperationsViewHolder asd = new GroupOperationsViewHolder(k);
        return asd;
    }
    @Override
    public void onBindViewHolder(@NonNull GroupOperationsViewHolder holder, int position) {
        Person persons = groupOperationsPersonList.get(position);
        holder.background.setColorFilter(GroupOperationsFragment.colourArrayInteger.get(position));
        Picasso.with(mCtx).load(persons.getPerson_photo()).into(holder.personImage);
        //holder.personImage.setImageResource(persons.getCardView_shareMethodPersonPicture());
        holder.personGroupDepth.setText(persons.getGroupDepth());

    }

    public class GroupOperationsViewHolder extends RecyclerView.ViewHolder{
        public ImageView background;
        public ImageView personImage;
        public TextView  personGroupDepth;
        public GroupOperationsViewHolder(@NonNull View itemView){
            super(itemView);
            background = itemView.findViewById(R.id.personalOperations_PersonBackGround);
            personImage=itemView.findViewById(R.id.personalOperations_imagePerson);
            personGroupDepth=itemView.findViewById(R.id.groupOperations_amount);
        }
    }

    @Override
    public int getItemCount() {
        return groupOperationsPersonList.size();
    }
}