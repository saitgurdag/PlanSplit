package com.example.plansplit.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class MyGroupActivity extends AppCompatActivity {
    private static final String TAG = "MyGroupActivity";
    Database database = Database.getInstance();
    private String person_id = "";
    private Friend friend;
    boolean ctrlType=false;             //eğer friend'den geliyorsa true, gruptan geliyorsa false
    private String group_type_option_home = "ev";
    private String group_type_option_work = "iş";
    private String group_type_option_trip = "seyahat";
    private String group_type_option_other = "diğer";
    int homePicture = R.drawable.ic_home_black_radius;
    int workPicture = R.drawable.ic_suitcase_radius;
    int tripPicture = R.drawable.ic_trip_radius;
    int otherPicture = R.drawable.ic_other;
    String todolistfriend="berkjay";
    Bundle extras;

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MyGroupActivity.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mygroup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.mygroup_group_options)
                    Toast.makeText(MyGroupActivity.this, "Grup Ayarları Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_table_export)
                    Toast.makeText(MyGroupActivity.this, "Tablo Olarak Çıkar Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_quick_add)
                    Toast.makeText(MyGroupActivity.this, "Hızlı Ekle Seçildi", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    public void loadActivity(String key) {
        Intent intent=new Intent(this,HomeActivity.class);
        intent.putExtra("navigation",key);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);



        TextView groupnameTv = findViewById(R.id.group_title_mygroupTv);
        final TextView list_titleTv = findViewById(R.id.list_buttonTv);
        final TextView events_titleTv = findViewById(R.id.events_buttonTv);
        final TextView group_op_titletV = findViewById(R.id.group_op_buttonTv);
        final TextView remove_txt = findViewById(R.id.remove_friend);
        LinearLayout l = findViewById(R.id.remove_friend_linear);
        ImageView groupPhotoIv = findViewById(R.id.group_pictureIv);
        ImageButton listBttn = findViewById(R.id.task_listButton);
        ImageButton eventsBttn = findViewById(R.id.eventsButton);
        ImageButton groupOpBttn = findViewById(R.id.groupOpButton);
        ImageButton backBttn = findViewById(R.id.mygroup_back_button);
        ImageButton removeFriendBttn = findViewById(R.id.removeFriendButton);
        ImageButton menu = findViewById(R.id.mygroup_menuline_button);


        BottomNavigationView navView = findViewById(R.id.nav_view2);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){

                String fragmentKey = null;

                switch (item.getItemId()) {
                    case R.id.navigation_personal:
                        fragmentKey = "personal";

                        break;
                    case R.id.navigation_friends:
                        fragmentKey = "friends";
                        break;
                    case R.id.navigation_groups:
                        fragmentKey = "groups";
                        break;
                    case R.id.navigation_notifications:
                        fragmentKey = "notifications";
                        break;
                }
                loadActivity(fragmentKey);
                return true;
            }



        });



        final NavController navController = Navigation.findNavController(this, R.id.fragment_place_mygroup);

        String group_title = "Group title";

         extras = getIntent().getExtras();
        if(extras != null && extras.keySet().contains("group_title")){
            navView.getMenu().getItem(2).setChecked(true);
            group_title = extras.getString("group_title");
            String resid = extras.getString("group_image");
            if(resid.equals(group_type_option_home)){
                groupPhotoIv.setImageResource(homePicture);
            }else if(resid.equals(group_type_option_work)){
                groupPhotoIv.setImageResource(workPicture);
            } else if(resid.equals(group_type_option_trip)){
                groupPhotoIv.setImageResource(tripPicture);
            } else if(resid.equals(group_type_option_other)){
                groupPhotoIv.setImageResource(otherPicture);
            }
            System.out.println("grouppp");
            groupOpBttn.setVisibility(View.VISIBLE);
            removeFriendBttn.setVisibility(View.INVISIBLE);
            l.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.VISIBLE);

            ctrlType=false;
        }else if(extras != null && extras.keySet().contains("friend")){
            navView.getMenu().getItem(1).setChecked(true);
            Gson gson = new Gson();
            String json = extras.getString("friend");
            friend = gson.fromJson(json, Friend.class);
             todolistfriend=friend.getKey();
             System.out.println(todolistfriend);
            if(extras.keySet().contains("person_id")){
                person_id = extras.getString("person_id");
            }
           // groupPhotoIv.setImageResource(friend.getPerson_image());
            Picasso.with(getApplicationContext()).load(friend.getPerson_image()).into(groupPhotoIv);
            group_title = friend.getName();
            System.out.println("friend" + friend.getName());
            groupOpBttn.setVisibility(View.INVISIBLE);
            removeFriendBttn.setVisibility(View.VISIBLE);
            l.setVisibility(View.VISIBLE);
            menu.setVisibility(View.INVISIBLE);

            ctrlType=true;
        }

        list_titleTv.setVisibility(View.GONE);
        events_titleTv.setVisibility(View.VISIBLE);
        groupnameTv.setText(group_title);
        group_op_titletV.setVisibility(View.GONE);
        remove_txt.setVisibility(View.GONE);


        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extras != null && extras.keySet().contains("group_title")){
                    Bundle bundlelistgroup = new Bundle();
                    bundlelistgroup.putString("group_title", extras.getString("group_title"));
                    navController.navigate(R.id.navi_todo_list, bundlelistgroup);
                    list_titleTv.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);

                }
                else {

                    Bundle bundlelistfriends = new Bundle();
                    bundlelistfriends.putString("friend_key", todolistfriend);
                    bundlelistfriends.putString("person_key", person_id);
                    navController.navigate(R.id.navi_todo_list, bundlelistfriends);
                    list_titleTv.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                }

                if(!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_events);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                if(!ctrlType)
                    group_op_titletV.setVisibility(View.GONE);
            }
        });

        if(!ctrlType) {
            groupOpBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.navi_operation);
                    group_op_titletV.setVisibility(View.VISIBLE);
                    events_titleTv.setVisibility(View.GONE);
                    list_titleTv.setVisibility(View.GONE);
                }
            });
        }else{
            removeFriendBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.removeFriend(person_id, friend.getKey(), new Database.DatabaseCallBack() {
                        @Override
                        public void onSuccess(String success) {
                            Log.i(TAG, success);
                            Toast.makeText(getBaseContext(), "Arkadaş silindi", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }

                        @Override
                        public void onError(String error_tag, String error) {
                            Log.e(TAG, error_tag + ": " + error);
                        }
                    });
                }
            });
        }



        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


}