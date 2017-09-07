package com.koiti.mctjobs.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koiti.mctjobs.R;
import com.koiti.mctjobs.adapters.JobAdapter;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedFragment extends Fragment {

    private List<Job> jobList = new ArrayList<>();
    private DataBaseManagerJob mJob;
    private JobAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished, container, false);

        // Database
        mJob = new DataBaseManagerJob(getActivity().getApplicationContext());

        // Recicler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        jobList = mJob.getJobsList("FINALIZADA");
        mAdapter = new JobAdapter(jobList, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void add(Job job) {
        jobList.add(job);

        if(mAdapter instanceof JobAdapter)
            mAdapter.notifyDataSetChanged();
    }

    private void loadList() {
        if(isAdded()) {
            jobList = mJob.getJobsList("FINALIZADA");
            mAdapter = new JobAdapter(jobList, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void filter(String text)
    {
        if(mAdapter instanceof JobAdapter) {
            text = text.toLowerCase(Locale.getDefault());
            jobList.clear();

            if (text.length() == 0) {
                loadList();
            } else {
                jobList = mJob.getFilterJobsList("FINALIZADA", text);
                mAdapter = new JobAdapter(jobList, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
