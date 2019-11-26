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
import com.koiti.mctjobs.helpers.RestClientApp;
import com.koiti.mctjobs.sqlite.DataBaseManagerJob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MaintenanceService extends Service {
    private static final String TAG = MaintenanceService.class.getSimpleName();

    private TimerTask timerTask;
    private DataBaseManagerJob mJob;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Database
        mJob = new DataBaseManagerJob(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mJob.getDb().beginTransaction();
                try
                {
                    // Delete data with more than 15 days left
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime( new Date() );
                    calendar.add(Calendar.DAY_OF_YEAR, -15);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = dateFormat.format( calendar.getTime() );

                    // System.out.println("Corriendo MaintenanceService ... " + date);
                    mJob.removeOldJobs( date );

                    // Transaction successful
                    mJob.getDb().setTransactionSuccessful();

                }catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());

                    // Tracker exception
                    Crashlytics.logException(e);
                }finally {
                    // End transaction
                    mJob.getDb().endTransaction();
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 3600000);
        return START_NOT_STICKY;
    }
}