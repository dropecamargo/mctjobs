package com.koiti.mctjobs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.models.Image;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserSessionManager mSession;
    private GPSTracker gps;

    private ImageView mHomeBackground;
    private ImageView mLoginLogo;

    private Button mButtonJobs;
    private Button mButtonTurn;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//         System.out.println(FirebaseInstanceId.getInstance().getToken());

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }

        // Terms
        if(!mSession.getTerms()) {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);

            finish();
            return;
        }

        mHomeBackground = (ImageView) findViewById(R.id.home_background);
        mLoginLogo = (ImageView) findViewById(R.id.login_logo);
        mButtonJobs = (Button) findViewById(R.id.button_jobs);
        mButtonTurn = (Button) findViewById(R.id.button_turn);

        // Drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Header
        View headerView =  navigationView.getHeaderView(0);
        TextView nav_username = (TextView) headerView.findViewById(R.id.nav_name);
        nav_username.setText(mSession.getName());

        // Show logout action
        if( mSession.getRol().equals("funcionario") ) {
            navigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(true);
        }

        // Load image avatar
        CircleImageView nav_image = (CircleImageView) headerView.findViewById(R.id.nav_image);
        try {
            if (mSession.getImage().trim().isEmpty()) {
                throw new NullPointerException();
            }
            nav_image.setImageBitmap( Image.decodeBase64(mSession.getImage()) );
        }catch (IllegalArgumentException | NullPointerException e) {
            ImageLoader loaderAvatar = ImageLoader.getInstance();
            DisplayImageOptions optionsAvatar = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true).build();
            loaderAvatar.displayImage("drawable://" + R.drawable.home_turn_icon, nav_image, optionsAvatar);
        }

        // Load image background
        ImageLoader loaderBackground = ImageLoader.getInstance();
        DisplayImageOptions optionsBackground = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderBackground.displayImage("drawable://" + R.drawable.home_background, mHomeBackground, optionsBackground);

        // Load image logo
        ImageLoader loaderLogo = ImageLoader.getInstance();
        DisplayImageOptions optionsLogo = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderLogo.displayImage("drawable://" + R.drawable.logo_mtc_black, mLoginLogo, optionsLogo);

        // Load image jobs
        ImageLoader loaderIconJobs = ImageLoader.getInstance();
        DisplayImageOptions optionsIconJobs = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();

        loaderIconJobs.loadImage("drawable://" + R.drawable.home_jobs_icon, null, optionsIconJobs, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                mButtonJobs.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        });

        // Load image turn
        ImageLoader loaderIconTurn = ImageLoader.getInstance();
        DisplayImageOptions optionsIconTurn = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();

        loaderIconTurn.loadImage("drawable://" + R.drawable.home_turn_icon, null, optionsIconTurn, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), loadedImage);
                mButtonTurn.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        });

        // Set up the action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.home_title);
        actionBar.setSubtitle(R.string.home_subtitle);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_item_turn:
                 turnAction();
                break;

            case R.id.nav_item_jobs:
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void turnAction() {
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Intent intent = new Intent(HomeActivity.this, TurnActivity.class);
            startActivity(intent);
        } else {
            gps.showSettingsAlert();
        }
    }

    public void turnAction(View view) {
        HomeActivity.this.turnAction();
    }

    public void jobsAction(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
