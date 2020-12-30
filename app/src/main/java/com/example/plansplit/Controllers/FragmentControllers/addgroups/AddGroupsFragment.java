package com.example.plansplit.Controllers.FragmentControllers.addgroups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private ArrayList<Friend> addgroups_personList;
    private ArrayList<Friend> otherFriends = new ArrayList<Friend>();
    ArrayList<Friend> group_members_objects;
    Groups group;
    RecyclerView recyclerView;
    private AddGroupsAdapter.ItemClickListener mListener;
    AddGroupsAdapter adapter, adapter2;
    private static final Database database = Database.getInstance();
    ArrayList<String> group_members_ID;
    private String person_id;
    Button buttonMakeGroup, buttonSaveGroup;
    EditText groupName_EditText, group_name_edittext;
    RadioGroup rgroupButton;
    CardView addNewPerson;
    AppCompatRadioButton rbuttonHouse, rbuttonWork, rbuttonTrip, rbuttonOther;
    private String group_type = "ev";
    ImageView groupPicture;
    int homePicture, workPicture, tripPicture, otherPicture;
    Dialog dialog;
    AlertDialog alertDialog;

    public void setGroup(Groups group) {
        this.group = group;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_addgroups, container, false);
        group_members_objects = new ArrayList<>();
        person_id = getArguments().getString("person_id");
        System.out.println(person_id);
        Database database2 = new Database();
        String useriddeneme = database2.getUserId();
        System.out.println("database denemesi id: " + useriddeneme);

        addNewPerson = root.findViewById(R.id.add_new_person);
        buttonMakeGroup = root.findViewById(R.id.buttonMakeGroup);
        buttonSaveGroup = root.findViewById(R.id.buttonSaveGroup);
        group_name_edittext = root.findViewById(R.id.editTextExpenseName);
        groupPicture = root.findViewById(R.id.GroupPicture_ImageView);
        rgroupButton = (RadioGroup) root.findViewById(R.id.rgroupButton2);
        rbuttonHouse = root.findViewById(R.id.rbuttonHome);
        rbuttonWork = root.findViewById(R.id.rbuttonWork);
        rbuttonTrip = root.findViewById(R.id.rbuttonTrip);
        rbuttonOther = root.findViewById(R.id.rbuttonOther);
        homePicture = R.drawable.ic_home_black_radius;
        workPicture = R.drawable.ic_suitcase_radius;
        tripPicture = R.drawable.ic_trip_radius;
        otherPicture = R.drawable.ic_other;

        recyclerView = root.findViewById(R.id.recycler_addgroups);
        recyclerView.setHasFixedSize(true);
        setOnClickListener();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);


        if (getArguments() != null && getArguments().keySet().contains("group")) {
            group = (Groups) getArguments().getSerializable("group");
            final String group_key = group.getKey(); //gerek yok

            group_members_ID = group.getGroup_members();
            adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView, group_members_ID, mListener);
            recyclerView.setAdapter(adapter);

            group_name_edittext.setText(group.getGroup_name());
            group_type = group.getGroup_type();
            checkGroupType(group_type);


            System.out.println("önce:" + group_members_ID);

            buttonMakeGroup.setVisibility(View.INVISIBLE);
            addNewPerson.setVisibility(View.VISIBLE);
            buttonSaveGroup.setVisibility(View.VISIBLE);

            addNewPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_add_friend_togroup);
                    Button ok_button = dialog.findViewById(R.id.AddFriendOKButton);
                    Button exit_button = dialog.findViewById(R.id.buttonExitFriendSelection);
                    final RecyclerView rvfriends = (RecyclerView) dialog.findViewById(R.id.recyclerFriends);

                    adapter2 = new AddGroupsAdapter(getContext(), person_id, group_members_ID, rvfriends);
                    rvfriends.setAdapter(adapter2);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    rvfriends.setLayoutManager(mLayoutManager);
                    System.out.println("adapterdaki sayı: " + adapter2.getItemCount());

                    //getOtherFriends(group_members_ID,person_id);
                    System.out.println("diğer arkadaşlar: " + otherFriends.size());
