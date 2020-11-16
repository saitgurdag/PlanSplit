package com.example.plansplit.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.R;
import com.example.plansplit.Objects.Addgroups_Person;

import java.util.List;

public class AddGroupsAdapter extends RecyclerView.Adapter<AddGroupsAdapter.AddGroupViewHolder> {

    private List<Addgroups_Person> addgroups_personList;
    Context nCtx;
    AddGroupsAdapter.OnItemClickListener nListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(AddGroupsAdapter.OnItemClickListener listener){
        nListener = listener;
    }
    public AddGroupsAdapter(Context nCtx, List<Addgroups_Person> addgroups_personList) {
        this.nCtx=nCtx;
        this.addgroups_personList=addgroups_personList;
    }

    @NonNull
    @Override
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(nCtx).inflate(R.layout.item_addgroups_person,parent,false);
        AddGroupsAdapter.AddGroupViewHolder addgroups_personViewHolder =new AddGroupsAdapter.AddGroupViewHolder(view,nListener);
        return addgroups_personViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
       Addgroups_Person addgroups_person = addgroups_personList.get(position);
       holder.Name.setText(addgroups_person.getName());
        holder.Mail.setText(addgroups_person.getMail());
       holder.cardView_addgroups.setForeground(nCtx.getResources().getDrawable(addgroups_person.getCardView_addgroupsPicture()));
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
