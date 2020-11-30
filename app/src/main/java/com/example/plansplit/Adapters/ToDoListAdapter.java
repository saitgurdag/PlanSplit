package com.example.plansplit.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Objects.ToDoList;
import com.example.plansplit.R;

import java.util.ArrayList;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder> {

    private Context mContext;
    private ArrayList<ToDoList> toDoList;

    public ToDoListAdapter(Context mContext, ArrayList<com.example.plansplit.Objects.ToDoList> toDoList) {
        this.mContext = mContext;
        this.toDoList = toDoList;
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder{
        TextView req_desc, recommender_person, order_status;
        ImageView desc_symbol, status_symbol_icon;
        CardView toDo_Card;

        public ToDoListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.req_desc = itemView.findViewById(R.id.req_descriptionTv);
            this.recommender_person = itemView.findViewById(R.id.recommender_personTv);
            this.order_status = itemView.findViewById(R.id.order_statusTv);
            this.desc_symbol = itemView.findViewById(R.id.desc_symbolIV);
            this.status_symbol_icon = itemView.findViewById(R.id.order_status_icon);
            this.toDo_Card = itemView.findViewById(R.id.req_list_card);
        }
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_req_list, parent,false);

        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListViewHolder holder, final int position) {
        final ToDoList toDo = toDoList.get(position);
        final int waitingImage = R.drawable.bekliyor;
        final int purchaseImage = R.drawable.alacak;
        final String waiting = mContext.getString(R.string.waiting);
        holder.req_desc.setText(toDo.getDescription());
        holder.desc_symbol.setImageResource(toDo.getSymbol());
        holder.order_status.setText(toDo.getStatus());
        if(toDo.getStatus().equals(waiting)){
            holder.status_symbol_icon.setImageResource(waitingImage);
        }else{
            holder.status_symbol_icon.setImageResource(purchaseImage);
        }


        holder.toDo_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toDo.getStatus().equals(waiting)){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                    View take_resp_view = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.dialog_take_resp, null);
                    TextView take_req_descTv = take_resp_view.findViewById(R.id.take_req_descriptionTv);
                    TextView req_question = take_resp_view.findViewById(R.id.req_questionTv);
                    TextView take_recommender = take_resp_view.findViewById(R.id.take_recommender_personTv);
                    TextView take_order_status = take_resp_view.findViewById(R.id.take_order_status_personTv);
                    ImageView take_resp_descIV = take_resp_view.findViewById(R.id.take_desc_symbolIV);
                    ImageView take_order_status_icon = take_resp_view.findViewById(R.id.take_order_status_icon);
                    take_req_descTv.setText(toDo.getDescription());
                    take_resp_descIV.setImageResource(toDo.getSymbol());
                    take_order_status_icon.setImageResource(waitingImage);
                    Button mBack = take_resp_view.findViewById(R.id.req_question_back_button);
                    Button mSave = take_resp_view.findViewById(R.id.req_question_save_button);

                    mBuilder.setView(take_resp_view);
                    final AlertDialog dialog = mBuilder.create();

                    mSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getRootView().getContext(), "Başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    mBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else{
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                    View cancel_resp_view = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.dialog_cancel_req_resp, null);
                    TextView cancel_req_descTv = cancel_resp_view.findViewById(R.id.cancel_req_descriptionTv);
                    TextView cancel_req_question = cancel_resp_view.findViewById(R.id.cancel_req_questionTv);
                    TextView cancel_recommender = cancel_resp_view.findViewById(R.id.cancel_recommender_personTv);
                    TextView cancel_order_status = cancel_resp_view.findViewById(R.id.cancel_order_status_personTv);
                    ImageView cancel_resp_descIV = cancel_resp_view.findViewById(R.id.cancel_desc_symbolIV);
                    ImageView cancel_order_status_icon = cancel_resp_view.findViewById(R.id.cancel_order_status_icon);
                    cancel_req_descTv.setText(toDo.getDescription());
                    cancel_resp_descIV.setImageResource(toDo.getSymbol());
                    cancel_order_status_icon.setImageResource(purchaseImage);

                    Button mBack = cancel_resp_view.findViewById(R.id.cancel_question_back_button);
                    Button mSave = cancel_resp_view.findViewById(R.id.cancel_question_save_button);

                    mBuilder.setView(cancel_resp_view);
                    final AlertDialog dialog = mBuilder.create();

                    mSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getRootView().getContext(), "Başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    mBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }

                Toast.makeText(mContext, "Tıkladığınız ihtiyaç: " + toDo.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }


}
