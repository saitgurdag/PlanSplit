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

import com.example.plansplit.ui.mygroup.EventsFragment;
import com.example.plansplit.ui.mygroup.GroupOperationsFragment;
import com.example.plansplit.ui.mygroup.ListFragment;

public class MyGroup extends AppCompatActivity {


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

        listBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_place_mygroup, new ListFragment());
                fr.addToBackStack(null);
                list_titleTv.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                group_op_titletV.setVisibility(View.GONE);
                fr.commit();
            }
        });

        eventsBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_place_mygroup, new EventsFragment());
                fr.addToBackStack(null);
                events_titleTv.setVisibility(View.VISIBLE);
                list_titleTv.setVisibility(View.GONE);
                group_op_titletV.setVisibility(View.GONE);
                fr.commit();
            }
        });

        groupOpBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_place_mygroup, new GroupOperationsFragment());
                fr.addToBackStack(null);
                group_op_titletV.setVisibility(View.VISIBLE);
                events_titleTv.setVisibility(View.GONE);
                list_titleTv.setVisibility(View.GONE);
                fr.commit();
            }
        });



        backBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}