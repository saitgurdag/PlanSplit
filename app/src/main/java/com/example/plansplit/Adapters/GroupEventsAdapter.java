package com.example.plansplit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.plansplit.Objects.Transfers;
import com.example.plansplit.R;

import java.util.ArrayList;

public class GroupEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Transfers> transfersList;

    public GroupEventsAdapter(ArrayList<Transfers> transfers) {
        this.transfersList = transfers;
    }

    @Override
    public int getItemViewType(int position) {
        System.out.println("POZİSYON"+position);
        if(transfersList.get(position).getType()==0){
           return 0;
       }else return 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 0= Object item , 1 = spend item      mantık şu item typ atadım 0 ve 1 olarak gelen type a göre view holder seçtirdim daha sonra sayfaya yeni item vs eklemek gerekirse type 2
        if (viewType == 0) {                    // oluşturup bu adaptörle rahatça hallederiz kolaylık sağlar bu yöntem
            return new ViewHolderGroupObject(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupevents_object, parent, false));
        }else return new GroupEventsViewHolderPayments(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupevents_payments,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transfers transfers= transfersList.get(position);

        if(getItemViewType(position)==0){                                                        //gelen viewtype 0 yani obje itemı gelmiş ise
            ViewHolderGroupObject viewHolderGroupObject =(ViewHolderGroupObject) holder;
            viewHolderGroupObject.object_image.setImageResource(transfers.getGroupEvents_object_image());
            viewHolderGroupObject.object_name.setText(transfers.getGroupEvents_object_objectName());
            viewHolderGroupObject.object_payer_name.setText(transfers.getGroupEvents_object_payerName());
            viewHolderGroupObject.object_payer_payAmount.setText(transfers.getGroupEvents_object_payAmounth());

            viewHolderGroupObject.object_depthamount.setText(transfers.getGroupEvents_object_depthAmount());
            viewHolderGroupObject.object_depthamount.setTextColor(ContextCompat                                      //borçlu isen kırmızı olcak renk seçio
                    .getColor(holder.itemView.getContext(),transfers.getColor()));

            viewHolderGroupObject.object_depthtatus.setText(transfers.getGroupEvents_object_depthStatus());
                viewHolderGroupObject.object_depthtatus.setTextColor(ContextCompat                                       //borçlu - alacaklı yazısı rengini seçiyor
                        .getColor(holder.itemView.getContext(),transfers.getColor()));

                viewHolderGroupObject.object_depthamount.setTextColor(ContextCompat
                    .getColor(holder.itemView.getContext(),transfers.getColor()));
                //payorPayed
            viewHolderGroupObject.object_payOrPayed.setText(transfers.getObject_payOrPayed());

        }else {                                                                                                     // gelen view type 1 yani payments itemi ise
            System.out.println("ELSE E GİRDİ");
            //bin view holder2
            GroupEventsViewHolderPayments groupEventsViewHolderPayments = (GroupEventsViewHolderPayments) holder;
            groupEventsViewHolderPayments.payment_money_image.setImageResource(transfers.getGroupEvents_payment_money_image());
            groupEventsViewHolderPayments.payment_userOne.setText(transfers.getGroupEvents_payments_payerName());
            groupEventsViewHolderPayments.payment_userTwo.setText(transfers.getGroupEvents_payments_payedName());
            groupEventsViewHolderPayments.payment_user_depth_amount.setText(transfers.getGroupEvents_payments_payAmount());
            groupEventsViewHolderPayments.payment_payments_payOrPayed.setText(transfers.getGroupEvents_payment_payOrpayed());
        }
    }

    @Override
    public int getItemCount() {
        System.out.println("HATA BULMA"+transfersList.size());
        return transfersList.size();
    }

    class ViewHolderGroupObject extends RecyclerView.ViewHolder {
        public ImageView object_image;
        public TextView object_name;
        public TextView object_payer_name;
        public TextView object_payer_payAmount;
        // public TextView object_payStatus;
        public TextView object_depthamount;
        public TextView object_depthtatus;
        public  TextView object_payOrPayed;
        public ViewHolderGroupObject(@NonNull View itemView) {
            super(itemView);
            object_image=itemView.findViewById(R.id.group_events_object_image);
            object_name=itemView.findViewById(R.id.groups_events_object_name);
            object_payer_name=itemView.findViewById(R.id.group_events_object_payer_name);
            object_payer_payAmount=itemView.findViewById(R.id.group_events_object_coast);
            object_payOrPayed=itemView.findViewById(R.id.group_events_object_payOrpayed_Text);
            object_depthamount=itemView.findViewById(R.id.group_events_object_user_depth);
            object_depthtatus = itemView.findViewById(R.id.group_events_object_user_depth_status);
        }
    }
    class GroupEventsViewHolderPayments extends ViewHolder {
        public ImageView payment_money_image;
        public TextView payment_userOne;
        public TextView payment_userTwo;
        public TextView payment_user_depth_amount;
        public TextView payment_payments_payOrPayed;
        public GroupEventsViewHolderPayments(@NonNull View itemView) {
            super(itemView);
            payment_money_image=itemView.findViewById(R.id.group_events_payment_money_image);
            payment_userOne=itemView.findViewById(R.id.groups_activity_payment_userone_text);
            payment_userTwo=itemView.findViewById(R.id.groups_activity_payment_usertwo_text);
            payment_user_depth_amount=itemView.findViewById(R.id.groups_activity_payment_user_depthAmounth_text);
            payment_payments_payOrPayed=itemView.findViewById(R.id.groups_activity_payment_user_payOrPayed_Text);
        }
    }
}
