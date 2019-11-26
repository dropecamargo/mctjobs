package com.koiti.mctjobs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.koiti.mctjobs.fragments.MessageFragment;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = NotificationActivity.class.getSimpleName();

    private ImageView mNotificarionBackground;
    private ImageView mNotificarionPicture;
    private LinearLayout mLayoutMessage;

    private UserSessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Session
        mSession = new UserSessionManager(this);
        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }

        // Title
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String picture = intent.getStringExtra("picture");

        // References
        mNotificarionBackground = (ImageView) findViewById(R.id.notification_background);
        mLayoutMessage = (LinearLayout) findViewById(R.id.layout_message);
        mNotificarionPicture = (ImageView) findViewById(R.id.notification_picture);

        // Load image background
        ImageLoader loaderBackground = ImageLoader.getInstance();
        DisplayImageOptions optionsBackground = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderBackground.displayImage("drawable://" + R.drawable.main_background, mNotificarionBackground, optionsBackground);

        // Parameters, inflate message
        Bundle messageParameters = new Bundle();
        messageParameters.putBoolean(Constants.MESSAGE_SUCCESS, true);
        messageParameters.putString(Constants.MESSAGE_TITLE, title);
        messageParameters.putString(Constants.MESSAGE_BODY, message);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(messageParameters);
        fragmentTransaction.add(R.id.layout_message, messageFragment, NotificationActivity.TAG);
        fragmentTransaction.commit();

        // Picture image
        if( picture != null ) {
            ImageLoader loaderPicture = ImageLoader.getInstance();
            DisplayImageOptions optionsPicture = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true).build();
            loaderPicture.displayImage(picture, mNotificarionPicture, optionsPicture);
            mNotificarionPicture.setVisibility(View.VISIBLE);
        }

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getResources().getString(R.string.home_title) );
        getSupportActionBar().setSubtitle( getResources().getString(R.string.notification_title) );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
