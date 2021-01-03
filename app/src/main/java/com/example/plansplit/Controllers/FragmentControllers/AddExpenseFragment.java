package com.example.plansplit.Controllers.FragmentControllers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.example.plansplit.Controllers.FragmentControllers.ShareMethod.ShareMethodFriendsFragment;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private int startYear, startMonth, startDay,expensePictureResourceID,foodPicture,wearPicture,hygienePicture,
            stationeryPicture, otherPicture;
    public static Dialog dialog;
    public static Button dialogBtn,calenderBtn,dpexitBtn,noteBtn,dpmenuBtn,shareBtn,expensetypeBtn,dpexpenseexitBtn,
            dpexpenseokBtn,saveexpenseBtn,dpokBtn;
    private String date,payer_name,expenseType,expenseamounth,expensename;
    public static String sharemethod;
    private EditText edittextexpensename,edittextexpenseamounth;
    private ImageView expensePicture;
    private String personId;
    boolean ctrlDate=false;         //tarih tuşuna tıklandı mı tıklanmadı mı anlamak için
    boolean ctrlFG;         //true = > friend         false = > group
    Database db;
    Friend friend;
    Groups group;
    Bundle extras;
    SharedPreferences mPrefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_expense, container, false);
        ctrlDate=false;
        db = Database.getInstance();
        db = new Database(this.getContext());
        mPrefs = getContext().getSharedPreferences("listbell", Context.MODE_PRIVATE);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (extras.keySet().contains("friend")) {
                    Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                    Gson gson = new Gson();
                    String json = extras.getString("friend");
                    friend = gson.fromJson(json, Friend.class);
                    intent.putExtra("friend", json);
                    intent.putExtra("friend_back", "back");
                    startActivity(intent);
                }
                if (extras.keySet().contains("group")) {
                    Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                    Gson gson = new Gson();
                    String json = extras.getString("group");
                    group = gson.fromJson(json, Groups.class);
                    intent.putExtra("group", json);
                    intent.putExtra("group_back", "back");
                    startActivity(intent);
                }
                if (extras.keySet().contains("friend_key_list")) {
                    Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                    Gson gson = new Gson();
                    String json = extras.getString("friend_from_list");
                    friend = gson.fromJson(json, Friend.class);
                    intent.putExtra("friend_to_list", json);
                    intent.putExtra("friend_back", "back");
                    startActivity(intent);

                }
                if (extras.keySet().contains("group_key_list")) {
                    Intent intent = new Intent(getParentFragment().getContext(), MyGroupActivity.class);
                    Gson gson = new Gson();
                    String json = extras.getString("group_from_list");
                    group = gson.fromJson(json, Groups.class);
                    intent.putExtra("group_to_list", json);
                    intent.putExtra("group_to_back", "back");
                    startActivity(intent);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        edittextexpensename=root.findViewById(R.id.editTextExpenseName);
        edittextexpenseamounth=root.findViewById(R.id.editTextExpenseAmounth);
        saveexpenseBtn=root.findViewById(R.id.saveExpenseButton);

        dialogBtn = root.findViewById(R.id.payer_button);
        calenderBtn = root.findViewById(R.id.calendarButton);
        noteBtn = root.findViewById(R.id.noteButton);
        shareBtn = root.findViewById(R.id.method_button);
        expensetypeBtn=root.findViewById(R.id.expense_type_button);
        date=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        calenderBtn.setText(date);

        Bundle extras = getArguments();
        if(extras != null) {
            personId = db.getPerson().getKey();
            if (extras.keySet().contains("friend")) {
                HomeActivity.navView.getMenu().getItem(1).setChecked(true);
                ctrlFG=true;
                Gson gson = new Gson();
                String json = extras.getString("friend");
                friend = gson.fromJson(json, Friend.class);
            } else if (extras.keySet().contains("group")) {
                HomeActivity.navView.getMenu().getItem(2).setChecked(true);
                ctrlFG=false;
                Gson gson = new Gson();
                String json = extras.getString("group");
                group = gson.fromJson(json, Groups.class);
            }else{
                edittextexpensename.setText(extras.getString("description"));
                edittextexpensename.setSelection(edittextexpensename.getText().length());
                if((extras.keySet().contains("friend_key_list"))){
                    HomeActivity.navView.getMenu().getItem(1).setChecked(true);

                }
                if((extras.keySet().contains("group_key_list"))){
                    HomeActivity.navView.getMenu().getItem(2).setChecked(true);

                }
            }
        }

        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });

        expensetypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_select_expense_type);
                dialog.show();
                dpexpenseokBtn=dialog.findViewById(R.id.dpExpenseOKButton);
                dpexpenseexitBtn=dialog.findViewById(R.id.dpExpenseExitButton);
                dpexpenseexitBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dpexpenseokBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        expensetypeBtn.setText(expenseType);
                    }
                });
                Spinner expensespinner=dialog.findViewById(R.id.spinnerExpense);
                expensePicture=dialog.findViewById(R.id.ImageExpense);
                foodPicture = R.drawable.ic_baseline_fastfood_24;
                wearPicture = R.drawable.ic_baseline_wear_24;
                hygienePicture = R.drawable.ic_baseline_hygiene_24;
                stationeryPicture = R.drawable.ic_baseline_school_24;
                otherPicture = R.drawable.ic_other;
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.expenseSpinnerItems, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expensespinner.setAdapter(adapter);
                expensespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedtext=adapterView.getItemAtPosition(i).toString();
                        switch (i){
                            case 0:
                                expensePictureResourceID=foodPicture;
                                break;
                            case 1:
                                expensePictureResourceID=wearPicture;
                                break;
                            case 2:
                                expensePictureResourceID=stationeryPicture;
                                break;
                            case 3:
                                expensePictureResourceID=hygienePicture;
                                break;
                            case 4:
                                expensePictureResourceID=otherPicture;
                                break;
                        }

                        if(Locale.getDefault().toString().equals("de")){
                            expensePicture.setImageResource(expensePictureResourceID);
                            System.out.println("DİLİBULDUUUUUU   ALMANCA");
                        }else if (Locale.getDefault().toString().equals("en")){
                            System.out.println("DİLİBULDUUUUUU   ENGL");
                            expensePicture.setImageResource(expensePictureResourceID);
                        }else{
                            System.out.println("DİLİBULDUUUUUU   TR");
                            expensePicture.setImageResource(expensePictureResourceID);
                        }
                        //expensePicture.setImageResource(expensePictureResourceID);
                        expenseType=selectedtext;
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


            }
        });

        final Database.DatabaseCallBack databaseCallBack=new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {

            }

            @Override
            public void onError(String error_tag, String error) {

            }
        };

        saveexpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expensename=edittextexpensename.getText().toString();
                expenseamounth=edittextexpenseamounth.getText().toString();
                Intent intent = new Intent(getContext(), MyGroupActivity.class);
                if(!android.text.TextUtils.isDigitsOnly(expenseamounth) || expenseamounth.matches("")
                        || expensename.matches("")){
                    Toast.makeText(getContext(), "Hatalı girdi", Toast.LENGTH_LONG).show();
                } else{
                    if(expenseType==null){
                        expenseType="Diğer";
                    }

                    if(extras != null&&extras.keySet().contains("friend_key_list")&&extras.keySet().contains("friend_from_list")){
                        Gson gson = new Gson();
                        String json = extras.getString("friend_from_list");
                        String friendkey=extras.getString("friend_key_list");
                        String todo_key=extras.getString("todo_key");
                        String description=extras.getString("description");
                        ctrlFG=true;
                        friend = gson.fromJson(json, Friend.class);
                        db.updateDoListFriend(friend.getKey(),todo_key,"delete",databaseCallBack );
                        intent.putExtra("friend", json);
                        getContext().startActivity(intent);
                    }

                    if(extras != null&&extras.keySet().contains("group_key_list")){
                        Gson gson = new Gson();
                        ctrlFG=false;
                        String json = extras.getString("group_from_list");
                        group = gson.fromJson(json, Groups.class);
                        String groupkey=extras.getString("group_key_list");
                        String todo_key=extras.getString("todo_key");
                        String description=extras.getString("description");
                        db.updateDoListGroup(groupkey,todo_key,"delete",databaseCallBack );
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putBoolean("listbell", false);
                        prefsEditor.putString("listbell_key", groupkey);
                        prefsEditor.apply();
                        intent.putExtra("group", json);
                        getContext().startActivity(intent);
                    }

                    Gson gson = new Gson();
                    String json;
                    intent.putExtra("person_id", personId);

                    if(expenseType.equals("Food") || expenseType.equals("Nahrung") || expenseType.equals("yiyecek")){
                        expenseType ="Yiyecek";
                    }else if(expenseType.equals("Clothing") || expenseType.equals("Kleidung") || expenseType.equals("giyecek")){
                        expenseType = "Giyecek";
                    }else if(expenseType.equals("Stationery") || expenseType.equals("Schreibwaren") || expenseType.equals("kırtasiye")){
                        expenseType = "Kırtasiye";
                    }else if(expenseType.equals("Cleaning") || expenseType.equals("Reinigungsmittel")|| expenseType.equals("temizlik")){
                        expenseType = "Temizlik";
                    }else if(expenseType.equals("Others") || expenseType.equals("Andere")|| expenseType.equals("diğer")){
                        expenseType = "Diğer";
                    }

                    if(ctrlFG) {
                        db.addExpenseToFriends(expensename, expenseType, expenseamounth, friend.getFriendshipsKey(), date);
                        json = gson.toJson(friend);
                        intent.putExtra("friend", json);
                    }else {
                        db.addExpenseToGroups(expensename, expenseType, expenseamounth, group.getGroupKey(), date, group.getGroup_members());
                        json = gson.toJson(group);
                        intent.putExtra("group", json);
                    }
                    getContext().startActivity(intent);
                }
            }
        });

        return root;

    }

    public void openShareMethodDialog() {//// aynı oldu
        ShareMethodFriendsFragment shareDialogFriend = new ShareMethodFriendsFragment();
        shareDialogFriend.show(getParentFragmentManager(), "friend share method dialog");


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void openPayerDialog(AddExpenseFragment activity) {

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
        dialog.show();

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