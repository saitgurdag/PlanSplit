package com.example.plansplit.ui.ShareMethod;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareMethodViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareMethodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
