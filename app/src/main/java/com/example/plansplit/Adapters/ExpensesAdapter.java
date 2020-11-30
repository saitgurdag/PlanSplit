package com.example.plansplit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plansplit.Objects.Expense;
import com.example.plansplit.R;
import java.util.List;

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
