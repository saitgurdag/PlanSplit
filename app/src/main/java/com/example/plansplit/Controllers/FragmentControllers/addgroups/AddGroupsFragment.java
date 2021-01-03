package com.example.plansplit.Controllers.FragmentControllers.addgroups;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;

import java.util.List;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private List<Friend> addgroups_personList;
    RecyclerView recyclerView;
    AddGroupsAdapter adapter;
    private static final Database database = Database.getInstance();
    private String person_id;
    Button buttonMakeGroup;
    EditText groupName_EditText;
    RadioGroup rgroupButton;
    AppCompatRadioButton rbuttonHouse, rbuttonWork, rbuttonTrip, rbuttonOther;
    private String group_type = "ev";
    ImageView groupPicture;
    int homePicture, workPicture, tripPicture, otherPicture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_addgroups, container, false);
        Database db = Database.getInstance();

        person_id = db.getPerson().getKey();

        recyclerView = root.findViewById(R.id.recycler_addgroups);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView, addgroups_personList);
        recyclerView.setAdapter(adapter);

        rbuttonHouse = root.findViewById(R.id.rbuttonFood);
        rbuttonWork = root.findViewById(R.id.rbuttonWear);
        rbuttonTrip = root.findViewById(R.id.rbuttonStationery);
        rbuttonOther = root.findViewById(R.id.rbuttonOther);

        homePicture = R.drawable.ic_home_black_radius;
        workPicture = R.drawable.ic_suitcase_radius;
        tripPicture = R.drawable.ic_trip_radius;
        otherPicture = R.drawable.ic_other;

        groupPicture = root.findViewById(R.id.GroupPicture_ImageView);
        groupName_EditText = root.findViewById(R.id.editTextExpenseName);
        buttonMakeGroup = root.findViewById(R.id.buttonMakeGroup);



        rgroupButton = (RadioGroup) root.findViewById(R.id.rgroupButton2);
        rgroupButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbuttonFood:
                        rbuttonHouse.setTextColor(Color.WHITE);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "ev";
                        groupPicture.setImageResource(homePicture);
                        System.out.println("Ev butonu tıklandı");
                        break;
                    case R.id.rbuttonWear:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.WHITE);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "iş";
                        groupPicture.setImageResource(workPicture);
                        System.out.println("iş butonu tıklandı");
                        break;
                    case R.id.rbuttonStationery:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.WHITE);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "seyahat";
                        groupPicture.setImageResource(tripPicture);
                        System.out.println("seyahat butonu tıklandı");
                        break;
                    case R.id.rbuttonOther:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.WHITE);
                        group_type = "diğer";
                        groupPicture.setImageResource(otherPicture);
                        System.out.println("Diğer butonu tıklandı");
                        break;
                }
            }
        });


        buttonMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewGroup();
            }
        });



        return root;
    }

    public void createNewGroup() {
        database.createNewGroup(person_id, AddGroupsAdapter.checked_personList, group_type, groupName_EditText, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                GroupsFragment GroupsFragment = new GroupsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, GroupsFragment).addToBackStack(null).commit();
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
