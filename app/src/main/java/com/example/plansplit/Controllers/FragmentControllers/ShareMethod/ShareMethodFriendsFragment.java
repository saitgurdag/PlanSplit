package com.example.plansplit.Controllers.FragmentControllers.ShareMethod;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Controllers.Adapters.ShareMethodAdapter;
import com.example.plansplit.Controllers.FragmentControllers.friends.FriendExpenseFragment;
import com.example.plansplit.R;
import com.example.plansplit.Models.Objects.Person;


import java.util.ArrayList;
import java.util.List;


public class ShareMethodFriendsFragment extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ShareMethodFriendsFragment";
    private Dialog dialogFriend;
    Button sharemethodExitBtn;
    Spinner spinnerFriend;
    Button sharemethodOKBtn;
    RecyclerView recyclerView;
    ShareMethodAdapter adapter;
    List<Person> ShareFriendsPersonList;

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        {

            dialogFriend = new Dialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            dialogFriend.setCancelable(false);
            dialogFriend.setContentView(R.layout.fragment_dialog_share_method);
            sharemethodExitBtn = dialogFriend.findViewById(R.id.shareMethodExitButton);
            sharemethodOKBtn = dialogFriend.findViewById(R.id.buttonShareMethodOk);
            spinnerFriend = dialogFriend.findViewById(R.id.shareMethodSpinner);


            ArrayAdapter<CharSequence> adapter3=ArrayAdapter.createFromResource(this.getContext(),
                    R.array.shareMethodSpinnerItems, android.R.layout.simple_spinner_item);
            adapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spinnerFriend.setAdapter(adapter3);
            spinnerFriend.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)this.getContext());   //Burda bi OLAY VAR


            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fragment_dialog_share_method, null);*/


            //builder.setView(view);

            recyclerView = (RecyclerView) dialogFriend.findViewById(R.id.recycler_share_method);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            ShareFriendsPersonList = new ArrayList<>();

            ShareFriendsPersonList.add(new Person("veli", R.drawable.denemeresim, 0));
            ShareFriendsPersonList.add(new Person("hüseyin", R.drawable.denemeresim, 0));
            ShareFriendsPersonList.add(new Person("ali", R.drawable.denemeresim, 0));
            ShareFriendsPersonList.add(new Person("sami", R.drawable.denemeresim, 0));


            adapter = new ShareMethodAdapter(this.getContext(), ShareFriendsPersonList);
            recyclerView.setAdapter(adapter);
            sharemethodExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogFriend.dismiss();
                }
            });
            sharemethodOKBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogFriend.dismiss();
                    FriendExpenseFragment.shareBtn.setText(FriendExpenseFragment.sharemethod);

                }
            });

            return dialogFriend;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
