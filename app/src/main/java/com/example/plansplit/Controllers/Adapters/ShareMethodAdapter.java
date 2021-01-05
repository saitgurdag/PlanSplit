package com.example.plansplit.Controllers.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.R;
import com.example.plansplit.Models.Objects.Person;

import java.util.List;

public class ShareMethodAdapter extends RecyclerView.Adapter<ShareMethodAdapter.ShareMethodViewHolder> {

    List<Person> shareMethodPersonList;
    Context kCtx;
    ShareMethodAdapter.OnItemClickListener kListener ;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ShareMethodAdapter.OnItemClickListener listener){
        kListener = listener;
    }

    public ShareMethodAdapter(Context kCtx,List<Person> shareMethodPersonList ){
        this.kCtx = kCtx;
        this.shareMethodPersonList=shareMethodPersonList;
    }


    @NonNull
    @Override
    public ShareMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(kCtx).inflate(R.layout.item_share_method_persons,parent,false);

        ShareMethodAdapter.ShareMethodViewHolder shareMethodViewHolder = new ShareMethodAdapter.ShareMethodViewHolder(view,kListener);

        return shareMethodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShareMethodViewHolder holder, int position) {
    Person shareMethodPerson = shareMethodPersonList.get(position);
    holder.checkButton.setActivated(false);

        holder.Name.setText(shareMethodPerson.getName());
    holder.cardview_shareMethod.setForeground(kCtx.getResources().getDrawable(shareMethodPerson.getCardView_shareMethodPersonPicture()));

    }

    @Override
    public int getItemCount() {
        return shareMethodPersonList.size();
    }

    public class ShareMethodViewHolder extends RecyclerView.ViewHolder{
        public TextView Name;
        public CardView cardview_shareMethod;
        public Button checkButton;
        public ShareMethodViewHolder(@NonNull View itemView, final ShareMethodAdapter.OnItemClickListener kListener) {
            super(itemView);
            this.Name = itemView.findViewById(R.id.textView_share_method_name);
            this.cardview_shareMethod = itemView.findViewById(R.id.cardView_share_method_picture);
            this.checkButton = (CheckBox)itemView.findViewById(R.id.checkbox_share_method) ;

            itemView.findViewById(R.id.cardView_share_method_picture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (kListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            kListener.onItemClick(position);
                        }
                    }
                }
            });

        }

    }

}
