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
import android.system.ErrnoException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koiti.mctjobs.helpers.GPSTracker;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;
    private GPSTracker gps;
    private Tracker tracker;
    private DataBaseManagerJob mJob;

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

        // Get tracker.
        tracker = ((Application) getApplication()).getTracker();

        // Database
        mJob = new DataBaseManagerJob(this);

        // System.out.println(FirebaseInstanceId.getInstance().getToken());

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

        // Rest client
        mRestClientApp = new RestClientApp(this);

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

        // Show and hide options (logout, turn, QR)
        if( mSession.getRol().equals("funcionario") ) {
            navigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(true);

            navigationView.getMenu().findItem(R.id.nav_item_qrmanifiesto).setVisible(false);

            mButtonTurn.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_item_turn).setVisible(false);
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

            case R.id.nav_item_qrmanifiesto:
                Intent intentqr = new Intent(HomeActivity.this, ManifestqrActivity.class);
                startActivity(intentqr);
                break;

            case R.id.nav_item_downexist:
                downExistAction();
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

    public void downExistAction() {
        try {
            Toast.makeText(HomeActivity.this, R.string.down_exist, Toast.LENGTH_SHORT).show();
            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    downExist(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureAccessToken(throwable, response);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(HomeActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void downExist(JSONObject oaut) {
        try{
            mRestClientApp.downExistJobs(mSession.getPartner(), oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mJob.getDb().beginTransaction();
                    try {
                        if (response.getBoolean("successful")) {
                            JSONObject data = response.getJSONObject("data");

                            // Inserts exits jobs
                            JSONArray works = data.getJSONArray("works");
                            mJob.parseExistJobs(works);

                            // Transaction successful
                            mJob.getDb().setTransactionSuccessful();

                            Toast.makeText(HomeActivity.this, R.string.down_exist_successfull, Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }finally {
                        // End transaction
                        mJob.getDb().endTransaction();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    try {
                        if (response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        // Connect timeout exception
                        if (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ErrnoException) {
                            Toast.makeText(HomeActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(HomeActivity.this, R.string.on_down_exist, Toast.LENGTH_LONG).show();
                    }catch (Exception e) {
                        Toast.makeText(HomeActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

                        // Tracker exception
                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:downExist:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(HomeActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void onFailureAccessToken(Throwable throwable, JSONObject response) {
        try {
            if (response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Connect timeout exception
            if (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ErrnoException) {
                Toast.makeText(HomeActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }

            // Error description
            String error = response.getString("error_description");
            if(!error.isEmpty()) {
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Toast.makeText(HomeActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            tracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(String.format("%s:AccessToken:%s", TAG, e.getLocalizedMessage()))
                    .setFatal(false)
                    .build());
        }
    }
}
