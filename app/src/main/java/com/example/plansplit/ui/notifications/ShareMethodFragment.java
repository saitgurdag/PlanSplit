package com.example.plansplit.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Adapters.ShareMethodAdapter;
import com.example.plansplit.R;
import com.example.plansplit.ui.personal.ShareMethodPerson;

import java.util.ArrayList;
import java.util.List;

public class ShareMethodFragment extends Fragment {
    private static final String TAG = "ShareMethodFragment";
    private com.example.plansplit.ui.notifications.ShareMethodViewModel ShareMethodViewModel;
    RecyclerView recyclerView;
    ShareMethodAdapter adapter;
    List<ShareMethodPerson> ShareGroupsPersonList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ShareMethodViewModel =
                ViewModelProviders.of(this).get(ShareMethodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dialog_share_method, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_share_method);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(),1);
        recyclerView.setLayoutManager(mLayoutManager);
        ShareGroupsPersonList = new ArrayList<>();

        ShareGroupsPersonList.add(new ShareMethodPerson("ali",R.drawable.addgroups_ustekran,0));
        ShareGroupsPersonList.add(new ShareMethodPerson("veli",R.drawable.addgroups_ustekran,0));
        ShareGroupsPersonList.add(new ShareMethodPerson("osman",R.drawable.addgroups_ustekran,0));
        ShareGroupsPersonList.add(new ShareMethodPerson("mahmut",R.drawable.addgroups_ustekran,0));
        ShareGroupsPersonList.add(new ShareMethodPerson("hüseyin",R.drawable.addgroups_ustekran,0));
        ;

        Log.d(TAG, "BURADA");

       adapter =new ShareMethodAdapter(this.getContext(),ShareGroupsPersonList);
       recyclerView.setAdapter(adapter);


        return root;
    }
}
