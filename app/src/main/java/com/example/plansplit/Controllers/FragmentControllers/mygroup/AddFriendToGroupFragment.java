package com.example.plansplit.Controllers.FragmentControllers.mygroup;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.AddFriendToGroupAdapter;
import com.example.plansplit.Models.Objects.Friend;
import com.example.plansplit.R;

import java.util.ArrayList;

public class AddFriendToGroupFragment extends AppCompatDialogFragment {
    private static final String TAG = "AddFriendToGroupFragment";
    private Dialog dialogFriend;
    Button ExitBtn;
    Button OKBtn;
    RecyclerView recyclerView;
    AddFriendToGroupAdapter adapter;
    ArrayList<Friend> friendsList;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_friend_togroup,null);
*/
        dialogFriend = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        dialogFriend.setCancelable(false);
        dialogFriend.setContentView(R.layout.dialog_add_friend_togroup);
        ExitBtn = dialogFriend.findViewById(R.id.buttonExitFriendSelection);
        OKBtn = dialogFriend.findViewById(R.id.AddFriendOKButton);

        recyclerView = (RecyclerView) dialogFriend.findViewById(R.id.recyclerFriends);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);

        friendsList.add(new Friend(null, "ahmet", "null"));

        adapter = new AddFriendToGroupAdapter(this.getContext(), friendsList);
        recyclerView.setAdapter(adapter);
        ExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFriend.dismiss();
            }
        });
        OKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFriend.dismiss();

            }
        });






        return dialogFriend;

    }
}
