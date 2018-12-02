package com.sponge.baebot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver {

    String TAG = "AlarmReceiver";

    public static class First extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Trigger the notification
            NotificationScheduler.showNotification(context, MainActivity.class,
                    "BAEBot Reminder");
        }
    }

    public static class Second extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Trigger the notification
            NotificationScheduler.showNotification(context, MainActivity.class,
                    "BAEBot Reminder");
        }
    }

}


