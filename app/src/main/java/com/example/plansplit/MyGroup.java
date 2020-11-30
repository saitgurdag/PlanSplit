package com.example.plansplit;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.plansplit.ui.mygroup.EventsFragment;
import com.example.plansplit.ui.mygroup.GroupOperationsFragment;
import com.example.plansplit.ui.mygroup.ListFragment;

public class MyGroup extends AppCompatActivity {
    //NavigationView navigationView;

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(MyGroup.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.mygroup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.mygroup_group_options)
                    Toast.makeText(MyGroup.this, "Grup Ayarları Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_table_export)
                    Toast.makeText(MyGroup.this, "Tablo Olarak Çıkar Seçildi", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.mygroup_quick_add)
                    Toast.makeText(MyGroup.this, "Hızlı Ekle Seçildi", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);

        TextView groupnameTv = findViewById(R.id.group_title_mygroupTv);
        final TextView list_titleTv = findViewById(R.id.list_buttonTv);
        final TextView events_titleTv = findViewById(R.id.events_buttonTv);
        final TextView group_op_titletV = findViewById(R.id.group_op_buttonTv);
        //BottomNavigationView navView = findViewById(R.id.nav_view_mygroup);
        list_titleTv.setVisibility(View.GONE);
        events_titleTv.setVisibility(View.VISIBLE);
        group_op_titletV.setVisibility(View.GONE);
        ImageView groupPhotoIv = findViewById(R.id.group_pictureIv);


        String group_title = "Group title";

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            group_title = extras.getString("group_title");
            int resid = extras.getInt("group_image");
            groupPhotoIv.setImageResource(resid);
        }

        groupnameTv.setText(group_title);


        ImageButton listBttn = (ImageButton) findViewById(R.id.task_listButton);
        ImageButton eventsBttn = (ImageButton) findViewById(R.id.eventsButton);
        ImageButton groupOpBttn = (ImageButton) findViewById(R.id.groupOpButton);
        ImageButton backBttn = (ImageButton) findViewById(R.id.mygroup_back_button);

        final NavController navController = Navigation.findNavController(this, R.id.fragment_place_mygroup);

        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_todo_list);
                list_titleTv.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                group_op_titletV.setVisibility(View.GONE);
            }
        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_events);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                group_op_titletV.setVisibility(View.GONE);
            }
        });

        groupOpBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navi_operation);
                group_op_titletV.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                list_titleTv.setVisibility(View.GONE);
            }
        });

        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

/*
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_personal, R.id.navigation_friends, R.id.navigation_groups, R.id.navigation_notifications)

                .build();
        NavController navController = Navigation.findNavController(MyGroup.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(MyGroup.this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navigationView=findViewById(R.id.nav_draw_view);
*/
    }


}