package com.sponge.baebot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive**: ");

        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,
                "BAEBot Task Reminder", "Wake up! It's time to check your daily tasks!");

    }
}


