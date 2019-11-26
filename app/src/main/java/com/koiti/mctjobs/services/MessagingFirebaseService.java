package com.koiti.mctjobs.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.koiti.mctjobs.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String picture = remoteMessage.getData().get("picture");
        String click_action = remoteMessage.getData().get("click_action");
        // System.out.println("Data -> " + remoteMessage.getData());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        // Default intent Notification activity
        if(click_action != null) {
            Intent intent = new Intent(click_action);
            switch (click_action) {
                case "INTENT_MESSAGE":
                    intent.putExtra("title", title);
                    intent.putExtra("message", message);
                    intent.putExtra("picture", picture);
                    break;

                case "INTENT_WORK":
                    intent.putExtra("job", remoteMessage.getData().get("job"));
                    break;
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        // Add custom images
        if(picture != null) {
            // set big content view for newer androids
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                try {
                    // Create bigPictureStyle
                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();

                    // Decode picture
                    URL picture_url = new URL(picture);
                    Bitmap pictureBitmap = BitmapFactory.decodeStream((InputStream) picture_url.getContent());

                    String small_picture = remoteMessage.getData().get("small_picture");
                    Bitmap smallPictureBitmap = null;
                    if(small_picture != null){
                        // Decode small picture
                        URL small_url = new URL(small_picture);
                        smallPictureBitmap = BitmapFactory.decodeStream((InputStream) small_url.getContent());
                    }

                    // Set picture
                    bigPictureStyle.bigPicture(pictureBitmap);

                    // Set small picture
                    bigPictureStyle.bigLargeIcon(pictureBitmap);
                    if(smallPictureBitmap != null){
                        bigPictureStyle.bigLargeIcon(smallPictureBitmap);
                    }

                    // Set title
                    bigPictureStyle.setBigContentTitle(title);

                    // Set text
                    String summary_text = remoteMessage.getData().get("summary_text");
                    if(summary_text != null) {
                        bigPictureStyle.setSummaryText(summary_text);
                    }

                    // Set style
                    notificationBuilder.setStyle(bigPictureStyle);
                    notificationBuilder.setLargeIcon(pictureBitmap);

                    // Set small picture
                } catch (Exception e) { }
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}