//                    if(adapter2.getItemCount()){
                    dialog.show();
//                    System.out.println("Arkadaş listesi : "+AddGroupsAdapter.addgroups_personList.size());
//                    } else{
//                        Toast.makeText(getContext(), "Eklenebilecek arkadaşınız bulunmamaktadır", Toast.LENGTH_SHORT).show();
//                    }

                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addUserToGroup(group, AddGroupsAdapter.checked_personList);
                        }
                    });

                    exit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                }
            });


        } else {
            adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView);
            recyclerView.setAdapter(adapter);
        }


        groupName_EditText = root.findViewById(R.id.editTextExpenseName);
        buttonMakeGroup = root.findViewById(R.id.buttonMakeGroup);

        rgroupButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbuttonHome:
                        rbuttonHouse.setTextColor(Color.WHITE);
                        rbuttonWork.setTextColor(Color.BLACK);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "ev";
                        groupPicture.setImageResource(homePicture);
                        System.out.println("Ev butonu tıklandı");
                        break;
                    case R.id.rbuttonWork:
                        rbuttonHouse.setTextColor(Color.BLACK);
                        rbuttonWork.setTextColor(Color.WHITE);
                        rbuttonTrip.setTextColor(Color.BLACK);
                        rbuttonOther.setTextColor(Color.BLACK);
                        group_type = "iş";
                        groupPicture.setImageResource(workPicture);
                        System.out.println("iş butonu tıklandı");
                        break;
                    case R.id.rbuttonTrip:
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

        buttonSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroup();
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

    public void addUserToGroup(final Groups group, final ArrayList<Friend> checked_personList) {
        database.addUserToGroup(group, checked_personList, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), "Başarıyla eklendi", Toast.LENGTH_SHORT).show();
                database.getAllGroups(person_id, GroupsFragment.groupsArrayList, new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        Log.d(TAG, success);
                        for (Groups group1 : GroupsFragment.groupsArrayList) {
                            if (group1.getKey().equals(group.getKey())) {

                                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
//                                Bundle bundle = new Bundle();
//                                bundle.putString("person_id", person_id);
//                                bundle.putSerializable("group", group1);
//                                addGroupsFragment.setArguments(bundle);


//                                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                                ft.detach(AddGroupsFragment.this).attach(AddGroupsFragment.this).commit();


                                Intent intent = new Intent(getContext(), MyGroupActivity.class);
                                intent.putExtra("person_id", person_id);
                                Gson gson = new Gson();
                                String json = gson.toJson(group1);
                                intent.putExtra("group", json);

                                getFragmentManager().beginTransaction().detach(AddGroupsFragment.this);
                                startActivity(intent);



//                                FragmentTransaction tr = getFragmentManager().beginTransaction();
//                                tr.replace(R.id.fragment_place_mygroup, addGroupsFragment);
//                                tr.commit();
                                //adapter.notifyDataSetChanged();

//                                for(Friend checkedfriend: checked_personList){
//                                    if(otherFriends.contains(checkedfriend)){
//                                        otherFriends.remove(checkedfriend);
//                                    }
//                                }
                                System.out.println("gruptan biri çıktıktan sonra grup üyeleri: " + group1.getGroup_members());
                            }
                        }
                    }

                    @Override
                    public void onError(String error_tag, String error) {

                    }
                });




                dialog.dismiss();
