package com.example.plansplit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Objects.FriendRequest;
import com.example.plansplit.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestsViewHolder>{
    private static final String TAG = "FriendRequestsAdapter";
    private ArrayList<FriendRequest> requests;
    private Context mCtx;
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_ref = db_ref.child("users");
    private String person_id;
    private RecyclerView m_RecyclerView;

    public FriendRequestsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.requests = new ArrayList<>();
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        getRequests(requestCallBack);
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mCtx).inflate(R.layout.item_friend_request, parent, false);
        FriendRequestsAdapter.RequestsViewHolder rvh = new FriendRequestsAdapter.RequestsViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsViewHolder holder, int position){
        FriendRequest request = requests.get(position);

        holder.name.setText(request.getName());
        holder.email.setText(request.getEmail());
        holder.foto.setImageResource(request.getFoto());
    }

    @Override
    public int getItemCount(){
        return requests.size();
    }

    public class RequestsViewHolder extends RecyclerView.ViewHolder {
        public ImageView foto;
        public TextView name;
        public TextView email;

        public RequestsViewHolder(@NonNull View itemView){
            super(itemView);
            this.foto = itemView.findViewById(R.id.friend_requests_image);
            this.name = itemView.findViewById(R.id.friend_requests_name);
            this.email = itemView.findViewById(R.id.friend_requests_email);

            itemView.findViewById(R.id.friend_requests_accept).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final String req_id = requests.get(position).getKey();
                        addAsFriend(req_id, addCallBack);
                        Log.i(TAG, "friend request id: " + req_id);
                        Log.i(TAG, "request position: " + position);
                        Log.i(TAG, "users own id: " + person_id);
                    }

                }
            });

            itemView.findViewById(R.id.friend_requests_cancel).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final String req_id = requests.get(position).getKey();
                        refuseRequest(req_id, refuseCallBack);
                        Log.i(TAG, "friend request id: " + req_id);
                        Log.i(TAG, "request position: " + position);
                        Log.i(TAG, "users own id: " + person_id);
                    }
                }
            });
        }
    }

    //**********************************************************************************************

    private interface FireBaseAddCallBack{
        void onAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id);
        void onFriendsAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id);
    }
    private interface FireBaseRequestCallBack{
        void onRequestCallBack(FriendRequest request);
    }
    private interface FireBaseRefuseCallBack{
        void onRefuseCallBack(ArrayList<String> friend_reqs, String req_id);
    }

    private final FireBaseRefuseCallBack refuseCallBack = new FireBaseRefuseCallBack(){
        @Override
        public void onRefuseCallBack(ArrayList<String> friend_reqs, String req_id){
            friend_reqs.remove(req_id);
            user_ref.child(person_id).child("friend_reqs").setValue(friend_reqs);
            Toast.makeText(mCtx, "İstek reddedildi!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "request removed");
            requests.clear();
            notifyDataSetChanged();
            getRequests(requestCallBack);
        }
    };

    private final FireBaseAddCallBack addCallBack = new FireBaseAddCallBack(){
        @Override
        public void onAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id){
            boolean already_added = false;
            for(String key: friends){
                if(req_id.equals(key)){
                    already_added = true;
                    Log.d(TAG, "already friends in users list but request still exist");
                    Log.d(TAG, "person id:" + person_id);
                    Log.d(TAG, "request id:" + req_id);
                    break;
                }
            }
            if(!already_added){
                friends.add(req_id);
                user_ref.child(person_id).child("friends").setValue(friends);
            }
            friend_reqs.remove(req_id);
            user_ref.child(person_id).child("friend_reqs").setValue(friend_reqs);
            Toast.makeText(mCtx, "Arkadaş eklendi", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "added as friend");
            requests.clear();
            notifyDataSetChanged();
            getRequests(requestCallBack);
        }

        @Override
        public void onFriendsAddCallBack(ArrayList<String> friends, ArrayList<String> friend_reqs, String req_id){
            boolean already_added = false;
            for(String key: friends){
                if(person_id.equals(key)){
                    already_added = true;
                    Log.d(TAG, "already friends in request senders list but request still exist");
                    Log.d(TAG, "person id:" + person_id);
                    Log.d(TAG, "request id:" + req_id);
                    break;
                }
            }
            if(!already_added){
                friends.add(person_id);
                user_ref.child(req_id).child("friends").setValue(friends);
            }
            friend_reqs.remove(person_id);
            user_ref.child(req_id).child("friend_reqs").setValue(friend_reqs);
        }
    };

    private final FireBaseRequestCallBack requestCallBack  = new FireBaseRequestCallBack(){
        @Override
        public void onRequestCallBack(FriendRequest request){
            requests.add(request);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendRequestsAdapter.this);
        }
    };

    private void refuseRequest(final String req_id, final FireBaseRefuseCallBack refuseCallBack){
        user_ref.orderByKey().equalTo(person_id).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friend_reqs = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friend_reqs").getValue();
                        if(friend_reqs == null){
                            friend_reqs = new ArrayList<>();
                        }
                        refuseCallBack.onRefuseCallBack(friend_reqs, req_id);
                    }else{
                        Toast.makeText(mCtx, "something went wrong \n" + snapshot.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, snapshot.toString() + "\n \"person_id\" ile ilişkili veri bulamadı, snapshot yok");
                    }
                }else{
                    Toast.makeText(mCtx, "something went wrong \n" + snapshot.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, snapshot.toString() + "\n \"person_id\" ile ilişkili veri bulamadı, snapshot yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  getRequests(final FireBaseRequestCallBack callBack){
        user_ref.orderByKey().equalTo(person_id).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friend_reqs = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friend_reqs").getValue();
                        if(friend_reqs == null){
                            friend_reqs = new ArrayList<>();
                        }
                        for(final String req_key: friend_reqs){
                            user_ref.orderByKey().equalTo(req_key).addListenerForSingleValueEvent(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot){
                                    if(snapshot.exists()){
                                        if(req_key.equals(snapshot.getChildren().iterator().next().getKey())){
                                            /*fixme: kayıt sırasında resim almadığımızdan
                                                rastgele resim koydum
                                             */
                                            FriendRequest request = new FriendRequest(
                                                    R.drawable.denemeresim,
                                                    snapshot.getChildren().iterator().next().child("name")
                                                            .getValue().toString() + " "
                                                            + snapshot.getChildren().iterator().next().child("surname")
                                                            .getValue().toString(),
                                                    snapshot.getChildren().iterator().next().child("email")
                                                            .getValue().toString(),
                                                    snapshot.getChildren().iterator().next().getKey()
                                            );
                                            callBack.onRequestCallBack(request);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error){
                                    Log.e(TAG, error.getMessage());
                                    Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else{
                        Toast.makeText(mCtx, "something went wrong \n" + snapshot.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, snapshot.toString() + "\n \"person_id\" ile ilişkili veri bulamadı, snapshot yok");
                    }
                }else{
                    Toast.makeText(mCtx, "something went wrong \n" + snapshot.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, snapshot.toString() + "\n \"person_id\" ile ilişkili veri bulamadı, snapshot yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAsFriend(final String friend_id, final FireBaseAddCallBack callBack){
        user_ref.orderByKey().equalTo(person_id).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
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
                        callBack.onAddCallBack(friends, requests, friend_id);
                    }
                }else{
                    Log.d(TAG, snapshot.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        user_ref.orderByKey().equalTo(friend_id).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(friend_id.equals(snapshot.getChildren().iterator().next().getKey())){
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
                        callBack.onFriendsAddCallBack(friends, requests, friend_id);
                    }
                }else{
                    Log.d(TAG, snapshot.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, error.getMessage());
                Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


