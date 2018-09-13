package com.info.ehabsalahalatif.autocompletetextview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RemoteViews;

import com.info.ehabsalahalatif.autocompletetextview.service.MyService;
import com.info.ehabsalahalatif.autocompletetextview.service.ScheduledService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ThirdActivity extends AppCompatActivity {
    private final static int NORMAL = 0x00;
    private final static int BIG_TEXT_STYLE = 0x01;
    private final static int BIG_PICTURE_STYLE = 0x02;
    private final static int INBOX_STYLE = 0x03;
    private final static int CUSTOM_VIEW = 0x04;
    String CHANNEL_ID = "my_channel_01";

    private static NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //  createNotificationChannel();
    }

    public void startService(View view) {
        // start service
        // MyService.p=true;
        // startService(new Intent (this,MyService.class));
        startService(new Intent(this, ScheduledService.class));

    }

    public void stopService(View view) {
        // stop service
        MyService.p = false;
        //stopService(new Intent (this,MyService.class));
        stopService(new Intent(this, ScheduledService.class));
    }


    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// The id of the channel.
            String id = "my_channel_01";
// The user-visible name of the channel.
            CharSequence name = getString(R.string.channel_name);
// The user-visible description of the channel.
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(id, name, importance);

// Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    public void simpleNotification(View view) {
        // The id of the channel.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.male_profile_pic))
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, SecondActivity.class); // onclick activity


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(com.info.ehabsalahalatif.autocompletetextview.ThirdActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }

    public void expandedLayoutNotification(View view) {
        new CreateNotification(BIG_PICTURE_STYLE).execute();
        // new CreateNotification(CUSTOM_VIEW).execute();
    }

    public class CreateNotification extends AsyncTask<Void, Void, Void> {

        int style = NORMAL;

        /**
         * Main constructor for AsyncTask that accepts the parameters below.
         *
         * @param style {@link #NORMAL}, {@link #BIG_TEXT_STYLE}, {@link #BIG_PICTURE_STYLE}, {@link #INBOX_STYLE}
         * @see #doInBackground
         */
        public CreateNotification(int style) {
            this.style = style;
        }

        /**
         * Creates the notification object.
         *
         * @see #setNormalNotification
         * @see #setBigTextStyleNotification
         * @see #setBigPictureStyleNotification
         * @see #setInboxStyleNotification
         */
        @Override
        protected Void doInBackground(Void... params) {
            Notification noti = new Notification();

            switch (style) {
                case NORMAL:
                    noti = setNormalNotification();
                    break;

                case BIG_TEXT_STYLE:
                    noti = setBigTextStyleNotification();
                    break;

                case BIG_PICTURE_STYLE:
                    try {
                        noti = setBigPictureStyleNotification();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                case INBOX_STYLE:
                    noti = setInboxStyleNotification();
                    break;

                case CUSTOM_VIEW:
                    noti = setCustomViewNotification();
                    break;

            }

            noti.defaults |= Notification.DEFAULT_LIGHTS;
            noti.defaults |= Notification.DEFAULT_VIBRATE;
            noti.defaults |= Notification.DEFAULT_SOUND;

            noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

            mNotificationManager.notify(0, noti);

            return null;

        }
    }

    private Notification setNormalNotification() {
        Bitmap remote_picture = null;

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL("https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, SecondActivity.class);

        // TaskStackBuilder ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(SecondActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
                .setContentTitle("Normal Notification")
                .setContentText("This is an example of a Normal Style.").build();
    }

    private Notification setBigTextStyleNotification() {
        Bitmap remote_picture = null;

        // Create the style object with BigTextStyle subclass.
        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
        notiStyle.setBigContentTitle("Big Text Expanded");
        notiStyle.setSummaryText("Nice big text.");

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL("https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the big text to the style.
        CharSequence bigText = "This is an example of a large string to demo how much " +
                "text you can show in a 'Big Text Style' notification.";
        notiStyle.bigText(bigText);

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, SecondActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(SecondActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
                .setContentTitle("Big Text Normal")
                .setContentText("This is an example of a Big Text Style.")
                .setStyle(notiStyle).build();
    }

    private Notification setBigPictureStyleNotification() throws ExecutionException, InterruptedException {
        Bitmap remote_picture = null;

        // Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle("Big Picture Expanded");
        notiStyle.setSummaryText("Nice big picture.");

        /*try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL("https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // Add the big picture to the style.

        try {
            remote_picture = GlideApp.with(this)
                    .asBitmap()
                    .load("https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg")
                    .centerInside()
                    .submit().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        notiStyle.bigPicture(remote_picture);


        Intent detailsIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(detailsIntent);

        Intent trailerIntent = new Intent(this, com.info.ehabsalahalatif.autocompletetextview.ThirdActivity.class);
        trailerIntent.putExtra("key", "key");
        stackBuilder.addNextIntent(trailerIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);// this is the target

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_new_movie))
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_action_play, "Trailer", resultPendingIntent)
                .addAction(R.drawable.ic_action_add_to_list, "Watchlist", resultPendingIntent)
                  /*.addAction(R.mipmap.ic_launcher, "Two", resultPendingIntent)
                   *.addAction(R.mipmap.ic_launcher, "Three", resultPendingIntent)

                   */
                .setContentTitle("Big Picture Normal")
                .setContentText("This is an example of a Big Picture Style.")
                .setStyle(notiStyle).build();

    }

    private Notification setInboxStyleNotification() {
        Bitmap remote_picture = null;

        // Create the style object with InboxStyle subclass.
        NotificationCompat.InboxStyle notiStyle = new NotificationCompat.InboxStyle();
        notiStyle.setBigContentTitle("Inbox Style Expanded");

        // Add the multiple lines to the style.
        // This is strictly for providing an example of multiple lines.
        for (int i = 0; i < 5; i++) {
            notiStyle.addLine("(" + i + " of 6) Line one here.");
        }
        notiStyle.setSummaryText("+2 more Line Samples");

        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL("https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, SecondActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(SecondActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
                .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
                .setContentTitle("Inbox Style Normal")
                .setContentText("This is an example of a Inbox Style.")
                .setStyle(notiStyle).build();
    }

    private Notification setCustomViewNotification() {

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, SecondActivity.class);

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SecondActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create remote view and set bigContentView.
        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification_custom_remote);

        Notification notification = new NotificationCompat.Builder(this, "")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                // .addAction(R.mipmap.ic_launcher, "One", resultPendingIntent)
                .setContentTitle("Custom View").build();


        notification.bigContentView = expandedView;

        return notification;
    }

    public void showEmail(final String text) {

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        Intent intentEmailView = new Intent(this, com.info.ehabsalahalatif.autocompletetextview.ThirdActivity.class);
        intentEmailView.putExtra("EmailId", "you can Pass emailId here");
        stackBuilder.addNextIntent(intentEmailView);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(5, notification);
    }
}
