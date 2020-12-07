package com.example.plansplit.Controllers.FragmentControllers.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    EditText expenseName, price;
    String type;
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

        return root;
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