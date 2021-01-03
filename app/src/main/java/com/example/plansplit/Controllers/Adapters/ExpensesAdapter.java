package com.example.plansplit.Controllers.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.R;
import java.util.List;
import java.util.Locale;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder> {

    List<Expense> expensesList;
    Context mCtx;
    OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ExpensesAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public ExpensesAdapter(Context mCtx, List<Expense> expenseList) {
        this.mCtx=mCtx;
        this.expensesList=expenseList;
    }

    @NonNull
    @Override
    public ExpensesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_expenses,
                parent, false);
        ExpensesAdapter.ExpensesViewHolder ExpensesViewHolder = new ExpensesAdapter.ExpensesViewHolder(view, mListener);

        return ExpensesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpensesViewHolder holder, int position) {

        Expense expense = expensesList.get(position);
        holder.price.setText(expense.getPrice());
        holder.name.setText(expense.getName());

        String type = "";
        String currentLang = Locale.getDefault().getLanguage();
        //System.out.println("TEL GERCEK DİLİ "+Lo);

        SharedPreferences prefs = mCtx.getSharedPreferences("LanguageSettings", Activity.MODE_PRIVATE);
        String selected_language = prefs.getString("My_Lang", "");
        System.out.println("seçilen dil: " + selected_language);

        System.out.println(Locale.getDefault().getDisplayLanguage()+ "TELİN ACILIS DİLİ BU");   //System.out.println(Locale.getDefault().getDisplayLanguage()+ "TELİN ACILIS DİLİ BU");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(selected_language.equals("en") ){   //Eger kullanıcı ingilizce kullanıyorsa   SDK 24 DEN BÜUYÜK OLUNCA CURR LANG İ HEP EN SANIYO
                System.out.println(Build.VERSION.SDK_INT +"SDK BU");
                System.out.println("DİL SDK BÜYÜK İNG");
                System.out.println(Locale.getDefault().getLanguage());
                if (expense.getType().toLowerCase().equals("yiyecek")){
                    type = "food";
                }else if(expense.getType().toLowerCase().equals("giyecek")){
                    type = "wear";
                }else if (expense.getType().toLowerCase().equals("kırtasiye")){
                    type = "stationary";
                }else if (expense.getType().toLowerCase().equals("temizlik")){
                    type = "cleaning";
                }else if(expense.getType().toLowerCase().equals("diğer")){
                    type = "others";
                }
            }else if(selected_language.equals("de")){  //Eger Kullanıcı almanca kullanıyosa
                System.out.println("DİL SDK BÜYÜK ALM");
                System.out.println(Locale.getDefault().getLanguage());
                if (expense.getType().toLowerCase().equals("yiyecek")){
                    type = "nahrung";
                }else if(expense.getType().toLowerCase().equals("giyecek")){
                    type = "kleidung";
                }else if (expense.getType().toLowerCase().equals("kırtasiye")){
                    type = "schreibwaren";
                }else if(expense.getType().toLowerCase().equals("temizlik")){
                    type = "reinigungsmittel";
                }else if(expense.getType().toLowerCase().equals("diğer")){
                    type = "andere";
                }
            }else{
                type = expense.getType();
            }
            holder.type.setText(type);
        }else {
            if(selected_language.equals("en")){   //Eger kullanıcı ingilizce kullanıyorsa
                System.out.println("DİL SDK KUCUK ENG");
                if (expense.getType().toLowerCase().equals("yiyecek")){
                    type = "food";
                }else if(expense.getType().toLowerCase().equals("giyecek")){
                    type = "wear";
                }else if (expense.getType().toLowerCase().equals("kırtasiye")){
                    type = "stationary";
                }else if (expense.getType().toLowerCase().equals("temizlik")){
                    type = "cleaning";
                }else if(expense.getType().toLowerCase().equals("diğer")){
                    type = "others";
                }
            }else if(selected_language.equals("de")){  //Eger Kullanıcı almanca kullanıyosa
                System.out.println("DİL SDK KUCUK ALM");
                if (expense.getType().toLowerCase().equals("yiyecek")){
                    type = "nahrung";
                }else if(expense.getType().toLowerCase().equals("giyecek")){
                    type = "kleidung";
                }else if (expense.getType().toLowerCase().equals("kırtasiye")){
                    type = "schreibwaren";
                }else if(expense.getType().toLowerCase().equals("temizlik")){
                    type = "reinigungsmittel";
                }else if(expense.getType().toLowerCase().equals("diğer")){
                    type = "andere";
                }
            }else{
                type = expense.getType();
            }
            holder.type.setText(type);
        }

    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public class ExpensesViewHolder extends RecyclerView.ViewHolder {

        TextView name, type, price;

        public ExpensesViewHolder(final View itemView, final ExpensesAdapter.OnItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            price = itemView.findViewById(R.id.price);

        }
    }
}
