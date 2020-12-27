package com.example.plansplit.Controllers.FragmentControllers;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Controllers.FragmentControllers.ShareMethod.ShareMethodFriendsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PayFragment extends Fragment {

    private int startYear, startMonth, startDay;
    public static Dialog dialog;
    private Button calenderBtn;
    private Button dpexitBtn;
    private Button noteBtn;
    private Button dpmenuBtn;
    private Button dpokBtn;
    private String date;
    boolean ctrlDate=false;
    ImageView payerPhoto;
    private static final String TAG = "PayFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pay, container, false);
        ctrlDate=false;
        calenderBtn = root.findViewById(R.id.calendarButtonPay);
        noteBtn = root.findViewById(R.id.noteButtonPay);
        date=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        calenderBtn.setText(date);
        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });

        //HomeActivity home = (HomeActivity) getContext();
        //payerPhoto=root.findViewById(R.id.imageViewFragmentPayPayerImage);
        //Picasso.with(getContext()).load(home.getPersonPhoto()).into(payerPhoto);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showDatePicker() {

        dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_datepicker_layout);
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.dp_group_expense);
        dpmenuBtn = dialog.findViewById(R.id.dpMenuButton);
        dpokBtn = dialog.findViewById(R.id.dpOKButton);
        dpexitBtn = dialog.findViewById(R.id.dpExitButton);
        dpokBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctrlDate=true;
                startYear = dp.getYear();
                startMonth = dp.getMonth() + 1;
                startDay = dp.getDayOfMonth();
                date = startDay + "/" + startMonth + "/" + startYear;
                calenderBtn.setText(date);
                dialog.dismiss();
            }
        });
        dpexitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dpmenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        dialog.show();
    }
}
