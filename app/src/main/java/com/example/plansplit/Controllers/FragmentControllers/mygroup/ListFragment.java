package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.plansplit.Controllers.Adapters.ToDoListAdapter;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.Models.Objects.ToDoList;
import com.example.plansplit.R;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private static final String TAG = "MyGroupListFragment";
    private static  Database database;
    private RecyclerView recyclerView;
    private ArrayList<ToDoList> toDoList;
    private ArrayList<Groups> theGroup;
    private ArrayList<Person> resp_person;
    private ToDoListAdapter toDoListAdapter;
    private Button add_new_reqBt;
    private String personId;
    private String friendkey;
    private String operation;
    RecyclerView.LayoutManager layoutManager;



    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_list, container, false);
        database=new Database(getContext());
        toDoList = new ArrayList<>();

        if(!getArguments().containsKey("group_title")){
            operation="friend";
            friendkey=getArguments().getString("friend_key");
            personId=getArguments().getString("person_key");

        }
        if(getArguments().containsKey("group_title")){
             operation="group";
        }
        updateUI(operation);

        /*recyclerView = root.findViewById(R.id.req_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));*/

        recyclerView = root.findViewById(R.id.req_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);






        add_new_reqBt = root.findViewById(R.id.add_new_req_button);

        add_new_reqBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(root.getContext());
                final View dialog_add_req_view = getLayoutInflater().inflate(R.layout.dialog_add_req, null);
                final EditText add_req_desc = (EditText) dialog_add_req_view.findViewById(R.id.add_req_descETv);
                final Button mBack = (Button) dialog_add_req_view.findViewById(R.id.dialog_list_back_button);
                final Button mSave = (Button) dialog_add_req_view.findViewById(R.id.dialog_list_save_button);

                mBuilder.setView(dialog_add_req_view);
                final AlertDialog dialog = mBuilder.create();


                    //Toast.makeText(getContext(), getArguments().getString("group_title"), Toast.LENGTH_SHORT).show();

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!add_req_desc.getText().toString().isEmpty()) {
                            String text = add_req_desc.getText().toString();
                            ToDoList toDoList=new ToDoList(text,personId);
                            if(operation.equals("friend")){
                                database.addtoDoListFriend(friendkey, toDoList, databaseCallBack);
                            }
                            if(operation.equals("group")){
                                Toast.makeText(getContext(), "Gruba ekleme yapılacak", Toast.LENGTH_SHORT).show();
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

    private void updateUI(String operation) {
        if (operation.equals("friend")) {
            database.gettoDoListFriend(friendkey, new Database.ToDoListCallBack() {
                @Override
                public void onToDoListRetrieveSuccess(ToDoList todo) {
                    System.out.println(todo.getDescription());
                    toDoListAdapter = new ToDoListAdapter(getContext(), friendkey, recyclerView);
                    recyclerView.setAdapter(toDoListAdapter);
                }

                @Override
                public void onError(String error_tag, String error) {
                    Log.e(error_tag, error);
                }
            });
        }
        if (operation.equals("group")) {
            System.out.println("operasyon var bnu gece");
        }
        }


    private final Database.DatabaseCallBack databaseCallBack = new Database.DatabaseCallBack() {
        @Override
        public void onSuccess(String success) {
            Log.d(TAG, success);
            updateUI(operation);
            Toast.makeText(getContext(),   " başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
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
        if( operation.equals("friend")) {

            toDoListAdapter = new ToDoListAdapter(getContext(), friendkey, recyclerView);
        }


    }

}