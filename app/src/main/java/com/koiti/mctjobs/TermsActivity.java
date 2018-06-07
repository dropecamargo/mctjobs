package com.koiti.mctjobs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class TermsActivity extends AppCompatActivity {
    private static final String TAG = TermsActivity.class.getSimpleName();

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;

    private View mProgressView;
    private View mFormView;
    private Tracker tracker;

    private ImageView mTermsBackground;
    private ImageView mTermsLogo;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Session
        mSession = new UserSessionManager(this);

        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }
        // Rest client
        mRestClientApp = new RestClientApp(this);

        // Get tracker.
        tracker = ((Application) getApplication()).getTracker();

        // Get Remote Config instance.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // References
        mProgressView = findViewById(R.id.progress_view);
        mFormView = findViewById(R.id.terms_form);
        mTermsBackground = (ImageView) findViewById(R.id.terms_background);
        mTermsLogo = (ImageView) findViewById(R.id.terms_logo);

        // Load image background
        ImageLoader loaderBackground = ImageLoader.getInstance();
        DisplayImageOptions optionsBackground = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderBackground.displayImage("drawable://" + R.drawable.terms_background, mTermsBackground, optionsBackground);

        // Load image logo
        ImageLoader loaderLogo = ImageLoader.getInstance();
        DisplayImageOptions optionsLogo = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderLogo.displayImage("drawable://" + R.drawable.logo_mtc_white, mTermsLogo, optionsLogo);

        // Set url terms
        TextView textView = (TextView) findViewById(R.id.text_link);
        textView.setText(Html.fromHtml("Toca \"Aceptar y continuar\" para aceptar los " +
                "<a href='http://mct.com.co'>Términos de servicio y Politica de privacidad de MCT</a>"));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void acceptTerms(View view) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        Utils.showProgress(true, mFormView, mProgressView);

        try {
            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    termsOn(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureAccessToken(throwable, response);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(TermsActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void termsOn(JSONObject oaut) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = dateFormat.format(new Date());

            mRestClientApp.termsOn(mSession.getPartner(), date, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // Set true terms
                    mSession.setTerms(true);

                    // HomeActivity
                    Intent intent = new Intent(TermsActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        String error = response.getString("error");
                        if(error != null && !error.isEmpty()) {
                            Toast.makeText(TermsActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        tracker.send(new HitBuilders.ExceptionBuilder()
                                .setDescription(String.format("%s:reportTerms:%s", TAG, e.getLocalizedMessage()))
                                .setFatal(false)
                                .build());

                        Toast.makeText(TermsActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        // Hide progress.
                        Utils.showProgress(false, mFormView, mProgressView);
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(TermsActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

    public void onFailureAccessToken(Throwable throwable, JSONObject response) {
        try {
            if (response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Connect timeout exception
            if (throwable.getCause() instanceof ConnectTimeoutException || throwable.getCause() instanceof ErrnoException) {
                Toast.makeText(TermsActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Toast.makeText(TermsActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            tracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(String.format("%s:AccessToken:%s", TAG, e.getLocalizedMessage()))
                    .setFatal(false)
                    .build());
        }finally {
            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);
        }
    }

}
