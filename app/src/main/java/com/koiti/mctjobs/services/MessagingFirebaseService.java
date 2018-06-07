package com.koiti.mctjobs.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.koiti.mctjobs.HomeActivity;
import com.koiti.mctjobs.NotificationActivity;
import com.koiti.mctjobs.R;
import com.koiti.mctjobs.TermsActivity;
import com.koiti.mctjobs.TurnActivity;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.models.Notification;

import java.util.Arrays;
import java.util.Random;

public class MessagingFirebaseService extends FirebaseMessagingService {
    private static final String TAG = MessagingFirebaseService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int notificationId = new Random().nextInt(60000);

        //System.out.println("Title -> " + remoteMessage.getData().get("TITLE"));
        //System.out.println("Message -> " + remoteMessage.getData().get("MESSAGE"));
        //System.out.println("ClickAction -> " + notification.getClickAction());
        //System.out.println("Data -> " + remoteMessage.getData());

        // Default intent Notification activity
        Intent intent = new Intent(notification.getClickAction());
        switch (notification.getClickAction().toString()) {
            case "INTENT_MESSAGE":
                intent.putExtra("TITLE", remoteMessage.getData().get("TITLE"));
                intent.putExtra("MESSAGE", remoteMessage.getData().get("MESSAGE"));
                break;

            case "INTENT_WORK":
                intent.putExtra("JOB", remoteMessage.getData().get("JOB"));
                break;
        }


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}