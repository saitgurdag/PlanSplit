package com.example.plansplit.ui.friends;

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

import com.example.plansplit.Adapters.FriendsAdapter;
import com.example.plansplit.Objects.Friend;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";
    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_ref = db_ref.child("users");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_friends, container, false);

        Button add_friend_button = root.findViewById(R.id.friend_add_button);
        final EditText add_friend_email_text = root.findViewById(R.id.friends_add_email);

        final String person_id = getArguments().get("person_id").toString();

        ArrayList<Friend> friends_list = new ArrayList<>();
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie", 30));
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie", -50));
        friends_list.add(new Friend(R.drawable.denemeresim, "Marie Curie",40));

        m_RecyclerView = root.findViewById(R.id.recycler_friends);
        m_RecyclerView.setHasFixedSize(true);
        m_Adapter = new FriendsAdapter(friends_list);
        m_LayoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_RecyclerView.setAdapter(m_Adapter);


        add_friend_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = add_friend_email_text.getText().toString();
                sendFriendRequest(email, person_id, new FireBaseCallBack(){
                    @Override
                    public void onCallBack(String key, ArrayList<String> friend_reqs){
                        user_ref.child(key).child("friend_reqs").setValue(friend_reqs);
                        Toast.makeText(getContext(),
                                "Friend request sent.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return root;
    }

    private interface FireBaseCallBack{
        void onCallBack(String key, ArrayList<String> friend_reqs);
    }

    private void sendFriendRequest(String email, final String person_id, final FireBaseCallBack callBack){

        user_ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
                        Toast.makeText(getContext(),
                                "You are trying to add yourself!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DataSnapshot friends_snapshot
                            = snapshot.getChildren().iterator().next().child("friends");
                    if(friends_snapshot.getChildrenCount() != 0){
                        boolean user_already_added = false;
                        for(DataSnapshot friend_ss: friends_snapshot.getChildren()){
                            if(person_id.equals(friend_ss.getKey())){
                                user_already_added = true;
                            }
                        }
                        if(!user_already_added){
                            //add to friend req list
                            @SuppressWarnings("unchecked")
                            ArrayList<String> friend_reqs = snapshot.getChildren().iterator().next()
                                    .child("friend_reqs").getValue(ArrayList.class);
                            if(friend_reqs != null){
                                friend_reqs.add(person_id);
                            }else{
                                friend_reqs = new ArrayList<>();
                                friend_reqs.add(person_id);
                            }
                            callBack.onCallBack(snapshot.getChildren().iterator().next().getKey(), friend_reqs);
                        }else{
                            Toast.makeText(getContext(),
                                    "Person has already been added to your friend list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //add to friend req list
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friend_reqs = snapshot.getChildren().iterator().next()
                                .child("friend_reqs").getValue(ArrayList.class);
                        if(friend_reqs != null){
                            friend_reqs.add(person_id);
                        }else{
                            friend_reqs = new ArrayList<>();
                            friend_reqs.add(person_id);
                        }
                        callBack.onCallBack(snapshot.getChildren().iterator().next().getKey(), friend_reqs);
                    }
                }else{
                    Toast.makeText(getContext(),
                            "This mail address belongs no one", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });
    }
}