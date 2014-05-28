package com.lfns.alarms;

import android.content.Context;

import com.lfns.skateQueries.Friday;
import com.lfns.skateQueries.SkateQuery;

import java.util.Calendar;

public class FridayAlarm extends AlarmReceiver {

    public FridayAlarm() {
        super();
    }
    public FridayAlarm(Context context) {
        super(context);
    }

    public Calendar getSkateTime() {
        return FridayAlarm.getStaticSkateTime();
    }

    public int getId() { return 2; };

    public static Calendar getStaticSkateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }


    public SkateQuery getQuery() {
        return new Friday();
    }
}
