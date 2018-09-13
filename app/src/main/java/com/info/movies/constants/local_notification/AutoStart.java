package com.info.movies.constants.local_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.info.movies.R;

import static com.info.movies.constants.local_notification.MyAlarm.setAlarm;

/**
 * Created by mohamedhussin on 2/7/18.
 */

public class AutoStart extends BroadcastReceiver {
    private static final String TAG = AutoStart.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: AutoStart 1 action ="+intent.getAction());

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())||"android.intent.action.QUICKBOOT_POWERON".equals(intent.getAction()))
        {
            Log.d(TAG, "onReceive: AutoStart 2 ");
            if (context.getSharedPreferences(context.getString(R.string.notification_data_key), Context.MODE_PRIVATE).getBoolean(context.getString(R.string.notification_switch_key),false)) {
                Log.d(TAG, "onReceive: AutoStart 3 ");
                setAlarm(context,context.getResources().getInteger(R.integer.notification_alarm_set_trigger_at_minutes_power_on));
            }
        }
        else
        {
            Log.d(TAG, "onReceive: AutoStart 4 ");
        }
    }
}