//                adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView, group_members_ID, mListener);
//                adapter2 = new AddGroupsAdapter(getContext(), person_id, group_members_ID, rvfriends);
                //adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);

            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateGroup() {
        database.updateGroup(group.getKey(), group_type, group_name_edittext, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getContext(), MyGroupActivity.class);
//                intent.putExtra("group_image", group_type);
//                intent.putExtra("group_title",group.getGroup_name());
//                intent.putExtra("person_id",person_id);
//                intent.putExtra("group", group);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

//                getActivity().finish();
//                startActivity(getActivity().getIntent());

//                Intent intent = new Intent(getContext(), HomeActivity.class);
//                intent.putExtra("group_update", group.getKey());
//                intent.putExtra("all_group_update",group);
//                startActivity(intent);
                database.getAllGroups(person_id, GroupsFragment.groupsArrayList, new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        Log.d(TAG, success);
                        for (Groups group1 : GroupsFragment.groupsArrayList) {
                            if (group1.getKey().equals(group.getKey())) {

                                Intent intent = new Intent(getContext(), MyGroupActivity.class);
//                                intent.putExtra("group_image", group1.getGroup_type());
//                                intent.putExtra("group_title", group1.getGroup_name());
                                intent.putExtra("person_id", person_id);
                                Gson gson = new Gson();
                                String json = gson.toJson(group1);
                                intent.putExtra("group", json);
                                startActivity(intent);

                                System.out.println("güncel " + group1.getGroup_name());
                            }
                        }
                    }

                    @Override
                    public void onError(String error_tag, String error) {

                    }
                });

//                Intent intent = new Intent(getContext(), MyGroupActivity.class);
//                for (Groups group1 : GroupsFragment.groupsArrayList) {
//                    if (group1.getKey().equals(group.getKey())) {
//                        intent.putExtra("group_update", group1);
//                    }
//                }
//
//                startActivity(intent);
//


//                intent.putExtra("group_image", group_type);
//                intent.putExtra("group_title", group.getGroup_name());
//                intent.putExtra("person_id", person_id);
//                intent.putExtra("group", group);
//                Gson gson = new Gson();
//                String json = gson.toJson(group);
//                intent.putExtra("group", json);
//                startActivity(intent);


                //  getActivity().recreate();

