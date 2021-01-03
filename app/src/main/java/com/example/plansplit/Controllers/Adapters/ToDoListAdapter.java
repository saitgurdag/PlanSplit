package com.example.plansplit.Controllers.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Controllers.FragmentControllers.friends.FriendsFragment;
import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.ToDoList;
import com.example.plansplit.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder> {

    //uzun basınca silme diyaloğu
    //tek basmada reserved waiting
    //ikinci basış reserved ve resp sensen harcama ekranına gönder
    //resp sen değilsen uyarı

    private Context mContext;
    public static ArrayList<ToDoList> toDoList;
    private Database database;
    private String key;
    private RecyclerView m_RecyclerView;
    private String operation;
    SharedPreferences mPrefs;
    ItemTouchHelper.SimpleCallback itemtouchhelpercallback;


    public ToDoListAdapter(Context mContext, String key,RecyclerView m_RecyclerView, String operation) {
        this.mPrefs = mContext.getSharedPreferences("listbell", Context.MODE_PRIVATE);
        database = Database.getInstance();
        this.mContext = mContext;
        this.m_RecyclerView=m_RecyclerView;
        this.key = key;
        this.operation=operation;
        if(operation.equals("friend")){
            loadTodosFriend();
        }else{
            loadTodosGroup();
        }
    }

    public void loadTodosFriend(){
            this.toDoList = new ArrayList<>();
            database.gettoDoListFriend(key, todolistCallBack);
    }
    public void loadTodosGroup(){
        this.toDoList = new ArrayList<>();
        database.gettoDoListGroup(key, todolistCallBack);
    }

    private Database.ToDoListCallBack todolistCallBack = new Database.ToDoListCallBack() {
        @Override
        public void onToDoListRetrieveSuccess(ToDoList todo) {
            toDoList.add(todo);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(ToDoListAdapter.this);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(ToDoListAdapter.this);
        }
    };

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

    final Database.DatabaseCallBack databaseCallBack=new Database.DatabaseCallBack() {
        @Override
        public void onSuccess(String success) {
            if(operation.equals("friend")){
                loadTodosFriend();
            }else{
                loadTodosGroup();
            }
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag ,"onError: " );
        }
    };

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_req_list, parent,false);

        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoListViewHolder holder, final int position) {
        final ToDoList toDo = toDoList.get(position);
        final int waitingImage = R.drawable.bekliyor;
        final int purchaseImage = R.drawable.alacak;
        final String waiting = "waiting";
        final String reserved="reserved";
        holder.req_desc.setText(toDo.getDescription());
        holder.desc_symbol.setImageResource(R.drawable.ic_shopping_cart);
        holder.recommender_person.setText(toDo.getWho_added()+" "+mContext.getResources().getString(R.string.added));
        if(toDo.getStatus().equals(waiting)){
            holder.status_symbol_icon.setImageResource(waitingImage);
            holder.order_status.setText(mContext.getResources().getString(R.string.waiting));
        }else{

            holder.status_symbol_icon.setImageResource(purchaseImage);
            holder.order_status.setText(toDo.getResp_person_name()+"\n"+mContext.getResources().getString(R.string.reserved));
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
                    take_recommender.setText(toDo.getWho_added()+" "+mContext.getResources().getString(R.string.added));
                    take_resp_descIV.setImageResource(R.drawable.ic_shopping_cart);
                    take_order_status_icon.setImageResource(waitingImage);
                    Button mBack = take_resp_view.findViewById(R.id.req_question_back_button);
                    Button mSave = take_resp_view.findViewById(R.id.req_question_save_button);

                    mBuilder.setView(take_resp_view);
                    final AlertDialog dialog = mBuilder.create();

                    mSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(operation.equals("friend")){
                                database.updateDoListFriend(key, toDoList.get(holder.getAdapterPosition()).getKey(), "save",databaseCallBack);
                            }else{
                                database.updateDoListGroup(key, toDoList.get(holder.getAdapterPosition()).getKey(),"save",databaseCallBack);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putBoolean("listbell", true);
                                prefsEditor.putString("listbell_key", key);
                                prefsEditor.apply();
                            }
                            dialog.dismiss();
                        }
                    });

                    mBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            prefsEditor.putBoolean("listbell", false);
                            prefsEditor.putString("listbell_key", key);
                            prefsEditor.apply();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } if(toDo.getStatus().equals(reserved)&&(toDo.getResp_person().equals(database.getPerson().getKey()))){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                    View cancel_resp_view = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.dialog_cancel_req_resp, null);
                    TextView cancel_req_descTv = cancel_resp_view.findViewById(R.id.cancel_req_descriptionTv);
                    TextView cancel_req_question = cancel_resp_view.findViewById(R.id.cancel_req_questionTv);
                    TextView cancel_recommender = cancel_resp_view.findViewById(R.id.cancel_recommender_personTv);
                    TextView cancel_order_status = cancel_resp_view.findViewById(R.id.cancel_order_status_personTv);
                    ImageView cancel_resp_descIV = cancel_resp_view.findViewById(R.id.cancel_desc_symbolIV);
                    ImageView cancel_order_status_icon = cancel_resp_view.findViewById(R.id.cancel_order_status_icon);
                    Button cancelBtn=cancel_resp_view.findViewById(R.id.imageViewCancelButton);
                    cancel_req_descTv.setText(toDo.getDescription());
                    cancel_resp_descIV.setImageResource(R.drawable.ic_shopping_cart);
                    cancel_order_status_icon.setImageResource(purchaseImage);
                    cancel_recommender.setText(toDo.getWho_added()+" "+mContext.getResources().getString(R.string.added));
                    cancel_order_status.setText(toDo.getResp_person_name()+"\n"+mContext.getResources().getString(R.string.will_buy));

                    Button mBack = cancel_resp_view.findViewById(R.id.cancel_question_back_button);
                    Button mSave = cancel_resp_view.findViewById(R.id.cancel_question_save_button);

                    mBuilder.setView(cancel_resp_view);
                    final AlertDialog dialog = mBuilder.create();

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    mSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Groups groups2=null;
                            Friend friend2=null;
                          /*  if(operation.equals("friend")) {
                                database.updateDoListFriend(key, toDoList.get(holder.getAdapterPosition()).getKey(), "delete", databaseCallBack);
                            }
                            else{
                                database.updateDoListGroup(key, toDoList.get(holder.getAdapterPosition()).getKey(), "delete", databaseCallBack);
                            }*/
                            //TODO:Harcama ekranına yollanacak ve harcama eklendiği anda yukarıdaki fonksiyonlar çalışacak!!!
                            dialog.dismiss();
                            MyGroupActivity myGroupActivity=new MyGroupActivity();
                            Intent intent = new Intent(view.getContext(), HomeActivity.class);
                            if(operation.equals("group")) {


                                intent.putExtra("group_key_list", key);
                                intent.putExtra("description", toDo.getDescription());
                                intent.putExtra("todo_key", toDo.getKey());
                                for (Groups groups : GroupsFragment.groupsArrayList) {
                                    if (groups.getGroupKey().equals(key)) {
                                        groups2 = groups;

                                    }
                                }
                                Gson gson = new Gson();
                                String json = gson.toJson(groups2);
                                System.out.println(json);
                                intent.putExtra("group_from_list", json);
                            }
                            else{

                                intent.putExtra("description", toDo.getDescription());
                                intent.putExtra("todo_key", toDo.getKey());
                                for (Friend friend : FriendsAdapter.friends) {
                                    if (friend.getKey().equals(key)) {
                                        friend2=friend;
                                    intent.putExtra("friend_key_list", friend.getFriendshipsKey());

                                    }
                                }
                                Gson gson = new Gson();
                                String json = gson.toJson(friend2);
                                System.out.println(json);
                                intent.putExtra("friend_from_list", json);
                            }

                            mContext.startActivity(intent);

                        }
                    });

                    mBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(operation.equals("friend")){
                                database.updateDoListFriend(key, toDoList.get(holder.getAdapterPosition()).getKey(), "cancel",databaseCallBack);
                            }else{
                                database.updateDoListGroup(key, toDoList.get(holder.getAdapterPosition()).getKey(),"cancel",databaseCallBack);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putBoolean("listbell", false);
                                prefsEditor.putString("listbell_key", key);
                                prefsEditor.apply();
                                dialog.dismiss();
                            }
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
                if(toDo.getStatus().equals(reserved)&&!(toDo.getResp_person().equals(database.getPerson().getKey()))){
                    Toast.makeText(mContext,  toDo.getResp_person_name()+" "+mContext.getResources().getString(R.string.already_reserved), Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }


}
