package com.example.plansplit.Controllers.FragmentControllers.groups;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ExpensesAdapter;
import com.example.plansplit.Controllers.Adapters.GroupAdapter;
import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Controllers.FragmentControllers.addgroups.AddGroupsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    private static final Database database = Database.getInstance();
    public static ArrayList<Groups> groupsArrayList;
    private static ImageView groupsfilterBtn;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private GroupAdapter.RecyclerViewClickListener mListener;
    private Button add_new_group;
    private String person_id;
    private String selectedFilter;
    private ImageView personImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_groups, container, false);



        final HomeActivity home = (HomeActivity) getContext();
        person_id = home.getPersonId();
        groupsfilterBtn=root.findViewById(R.id.imageViewFilterGroup);

        recyclerView = root.findViewById(R.id.recyclerGroups);
        recyclerView.setHasFixedSize(true);
        setOnClickListener();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        personImage=root.findViewById(R.id.notification_image2);
        Picasso.with(getContext()).load(home.getPersonPhoto()).into(personImage);

        groupsArrayList = new ArrayList<>();

        groupAdapter = new GroupAdapter(this.getContext(), groupsArrayList, mListener);
        recyclerView.setAdapter(groupAdapter);

        groupsfilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(getContext(), groupsfilterBtn);
                //popup.getMenuInflater().inflate(R.menu.date_picker_menu,popup.getMenu());
                popup.inflate(R.menu.filter_menu_groups);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.filter_house:
                                //selectedFilter=getResources().getString(R.string.title_house);
                                selectedFilter="ev";
                                break;
                            case R.id.filter_travel:
                                //selectedFilter=getResources().getString(R.string.title_travel);
                                selectedFilter="seyahat";
                                break;
                            case R.id.filter_business:
                                //selectedFilter=getResources().getString(R.string.title_business);
                                selectedFilter="iş";
                                break;
                            case R.id.filter_others:
                                //selectedFilter=getResources().getString(R.string.title_others);
                                selectedFilter="diğer";
                                break;
                            case R.id.filter_all:
                               // selectedFilter=getResources().getString(R.string.title_all);
                                selectedFilter="hepsi";
                                break;
                        }
                        filterList(selectedFilter);
                        return false;
                    }
                });
                popup.show();

            }
        });

        getGroups();

        add_new_group = root.findViewById(R.id.add_new_group);
        add_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, addGroupsFragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
            }
        });



        return root;

    }

    private void setOnClickListener() {
        mListener = new GroupAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(groupsArrayList.get(position));
                intent.putExtra("group", json);
                startActivity(intent);
            }
        };
    }

    public void getGroups() {
        database.getAllGroups(person_id, groupsArrayList, groupAdapter);
    }

    public void filterList(String filtertype) {
        if (!filtertype.equals(getResources().getString(R.string.title_all))) {
            ArrayList filteredgrouparray = new ArrayList();
            for (Groups groups : groupsArrayList) {
                if (groups.getGroup_type().contains(filtertype)) {
                    filteredgrouparray.add(groups);
                }
            }
            groupAdapter = new GroupAdapter(getContext(), filteredgrouparray, mListener);
            recyclerView.setAdapter(groupAdapter);
        } else {
            groupAdapter = new GroupAdapter(getContext(), groupsArrayList, mListener);
            recyclerView.setAdapter(groupAdapter);
        }
    }




}
