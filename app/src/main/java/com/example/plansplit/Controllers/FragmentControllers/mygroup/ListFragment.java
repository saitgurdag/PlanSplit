package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.plansplit.Controllers.Adapters.ToDoListAdapter;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.Models.Objects.ToDoList;
import com.example.plansplit.R;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private static final String TAG = "MyGroupListFragment";
    private RecyclerView recyclerView;
    private ArrayList<ToDoList> toDoList;
    private ArrayList<Groups> theGroup;
    private ArrayList<Person> resp_person;
    private ToDoListAdapter toDoListAdapter;
    private Button add_new_reqBt;


    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = root.findViewById(R.id.req_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        toDoList = new ArrayList<>();

        Person p1 = new Person("Einstein");
        Person p2 = new Person("Curie");

        Groups g1 = new Groups(1,"Ev","Ev",R.drawable.ic_home_black_radius,R.drawable.debt_remind,10);

        toDoList.add(new ToDoList("Su","Bekliyor", R.drawable.ic_shopping_cart));
        toDoList.add(new ToDoList("Zeytin Yağı", "Bekliyor", R.drawable.ic_shopping_cart));
        toDoList.add(new ToDoList("Sabun", "Einstein Alacak", R.drawable.ic_shopping_cart));
        toDoList.add(new ToDoList("Peynir", "Tesla Alacak", R.drawable.ic_shopping_cart));

        toDoListAdapter = new ToDoListAdapter(this.getContext(), toDoList);
        recyclerView.setAdapter(toDoListAdapter);

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

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!add_req_desc.getText().toString().isEmpty()) {
                            String text = add_req_desc.getText().toString();
                            Toast.makeText(getContext(), text + " başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Lütfen bir ihtiyaç giriniz", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}