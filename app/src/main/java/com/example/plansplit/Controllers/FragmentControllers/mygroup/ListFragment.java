package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ToDoListAdapter;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.ToDoList;
import com.example.plansplit.R;

public class ListFragment extends Fragment {
    private static final String TAG = "MyGroupListFragment";
    private static  Database database;
    private RecyclerView recyclerView;
    private ToDoListAdapter toDoListAdapter;
    private Button add_new_reqBt;
    private String personId;
    private String friendkey;
    private String groupkey;
    private String operation;   //Indicates for which part the todolist will be processed
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_list, container, false);
        database = Database.getInstance();

        if(!getArguments().containsKey("group_title")){
            operation="friend";
            friendkey=getArguments().getString("friend_key");
            personId = database.getPerson().getKey();

        }
        if(getArguments().containsKey("group_title")){
             operation="group";
             groupkey=getArguments().getString("group_title");
             personId=database.getPerson().getKey();
        }

        recyclerView = root.findViewById(R.id.req_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                System.out.println("status"+ToDoListAdapter.toDoList.get(viewHolder.getAdapterPosition()).getWho_added_id());

                if (ToDoListAdapter.toDoList.get(viewHolder.getAdapterPosition()).getWho_added_id().equals(personId)) {
                    if (operation.equals("friend")) {
                        database.updateDoListFriend(friendkey, ToDoListAdapter.toDoList.get(viewHolder.getAdapterPosition()).getKey(), "delete", databaseCallBack);
                    } else {
                        database.updateToDoListGroup(groupkey, ToDoListAdapter.toDoList.get(viewHolder.getAdapterPosition()).getKey(), "delete", databaseCallBack);
                    }
                }
                toDoListAdapter.notifyDataSetChanged();
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        add_new_reqBt = root.findViewById(R.id.add_new_req_button);

        add_new_reqBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(root.getContext());
                final View dialog_add_req_view = getLayoutInflater().inflate(R.layout.dialog_add_req, null);
                final EditText add_req_desc = dialog_add_req_view.findViewById(R.id.add_req_descETv);
                final Button mBack = dialog_add_req_view.findViewById(R.id.dialog_list_back_button);
                final Button mSave = dialog_add_req_view.findViewById(R.id.dialog_list_save_button);

                mBuilder.setView(dialog_add_req_view);
                final AlertDialog dialog = mBuilder.create();

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!add_req_desc.getText().toString().isEmpty()) {
                            String text = add_req_desc.getText().toString();
                            ToDoList toDoList=new ToDoList(text,personId);
                            if(operation.equals("friend")){
                                database.addToDoListFriend(friendkey, toDoList, databaseCallBack);
                            }
                            if(operation.equals("group")){
                                System.out.println("grup keyi"+groupkey);
                                System.out.println("personid"+personId);
                                database.addToDoListGroup(groupkey, toDoList, databaseCallBack);
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), getResources().getString(R.string.ask_need), Toast.LENGTH_SHORT).show();
                        }
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
        });

        return root;
    }
//user interface is refreshed
    private void updateUI(String operation) {
        if (operation.equals("friend")) {
            toDoListAdapter = new ToDoListAdapter(getContext(), friendkey, recyclerView, operation);
        }
        if (operation.equals("group")) {
            toDoListAdapter = new ToDoListAdapter(getContext(), groupkey, recyclerView, operation);
        }
    }

    private final Database.DatabaseCallBack databaseCallBack = new Database.DatabaseCallBack() {
        @Override
        public void onSuccess(String success) {
            Log.d(TAG, success);
            updateUI(operation);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(error_tag, error);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(operation);
    }
}