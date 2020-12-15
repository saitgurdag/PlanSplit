package com.example.plansplit.Controllers.Adapters;

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

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.FriendRequest;
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
    private Database database = Database.getInstance();
    private String person_id;
    private RecyclerView m_RecyclerView;

    public FriendRequestsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        loadRequests();
    }

    public void loadRequests(){
        this.requests = new ArrayList<>();
        database.getFriendRequests(person_id, requestCallBack);
    }

    private final Database.FriendRequestCallBack requestCallBack = new Database.FriendRequestCallBack() {
        @Override
        public void onFriendRequestRetrieveSuccess(FriendRequest friend_request) {
            requests.add(friend_request);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendRequestsAdapter.this);
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(TAG, error_tag + ": " + error);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendRequestsAdapter.this);
        }
    };

    private final Database.DatabaseCallBack callBack = new Database.DatabaseCallBack() {
        @Override
        public void onSuccess(String success) {
            Log.i(TAG, success);
            loadRequests();
        }

        @Override
        public void onError(String error_tag, String error) {
            Log.e(TAG, error_tag + ": " + error);
        }
    };

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
                        database.addAsFriend(person_id, req_id, callBack);
                        //addAsFriend(req_id, addCallBack);
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
                        database.declineFriendRequest(person_id, req_id, callBack);
                        Log.i(TAG, "friend request id: " + req_id);
                        Log.i(TAG, "request position: " + position);
                        Log.i(TAG, "users own id: " + person_id);
                    }
                }
            });
        }
    }
}


