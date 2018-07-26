package com.koiti.mctjobs.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.StringTokenizer;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private Integer year;
    private Integer month;
    private Integer day;

    public static DatePickerFragment newInstance(String date, DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener);
        fragment.setDefault(date);
        return fragment;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public void setDefault(String date) {
        StringTokenizer dateFormat = new StringTokenizer(date, "-");

        // -1 because january is zero
        this.year = Integer.parseInt(dateFormat.nextElement().toString());
        this.month = (Integer.parseInt(dateFormat.nextElement().toString()) - 1);
        this.day = Integer.parseInt(dateFormat.nextElement().toString());
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}