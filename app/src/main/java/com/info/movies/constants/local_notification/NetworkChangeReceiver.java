package com.info.movies.constants.local_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.info.movies.R;

import static android.content.Context.MODE_PRIVATE;
import static com.info.movies.constants.local_notification.MyAlarm.setAlarm;

/**
 * Created by mohamedhussin on 2/7/18.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
        private static final String TAG = NetworkChangeReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: NetworkChangeReceiver 1");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Log.d(TAG, "onReceive: NetworkChangeReceiver 2");
            boolean isKey  = context.getSharedPreferences(context.getString(R.string.notification_data_key),MODE_PRIVATE).getBoolean(context.getString(R.string.notification_switch_key),false);
            boolean isMissed  = context.getSharedPreferences(context.getString(R.string.notification_data_key),MODE_PRIVATE).getBoolean(context.getString(R.string.notification_missed_name),false);

            Log.d(TAG, "onReceive: isMissed = "+isMissed);
            Log.d(TAG, "onReceive: isKey = "+isKey);
            Log.d(TAG, "onReceive: isOnline - "+isOnline(context));
            if (isOnline(context)&&isKey&&isMissed)
            {
               Log.d(TAG, "onReceive: NetworkChangeReceiver 3");
                setAlarm(context,1);
                SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.notification_data_key), MODE_PRIVATE).edit();
                editor.putBoolean("key_missed",false);
                editor.apply();

            }
        }
    }
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }
}
