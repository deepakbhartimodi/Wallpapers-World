package app.deepakbharti.com.wallpapersworld.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.activities.HomeActivity;

public class NotificationReciever extends BroadcastReceiver {

    Notification.Builder notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeat_intent = new Intent(context,HomeActivity.class);
        repeat_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeat_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new Notification.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentText("Checkout some cool wallpapers for you smartphone.")
                .setContentTitle("Wallpaper's World")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        notificationManager.notify(100,notification.build());
    }
}
