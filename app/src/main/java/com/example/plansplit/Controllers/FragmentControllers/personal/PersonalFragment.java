package com.example.plansplit.Controllers.FragmentControllers.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ExpensesAdapter;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class PersonalFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    String[] country;
    private static final String TAG = "PersonalFragment";
    RecyclerView recyclerView;
    ExpensesAdapter adapter;
    int butce;                        //butçe progressBar'da 360 dereceye denk gelecek
    int totExpense;                   //toplam harcamayı gösteriyor.
    List<Expense> expenseList;
    ProgressBar progressBar;
    TextView progressText, kalanButce;
    Button addExpense;
    ImageView filter;
    EditText expenseName, price;
    String type;
    String selectedFilter;
    Spinner spin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);

        country = new String[]{"Yiyecek", "Giyecek", "Diğer"};
        butce=3000;         //örnek olarak bu değerler eklendi.

        spin = (Spinner) root.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this.getContext(), simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        filter=root.findViewById(R.id.filter);

        progressBar = root.findViewById(R.id.progressBar);
        progressText = root.findViewById(R.id.progress_text);
        addExpense = root.findViewById(R.id.add_expense);
        price = root.findViewById(R.id.price);
        expenseName = root.findViewById(R.id.name);
        kalanButce = root.findViewById(R.id.kalan_butce);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_expense);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        expenseList = new ArrayList<>();

        expenseList.add(new Expense("Kıyafet Alışverişi", "Giyecek", 50));  //ÖRNEKKKK
        expenseList.add(new Expense("Yemekhane Ücreti", "Yiyecek", 150));   //ÖRNEKKKK
        totExpense+=50;             //ÖRNEKKKK
        totExpense+=150;            //ÖRNEKKKK
        kalanButce.setText("Kalan Bütçe : " + String.valueOf(butce-totExpense) + " TL");


        progressBar.setMax(butce);
        progressBar.setProgress(totExpense);
        progressText.setText(butce + " TL");

        adapter = new ExpensesAdapter(this.getContext(), expenseList);
        recyclerView.setAdapter(adapter);

        addExpense.setOnClickListener(this);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(getContext(), filter);
                //popup.getMenuInflater().inflate(R.menu.date_picker_menu,popup.getMenu());
                popup.inflate(R.menu.filter_menu_personal);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.filter_food:
                                selectedFilter=getResources().getString(R.string.title_food);

                                break;
                            case R.id.filter_wear:
                                selectedFilter=getResources().getString(R.string.title_wear);
                                break;
                            case R.id.filter_stationery:
                                selectedFilter=getResources().getString(R.string.title_stationery);
                                break;
                            case R.id.filter_hygiene:
                                selectedFilter=getResources().getString(R.string.title_hygiene);
                            case R.id.filter_others:
                                selectedFilter=getResources().getString(R.string.title_others);
                                break;
                            case R.id.filter_all:
                                selectedFilter=getResources().getString(R.string.title_all);
                                break;

                        }
                        filterList(selectedFilter);
                        return false;
                    }
                });
                popup.show();


               //
            }
        });

        return root;
    }

    private void filterList(String status){
        if(!status.equals(getResources().getString(R.string.title_all))) {
            selectedFilter = status;
            ArrayList filteredList = new ArrayList<>();
            for (Expense expense : expenseList) {
                if (expense.getExpense_type().toLowerCase().contains(status)) {
                    filteredList.add(expense);
                }

            }
            adapter = new ExpensesAdapter(this.getContext(), filteredList);
            recyclerView.setAdapter(adapter);
        }
        else{
            adapter = new ExpensesAdapter(this.getContext(), expenseList);
            recyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        type = country[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==addExpense.getId()){
            if(!price.getText().toString().matches("") && !expenseName.getText().toString().matches("")) {
                String name = String.valueOf(expenseName.getText());
                int p = Integer.parseInt(String.valueOf(price.getText()));
                expenseList.add(new Expense(name, type, p));
                totExpense += p;
                progressBar.setProgress(totExpense);
                adapter = new ExpensesAdapter(this.getContext(), expenseList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                kalanButce.setText("Kalan Bütçe : " + String.valueOf(butce - totExpense) + " TL");
            }
        }
    }
}