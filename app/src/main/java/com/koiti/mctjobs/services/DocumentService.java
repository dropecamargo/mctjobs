package com.koiti.mctjobs.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.koiti.mctjobs.R;
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.models.Document;
import com.koiti.mctjobs.models.Image;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class DocumentService extends Service {
    private static final String TAG = DocumentService.class.getSimpleName();

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
        mJob.resetDocumentProcessing();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try
                {
                    // System.out.println("Ingreso DocumentService");
                    final Document document = mJob.getNextDocument();
                    if(document != null) {
                        // System.out.println("Encuentro documento -> " + document.getId() + " Processing -> " + document.getProcessing());
                        if( document.getProcessing() == false ) {
                            // System.out.println("Ingreso a procesar documento -> " + document.getId());
                            mRestClientApp.getSyncAccessToken(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    sendDocument(document, response);
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
                    mJob.resetDocumentProcessing();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 5000);
        return START_NOT_STICKY;
    }

    public void sendDocument(final Document document, JSONObject oauth) {
        try
        {
            // Set processing true
            mJob.setDocumentProcessing(document, true);

            // Image
            JSONObject item = new JSONObject();
            item.put("filename", document.getName());

            Bitmap bmImage = null;
            File fileimage = new File(document.getContent());
            if(fileimage.exists()) {
                bmImage = GlideBitmapFactory.decodeFile(document.getContent());
                item.put("content", Image.encodeImage(bmImage));
            }else{
                bmImage = GlideBitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.not_found);
                item.put("content", Image.encodeImage(bmImage));
            }

            // Object
            JSONObject params = new JSONObject();
            params.put("id_work", document.getId_work());
            params.put("id_step", document.getId_workstep());
            params.put("id_report_app", document.getId_report());
            params.put("document", (Object) item);

            // Recicle bitmap
            GlideBitmapPool.putBitmap(bmImage);

            if(bmImage != null) {
                bmImage.recycle();
            }

            // System.out.println("Document reportado -> " + params.toString());

            // Sync report
            ByteArrayEntity entity = new ByteArrayEntity(params.toString().getBytes("UTF-8"));
            JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // System.out.println("onSuccess -> " + response.toString());
                    mJob.setDocumentSynd(document);
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
                        mJob.setDocumentProcessing(document, false);
                    }
                }
            };

            jsonHttpResponseHandler.setUsePoolThread(true);
            mRestClientApp.syncDocument(entity, oauth, jsonHttpResponseHandler);
        }catch (OutOfMemoryError | Exception e) {
            Log.e(TAG, e.getLocalizedMessage());

            // Tracker exception
            Crashlytics.logException(e);

            // Reset processing
            mJob.resetDocumentProcessing();
        }
    }
}
