package com.example.plansplit.ui.addgroups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.AddGroupsAdapter;
import com.example.plansplit.R;
import com.example.plansplit.Objects.Person;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private com.example.plansplit.ui.addgroups.AddGroupsViewModel AddGroupsViewModel;
    RecyclerView recyclerView;
    AddGroupsAdapter adapter;
    List<Person> AddGroupsPersonList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AddGroupsViewModel =
                ViewModelProviders.of(this).get(com.example.plansplit.ui.addgroups.AddGroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addgroups, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_addgroups);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        AddGroupsPersonList = new ArrayList<>();

        AddGroupsPersonList.add(new Person("Curie","qwewq",R.drawable.denemeresim));
        AddGroupsPersonList.add(new Person("Albert","qwewq",R.drawable.denemeresim));
        AddGroupsPersonList.add(new Person("Benjamin","qwewq",R.drawable.denemeresim));

        Log.d(TAG, "BURADA");

        adapter = new AddGroupsAdapter(this.getContext(),AddGroupsPersonList);
        recyclerView.setAdapter(adapter);


        return root;
    }
}
