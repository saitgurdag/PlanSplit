package com.example.plansplit.Controllers.FragmentControllers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PayFragment extends Fragment {

    private static final String TAG = "PayFragment";
    CardView who;
    ImageView whoImage;
    ArrayList<Friend> memberInfos = new ArrayList<>();
    Friend selectedFriend=null;
    boolean ctrlFriend=false;
    Database database = Database.getInstance();
    TextView debtTxt;
    private float debt=0;
    Button saveBtn;
    EditText payEdit;

    public void setDebt(float debt) {
        this.debt = debt;
    }

    public float getDebt() {
        return debt;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pay, container, false);
        memberInfos.clear();
        OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        }; requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),onBackPressedCallback);
        ctrlFriend=false;
        saveBtn = root.findViewById(R.id.savePayButton);
        who = root.findViewById(R.id.CardViewFragmentPayPayed);
        whoImage = root.findViewById(R.id.who_image);
        debtTxt = root.findViewById(R.id.debt_text);
        payEdit = root.findViewById(R.id.payEdt);
        ImageView userPhoto = root.findViewById(R.id.userPhoto);
        Picasso.with(getContext()).load(database.getPerson().getImage()).into(userPhoto);

        Type type = new TypeToken<ArrayList<Friend>>() {
        }.getType();
        memberInfos = new Gson().fromJson(getArguments().getString("membersInfos"), type);

        if(memberInfos.size()==1){      //arkadaşlardan geldiğini anlamak için
            ctrlFriend=true;
        }
        ArrayList<Friend> m2 = new ArrayList<>();
        for (Friend f : memberInfos){
            if(!f.getKey().equals(database.getPerson().getKey())){
                m2.add(f);
            }
        }
        memberInfos=m2;

        if (memberInfos.size()>=2) {
            who.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(getContext(), who);
                    for (Friend member : memberInfos) {
                        if (!member.getKey().equals(database.getPerson().getKey()))
                            popup.getMenu().add(member.getName());
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            for (Friend friend : memberInfos) {
                                if (friend.getName().equals(menuItem.toString())) {
                                    selectedFriend = friend;
                                }
                            }
                            Picasso.with(getContext()).load(selectedFriend.getPerson_image()).into(whoImage);
                            database.getDebtFromGroups(database.getPerson().getKey(), selectedFriend, groupCallBack);
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }else {
            for (Friend member : memberInfos) {
                if (!member.getKey().equals(database.getPerson().getKey()))
                    selectedFriend=member;
            }
            Picasso.with(getContext()).load(selectedFriend.getPerson_image()).into(whoImage);
        }

        if(ctrlFriend){
            database.getDebtFromFriend(database.getPerson().getKey(), selectedFriend, callBack);
        }else{
            if(selectedFriend!=null){
                database.getDebtFromGroups(database.getPerson().getKey(), selectedFriend, groupCallBack);
            }
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = payEdit.getText().toString();
                if(!amount.matches("") && android.text.TextUtils.isDigitsOnly(amount) && Float.parseFloat(amount)<=debt) {
                    if(ctrlFriend){
                        database.payToFriend(database.getPerson().getKey(), selectedFriend, amount);
                        ((MyGroupActivity) getContext()).setNavController(R.id.navi_events);
                    }else{
                        System.out.println("dogruuuuuu");
                        database.payToGroupsMember(database.getPerson().getKey(), selectedFriend, amount);
                        ((MyGroupActivity) getContext()).setNavController(R.id.navi_events);
                    }
                }else if(!amount.matches("") && android.text.TextUtils.isDigitsOnly(amount) && Float.parseFloat(amount)>debt){
                    Toast.makeText(getContext(), "Borcunuzdan fazla para miktarı girdiniz!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Hatalı girdi", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    final Database.getDebtFromFriendCallBack callBack = new Database.getDebtFromFriendCallBack() {
        @Override
        public void onGetDebtFromFriendRetrieveSuccess(float debt) {
            System.out.println("borçlar "+debt);
            setDebt(debt);
            debtTxt.setText(getResources().getString(R.string.personal_debt) + " : " + String.valueOf(debt) + " TL");
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.d(error_tag, error);
        }
    };

    final Database.getDebtFromGroupCallBack groupCallBack = new Database.getDebtFromGroupCallBack() {
        @Override
        public void onGetDebtFromGroupRetrieveSuccess(float debt) {
            System.out.println("borçlar "+debt);
            setDebt(debt);
            debtTxt.setText(getResources().getString(R.string.personal_debt) + " : " + (debt) + " TL");
        }

        @Override
        public void onError(String error_tag, String error) {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            database.getDebtFromGroups(database.getPerson().getKey(), selectedFriend, groupCallBack);
        }
    };




}
