package app.deepakbharti.com.wallpapersworld.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.activities.HomeActivity;
import app.deepakbharti.com.wallpapersworld.activities.MainActivity;

public class CategoryNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    public void showCategoryNotification(String message){

        Notification.Builder notification;

        Intent repeat_intent = new Intent(this,HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivities(this,0, new Intent[]{repeat_intent},0);

        notification = new Notification.Builder(this)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText(message)
                .setContentTitle("Wallpaper's World")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification.build());
    }
}