//                Intent intent = new Intent(getActivity(), MyGroupActivity.class); //sorunlu çalışıyor
//                intent.putExtra("group_image", group_type);
//                intent.putExtra("group_title",group.getGroup_name());
//                startActivity(intent);
//                EventsFragment eventsFragment = new EventsFragment();
//                FragmentManager fragmentManager = getParentFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.fragment_place_mygroup, eventsFragment).addToBackStack(null).commit();
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeFromGroup(String selected_person_id) {
        database.removeFromGroup(selected_person_id, group, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                database.getAllGroups(person_id, GroupsFragment.groupsArrayList, new Database.DatabaseCallBack() {
                    @Override
                    public void onSuccess(String success) {
                        Log.d(TAG, success);
                        for (Groups group1 : GroupsFragment.groupsArrayList) {
                            if (group1.getKey().equals(group.getKey())) {
                                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
//                                Bundle bundle = new Bundle();
//                                bundle.putString("person_id", person_id);
//                                bundle.putSerializable("group", group1);
//                                addGroupsFragment.setArguments(bundle);

//                                Intent intent = new Intent(getContext(), MyGroupActivity.class);
//                                intent.putExtra("person_id", person_id);
//                                Gson gson = new Gson();
//                                String json = gson.toJson(group1);
//                                intent.putExtra("group", json);
//                                startActivity(intent);


                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(AddGroupsFragment.this).attach(AddGroupsFragment.this).commit();

//                                FragmentManager fragmentManager = getParentFragmentManager();
//                                FragmentTransaction tr = fragmentManager.beginTransaction();
//                                tr.replace(R.id.fragment_place_mygroup, addGroupsFragment);
//                                tr.commit();
//                                adapter.notifyDataSetChanged();


                                System.out.println("gruptan biri çıktıktan sonra grup üyeleri: " + group1.getGroup_members());
                            }
                        }
                    }

                    @Override
                    public void onError(String error_tag, String error) {

                    }
                });
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener() {
        mListener = new AddGroupsAdapter.ItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                String selected_person_id = AddGroupsAdapter.addgroups_personList.get(position).getKey();
                String person_name = AddGroupsAdapter.addgroups_personList.get(position).getName();
                System.out.println("tıklanılan kişi: " + person_name);
                showAlert(selected_person_id, person_name);
            }
        };
    }

    private void showAlert(final String person_id, final String person_name) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        final CharSequence[] remove_action = {person_name + " adlı kişiyi gruptan çıkar"};
        mBuilder.setItems(remove_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeFromGroup(person_id);
                Toast.makeText(getContext(), person_name + "adlı kişi gruptan çıkarıldı", Toast.LENGTH_SHORT).show();
                //adapter.notifyDataSetChanged();
                //adapter = new AddGroupsAdapter(getContext(), person_id, recyclerView, group_members_ID, mListener);
//                adapter2 = new AddGroupsAdapter(getContext(), person_id, group_members_ID, rvfriends);
//                AddGroupsFragment addGroupsFragment = new AddGroupsFragment();
//                FragmentManager fragmentManager = getParentFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_place_mygroup, addGroupsFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

            }
        });
        alertDialog = mBuilder.create();
        alertDialog.show();
    }

    private void checkGroupType(String group_type) {
        if (group_type.equals("ev")) {
            rgroupButton.clearCheck();
            rbuttonHouse.setChecked(true);
            rbuttonHouse.setTextColor(Color.WHITE);
            rbuttonWork.setTextColor(Color.BLACK);
            rbuttonTrip.setTextColor(Color.BLACK);
            rbuttonOther.setTextColor(Color.BLACK);
            groupPicture.setImageResource(homePicture);
        } else if (group_type.equals("iş")) {
            rgroupButton.clearCheck();
            rbuttonWork.setChecked(true);
            rbuttonHouse.setTextColor(Color.BLACK);
            rbuttonWork.setTextColor(Color.WHITE);
            rbuttonTrip.setTextColor(Color.BLACK);
            rbuttonOther.setTextColor(Color.BLACK);
            groupPicture.setImageResource(workPicture);
        } else if (group_type.equals("seyahat")) {
            rgroupButton.clearCheck();
            rbuttonTrip.setChecked(true);
            rbuttonHouse.setTextColor(Color.BLACK);
            rbuttonWork.setTextColor(Color.BLACK);
            rbuttonTrip.setTextColor(Color.WHITE);
            rbuttonOther.setTextColor(Color.BLACK);
            groupPicture.setImageResource(tripPicture);
        } else if (group_type.equals("diğer")) {
            rgroupButton.clearCheck();
            rbuttonOther.setChecked(true);
            rbuttonHouse.setTextColor(Color.BLACK);
            rbuttonWork.setTextColor(Color.BLACK);
            rbuttonTrip.setTextColor(Color.BLACK);
            rbuttonOther.setTextColor(Color.WHITE);
            groupPicture.setImageResource(otherPicture);
        }

    }

    private void getOtherFriends(final ArrayList<String> group_members, String person_id) {
        Database.FriendCallBack friendsCallback_toadd = new Database.FriendCallBack() {
            @Override
            public void onFriendRetrieveSuccess(Friend friend) {
                String friendkey = friend.getKey();
                System.out.println("database'den arkadaş keyi alma: " + friendkey);
                if (!group_members.contains(friendkey)) {
                    for(Friend otherfriend: otherFriends){
                        String otherfriend_key = otherfriend.getKey();
                        if(otherFriends.isEmpty()){
                            otherFriends.add(friend);
                        }else if(!otherfriend_key.equals(friend.getKey())){
                            otherFriends.add(friend);
                        }
                    }
                    if(otherFriends.size() < 0){
                        Toast.makeText(getContext(), "Eklenebilecek arkadaşınız bulunmamaktadır", Toast.LENGTH_SHORT).show();
                    }else{
                        dialog.show();
                    }
                    System.out.println("otherfriends size: " + otherFriends.size());
                }
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(error_tag, error);
            }
        };
        System.out.println("otherfriends size12312313: " + otherFriends.size());
        database.getFriends(person_id, friendsCallback_toadd);

    }

}
