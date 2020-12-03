package com.example.plansplit.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.GroupAdapter;
import com.example.plansplit.MyGroup;
import com.example.plansplit.Objects.Groups;
import com.example.plansplit.R;
import com.example.plansplit.ui.addgroups.AddGroupsFragment;
import com.example.plansplit.ui.mygroup.EventsFragment;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    private GroupsViewModel groupsViewModel;
    private ArrayList<Groups> groups;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private GroupAdapter.RecyclerViewClickListener mListener;
    private ImageView add_expense;
    private Button add_new_group;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        groupsViewModel = ViewModelProviders.of(this).get(GroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_groups, container, false);

        recyclerView = root.findViewById(R.id.recyclerGroups);
        recyclerView.setHasFixedSize(true);
        setOnClickListener();
        groupAdapter = new GroupAdapter(groups, mListener);
        recyclerView.setAdapter(groupAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        groups = new ArrayList<>();

        groups.add(new Groups(1,"Ev","Ev",R.drawable.ic_home_black_radius,R.drawable.debt_remind,10));
        groups.add(new Groups(2,"İŞ","İş",R.drawable.ic_suitcase_radius,R.drawable.debt_remind,20));
        groups.add(new Groups(4,"Ev","Ev",R.drawable.ic_home_page,R.drawable.debt_remind,30));
        groups.add(new Groups(3,"Ev","Ev",R.drawable.ic_home_page,R.drawable.debt_remind,30));
        groups.add(new Groups(5,"Ev","Ev",R.drawable.ic_home_page,R.drawable.debt_remind,30));
        groups.add(new Groups(6,"Ev","Ev",R.drawable.ic_home_page,R.drawable.debt_remind,30));
        groups.add(new Groups(7,"Ev","Ev",R.drawable.ic_home_page,R.drawable.debt_remind,30));

        Log.d(TAG, "BURADA");

        groupAdapter = new GroupAdapter(groups, mListener);
        recyclerView.setAdapter(groupAdapter);

        add_expense=root.findViewById(R.id.add_expense);
        add_new_group=root.findViewById(R.id.add_new_group);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupExpenseFragment expenseFragment = new GroupExpenseFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, expenseFragment);
                fragmentTransaction.commit();
            }
        });

        add_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, addGroupsFragment);
                fragmentTransaction.commit();
            }
        });



        return root;
    }

    private void setOnClickListener() {
        mListener = new GroupAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getParentFragment().getContext(), MyGroup.class);
                intent.putExtra("group_title", groups.get(position).getGroup_name());
                intent.putExtra("group_image", groups.get(position).getGroup_photo());
                startActivity(intent);
            }
        };
    }
}
