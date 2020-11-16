package com.example.plansplit.ui.notifications;

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
import com.example.plansplit.ui.Addgroups_Person;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private com.example.plansplit.ui.notifications.AddGroupsViewModel AddGroupsViewModel;
    RecyclerView recyclerView;
    AddGroupsAdapter adapter;
    List<Addgroups_Person> AddGroupsPersonList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AddGroupsViewModel =
                ViewModelProviders.of(this).get(AddGroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addgroups, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_addgroups);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        AddGroupsPersonList = new ArrayList<>();

        AddGroupsPersonList.add(new Addgroups_Person("Curie","qwewq",R.drawable.addgroups_ustekran));
        AddGroupsPersonList.add(new Addgroups_Person("Albert","qwewq",R.drawable.addgroups_ustekran));
        AddGroupsPersonList.add(new Addgroups_Person("Benjamin","qwewq",R.drawable.addgroups_ustekran));

        Log.d(TAG, "BURADA");

        adapter = new AddGroupsAdapter(this.getContext(),AddGroupsPersonList);
        recyclerView.setAdapter(adapter);


        return root;
    }
}
