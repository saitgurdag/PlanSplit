package com.example.plansplit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.R;
import com.example.plansplit.Objects.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{
    private static final String TAG = "FriendsAdapter";
    private ArrayList<Friend> friends;
    private Context mCtx;
    private DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference user_ref = db_ref.child("users");
    private String person_id;
    private RecyclerView m_RecyclerView;

    public FriendsAdapter(Context mCtx, String person_id, RecyclerView m_RecyclerView){
        this.mCtx = mCtx;
        this.person_id = person_id;
        this.m_RecyclerView = m_RecyclerView;
        this.friends = new ArrayList<>();
        getFriends(friendsCallBack);
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendsViewHolder fvh = new FriendsViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position){
        Friend friend = friends.get(position);

        holder.friend_image.setImageResource(friend.getPerson_image());
        holder.friend_name.setText(friend.getName());
        holder.friend_amount.setText(friend.getAmount());
        holder.friend_amount.setTextColor(ContextCompat.
                getColor(holder.itemView.getContext(), friend.getColor()));
        holder.friend_amount_text.setText(friend.getAmount_text());
        holder.friend_amount_text.setTextColor(ContextCompat.
                getColor(holder.itemView.getContext(), friend.getColor()));
        holder.friend_layout.setBackgroundResource(friend.getLayout_background());
        holder.friend_image_balance.setImageResource(friend.getImage_background());


    }

    @Override
    public int getItemCount(){
        return friends.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        public ImageView friend_image;
        public TextView friend_name;
        public TextView friend_amount;
        public TextView friend_amount_text;
        public RelativeLayout friend_layout;
        public ImageView friend_image_balance;

        public FriendsViewHolder(@NonNull View itemView){
            super(itemView);
            friend_image = itemView.findViewById(R.id.friend_image);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_amount = itemView.findViewById(R.id.friend_amount);
            friend_amount_text = itemView.findViewById(R.id.friend_amount_text);
            friend_layout = itemView.findViewById(R.id.friend_background_layout);
            friend_image_balance = itemView.findViewById(R.id.friend_image_balance);

            itemView.findViewById(R.id.friend_card).setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //todo arkadaş detaylar ve arkadaş sil dialog seçeneği
                    }
                    return true;
                }
            });
        }
    }

    //**********************************************************************************************

    private interface FireBaseFriendsCallBack{
        void onFriendsCallBack(Friend friend);
    }

    private final FireBaseFriendsCallBack friendsCallBack = new FireBaseFriendsCallBack(){
        @Override
        public void onFriendsCallBack(Friend friend){
            friends.add(friend);
            notifyDataSetChanged();
            m_RecyclerView.setAdapter(FriendsAdapter.this);
        }
    };

    private void getFriends(final FireBaseFriendsCallBack friendsCallBack){
        user_ref.orderByKey().equalTo(person_id).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    if(person_id.equals(snapshot.getChildren().iterator().next().getKey())){
                        @SuppressWarnings("unchecked")
                        ArrayList<String> friends = (ArrayList<String>)
                                snapshot.getChildren().iterator().next()
                                        .child("friends").getValue();
                        if(friends == null){
                            friends = new ArrayList<>();
                        }
                        for(final String friend_key: friends){
                            user_ref.orderByKey().equalTo(friend_key).addListenerForSingleValueEvent(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot){
                                    if(snapshot.exists()){
                                        if(friend_key.equals(snapshot.getChildren().iterator().next().getKey())){
                                            /*fixme: kayıt sırasında resim almadığımızdan
                                                rastgele resim koydum
                                             */

                                            //fixme borçların database'de tutulma yöntemi geçici, acilen değişmeli
                                            Friend friend = new Friend(
                                                    R.drawable.denemeresim,
                                                    snapshot.getChildren().iterator().next().child("name")
                                                            .getValue().toString() + " "
                                                            + snapshot.getChildren().iterator().next().child("surname")
                                                            .getValue().toString(),
                                                    0,
                                                    snapshot.getChildren().iterator().next().getKey()
                                            );
                                            friendsCallBack.onFriendsCallBack(friend);
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
}
