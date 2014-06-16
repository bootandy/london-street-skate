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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public abstract class AlarmReceiver extends BroadcastReceiver {

    private static long REPEAT_FREQUANCY = 1000 * 60 * 1; // Every 5 minutes

    public AlarmReceiver() {
        super();
    }

    public AlarmReceiver(Context context) {
        super();

    }

    private void setupAlarms() {
        Log.i(this.getClass().getName(), "alarm set on boot");
    }

    public abstract Calendar getSkateTime();
    public abstract SkateQuery getQuery();
    public abstract int getId();

    private static long getPollTime(Calendar c) {
        c.add(Calendar.HOUR, -2);
        return c.getTimeInMillis();
    }

    static Calendar moveTimeIfVeryClose(Calendar calendar) {
        long timeDiff = calendar.getTimeInMillis() - new Date().getTime();
        if (Math.abs(timeDiff) < 1000 * 60 * 60 || timeDiff > 0) {
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

        // clear old alarm
        PendingIntent alarm = PendingIntent.getBroadcast(pContext, this.getId(), intent, PendingIntent.FLAG_NO_CREATE);
        if (alarm != null) {
            alarmMgr.cancel(alarm);
            alarm.cancel();
        }

        if (setAlarm) {
            PendingIntent handler = PendingIntent.getBroadcast(pContext, this.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, getPollTime(moveTimeIfVeryClose(this.getSkateTime())), REPEAT_FREQUANCY, handler);
            // Ensure alarm comes back if phone rebooted.
            ComponentName receiver = new ComponentName(pContext, this.getClass());
            pContext.getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
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

        if (Calendar.getInstance().after(skateTime)) {
            PendingIntent alarm = PendingIntent.getBroadcast(pContext, this.getId(), pIntent, PendingIntent.FLAG_NO_CREATE);
            if (alarm != null) {
                this.setAlarm(true, pContext);
                // Cancel alarm
//                AlarmManager alarmMgr = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
//                Log.i(this.getClass().getName(), "alarm cancelled");
//                alarmMgr.cancel(alarm);
//                alarm.cancel();
//
//                // Recreate it for same time next week
//                long repeat_frequency = 1000 * 60 * 10; //duplicate code in alarmservice
//                PendingIntent newAlarm = PendingIntent.getBroadcast(pContext, this.getId(), new Intent(pContext, this.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);
//                Calendar c = this.getSkateTime();
//                c.add(Calendar.HOUR, -2);
//                c.add(Calendar.DATE, 7); // Restart This time next week please.
//                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), repeat_frequency, newAlarm);
            }
        }

        if (pIntent != null && pIntent.getAction() != null && pIntent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setupAlarms();
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

        Log.i(this.getClass().getName(), "response:");
        Log.i(this.getClass().getName(), query[0]);

        if (!query[0].equals("")) {
            String oldState = sharedPreferences.getString("previous_state", "");
            Log.i(this.getClass().getName(), oldState);

            if (!oldState.equals(query[0])) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(pContext, 0, new Intent(pContext, TodaysSkateActivity.class), 0);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(pContext)
                        .setSmallIcon(R.drawable.ic_launcher)
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



        //Intent resultIntent = new Intent(pContext, TodaysSkate.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(pContext);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(TodaysSkate.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);

        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    }

}
