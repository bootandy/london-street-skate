package com.lfns.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Calendar;


public class AlarmService {
    private static long REPEAT_FREQUANCY = 1000 * 60 * 5; // Every 5 minutes
    private AlarmManager alarmMgr;
    private Context context;


    public AlarmService(Context context) {
        this.context = context;
        System.out.println("set alarms");
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private void setAlarmDay(boolean day, int id, long pollTime, Class clazz) {
        Intent intent = new Intent(context, clazz);

        // clear old alarm
        PendingIntent alarm = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        if (alarm != null) {
            alarmMgr.cancel(alarm);
            alarm.cancel();
        }

        if (day) {
            PendingIntent handler = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, pollTime, REPEAT_FREQUANCY, handler);
            // Ensure alarm comes back if phone rebooted.
            ComponentName receiver = new ComponentName(context, clazz);
            context.getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    private long getPollTime(Calendar c) {
        c.add(Calendar.HOUR, -2);
        return c.getTimeInMillis();
    }

    public void startAlarms(boolean sunday, boolean wednesday, boolean friday) {

        setAlarmDay(sunday, new SundayAlarm().getId(), getPollTime(SundayAlarm.getStaticSkateTime()), SundayAlarm.class);
        setAlarmDay(wednesday, new WednesdayAlarm().getId(), getPollTime(WednesdayAlarm.getStaticSkateTime()), WednesdayAlarm.class);
        setAlarmDay(friday, new FridayAlarm().getId(), getPollTime(FridayAlarm.getStaticSkateTime()), FridayAlarm.class);

        // state wiper:
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
