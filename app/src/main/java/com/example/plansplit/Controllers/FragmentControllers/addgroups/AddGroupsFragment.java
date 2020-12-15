package com.example.plansplit.Controllers.FragmentControllers.addgroups;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private List<Friend> addgroups_personList;
    RecyclerView recyclerView;
    AddGroupsAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference group_ref;
    private String person_id;
    Button buttonMakeGroup;
    EditText editTextTextPersonName;
    RadioGroup rgroupButton;
    AppCompatRadioButton rbuttonHouse, rbuttonWork, rbuttonTrip, rbuttonOther;
    private String group_type = "ev";
    private ArrayList<Groups> groupsArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_addgroups, container, false);

        database = FirebaseDatabase.getInstance();
        group_ref = database.getReference("groups");

        final HomeActivity home = (HomeActivity) getContext();

        person_id = home.getPersonId();

        recyclerView = root.findViewById(R.id.recycler_addgroups);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView, addgroups_personList);
        recyclerView.setAdapter(adapter);

        rbuttonHouse = root.findViewById(R.id.rbuttonHouse);
        rbuttonWork = root.findViewById(R.id.rbuttonWork);
        rbuttonTrip = root.findViewById(R.id.rbuttonTrip);
        rbuttonOther = root.findViewById(R.id.rbuttonOther);

        editTextTextPersonName = root.findViewById(R.id.editTextTextPersonName);
        buttonMakeGroup = root.findViewById(R.id.buttonMakeGroup);

        rgroupButton = (RadioGroup) root.findViewById(R.id.rgroupButton);
        rgroupButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbuttonHouse:
                        rbuttonHouse.setTextColor(Color.WHITE);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "ev";
                        System.out.println("Ev butonu tıklandı");
                        break;
                    case R.id.rbuttonWork:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.WHITE);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "iş";
                        System.out.println("iş butonu tıklandı");
                        break;
                    case R.id.rbuttonTrip:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.WHITE);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "seyahat";
                        System.out.println("seyahat butonu tıklandı");
                        break;
                    case R.id.rbuttonOther:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.WHITE);
                        group_type = "diğer";
                        System.out.println("Diğer butonu tıklandı");
                        break;
                }
            }
        });


        buttonMakeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AddGroupsAdapter.checked_personList.size() > 0) {
                    final String group_name = editTextTextPersonName.getText().toString().trim();
                    Groups group = new Groups(group_name,group_type);
                    group.addFriend(person_id);
                    for (Friend friend : AddGroupsAdapter.checked_personList) {
                        String friendKey = friend.getKey();
                        group.addFriend(friendKey);
                    }
                    group_ref.push().setValue(group);

                    getFragmentManager().popBackStack();

                } else {
                    Toast.makeText(getContext(), "Lütfen Arkadaş Seçiniz", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return root;
    }


}
