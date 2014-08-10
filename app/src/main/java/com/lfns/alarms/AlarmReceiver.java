package com.lfns.alarms;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lfns.TodaysSkateActivity;
import com.lfns.util.QueryUrl;
import com.lfns.R;
import com.lfns.skateQueries.SkateQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public abstract class AlarmReceiver extends BroadcastReceiver {

    private static long REPEAT_FREQUANCY = 1000 * 60 * 5; // Every 5 minutes

    public AlarmReceiver() {
        super();
    }

    public AlarmReceiver(Context context) {
        super();

    }


    public abstract Calendar getSkateTime();
    public abstract SkateQuery getQuery();
    public abstract int getId();

    private static Calendar getPollTime(Calendar c) {
        c.add(Calendar.HOUR, -2);
        c.add(Calendar.MINUTE, -30);
        return c;
    }

    static Calendar moveTimeIfVeryClose(Calendar calendar) {
        long timeDiff = calendar.getTimeInMillis() - new Date().getTime();
        if (timeDiff < 0) {
            calendar.add(Calendar.DATE, 7);
        }
        return calendar;
    }

    public String[] runQuery() throws ExecutionException, InterruptedException {
        SkateQuery query = getQuery();
        AsyncTask<String, Integer, String[]> execute = new QueryUrl(query).execute();
        String[] result = execute.get();
        return result;
    }

    public void setAlarm(boolean setAlarm, Context pContext) {
        AlarmManager alarmMgr = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(pContext, this.getClass());
        Log.i(this.getClass().getName(), "Set alarm for "+ this.getClass().getName());

        // clear old alarm
        PendingIntent alarm = PendingIntent.getBroadcast(pContext, this.getId(), intent, PendingIntent.FLAG_NO_CREATE);
        if (alarm != null) {
            alarmMgr.cancel(alarm);
            alarm.cancel();
        }

        if (setAlarm) {
            PendingIntent handler = PendingIntent.getBroadcast(pContext, this.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar pollTime = moveTimeIfVeryClose(getPollTime(this.getSkateTime()));
            Log.i(this.getClass().getName(), "Set alarm wake up time: "+ new SimpleDateFormat().format(pollTime.getTime()));

//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(pContext)
//                    .setSmallIcon(R.drawable.skate_notify1)
//                    .setContentTitle("Skate wake up")
//                    .setContentText("Skate wakes up" + new SimpleDateFormat().format(pollTime.getTime()));
//
//            NotificationManager notificationManager = (NotificationManager) pContext
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify((int)(Math.random()*99999 + 1), notificationBuilder.build());

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, pollTime.getTimeInMillis(), REPEAT_FREQUANCY, handler);
            // Ensure alarm comes back if phone rebooted.
            ComponentName receiver = new ComponentName(pContext, this.getClass());
            pContext.getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            // Clear stored skate state
            SharedPreferences sharedPreferences = pContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("previous_state", "");
            editor.commit();
        }
    }

    @Override
    public void onReceive(Context pContext, Intent pIntent) {
        Log.i(this.getClass().getName(), "alarm received");
        Log.i(this.getClass().getName(), "" + pContext);
        Log.i(this.getClass().getName(), "" + pIntent);

        SharedPreferences sharedPreferences = pContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // If skate has already started stop this polling.

        Calendar skateTime = this.getSkateTime();
        skateTime.add(Calendar.MINUTE, -10);

        // Cancel alarm
        if (Calendar.getInstance().after(skateTime)) {
            PendingIntent alarm = PendingIntent.getBroadcast(pContext, this.getId(), pIntent, PendingIntent.FLAG_NO_CREATE);
            if (alarm != null) {
                this.setAlarm(true, pContext);
                return;
            }
        }

        if (pIntent != null && pIntent.getAction() != null && pIntent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i(this.getClass().getName(), "alarm set on boot");
            this.setAlarm(true, pContext);
            return;
        }


        String query[] = {"", ""};
        try {
            query = this.runQuery();
            //query[0] = "testing!";
        } catch(Exception e) {
            query[0] = e.getMessage();
            Log.e(this.getClass().getName(), "Error getting url", e);
        }

        // temp debugging:
//        NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(pContext)
//                .setSmallIcon(R.drawable.skate_notify1)
//                .setContentTitle("Skate Test")
//                .setContentText("Skate is " + query[0] + " vs "+ sharedPreferences.getString("previous_state", "") )
//                .setLights(Color.GRAY, 500, 1000);
//        NotificationManager notificationManager2 = (NotificationManager) pContext
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager2.notify((int)(Math.random()*9999), notificationBuilder2.build());

        Log.i(this.getClass().getName(), "response:");
        Log.i(this.getClass().getName(), query[0]);

        if (!query[0].equals("")) {
            String oldState = sharedPreferences.getString("previous_state", "");
            Log.i(this.getClass().getName(), oldState);

            if (!oldState.equals(query[0])) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(pContext, 0, new Intent(pContext, TodaysSkateActivity.class), 0);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(pContext)
                        .setSmallIcon(R.drawable.skate_notify1)
                        .setContentTitle("Skate Notification")
                        .setContentText("Skate is " + query[0])
                        .setSound(alarmSound)
                        .setLights(Color.BLUE, 500, 1000)
                        .setContentIntent(resultPendingIntent);

                NotificationManager notificationManager = (NotificationManager) pContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("previous_state", query[0]);
                editor.commit();
            }
        }

        // if no network do nothing.

    }

}
