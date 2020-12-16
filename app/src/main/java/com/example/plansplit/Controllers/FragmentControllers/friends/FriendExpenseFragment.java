package com.example.plansplit.Controllers.FragmentControllers.friends;

//public class FriendExpenseFragment {
//}


import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;

import com.example.plansplit.Controllers.Adapters.CustomDialogAdapter;

import com.example.plansplit.Controllers.Adapters.CustomDialogAdapterFriendExpense;
import com.example.plansplit.Models.Objects.Person;
import com.example.plansplit.R;
import com.example.plansplit.Controllers.FragmentControllers.ShareMethod.ShareMethodFragment;

import java.util.ArrayList;

public class FriendExpenseFragment extends Fragment {

    private int startYear, startMonth, startDay;
    public static Dialog dialog;
    public static Button dialogBtn;
    private Button calenderBtn;
    private Button dpexitBtn;
    private Button noteBtn;
    private Button dpmenuBtn;
    private String repetition;
    public static String sharemethod;
    public static Button shareBtn;
    private Button dpokBtn;
    private String payer_name;
    private String note;
    private String date;
    private ArrayList myImageNameListFriend;

    public static FriendExpenseFragment newInstance() {
        return new FriendExpenseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_expense, container, false);

        myImageNameListFriend = new ArrayList<>();

        myImageNameListFriend.add(new Person("ali", R.drawable.denemeresim, 0));
        myImageNameListFriend.add(new Person("veli", R.drawable.denemeresim, 0));


        dialogBtn = root.findViewById(R.id.payer_button);
        calenderBtn = root.findViewById(R.id.calendarButton);
        noteBtn = root.findViewById(R.id.noteButton);
        shareBtn = root.findViewById(R.id.method_button);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteDialog();
            }
        });

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPayerDialog(FriendExpenseFragment.this);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareMethodDialog();

            }
        });


        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });

        return root;

    }

    public void openNoteDialog() {
        FriendExpenseNoteDialog friendExpenseNoteDialog = new FriendExpenseNoteDialog();
        friendExpenseNoteDialog.show(getParentFragmentManager(), "friend note dialog");

    }

    public void openShareMethodDialog() {//// aynı oldu
        ShareMethodFragment shareDialog = new ShareMethodFragment();
        shareDialog.show(getParentFragmentManager(), "friend share method dialog");


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void openPayerDialog(FriendExpenseFragment activity) {

        dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_recycler_select_payer);


        Button btndialog = (Button) dialog.findViewById(R.id.buttonExitPayerSelection);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerPayer);
        CustomDialogAdapterFriendExpense adapterFriend = new CustomDialogAdapterFriendExpense(getContext(), myImageNameListFriend);
        recyclerView.setAdapter(adapterFriend);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();

    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public String getPayer_name() {
        return payer_name;
    }

    public void setPayer_name(String payer_name) {
        this.payer_name = payer_name;
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

                PopupMenu popup = new PopupMenu(getContext(), dpmenuBtn);
                //popup.getMenuInflater().inflate(R.menu.date_picker_menu,popup.getMenu());
                popup.inflate(R.menu.date_picker_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.date_picker_menu_for_once:
                                repetition = getResources().getString(R.string.date_picker_menu_for_once);

                                break;
                            case R.id.date_picker_menu_weekly_repeat:
                                repetition = getResources().getString(R.string.date_picker_menu_weekly_repeat);
                                break;
                            case R.id.date_picker_menu_monthly_repeat:
                                repetition = getResources().getString(R.string.date_picker_menu_monthly_repeat);
                                break;
                            case R.id.date_picker_menu_yearly_repeat:
                                repetition = getResources().getString(R.string.date_picker_menu_yearly_repeat);
                                break;
                        }
                        dpmenuBtn.setText(repetition);
                        return false;
                    }
                });
                popup.show();
            }
        });
        dialog.show();
    }
}