package com.koiti.mctjobs.services;


import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.koiti.mctjobs.helpers.RestClientApp;
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

public class InstanceFirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = InstanceFirebaseIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        try {
            // Rest client
            RestClientApp mRestClientApp = new RestClientApp(getApplicationContext());
            mRestClientApp.getSyncAccessToken(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    sendToken(response, token);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    Log.e(TAG, response.toString());

                    // Tracker exception
                    Crashlytics.logException(throwable);
                }
            });
        } catch (JSONException | IOException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException |
                CertificateException | KeyStoreException e ) {
            Log.e(TAG, e.getMessage());

            // Tracker exception
            Crashlytics.logException(e);
        }
    }

    private void sendToken(JSONObject oaut, String token) {
        try {
            // Rest client
            RestClientApp mRestClientApp = new RestClientApp(getApplicationContext());
            mRestClientApp.sendTokenFirebase(token, oaut, new JsonHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    Log.e(TAG, response.toString());

                    // Tracker exception
                    Crashlytics.logException(throwable);
                }
            });
        } catch (JSONException | UnsupportedEncodingException e ) {
            Log.e(TAG, e.getMessage());

            // Tracker exception
            Crashlytics.logException(e);
        }
    }
}