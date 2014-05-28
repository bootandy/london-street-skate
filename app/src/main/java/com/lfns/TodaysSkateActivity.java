package com.lfns;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        QueryUrlWithUpdates qu = new QueryUrlWithUpdates(Util.getQueryHandlerForToday());
        qu.execute("");

        alarmService = new AlarmService(this.getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("alarms_configured", false)) {
            load();
            this.saveStateConfigureAlarms(null);
        }
    }

    public class QueryUrlWithUpdates extends QueryUrl {
        public QueryUrlWithUpdates(SkateQuery skateQuery) {
            super(skateQuery);
        }

        protected void onPostExecute(String[] data) {
            skateStatus.setText(Html.fromHtml(data[1]));
            todayText.setText("Next Skate is:");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    public void saveStateConfigureAlarms(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sunday", sundayBox.isChecked());
        editor.putBoolean("wednesday", wednesdayBox.isChecked());
        editor.putBoolean("friday", fridayBox.isChecked());
        editor.putBoolean("alarms_configured", true);
        editor.commit();
        alarmService.startAlarms(sundayBox.isChecked(), wednesdayBox.isChecked(), fridayBox.isChecked());
    }

    private void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        sundayBox.setChecked(sharedPreferences.getBoolean("sunday", false));
        wednesdayBox.setChecked(sharedPreferences.getBoolean("wednesday", false));
        fridayBox.setChecked(sharedPreferences.getBoolean("friday", false));
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
