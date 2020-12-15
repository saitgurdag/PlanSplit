package com.example.plansplit.Controllers.FragmentControllers.personal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.R;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.R.layout.simple_spinner_item;

public class PersonalFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    String[] country;
    Database db;
    private static final String TAG = "PersonalFragment";
    RecyclerView recyclerView;
    ExpensesAdapter adapter;
    int budget;                        //butçe progressBar'da 360 dereceye denk gelecek
    int totExpense;                   //toplam harcamayı gösteriyor.
    ArrayList<Expense> expenseList;
    ProgressBar progressBar;
    TextView progressText, kalanbudget;
    Button addExpense;
    ImageView filter;
    EditText expenseName, price;
    String type;
    String selectedFilter;
    Spinner spin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);
        db = new Database(this.getContext(), this);
        db.getBudget();
        expenseList = new ArrayList<>();
        expenseList.clear();
        totExpense = 0;
        expenseList = db.getExpenses();
        totExpense = db.getTotExpense();

        country = new String[]{"Yiyecek", "Giyecek", "Kırtasiye", "Temizlik" , "Diğer"};
        spin = (Spinner) root.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this.getContext(), simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        filter=root.findViewById(R.id.filter);

        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setProgress(totExpense);
        progressText = root.findViewById(R.id.progress_text);
        addExpense = root.findViewById(R.id.add_expense);
        price = root.findViewById(R.id.price);
        expenseName = root.findViewById(R.id.name);
        kalanbudget = root.findViewById(R.id.kalan_butce);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_expense);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);

        update();

        adapter = new ExpensesAdapter(this.getContext(), expenseList);
        recyclerView.setAdapter(adapter);

        addExpense.setOnClickListener(this);
        progressText.setOnClickListener(this);

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
                                break;
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
                db.addExpense(name, type, String.valueOf(p));
            }
        }else if(v.getId()==progressText.getId()){
            addBudgetDialog();
        }
    }

    public void newExpense(ArrayList e, int p) {
        expenseList = e;
        totExpense = p;
        adapter = new ExpensesAdapter(this.getContext(), expenseList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        update();
    }

    private void addBudgetDialog() {

        @SuppressLint("InflateParams") final View DialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_add_budget, null);
        final Dialog dialog = new Dialog(this.getContext(), R.style.DialogAddBudget);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(DialogView);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();

        dialog.findViewById(R.id.add_budget_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText budgetEdit = dialog.findViewById(R.id.add_budget_edit_txt);
                if(!budgetEdit.getText().toString().matches("")){
                    dialog.cancel();
                    budget=Integer.parseInt(String.valueOf(budgetEdit.getText()));
                    update();
                    db.setBudget(budget);
                }else{
                    Log.d(TAG, "Budget eklema dialogunda edit text hatalı");
                }
            }
        });

    }

    public void checkBudget(String i){
        if(i==null) {
            addBudgetDialog();
        }else{
            budget = Integer.parseInt(i);
            System.out.println("Aylık Bütçe : " + budget);
            update();
        }
    }

    public void update(){
        progressText.setText(String.valueOf(budget) + " TL");
        progressBar.setMax(budget);
        kalanbudget.setText("Kalan Bütçe : " + String.valueOf(budget - totExpense) + " TL");
        progressBar.setProgress(totExpense);

    }
}