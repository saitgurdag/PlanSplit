package com.example.plansplit.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database {

    private static final String TAG = "DATABASE";

    private final DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference("users");

    private Database(){ }

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

}
