package com.lfns.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;


public class AlarmService {
    private static long REPEAT_FREQUANCY = 1000 * 60 * 1; // Every 5 minutes
    private AlarmManager alarmMgr;
    private Context context;


    public AlarmService(Context context) {
        this.context = context;
        System.out.println("set alarms");
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }



//    private long getPollTime(Calendar c) {
//        c.add(Calendar.HOUR, -2);
//        return c.getTimeInMillis();
//    }
//
//    public void startAlarm(AlarmReceiver ar, boolean isOn) {
//        setAlarmDay(isOn, ar.getId(), getPollTime(ar.getSkateTime()), ar.getClass());
//    }

    public void startStateWiper() {
        Intent stateIntent = new Intent(context, ClearState.class);
        PendingIntent alarm = PendingIntent.getBroadcast(context, -1, stateIntent, PendingIntent.FLAG_NO_CREATE);
        if (alarm != null) {
            alarmMgr.cancel(alarm);
            alarm.cancel();
        }

        PendingIntent handler = PendingIntent.getBroadcast(context, -1, stateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, handler);
        ComponentName receiver = new ComponentName(context, ClearState.class);
        context.getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
