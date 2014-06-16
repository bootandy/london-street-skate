package com.lfns.alarms;

import android.content.Context;

import com.lfns.skateQueries.SkateQuery;
import com.lfns.skateQueries.Wednesday;

import java.util.Calendar;


public class WednesdayAlarm extends AlarmReceiver {

    public WednesdayAlarm() {
        super();
    }

    public WednesdayAlarm(Context context) {
        super(context);
    }

    public Calendar getSkateTime() {
        return WednesdayAlarm.getStaticSkateTime();
    }

    public int getId() { return 1; }

    public static Calendar getStaticSkateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // If we are past skate time of today do not generate a notification
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 20) {
                calendar.add(Calendar.DATE, 7);
            }
        }
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    public SkateQuery getQuery() {
        return new Wednesday();
    }
}
