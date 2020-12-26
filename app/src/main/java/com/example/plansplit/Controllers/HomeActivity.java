package com.example.plansplit.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.plansplit.Controllers.FragmentControllers.AddExpenseFragment;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "HomeActivity";
    private String personId;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Bundle bundle;
    String navigation_key;
    public static Uri personPhoto;
    SharedPreferences mPrefs;

    FirebaseAuth mAuth;
    private static final Database database = Database.getInstance();
    Database db = new Database();



    public String getPersonId() {
        return personId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mAuth = FirebaseAuth.getInstance();
        mPrefs = getSharedPreferences("userName", Context.MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Toolbar toolbar =  findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_personal, R.id.navigation_friends, R.id.navigation_groups, R.id.navigation_notifications)

                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);




        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
               bundle = new Bundle();
                bundle.putString("person_id", personId);
                navController.navigate(item.getItemId(), bundle);
                return true;
            }
        });
        navigationView=findViewById(R.id.nav_draw_view);

        //firebase'e ilk girişte mail isim soyisim kayıt yapılıyor.
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personId = acct.getId();
            String name = acct.getDisplayName();
            if (name == null){
                name = "No name";
            }
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("userName", name);
            prefsEditor.apply();
            String email = acct.getEmail();

            //todo:kod çökmesin diye bir süre daha soyisim çekicez
            // ama uygulama içi kullanım en kısa zamanda sıfırlanmalı
            String surname = acct.getFamilyName();
            if (surname == null){
                surname = "No surname";
            }

            personPhoto = acct.getPhotoUrl();
            setHeader(personPhoto,name,email);
            String image=personPhoto.toString();




            database.registerUser(personId, name, email, surname,image);
            Log.d(TAG, "user registered with this email: " + email + "\n" + "and this key: " + personId);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null && (extras.keySet().contains("friend") || extras.keySet().contains("group"))) {
            bundle = new Bundle();
            bundle.putString("person_id", personId);
            if (extras.keySet().contains("friend")) {
                bundle.putString("friend", extras.getString("friend"));
            } else if (extras.keySet().contains("group")){
                bundle.putString("group", extras.getString("group"));
            }
            navController.navigate(R.id.navigation_add_expense, bundle);
        }else if(extras != null && extras.keySet().contains("navigation")) {
            navigation_key = extras.getString("navigation");
            bundle = new Bundle();
            bundle.putString("person_id", personId);
            switch (navigation_key) {
                case "personal":
                    navController.navigate(R.id.navigation_personal, bundle);
                    break;
                case "friends":
                    navController.navigate(R.id.navigation_friends, bundle);
                    break;
                case "groups":
                    navController.navigate(R.id.navigation_groups, bundle);
                    break;
                case "notifications":
                    navController.navigate(R.id.navigation_notifications, bundle);
                    break;

            }
        }

      
        //----------------------------------------------------------------------

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();



                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.navigation_logout){
                    if (id == R.id.navigation_logout) {
                      
                        Log.d(TAG, "SignOut yapıldı");
                        signOut();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
                if (id==R.id.navigation_home){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    bundle = new Bundle();
                    bundle.putString("person_id", personId);
                    navController.navigate(R.id.navigation_personal, bundle);
                }
                return true;
            }
        });

    }

    private void setHeader(Uri personphoto,String name,String email) {
        View header = navigationView.getHeaderView(0);
        ImageView imageView=header.findViewById(R.id.imageViewHeaderProfilPhoto);
        TextView headerpersonname=header.findViewById(R.id.textHeaderPersonName);
        TextView headerpersonmail=header.findViewById(R.id.textHeaderMail);
        Picasso.with(this).load(personphoto).into(imageView);
        headerpersonname.setText(name);
        headerpersonmail.setText(email);
    }

    private void signOut() {
        mAuth.signOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HomeActivity.this, "Uygulamadan başarıyla çıkış yapıldı!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text=adapterView.getItemAtPosition(i).toString();
        AddExpenseFragment.sharemethod=text;
        //Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static Uri getPersonPhoto() {
        return personPhoto;
    }
}