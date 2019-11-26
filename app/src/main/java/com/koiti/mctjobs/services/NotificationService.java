package com.koiti.mctjobs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.Application;
import com.koiti.mctjobs.R;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.models.Notification;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.loopj.android.http.JsonHttpResponseHandler;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();

    private TimerTask timerTask;
    private DataBaseManagerJob mJob;
    private RestClientApp mRestClientApp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Rest client
        mRestClientApp = new RestClientApp(getApplicationContext());

        // Database
        mJob = new DataBaseManagerJob(this);

        // Reset processing
        mJob.resetProcessing();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try
                {
                    final Notification notification = mJob.getNextNotification();
                    if(notification != null) {
                        // System.out.println("Encuentro -> work " + notification.getId_work() + " -> step " + notification.getId_workstep());
                        if( notification.getProcessing() == false ) {
                            // System.out.println("Ingreso a procesar -> work " + notification.getId_work() + " -> step " + notification.getId_workstep());
                            mRestClientApp.getSyncAccessToken(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    sendNotification(notification, response);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                                    // Tracker exception
                                    Crashlytics.logException(throwable);
                                }
                            });
                        }
                    }
                }catch (JSONException | IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
                    Log.e(TAG, e.getLocalizedMessage());

                    // Tracker exception
                    Crashlytics.logException(e);

                    // Reset processing
                    mJob.resetProcessing();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 5000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        Intent localIntent = new Intent(Constants.ACTION_MEMORY_EXIT);
    }

    public void sendNotification(final Notification notification, JSONObject oauth) {
        try
        {
            // Current date
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Set processing true
            mJob.setProcessing(notification, true);

            // Diference seconds report
            final Date report_date = dateFormat.parse(notification.getReport_date());
            long seconds = (date.getTime() - report_date.getTime()) / 1000;

            JSONObject params = new JSONObject();
            params.put("id_report_app", notification.getId());
            params.put("id_user", notification.getId_user());
            params.put("id_work", notification.getId_work());
            params.put("id_workstep", notification.getId_workstep());
            params.put("modify_step", notification.getModify_step());
            params.put("id_workphase", notification.getId_workphase());
            params.put("modify_phase", notification.getModify_phase());
            params.put("sequence", notification.getSequence());
            params.put("type", (seconds > 30 ? Notification.type_batch : Notification.type_online));
            params.put("report_date", notification.getReport_date());
            params.put("send_date", dateFormat.format(date));
            params.put("state", notification.getState());
            params.put("statebefore", notification.getStatebefore());
            params.put("paused", notification.getPaused());
            params.put("unpaused", notification.getUnpaused());
            params.put("pausetime", notification.getPausetime());
            params.put("modifier", "CLIENT");
            params.put("latitude", notification.getLatitude() != null ? notification.getLatitude() : JSONObject.NULL);
            params.put("longitude", notification.getLongitude() != null ? notification.getLongitude() : JSONObject.NULL);
            params.put("message", notification.getMessage());
            params.put("tittle", notification.getTittle());
            params.put("message_wrote", notification.getMessage_wrote() != null ? notification.getMessage_wrote() : JSONObject.NULL);
            params.put("ignored", notification.getIgnored());
            params.put("create_action", notification.getCreate_action());
            params.put("action", notification.getAction());
            params.put("message_action", notification.getMessage_action());
            params.put("pictures", notification.getPictures());
            params.put("videos", notification.getVideos());
            params.put("report_type", notification.getReport_type());
            params.put("id_step_new", notification.getId_step_new());

            // Fields
            JSONArray fields = null;
            if(notification.getFields() != null && !notification.getFields().isEmpty()) {
                fields = new JSONArray(notification.getFields());
            }
            params.put("fields", fields != null ? (Object) fields : JSONObject.NULL);

            // Documents
            JSONArray documents = new JSONArray();
            if (notification.getDocuments() != null && !notification.getDocuments().isEmpty()) {
                JSONArray files = new JSONArray(notification.getDocuments());
                for (int f = 0; f < files.length(); f++) {
                    JSONObject  file = (JSONObject) files.get(f);
                    documents.put(file);
                }
            }
            params.put("documents", (Object) documents);

            // Sync report
            ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));
            JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println("onSuccess -> " + response.toString());
                    mJob.removeNotification(notification);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                    // System.out.println("onFailure -> " + response);
                    try {
                        if (response == null) {
                            throw new NullPointerException(getApplicationContext().getResources().getString(R.string.on_null_server_exception));
                        }

                        if (response.getBoolean("successful") == false) {
                            JSONObject error = response.getJSONObject("error");
                            throw new Exception(error.getString("message"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());

                        // Tracker exception
                        Crashlytics.logException(e);
                    }finally {
                        // Set processing false
                        mJob.setProcessing(notification, false);
                    }
                }
            };

            jsonHttpResponseHandler.setUsePoolThread(true);
            mRestClientApp.syncReport(entity, oauth, jsonHttpResponseHandler);

        }catch (JSONException | ParseException | UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage());

            // Tracker exception
            Crashlytics.logException(e);

            // Reset processing
            mJob.resetProcessing();
        }
    }
}
