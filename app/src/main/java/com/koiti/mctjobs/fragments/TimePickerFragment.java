package com.koiti.mctjobs.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.StringTokenizer;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;
    private Integer hour;
    private Integer minutes;

    public static TimePickerFragment newInstance(String time, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setListener(listener);
        fragment.setDefault(time);
        return fragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    public void setDefault(String time) {
        StringTokenizer dateFormat = new StringTokenizer(time, ":");

        this.hour = Integer.parseInt(dateFormat.nextElement().toString());
        this.minutes = Integer.parseInt(dateFormat.nextElement().toString());
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minutes, true);
    }
}
