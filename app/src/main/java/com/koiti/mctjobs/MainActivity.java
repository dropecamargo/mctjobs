package com.koiti.mctjobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.koiti.mctjobs.fragments.AttendingFragment;
import com.koiti.mctjobs.fragments.FinishedFragment;
import com.koiti.mctjobs.fragments.JobsFragment;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.models.Job;
import com.koiti.mctjobs.models.Step;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DataBaseManagerJob mJob;
    private UserSessionManager mSession;
    private GPSTracker gps;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    private PagerAdapter mAdapter;
    private ActionBar actionBar;
    private SearchView edit_search;

    private JobsFragment jobsFragment;
    private AttendingFragment attendingFragment;
    private FinishedFragment finishedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Terms
        // Intent intent = new Intent(this, TermsActivity.class);
        // startActivity(intent);

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
        }

        // Database
        mJob = new DataBaseManagerJob(this);

        jobsFragment = new JobsFragment();
        attendingFragment = new AttendingFragment();
        finishedFragment = new FinishedFragment();

        // Drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Header
        View headerView =  navigationView.getHeaderView(0);
        TextView nav_username = (TextView) headerView.findViewById(R.id.nav_name);
        nav_username.setText(mSession.getName());

        CircleImageView nav_image = (CircleImageView) headerView.findViewById(R.id.nav_image);
        nav_image.setImageBitmap(Image.decodeBase64(mSession.getImage()));

        // Set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(R.string.app_def_name);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Pager
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(mAdapter);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        System.out.println("push....");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Search view
        edit_search = (SearchView) menu.findItem(R.id.edit_search).getActionView();;
        edit_search.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_item_turn:
                turnAction();
                break;

            case R.id.nav_item_close:
                finish();
                break;

            case R.id.nav_item_logout:
                mSession.logout();
                finish();
                break;
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        // Filter jobs
        jobsFragment.filter(s);

        // Filter attending
        attendingFragment.filter(s);

        // Filter finished
        finishedFragment.filter(s);

        return false;
    }

    public void turnAction() {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Intent intent = new Intent(MainActivity.this, TurnActivity.class);
            startActivity(intent);
        } else {
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onDestroy() {
        mJob.close();

        super.onDestroy();
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        Job job = null;
        if(Constants.PICK_STEP_REQUEST == requestCode)
        {
            switch(resultCode) {
                case Constants.RESULT_REFRESH_JOBS:
                    job = mJob.getJob(data.getIntExtra("JOB", 0));
                    if(job instanceof Job) {
                        // Remove job from jobsFragment
                        if(jobsFragment instanceof JobsFragment) {
                            jobsFragment.remove(job);
                        }
                    }
                    break;

                case Constants.RESULT_REFRESH_ATTENDING:
                    job = mJob.getJob(data.getIntExtra("JOB", 0));
                    if(job instanceof Job) {
                        // Remove job from jobsFragment
                        if(jobsFragment instanceof JobsFragment) {
                            jobsFragment.remove(job);
                        }
                        // Add job from attendingFragment
                        if(attendingFragment instanceof AttendingFragment) {
                            attendingFragment.add(job);
                        }

                        // Find next step
                        Step step = mJob.nextStep(job);
                        if(step != null) {
                            Intent intent = new Intent(this, StepActivity.class);
                            intent.putExtra("JOB", job.getId());
                            startActivityForResult(intent, Constants.PICK_STEP_REQUEST);
                        }
                    }
                break;

                case Constants.RESULT_NEXT_STEP:
                    job = mJob.getJob(data.getIntExtra("JOB", 0));
                    if(job instanceof Job) {
                        Intent intent = new Intent(this, StepActivity.class);
                        intent.putExtra("JOB", job.getId());
                        startActivityForResult(intent, Constants.PICK_STEP_REQUEST);
                    }
                    break;

                case Constants.RESULT_REFRESH_FINISHED:
                    job = mJob.getJob(data.getIntExtra("JOB", 0));
                    if(job instanceof Job) {
                        // Remove job from attendingFragment
                        if(attendingFragment instanceof AttendingFragment) {
                            attendingFragment.remove(job);
                        }
                        // Add job from finishedFragment
                        if(finishedFragment instanceof FinishedFragment) {
                            finishedFragment.add(job);
                        }

                        Intent intent = new Intent(this, StepActivity.class);
                        intent.putExtra("JOB", job.getId());
                        startActivity(intent);
                    }
                break;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1: return attendingFragment;
                case 2: return finishedFragment;
                default: return jobsFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1: return getString(R.string.tab_attending);
                case 2: return getString(R.string.tab_finished);
                default: return getString(R.string.tab_scheduled);
            }
        }
    }
}
