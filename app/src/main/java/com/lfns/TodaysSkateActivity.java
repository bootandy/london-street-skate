package com.lfns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lfns.alarms.FridayAlarm;
import com.lfns.alarms.SundayAlarm;
import com.lfns.alarms.WednesdayAlarm;
import com.lfns.skateQueries.SkateQuery;
import com.lfns.alarms.AlarmService;
import com.lfns.util.QueryUrl;
import com.lfns.util.Util;

public class TodaysSkateActivity extends ActionBarActivity {
    private WebView webView;
    private CheckBox sundayBox;
    private CheckBox wednesdayBox;
    private CheckBox fridayBox;
    private TextView skateStatus;
    private TextView todayText;
    private AlarmService alarmService;
    private String mapUrl = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(this.getClass().getName(), "onCreate");
        setContentView(R.layout.todays_skate);
        skateStatus = (TextView) findViewById(R.id.skateStatus);
        todayText = (TextView) findViewById(R.id.today);

        sundayBox = (CheckBox) findViewById(R.id.sundayBox);
        wednesdayBox = (CheckBox) findViewById(R.id.wednesdayBox);
        fridayBox = (CheckBox) findViewById(R.id.fridayBox);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(Util.getUrlForToday());

        alarmService = new AlarmService(this.getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("alarms_configured", false)) {
            load();
            new SundayAlarm().setAlarm(sundayBox.isChecked(), this.getApplicationContext());
            new WednesdayAlarm().setAlarm(wednesdayBox.isChecked(), this.getApplicationContext());
            new FridayAlarm().setAlarm(fridayBox.isChecked(), this.getApplicationContext());
            alarmService.startStateWiper();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("alarms_configured", true);
            editor.commit();
        }

    }

    public class QueryUrlWithUpdates extends QueryUrl {
        public QueryUrlWithUpdates(SkateQuery skateQuery) {
            super(skateQuery);
        }

        protected void onPostExecute(String[] data) {
            if (data[1].equals("")) {
                skateStatus.setText("No news");
            } else {
                skateStatus.setText(Html.fromHtml(data[1]));
            }
            mapUrl = data[2];
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(this.getClass().getName(), "onStart");
        skateStatus.setText("Loading skate...");

        QueryUrlWithUpdates qu = new QueryUrlWithUpdates(Util.getQueryHandlerForToday());
        qu.execute("");

        load();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void saveStateConfigureAlarms(View v) {
        final Context context = this.getApplicationContext();
        AsyncTask<String, Integer, String[]> task = new AsyncTask<String, Integer, String[]>() {
            @Override
            protected String[] doInBackground(String... strings) {
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                boolean oldSunday = sharedPreferences.getBoolean("sunday", true);
                boolean oldWednesday = sharedPreferences.getBoolean("wednesday", true);
                boolean oldFriday = sharedPreferences.getBoolean("friday", true);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sunday", sundayBox.isChecked());
                editor.putBoolean("wednesday", wednesdayBox.isChecked());
                editor.putBoolean("friday", fridayBox.isChecked());
                editor.commit();

                if (oldSunday != sundayBox.isChecked()) {
                    //alarmService.startAlarm(new SundayAlarm(), sundayBox.isChecked());
                    new SundayAlarm().setAlarm(sundayBox.isChecked(), context);
                }
                if (oldWednesday != wednesdayBox.isChecked()) {
                    new WednesdayAlarm().setAlarm(wednesdayBox.isChecked(), context);
                }
                if (oldFriday != fridayBox.isChecked()) {
                    new FridayAlarm().setAlarm(fridayBox.isChecked(), context);
                }
                alarmService.startStateWiper();

                return null;
            }
        };

        task.execute();
    }

    public void viewWeather(View v) {
        Uri uriUrl = Uri.parse("http://forecast.io/#/f/51.5075,-0.1633");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void viewRoute(View v) {
        if (!mapUrl.equals("")) {
            Uri uriUrl = Uri.parse(mapUrl);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);
        } else {
            Toast.makeText(getApplicationContext(), "Can't find route", Toast.LENGTH_SHORT).show();
        }
    }

    private void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        sundayBox.setChecked(sharedPreferences.getBoolean("sunday", true));
        wednesdayBox.setChecked(sharedPreferences.getBoolean("wednesday", true));
        fridayBox.setChecked(sharedPreferences.getBoolean("friday", true));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_debugging) {
            Intent i = new Intent(this, DebugActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_todays_skate) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
