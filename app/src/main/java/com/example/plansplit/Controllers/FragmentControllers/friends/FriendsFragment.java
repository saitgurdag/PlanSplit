package com.example.plansplit.Controllers.FragmentControllers.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.FriendsAdapter;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_ref = db_ref.child("users");
    private String person_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_friends, container, false);
        Log.d(TAG, "BURADA");

        Button add_friend_button = root.findViewById(R.id.friend_add_button);
        final EditText add_friend_email_text = root.findViewById(R.id.friends_add_email);

        person_id = getArguments().get("person_id").toString();


        m_RecyclerView = root.findViewById(R.id.recycler_friends);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_Adapter = new FriendsAdapter(getContext(), person_id, m_RecyclerView);
        m_RecyclerView.setAdapter(m_Adapter);

        add_friend_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = add_friend_email_text.getText().toString();
                sendFriendRequest(email, person_id, callBack);
                Log.i(TAG, "friend request email: " + email);
                Log.i(TAG, "users own id: " + person_id);
            }
        });
        return root;
    }

    private interface FireBaseCallBack{
        void onCallBack(String key, ArrayList<String> friend_reqs);
        void onAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id);
        void onFriendsAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id);
    }
    private final FireBaseCallBack callBack = new FireBaseCallBack(){
        @Override
        public void onCallBack(String key, ArrayList<String> friend_reqs){
            user_ref.child(key).child("friend_reqs").setValue(friend_reqs);
            Toast.makeText(getContext(),
                    "Arkadaşlık isteği gönderildi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "sent friend request");
        }

        @Override
        public void onAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id){
            friends.add(req_id);
            friend_reqs.remove(req_id);
            user_ref.child(person_id).child("friends").setValue(friends);
            user_ref.child(person_id).child("friend_reqs").setValue(friend_reqs);
            Toast.makeText(getContext(),
                    "İkinizde birbirinize istek yollamışsınız, artık arkadaşsınız!",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "added as friend");
        }

        @Override
        public void onFriendsAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id){
            friends.add(person_id);
            friend_reqs.remove(person_id);
            user_ref.child(req_id).child("friends").setValue(friends);
            user_ref.child(req_id).child("friend_reqs").setValue(friend_reqs);
        }
    };

    //kör olma garantili okuyana kolay gelsin -arda
    //when i wrote this only god and i understood what i was doing now god only knows
    private void sendFriendRequest(final String email, final String person_id, final FireBaseCallBack callBack){
        user_ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
                        Log.d(TAG, "kendini eklemeye çalışıyor");
                        Toast.makeText(getContext(),
                                "Kendini eklemeye çalışıyorsun!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String friend_key = snapshot.getChildren().iterator().next().getKey();
                    DataSnapshot friends_snapshot
                            = snapshot.getChildren().iterator().next().child("friends");
                    //eğer karşıdakinin arkadaş listesi boş değilse
                    if(friends_snapshot.getChildrenCount() != 0){
                        //karşıdaki kullanıcının friends listesinde var mıyız?
                        boolean user_already_added = false;
                        for(DataSnapshot friend_ss: friends_snapshot.getChildren()){
                            if(person_id.equals(Objects.requireNonNull(friend_ss.getValue()).toString())){
                                user_already_added = true;
                            }
                        }
                        //eğer arkadaş değilsek
                        if(!user_already_added){
                            //onun friend_reqs listesinde keyimiz var mı, varsa yollama
                            //bizim friend_reqs listemizde keyi var mı, varsa direkt arakdaş ekle

                            //onun arkadaş listesine daha önce istek yollanmış mı?
                            DataSnapshot friend_reqs_ss = snapshot.getChildren().iterator().next()
                                    .child("friend_reqs");
                            boolean user_already_added_to_list = false;
                            for(DataSnapshot friend_req_ss: friend_reqs_ss.getChildren()){
                                if(person_id.equals(Objects.requireNonNull(friend_req_ss.getValue()).toString())){
                                    user_already_added_to_list = true;
                                }
                            }
                            if(user_already_added_to_list){
                                Log.d(TAG, "önceden bu maile istek yollanmış: " + email);
                                Toast.makeText(getContext(),
                                        "Daha önce istek gönderdin!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }else{
                                //karşıdaki bize daha önce arkadaşlık isteği yolladı mı?
                                //yolladıysa direkt arkadaş ekle, yollamadıysa sadece istek yolla
                                checkOwnFriendRequests(person_id, friend_key, callBack);
                            }
                            //add to friend req list
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friend_reqs = (ArrayList<String>)
                                    snapshot.getChildren().iterator().next()
                                            .child("friend_reqs").getValue();
                            if(friend_reqs == null){
                                friend_reqs = new ArrayList<>();
                            }
                            friend_reqs.add(person_id);
                            callBack.onCallBack(snapshot.getChildren().iterator().next().getKey(), friend_reqs);
                        }else{
                            //eğer zaten arkadaşsak
                            Toast.makeText(getContext(),
                                    "Zaten arkadaşsınız!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //eğer karşıdakinin arkadaşları yoksa:
                        DataSnapshot friend_reqs_ss = snapshot.getChildren().iterator().next()
                                .child("friend_reqs");
                        //arkadaşlık istekleri de sıfır değilse
                        if(friend_reqs_ss.getChildrenCount() != 0){
                            //arkadaşlık isteği daha önce gönderdik mi
                            boolean user_already_added = false;
                            for(DataSnapshot friend_req_ss: friend_reqs_ss.getChildren()){
                                if(person_id.equals(friend_req_ss.getValue().toString())){
                                    user_already_added = true;
                                }
                            }
                            if(!user_already_added){
                                checkOwnFriendRequests(person_id, friend_key, callBack);
                            }else {
                                Log.d(TAG, "önceden bu maile istek yollanmış: " + email);
                                Toast.makeText(getContext(),
                                        "Daha önce istek gönderdin!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //karşıdakinin arkadaşları yok ve arkadaşlık isteği yok
                            Log.d(TAG, "karşıdakinin arkadaşları yok ve arkadaşlık isteği yok");
                            checkOwnFriendRequests(person_id, friend_key, callBack);
                        }
                    }
                }else{
                    //eğer bu maile sahip biri yoksa
                    Toast.makeText(getContext(),
                            "Bu mail adresine sahip kayıtlı kullanıcı yok!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "bu maile sahip biri yok: " + email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOwnFriendRequests(final String person_id, final String friend_key, final FireBaseCallBack callBack){
        user_ref.child(person_id).child("friend_reqs").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    boolean user_already_added_to_own_list = false;
                    for(DataSnapshot friend_req_ss: snapshot.getChildren()){
                        if(friend_key.equals(Objects.requireNonNull(friend_req_ss.getValue()).toString())){
                            user_already_added_to_own_list = true;
                        }
                    }
                    //bizim ark istek listemizde karşnının keyi var
                    if(user_already_added_to_own_list){
                        //arkadaş ekle
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friends_own = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friends").getValue();
                        @SuppressWarnings("unchecked")
                        ArrayList<String> requests_own = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friend_reqs").getValue();
                        if(friends_own == null){
                            friends_own = new ArrayList<>();
                        }
                        if(requests_own == null){
                            requests_own = new ArrayList<>();
                        }
                        callBack.onAddCallBack(friends_own, requests_own, friend_key);
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friends = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friends").getValue();
                        @SuppressWarnings("unchecked")
                        ArrayList<String> requests = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friend_reqs").getValue();
                        if(friends == null){
                            friends = new ArrayList<>();
                        }
                        if(requests == null){
                            requests = new ArrayList<>();
                        }
                        callBack.onFriendsAddCallBack(friends, requests, friend_key);
                    }else{
                        //arkadaşlık isteği yolla
                        ArrayList<String> friend_reqs = new ArrayList<>();
                        friend_reqs.add(person_id);
                        callBack.onCallBack(friend_key, friend_reqs);
                    }
                }else{
                    ArrayList<String> friend_reqs = new ArrayList<>();
                    friend_reqs.add(person_id);
                    callBack.onCallBack(friend_key, friend_reqs);
                    Log.d(TAG, snapshot.toString() + "\n \"person_id\"friend_reqs ile ilişkili veri bulamadı, snapshot yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}