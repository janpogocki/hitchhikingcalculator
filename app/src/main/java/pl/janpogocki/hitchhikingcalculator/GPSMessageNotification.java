package pl.janpogocki.hitchhikingcalculator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling gpsmessage
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class GPSMessageNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "MotoCalc_GPSMessage";

    public static void notify(final Context context, final String distance) {
        final Resources res = context.getResources();

        final String title = res.getString(R.string.app_name);
        final String text = res.getString(R.string.gpsmessage_notification_text, distance);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "motocalc")

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                //.setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_LOW)

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Set ticker text (preview) information for this notification.
                .setTicker(title)

                .setColor(context.getResources().getColor(R.color.colorPrimaryDark))

                .setOngoing(true)

                .setShowWhen(false)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(false);

        notify(context, builder.build());
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel("motocalc", "Standardowe powiadomienie", NotificationManager.IMPORTANCE_LOW);
            nc.enableVibration(false);
            nc.setSound(null, null);
            nm.createNotificationChannel(nc);
        }

        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }
}
