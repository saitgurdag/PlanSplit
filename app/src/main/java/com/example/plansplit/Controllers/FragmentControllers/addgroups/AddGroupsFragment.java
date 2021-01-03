package com.example.plansplit.Controllers.FragmentControllers.addgroups;

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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.AddGroupsAdapter;
import com.example.plansplit.Controllers.FragmentControllers.groups.GroupsFragment;
import com.example.plansplit.Controllers.HomeActivity;
import com.example.plansplit.Controllers.MyGroupActivity;
import com.example.plansplit.Models.Database;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddGroupsFragment extends Fragment {

    private static final String TAG = "AddGroupsFragment";
    private ArrayList<Friend> addgroups_personList;
    RecyclerView recyclerView;
    AddGroupsAdapter adapter, adapter2;
    private static final Database database = Database.getInstance();
    private String person_id;
    Button buttonMakeGroup, buttonSaveGroup;
    EditText groupName_EditText, group_name_edittext;
    RadioGroup rgroupButton;
    AppCompatRadioButton rbuttonHouse, rbuttonWork, rbuttonTrip, rbuttonOther;
    private String group_type = "ev";
    ImageView groupPicture;
    CardView addNewPerson;
    Bundle extras;
    Groups group;
    Intent intent;
    int homePicture, workPicture, tripPicture, otherPicture;
    final private String add_members = "add_members";
    final private String new_group = "new_group";
    final private String group_members = "group_members";
    Dialog dialog;
    AlertDialog alertDialog;
    boolean ctrlNewGroup = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_addgroups, container, false);
        Database db = new Database(getContext());
        person_id = db.getUserId();
        intent = new Intent(getContext(), MyGroupActivity.class);
        ctrlNewGroup = false;

        rbuttonHouse = root.findViewById(R.id.rbuttonFood);
        rbuttonWork = root.findViewById(R.id.rbuttonWear);
        rbuttonTrip = root.findViewById(R.id.rbuttonStationery);
        rbuttonOther = root.findViewById(R.id.rbuttonOther);
        buttonSaveGroup = root.findViewById(R.id.buttonSaveGroup);
        group_name_edittext = root.findViewById(R.id.editTextGroupName);
        addNewPerson = root.findViewById(R.id.add_new_person);

        homePicture = R.drawable.ic_home_black_radius;
        workPicture = R.drawable.ic_suitcase_radius;
        tripPicture = R.drawable.ic_trip_radius;
        otherPicture = R.drawable.ic_other;

        groupPicture = root.findViewById(R.id.GroupPicture_ImageView);
        buttonMakeGroup = root.findViewById(R.id.buttonMakeGroup);
        groupName_EditText = root.findViewById(R.id.editTextGroupName);
        rgroupButton = (RadioGroup) root.findViewById(R.id.rgroupButton2);

        extras = getArguments();
        if (extras != null && extras.keySet().contains("group")) {
            System.out.println("BURAYA GELDİM EXTRAS " + extras.getString("group"));
            Gson gson = new Gson();
            String json = extras.getString("group");
            group = gson.fromJson(json, Groups.class);
            final String group_key = group.getKey();

            buttonMakeGroup.setVisibility(View.INVISIBLE);
            addNewPerson.setVisibility(View.VISIBLE);
            buttonSaveGroup.setVisibility(View.VISIBLE);

            buttonMakeGroup.setVisibility(View.INVISIBLE);
            buttonSaveGroup.setVisibility(View.VISIBLE);
            group_name_edittext.setText(group.getGroup_name());
            group_type = group.getGroup_type();
            checkGroupType(group_type);

            recyclerView = root.findViewById(R.id.recycler_addgroups);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);

            adapter = new AddGroupsAdapter(getContext(), recyclerView, group_members, group_key);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new AddGroupsAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    String selected_person_id = AddGroupsAdapter.addgroups_personList.get(position).getKey();
                    String person_name = AddGroupsAdapter.addgroups_personList.get(position).getName();
                    System.out.println("tıklanılan kişi: " + person_name);
                    if(person_id.equals(group.getGroup_members().get(0)) || person_id.equals(selected_person_id)){
                        showAlert(selected_person_id, person_name);

                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                recyclerView = root.findViewById(R.id.recycler_addgroups);
                                recyclerView.setHasFixedSize(true);
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
                                recyclerView.setLayoutManager(mLayoutManager);

                                adapter = new AddGroupsAdapter(getContext(), recyclerView, group_members, group_key);
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                }
            });

            addNewPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_add_friend_to_group);
                    Button save_button = dialog.findViewById(R.id.AddFriendSaveButton);
                    Button exit_button = dialog.findViewById(R.id.buttonExitFriendSelection);

                    recyclerView = dialog.findViewById(R.id.recyclerFriends);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
                    recyclerView.setLayoutManager(mLayoutManager);

                    adapter = new AddGroupsAdapter(getContext(), recyclerView, add_members, group_key, person_id);
                    recyclerView.setAdapter(adapter);

                    dialog.show();

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            recyclerView = root.findViewById(R.id.recycler_addgroups);
                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager zLayoutManager = new GridLayoutManager(getContext(), 1);
                            recyclerView.setLayoutManager(zLayoutManager);

                            adapter = new AddGroupsAdapter(getContext(), recyclerView, group_members, group_key);
                            recyclerView.setAdapter(adapter);
                        }
                    });

                    save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Checklenen insan sayısı: " + AddGroupsAdapter.checked_personList.size());
                            addUserToGroup(group, AddGroupsAdapter.checked_personList);

                            dialog.dismiss();
                        }
                    });

                    exit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });

                }
            });


            buttonSaveGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateGroup(group_key, group_type, group_name_edittext);

                    database.getSelectedGroup(person_id, group_key, new Database.GroupCallBack() {
                        @Override
                        public void onGroupRetrieveSuccess(Groups selected_group) {
                            group = selected_group;
                            Gson gson = new Gson();
                            String json = gson.toJson(group);
                            intent.putExtra("group", json);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(String error_tag, String error) {

                        }
                    });
                }
            });

        }else {
            ctrlNewGroup = true;
            recyclerView = root.findViewById(R.id.recycler_addgroups);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            adapter = new AddGroupsAdapter(getContext(), recyclerView, new_group, person_id);
            recyclerView.setAdapter(adapter);

        }

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {
                if (ctrlNewGroup) {
                    Intent intent2 = new Intent(getContext(), HomeActivity.class);
                    String key = "groups";
                    intent2.putExtra("navigation", key);
                    startActivity(intent2);
                } else {
                    Gson gson = new Gson();
                    String json = gson.toJson(group);
                    intent.putExtra("group", json);
                    startActivity(intent);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);


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

    public void addUserToGroup(final Groups group, final ArrayList<Friend> checked_personList) {
        database.addUserToGroup(group, checked_personList, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), "Başarıyla eklendi", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateGroup(String group_key, String group_type, EditText group_name_edittext) {
        database.updateGroup(group_key, group_type, group_name_edittext, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeFromGroup(final String selected_person_id) {
        database.removeFromGroup(selected_person_id, group, new Database.DatabaseCallBack() {
            @Override
            public void onSuccess(String success) {
                Log.d(TAG, success);
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                if(success.equals("Grup silindi")){
                    Intent intent2 = new Intent(getContext(), HomeActivity.class);
                    String key = "groups";
                    intent2.putExtra("navigation", key);
                    startActivity(intent2);
                } else if (selected_person_id.equals(person_id)){
                    Intent intent2 = new Intent(getContext(), HomeActivity.class);
                    String key = "groups";
                    intent2.putExtra("navigation", key);
                    startActivity(intent2);
                }
            }

            @Override
            public void onError(String error_tag, String error) {
                Log.e(TAG, error_tag + ": " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(final String person_id, final String person_name) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        final CharSequence[] remove_action = {person_name + " adlı kişiyi gruptan çıkar"};
        mBuilder.setItems(remove_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeFromGroup(person_id);
                Toast.makeText(getContext(), person_name + " adlı kişi gruptan çıkarıldı", Toast.LENGTH_SHORT).show();

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

}
