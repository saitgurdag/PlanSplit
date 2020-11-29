package com.example.plansplit.ui.ShareMethod;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.ShareMethodAdapter;
import com.example.plansplit.R;
import com.example.plansplit.Objects.Person;
import com.example.plansplit.ui.groups.GroupExpenseFragment;

import java.util.ArrayList;
import java.util.List;



public class ShareMethodFragment extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ShareMethodFragment";
    private Dialog dialog;
    Button sharemethodExitBtn;
    Spinner spinner;
    Button sharemethodOKBtn;
    private com.example.plansplit.ui.ShareMethod.ShareMethodViewModel ShareMethodViewModel;
    RecyclerView recyclerView;
    ShareMethodAdapter adapter;
    List<Person> ShareGroupsPersonList;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        {

            ShareMethodViewModel =
                    ViewModelProviders.of(this).get(com.example.plansplit.ui.ShareMethod.ShareMethodViewModel.class);
            dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.fragment_dialog_share_method);
            sharemethodExitBtn = dialog.findViewById(R.id.shareMethodExitButton);
            sharemethodOKBtn = dialog.findViewById(R.id.buttonShareMethodOk);
            spinner = dialog.findViewById(R.id.shareMethodSpinner);
            ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this.getContext(),
                    R.array.shareMethodSpinnerItems, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(adapter2);
            spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this.getContext());


            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_dialog_share_method, null);*/


            //builder.setView(view);

            recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_share_method);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            ShareGroupsPersonList = new ArrayList<>();

            ShareGroupsPersonList.add(new Person("ali", R.drawable.denemeresim, 0));
            ShareGroupsPersonList.add(new Person("veli", R.drawable.denemeresim, 0));
            ShareGroupsPersonList.add(new Person("osman", R.drawable.denemeresim, 0));


            adapter = new ShareMethodAdapter(this.getContext(), ShareGroupsPersonList);
            recyclerView.setAdapter(adapter);
            sharemethodExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            sharemethodOKBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    GroupExpenseFragment.shareBtn.setText(GroupExpenseFragment.sharemethod);
                }
            });

            return dialog;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String text=adapterView.getItemAtPosition(i).toString();
            Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
