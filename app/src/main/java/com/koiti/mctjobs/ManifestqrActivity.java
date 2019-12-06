package com.koiti.mctjobs;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.helpers.UserSessionManager;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Image;
import com.loopj.android.http.JsonHttpResponseHandler;

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

public class ManifestqrActivity extends AppCompatActivity {
    private static final String TAG = ManifestqrActivity.class.getSimpleName();

    private UserSessionManager mSession;
    private RestClientApp mRestClientApp;

    private View mFormView;
    private View mProgressView;
    private ImageView mQRManifestImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manifestqr);

        // Session
        mSession = new UserSessionManager(this);

        // Check user login
        if(mSession.checkLogin()) {
            finish();
            return;
        }

        // Rest client
        mRestClientApp = new RestClientApp(this);

        // References
        mFormView = findViewById(R.id.form_view);
        mProgressView = findViewById(R.id.progress_view);
        mQRManifestImage = (ImageView) findViewById(R.id.image_qr_manifest);

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        Utils.showProgress(true, mFormView, mProgressView);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getResources().getString(R.string.nav_item_qr) );

        // Start turn
        startQRManifest();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startQRManifest() {
        try {
            mRestClientApp.getAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    prepareQRManifest(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    onFailureAccessToken(throwable, response);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(ManifestqrActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
        }
    }

    public void prepareQRManifest(JSONObject oaut) {
        try {
            mRestClientApp.getQRManifest(mSession.getPartner(), oaut, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     showQRManifes(response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    try
                    {
                        if(response == null) {
                            throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
                        }

                        if( response.getBoolean("successful") == false) {
                            JSONObject error = response.getJSONObject("error");
                            Toast.makeText(ManifestqrActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException e ) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);

                        Toast.makeText(ManifestqrActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();
                    } finally {
                        finish();
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(ManifestqrActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            Utils.showProgress(false, mFormView, mProgressView);

            finish();
        }
    }

    public void showQRManifes(JSONObject response)
    {
        try {
            // Errors
            if(response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Data form
            if( response.getBoolean("successful") == false) {
                JSONObject error = response.getJSONObject("error");
                Toast.makeText(ManifestqrActivity.this, error.getString("msg"), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            JSONObject data = response.getJSONObject("data");
            mQRManifestImage.setImageBitmap( Image.decodeBase64( data.getString("contenido") ) );
        } catch (JSONException | NullPointerException e ) {
            Log.e(TAG, e.getMessage());

            // Tracker exception
            Crashlytics.logException(e);

            Toast.makeText(ManifestqrActivity.this, R.string.on_failure, Toast.LENGTH_LONG).show();

            // Close activity
            finish();
        } finally {
            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);
        }
    }
    public void onFailureAccessToken(Throwable throwable, JSONObject response) {
        try {
            if (response == null) {
                throw new NullPointerException(getResources().getString(R.string.on_null_server_exception));
            }

            // Connect timeout exception
            if (throwable.getCause() instanceof ConnectTimeoutException) {
                Toast.makeText(ManifestqrActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (throwable.getCause() instanceof ErrnoException) {
                    Toast.makeText(ManifestqrActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();
                }
            }

            // Error description
            String error = response.getString("error_description");
            if(!error.isEmpty()) {
                Toast.makeText(ManifestqrActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            Toast.makeText(ManifestqrActivity.this, R.string.on_host_exception, Toast.LENGTH_LONG).show();

            // Tracker exception
            Crashlytics.logException(e);
        }finally {
            // Hide progress.
            Utils.showProgress(false, mFormView, mProgressView);

            finish();
        }
    }
}
