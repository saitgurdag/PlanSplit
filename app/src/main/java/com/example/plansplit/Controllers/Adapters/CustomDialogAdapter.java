package com.example.plansplit.Controllers.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.R;

import java.util.ArrayList;

public class CustomDialogAdapter extends RecyclerView.Adapter<CustomDialogAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private AddExpenseFragment addExpenseFragment;

        private ArrayList myImageNameList;


        public CustomDialogAdapter(Context ctx, ArrayList myImageNameList){

            inflater = LayoutInflater.from(ctx);
            this.myImageNameList = myImageNameList;
        }

        @Override
        public CustomDialogAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.item_dialog_recycler_select_payer, parent, false);
            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(CustomDialogAdapter.MyViewHolder holder, int position) {
            Person shareMethodPerson = (Person) myImageNameList.get(position);
            holder.name.setText(shareMethodPerson.getName());
            holder.image.setImageDrawable(inflater.getContext().getResources().getDrawable(shareMethodPerson.getCardView_shareMethodPersonPicture()));

        }

        @Override
        public int getItemCount() {
            return myImageNameList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView name;
            ImageView image;


            public MyViewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.payer_name_text);
                image= (ImageView) itemView.findViewById(R.id.ImagePayer);
                /*

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addExpenseFragment =new AddExpenseFragment();
                        addExpenseFragment.setPayer_name((String) name.getText());
                        AddExpenseFragment.dialogBtn.setText(name.getText());
                        AddExpenseFragment.dialog.dismiss();
                    }
                });

                 */


            }

        }
}
