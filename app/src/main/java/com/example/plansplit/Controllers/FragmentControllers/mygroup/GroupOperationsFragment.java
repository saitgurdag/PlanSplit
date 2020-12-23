package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.GroupEventsAdapter;
import com.example.plansplit.Controllers.Adapters.GroupOperationsAdapter;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.Models.Objects.Transfers;
import com.example.plansplit.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class GroupOperationsFragment extends Fragment {
    //BERKAY EKLEME KISMI

    RecyclerView recyclerView;
    GroupOperationsAdapter adapter;
    ArrayList<Person> groupOperationsPersonList;

    ArrayList<Integer> depthInInteger = new ArrayList();
    public static ArrayList<Integer> colourArrayInteger = new ArrayList<>();

    int totalDepth;
    int background;
    //BERKAY EKLEME END

    public static GroupOperationsFragment newInstance() {
        return new GroupOperationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //BERKAY EKLEME KISMI

        View root = inflater.inflate(R.layout.fragment_group_operations,container,false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerGroupOperations);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);

        groupOperationsPersonList = new ArrayList<>();


        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"100"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"300"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"400"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"500"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"1000"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"700"));
        groupOperationsPersonList.add(new Person(background,R.drawable.denemeresim,"1200"));


        recyclerView = root.findViewById(R.id.recyclerGroupOperations);
        recyclerView.setHasFixedSize(true);
        adapter =new GroupOperationsAdapter(groupOperationsPersonList);

        recyclerView.setAdapter(adapter);
        yazdir(groupOperationsPersonList);
        totalDepth(depthInInteger);

        System.out.println("TOPLAM BORC : "+totalDepth);

        //PİE CHART BAŞLA
        PieChart pieChart = root.findViewById(R.id.pieChart);
        ArrayList<PieEntry> depthOfPerson = new ArrayList<>();

        for (int i = 0;i<groupOperationsPersonList.size();i++){               //Pie Chart girdileri
            depthOfPerson.add(new PieEntry(depthInInteger.get(i),""));
        }


        //Pie Chard Daki RENK OLAYI BAŞLANGIÇ

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        //PİE CHART TAKİ RENK OLAYI BİTİŞ

        PieDataSet pieDataSet = new PieDataSet(depthOfPerson,"");

        pieChart.setHoleRadius(65);
        pieDataSet.setValueTextColor(Color.BLACK);


        pieDataSet.setColors(colors);


        for (int i = 0;i<pieDataSet.getColors().size();i++){   // Dilimlerin renklerini int olarak alıp bir arraye attımki kişilerin arka planı yapayım
            int k = pieDataSet.getColors().get(i);
            colourArrayInteger.add(k);
        }



        pieDataSet.setValueTextSize(13f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);          //Daire dilimi dışındaki şeyleri seildik description boxları
        pieChart.setCenterText(totalDepth+" TL");
        pieChart.setCenterTextSize(20f);
        pieChart.animate();
        //PİE CHART BİTİŞ

        return root;

        //BERKAY EKLEME KISMI BİTİŞ
    }

    //Daire Grafiği başla
    public ArrayList getAllValues(ArrayList<Person> l){       // Recycler View daki kişilerin borcunu depthinString Arrayine atıyor
        ArrayList  <String> depthInString = new ArrayList<>();
        for(int i = 0; i < l.size() ; i++ ){
            depthInString.add(l.get(i).getGroupDepth());
        }
        return depthInString;
    }

    public void yazdir(ArrayList l){                           //Depth in string deki String olarak tutulan borcu int e çavirip k ya eşitliyor
        for(int i =0 ; i<getAllValues(l).size() ; i++ ){
            int k =  Integer.parseInt((String) getAllValues(l).get(i));

            depthInInteger.add(k);                             //Artık int olan k değerlerini int olan bir array e attık
        }
    }
    public int totalDepth(ArrayList depthInInteger){            //Toplam borç
        for (int i =0 ; i<depthInInteger.size(); i++){
            int t = (int) depthInInteger.get(i);
            totalDepth = totalDepth +t;
        }
        return totalDepth;
    }

    //Daire Grafiği bitiş

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}