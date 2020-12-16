package com.example.plansplit.Controllers.FragmentControllers.friends;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.plansplit.R;

public class FriendExpenseNoteDialog extends AppCompatDialogFragment {

    private EditText edittextnote;
    private  FriendExpenseFragment friendExpenseFragment;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        final View view=layoutInflater.inflate(R.layout.group_expense_note_dialog,null);
        edittextnote=view.findViewById(R.id.editTextNote);
        friendExpenseFragment=new FriendExpenseFragment();

        builder.setView(view)
                .setTitle(getResources().getString(R.string.group_expense_note_dialog_title))
                .setNegativeButton(getResources().getString(R.string.group_expense_note_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })

                .setPositiveButton(getResources().getString(R.string.group_expense_note_dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        friendExpenseFragment.setNote(edittextnote.getText().toString());
                    }
                });







        return builder.create(); }

    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setTextColor(getResources().getColor(R.color.dialog_positive_button_color));
        positive.setTextSize(15);
        Button negative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        negative.setTextColor(getResources().getColor(R.color.colorAccent));
        negative.setTextSize(15);
    }



    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener=(GroupExpenseNoteDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    public interface GroupExpenseNoteDialogListener{
        void applyTexts(String note);
    }
*/
}
