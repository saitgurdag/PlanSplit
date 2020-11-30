package com.example.plansplit.ui.groups;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.plansplit.R;

public class GroupExpenseNoteDialog extends AppCompatDialogFragment {

    private EditText edittextnote;
    private  GroupExpenseFragment groupExpenseFragment;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        LayoutInflater layoutInflater=getActivity().getLayoutInflater();
        final View view=layoutInflater.inflate(R.layout.group_expense_note_dialog,null);
        edittextnote=view.findViewById(R.id.editTextNote);
        groupExpenseFragment=new GroupExpenseFragment();

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
                            groupExpenseFragment.setNote(edittextnote.getText().toString());
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
