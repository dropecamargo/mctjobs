package com.koiti.mctjobs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.koiti.mctjobs.Application;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.GPSTracker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TrackerGpsService extends Service {
    private static final String TAG = TrackerGpsService.class.getSimpleName();

    private TimerTask timerTask;
    private Tracker tracker;
    private GPSTracker gps;
    private TelephonyManager telephony;

    private String NMEA_VERSION = "MM001";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "Service TrackerGpsService start...");

        // Get tracker.
        tracker = ((Application) getApplicationContext()).getTracker();

        // Get telephony manager
        telephony = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    gps = new GPSTracker(getApplicationContext());
                    if (gps.canGetLocation()) {
                        // Current date
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        // Prepare nmea
                        StringBuffer nmea = new StringBuffer();
                        nmea.append(telephony.getDeviceId()).append(":").append("200");
                        nmea.append(",").append(NMEA_VERSION);
                        nmea.append(",").append(gps.getLatitude()).append(",").append(gps.getLongitude());
                        nmea.append(",").append(gps.getSpeed());
                        nmea.append(",").append(gps.getAccuracy());
                        nmea.append(",").append(gps.getAltitude());
                        nmea.append(",").append(gps.getBearing());
                        nmea.append(",").append(gps.getTime());
                        nmea.append(",").append(dateFormat.format(date));

                        DatagramSocket socket = new DatagramSocket();
                        InetAddress server = InetAddress.getByName("gps.mct.com.co");
                        int msg_length = nmea.toString().length();
                        byte[] message = nmea.toString().getBytes();

                        DatagramPacket packet = new DatagramPacket(message, msg_length, server, 31401);
                        socket.send(packet);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 300000);
//         timer.scheduleAtFixedRate(timerTask, 0, 5000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
        Intent localIntent = new Intent(Constants.ACTION_MEMORY_EXIT);
    }
}
