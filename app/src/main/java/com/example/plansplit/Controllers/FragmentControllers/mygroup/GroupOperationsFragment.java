package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.GroupOperationsAdapter;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.JsonUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GroupOperationsFragment extends Fragment {

    RecyclerView recyclerView;
    GroupOperationsAdapter adapter;
    ArrayList<Person> groupOperationsPersonList = new ArrayList<>();
    ArrayList<Friend> group_members_allstar = new ArrayList<>();
    ArrayList<Expense> group_expenses = new ArrayList<>();

    private static final Database database = Database.getInstance();

    public static ArrayList<Integer> colourArrayInteger = new ArrayList<>();

    Bundle extras;
    Groups group;


    public static GroupOperationsFragment newInstance() {
        return new GroupOperationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_group_operations, container, false);

        recyclerView = root.findViewById(R.id.recyclerGroupOperations);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);


        extras = getArguments();
        Gson gson = new Gson();
        String json = extras.getString("group");
        group = gson.fromJson(json, Groups.class);
        String groupkey = group.getKey();
        System.out.println(groupkey);

        database.getSelectedGroup(groupkey, new Database.GroupCallBack() {
            @Override
            public void onGroupRetrieveSuccess(Groups selected_group) {
                group = selected_group;
                group_expenses = new ArrayList<>(group.expenses.values());
                database.getGroupMembersInfo(group.getGroup_members(), group_members_allstar, new Database.GetMemberInfoCallBack() {
                    @Override
                    public void onGetMemberInfoRetrieveSuccess(ArrayList<Friend> members) {
                        ArrayList<Friend> group_members = new ArrayList<>(members);
                        for(Friend friend: group_members){
                            groupOperationsPersonList.add(new Person(friend.getKey(), friend.getPerson_image().toString(), friend.getName()));
                        }
                        int total_expense = 0;
                        ArrayList<String> addedByIdOldList = new ArrayList<>();
                        for (int i = 0; i < group_expenses.size(); i++) {
                            String addedById = group_expenses.get(i).getAddedById();
                            if (!addedByIdOldList.contains(addedById)) {
                                int total_expense_of_member = 0;
                                for (int j = 0; j < group_expenses.size(); j++) {
                                    if (group_expenses.get(j).getAddedById().equals(addedById)) {
                                        total_expense_of_member += Integer.parseInt(group_expenses.get(j).getPrice());
                                    }
                                }
                                for(Person person: groupOperationsPersonList){
                                    String person_id = person.getPerson_id();
                                    if(person_id.equals(addedById)){
                                        person.setTotal_expense(String.valueOf(total_expense_of_member));
                                    }
                                }
                                total_expense += total_expense_of_member;
                                addedByIdOldList.add(addedById);
                            }
                        }


                        adapter = new GroupOperationsAdapter(getContext(), groupOperationsPersonList);
                        recyclerView.setAdapter(adapter);

                        ArrayList<PieEntry> depthOfPerson = new ArrayList<>();

                        //Pie Chart girdileri
                        int total_expense_current_members = 0;
                        for (int i = 0; i < groupOperationsPersonList.size(); i++) {
                            float expense = Integer.parseInt(groupOperationsPersonList.get(i).getTotal_expense());
                            total_expense_current_members += expense;
                            if (expense > 0){
                                depthOfPerson.add(new PieEntry(expense, groupOperationsPersonList.get(i).getName()));
                            }else{
                                depthOfPerson.add(new PieEntry(0, ""));
                            }
                        }
                        if (total_expense_current_members < total_expense){
                            float expense = total_expense - total_expense_current_members;
                            depthOfPerson.add(new PieEntry(expense, getString(R.string.other)));
                        }


                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
                        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
                        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
                        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
                        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);

                        PieChart pieChart = root.findViewById(R.id.pieChart);
                        PieDataSet pieDataSet = new PieDataSet(depthOfPerson, "");
                        pieChart.setHoleRadius(65);
                        pieDataSet.setValueTextColor(Color.BLACK);
                        pieDataSet.setColors(colors);

                        // Dilimlerin renklerini int olarak alıp bir arraye attımki kişilerin arka planı yapayım
                        colourArrayInteger.addAll(pieDataSet.getColors());

                        pieDataSet.setValueTextSize(13f);

                        PieData pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.getLegend().setEnabled(false);          //Daire dilimi dışındaki şeyleri seildik description boxları
                        pieChart.setCenterText(total_expense + " TL");
                        pieChart.setCenterTextSize(20f);
                        pieChart.setEntryLabelColor(Color.BLACK);
                        pieChart.invalidate();
                        pieChart.animate();
                    }

                    @Override
                    public void onError(String error_tag, String error) {
                        Log.e(error_tag, error);
                    }
                });
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(error_tag, error);
            }
        });
        return root;


    }


}