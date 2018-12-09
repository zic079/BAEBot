package com.sponge.baebot;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.content.res.Resources;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.content.ContextCompat.getSystemService;

public class NotificationScheduler
{
    private static final int DAILY_REMINDER_8AM = 0;
    private static final int DAILY_REMINDER_11PM = 1;
    public static final String TAG="NotificationScheduler";
    //private static int num_event;
    private static int num_task;
    public static void setMorningReminder(Context context)
    {
        //num_task = task;

        Log.d(TAG, "inReminder**");
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar_1 = Calendar.getInstance();
        setcalendar_1.set(Calendar.HOUR_OF_DAY, 8);
        setcalendar_1.set(Calendar.MINUTE, 0);
        setcalendar_1.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        // cancelReminder(context,cls);

        if(setcalendar_1.before(calendar))
            setcalendar_1.add(Calendar.DATE,1);

        // Enable a receiver
        ComponentName receiver1 = new ComponentName(context, AlarmReceiver.First.class);
        PackageManager pm1 = context.getPackageManager();

        pm1.setComponentEnabledSetting(receiver1,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent notificationIntent1 = new Intent(context, AlarmReceiver.First.class);

       // int quest_code = 0;
        //if (hour == 8) {
        //    quest_code =  DAILY_REMINDER_8AM;
        //}
        //else if (hour == 23){
         //   quest_code =  DAILY_REMINDER_11PM ;
        //}

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 0, notificationIntent1, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am1 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am1.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar_1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);

    }

    public static void setSleepReminder(Context context)
    {
        //num_task = task;

        Log.d(TAG, "inReminder**");
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar_2 = Calendar.getInstance();
        setcalendar_2.set(Calendar.HOUR_OF_DAY, 23);
        setcalendar_2.set(Calendar.MINUTE, 0);
        setcalendar_2.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        // cancelReminder(context,cls);

        if(setcalendar_2.before(calendar))
            setcalendar_2.add(Calendar.DATE,1);

        // Enable a receiver
        ComponentName receiver2 = new ComponentName(context, AlarmReceiver.Second.class);
        PackageManager pm2 = context.getPackageManager();

        pm2.setComponentEnabledSetting(receiver2,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent notificationIntent2 = new Intent(context, AlarmReceiver.Second.class);

        // int quest_code = 0;
        //if (hour == 8) {
        //    quest_code =  DAILY_REMINDER_8AM;
        //}
        //else if (hour == 23){
        //   quest_code =  DAILY_REMINDER_11PM ;
        //}

        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, notificationIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am2 = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am2.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar_2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);

    }
    public static void cancelMorningReminder(Context context)
    {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, AlarmReceiver.First.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent_cancel = new Intent(context, AlarmReceiver.First.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void cancelSleepReminder(Context context)
    {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, AlarmReceiver.Second.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent_cancel = new Intent(context, AlarmReceiver.Second.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent_cancel, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, String title)
    {

        Log.d(TAG, "showNotification: **title: " + title);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // show today's event
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int quest_code = 0;
        if (hour == 8) {
            quest_code = DAILY_REMINDER_8AM;
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = builder.setContentTitle(title)
                    .setContentText("Wake up! Wake up! Check your daily tasks here!")
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.logo1)
                    .setContentIntent(pendingIntent).build();
            Log.d(TAG, "8am** ");
        }

        else if (hour == 23) {
            quest_code = DAILY_REMINDER_11PM;
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = builder.setContentTitle(title)
                    .setContentText("It's time to sleep now. Good night~")
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setSmallIcon(R.drawable.logo1)
                    .setContentIntent(pendingIntent).build();
            Log.d(TAG, "11pm** ");
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel BAEbot",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);

            Log.d(TAG, "onChannel** ");
        }

        notificationManager.notify(quest_code, builder.build());
        Log.d(TAG, "onNotification** ");
    }

}
