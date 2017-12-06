package com.koiti.mctjobs.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.Application;
import com.koiti.mctjobs.BuildConfig;
import com.koiti.mctjobs.R;
import com.koiti.mctjobs.adapters.JobAdapter;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobsFragment extends Fragment {

    private static final String TAG = JobsFragment.class.getSimpleName();
    private static final String KEY_LAYOUT_MANAGER = "layoutJobsManager";

    private List<Job> jobList = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    private DataBaseManagerJob mJob;
    private JobAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private UserSessionManager mSession;
    private Tracker tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        // Session
        mSession = new UserSessionManager(getActivity().getApplicationContext());

        // Get tracker.
        tracker = ((Application) getActivity().getApplication()).getTracker();

        // Database
        mJob = new DataBaseManagerJob(getActivity().getApplicationContext());

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

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

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Sync jobs
                syncJobs();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryInternal);

        // Load list
        jobList = mJob.getJobsList("NOTIFICADA");
        mAdapter = new JobAdapter(jobList, getActivity(), false);
        mRecyclerView.setAdapter(mAdapter);

        // Start refresh animation
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Call default Sync jobs
        syncJobs();

        super.onActivityCreated(savedInstanceState);
    }

    private void loadList() {
        if(isAdded()) {
            jobList = mJob.getJobsList("NOTIFICADA");
            mAdapter = new JobAdapter(jobList, getActivity(), false);
            mRecyclerView.setAdapter(mAdapter);
        }

        // Stop refresh animation
        swipeContainer.setRefreshing(false);
    }

    public void remove(Job job) {
        Iterator<Job> it = jobList.iterator();
        while (it.hasNext()) {
            Job one_job = it.next();
            if (one_job.getId() == job.getId()) {
                it.remove();
                break;
            }
        }

        if(mAdapter instanceof JobAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void syncJobs() {
        try {
            JSONObject params = new JSONObject();
            params.put("id_partner", mSession.getPartner());
            params.put("version", BuildConfig.VERSION_NAME);
            ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));

            AsyncHttpClient client = new AsyncHttpClient();
            client.setMaxRetriesAndTimeout(Constants.DEFAULT_MAX_RETRIES, Constants.DEFAULT_TIMEOUT);

            client.post(null, Constants.URL_GET_WORKS_API, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mJob.getDb().beginTransaction();
                    try {
                        if (response.getBoolean("successful")) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray works = data.getJSONArray("works");

                            // Parse and insert Jobs
                            mJob.parseJobs(works);

                            // Transaction successful
                            mJob.getDb().setTransactionSuccessful();
                        }
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

                        Toast.makeText(getActivity(), R.string.on_failure, Toast.LENGTH_LONG).show();

                    }finally {
                        // End transaction
                        mJob.getDb().endTransaction();

                        // Load adapter list
                        loadList();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    // Load adapter list
                    loadList();

                    super.onFailure(statusCode, headers, throwable, response);
                }
            });
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
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
                jobList = mJob.getFilterJobsList("NOTIFICADA", text);
                mAdapter = new JobAdapter(jobList, getActivity(), false);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
