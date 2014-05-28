package com.lfns.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.lfns.alarms.AlarmReceiver;
import com.lfns.skateQueries.SkateQuery;

import java.util.Calendar;

public class ClearState extends BroadcastReceiver {

    public void onReceive(Context pContext, Intent pIntent) {
        Log.i(this.getClass().getName(), "wiper");
        SharedPreferences sharedPreferences = pContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("previous_state", "");
        editor.commit();
    }

}
