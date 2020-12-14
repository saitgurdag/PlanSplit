package com.example.plansplit.Models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plansplit.Controllers.FragmentControllers.personal.PersonalFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Objects.Expense;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Database {

    private static final String TAG = "DATABASE";
    final int[] butce = new int[1];
    public boolean ctrlRun=false;
    private Context context;
    private Fragment fragment;
    private int totExpense = 0;

    public int getTotExpense() {
        return totExpense;
    }

    public int getButce() {
        return butce[0];
    }

    private String userId = null;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    private final DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference("users");

    public Database(){ }

    public Database(Context context){
        this.context=context;
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            setUserId(acct.getId());
            System.out.println("acct not null");
        }else{
            System.out.println("acct null");
        }
    }

    public Database(Context context, Fragment fragment){
        this.fragment = fragment;
        this.context=context;
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
        if (acct != null) {
            setUserId(acct.getId());
            System.out.println("acct not null");
        }else{
            System.out.println("acct null");
        }
    }

    private static class Holder{
        private static final Database INSTANCE = new Database();
    }

    public static Database getInstance(){
        return Holder.INSTANCE;
    }

    public interface FireBaseFriendCallBack {
        void onFriendRetrieveSuccess(Friend friend);
        void onEmptyListError();
        void onError(String error);
    }

    public void getFriends(@NonNull final String user_key, final FireBaseFriendCallBack callBack){
        final String methodTAG = "getFriends";
        user_reference.child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    @SuppressWarnings("unchecked")
                    ArrayList<String> friends = (ArrayList<String>)
                            snapshot.child("friends").getValue();
                    if(friends == null){
                        callBack.onEmptyListError();
                        Log.e(TAG, "anan");
                        return;
                    }
                    for(final String friend_key: friends){
                        getFriend(friend_key, callBack);
                    }
                }else{
                    callBack.onError(TAG + ": " + methodTAG + ", " + user_key + " ile ilişkili kullanıcı yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(TAG + ": " + methodTAG + ", " +  error.getMessage());
            }
        });
    }

    public void getFriend(@NonNull final String friend_key, final FireBaseFriendCallBack callBack){
        final String methodTAG = "getFriend";
        user_reference.child(friend_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    /*fixme: kayıt sırasında resim almadığımızdan
                            rastgele resim koydum.
                            borçların database'de tutulma yöntemi geçici,
                            acilen değişmeli
                        */
                    int photo = R.drawable.denemeresim;
                    String name = snapshot.child("name").getValue().toString();
                    String surname = snapshot.child("surname").getValue().toString();
                    if(name == null && surname == null){
                        name = "No Name";
                        surname = "";
                    }
                    int amount = 0;
                    Friend friend = new Friend(photo, name + " " + surname, amount, friend_key);
                    callBack.onFriendRetrieveSuccess(friend);
                }else{
                    callBack.onError(TAG + ": " + methodTAG + ", " + friend_key + " ile ilişkili kullanıcı yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onError(TAG + ": " + methodTAG + ", " +  error.getMessage());
            }
        });
    }

    public void removeFriend(){
        System.out.println("arkadaş silmeye tıklandı");
    }

    public void getBudget(){
        user_reference.child(userId).child("budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String b;
                    b = snapshot.getValue().toString();
                    ((PersonalFragment) fragment).checkBudget(b);
                }else {
                    ((PersonalFragment) fragment).checkBudget(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setBudget(int budget){
        user_reference.child(userId).child("budget").setValue(budget);
    }

    public void addExpense(String name, String type, String price){

        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        String key = dbRef.push().getKey();
        DatabaseReference dbr = dbRef.child(key);
        dbr.child("name").setValue(name);
        dbr.child("type").setValue(type);
        dbr.child("price").setValue(price);
        getExpenses();

    }

    public ArrayList getExpenses() {
        final ArrayList<Expense> expenses = new ArrayList<>();
        DatabaseReference dbRef = user_reference.child(userId).child("expenses");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totExpense=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = (String) ds.child("name").getValue();
                    String type = (String) ds.child("type").getValue();
                    String price = (String) ds.child("price").getValue();
                    if(price!=null) {
                        int p = Integer.parseInt(price);
                        totExpense+=p;
                        expenses.add(new Expense(name, type, p));
                    }

                }
                ((PersonalFragment)fragment).newExpense(expenses, totExpense);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return expenses;
    }



}
