package com.lfns.alarms;

import android.content.Context;

import com.lfns.skateQueries.SkateQuery;
import com.lfns.skateQueries.Sunday;

import java.util.Calendar;

public class SundayAlarm extends AlarmReceiver {

    public SundayAlarm() {
        super();
    }

    public SundayAlarm(Context context) {
        super(context);
    }

    public Calendar getSkateTime() {
        return SundayAlarm.getStaticSkateTime();
    }

    public int getId() { return 3; }

    public static Calendar getStaticSkateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }


    public SkateQuery getQuery() {
        return new Sunday();
    }
}
