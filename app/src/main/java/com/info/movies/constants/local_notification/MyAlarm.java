package com.info.movies.constants.local_notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.info.movies.R;

/**
 * Created by Ehab Salah on 2/10/2018.
 */

public class MyAlarm {

    public MyAlarm() {

    }

    private static final String TAG = MyAlarm.class.getSimpleName();

    public static void setAlarm(Context context, int trigger_at_minutes) {
        Log.d(TAG, "setAlarm: ");
        Intent i = new Intent(context, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000*60*1,1000*60*1, pendingIntent);
        assert am != null;
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000*60*trigger_at_minutes, 1000*60*60*context.getResources().getInteger(R.integer.notification_interval_hour), pendingIntent); // Millisec * Second * Minute
        Log.d(TAG, "setAlarm: 1");
    }

    public static void cancelAlarm(Context context) {
        Log.d(TAG, "cancelAlarm: ");
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "cancelAlarm: 1");

    }


}
