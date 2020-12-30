package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.MainActivity;
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
        holder.name.setText(expense.getExpense_name());

        //BERGAY
/*
        if((Locale.getDefault().toString().equals("en"))){
                if (expense.getExpense_type().equals("yiyecek") || expense.getExpense_type().equals("nahrung")){
                       // holder.type.setText(R.string.title_food); // food
                    holder.type.setText("food");
                     }if (expense.getExpense_type().equals("giyecek") || expense.getExpense_type().equals("kleidung")){
                             holder.type.setText("wear"); // clothing
                        }if (expense.getExpense_type().equals("kırtasiye") || expense.getExpense_type().equals("schreibwaren")){
                                     holder.type.setText("stationary"); // kırtasiye
                            }if(expense.getExpense_type().equals("Temizlik") || expense.getExpense_type().equals("reinigungsmittel")){
                                             holder.type.setText("cleaning"); // temizlik
                                }if (expense.getExpense_type().equals("Diğer") || expense.getExpense_type().equals("andere")){
                                holder.type.setText("others"); // temizlik
                                         }
        }else if (Locale.getDefault().toString().equals("de")){
            if (expense.getExpense_type().equals("yiyecek") || expense.getExpense_type().equals("food")){
                holder.type.setText("Nahrung"); // food
            }if (expense.getExpense_type().equals("giyecek") || expense.getExpense_type().equals("clothing")){
                holder.type.setText("Kleidung"); // clothing
            }if (expense.getExpense_type().equals("kırtasiye") || expense.getExpense_type().equals("stationery")){
                holder.type.setText("schreibwaren"); // kırtasiye
            }if(expense.getExpense_type().equals("temizlik") || expense.getExpense_type().equals("wear")){
                holder.type.setText("reinigungsmittel"); // temizlik
            }if (expense.getExpense_type().equals("diğer") || expense.getExpense_type().equals("other")){
                holder.type.setText("andere"); // diğer
            }else holder.type.setText(expense.getExpense_type());
        }
*/
        holder.type.setText(expense.getExpense_type());

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
