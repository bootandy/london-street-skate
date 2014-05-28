package com.lfns;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lfns.skateQueries.Friday;
import com.lfns.skateQueries.SkateQuery;
import com.lfns.skateQueries.Sunday;
import com.lfns.skateQueries.Wednesday;
import com.lfns.util.QueryUrl;
import com.lfns.util.Util;

public class DebugActivity extends ActionBarActivity {
    TextView checkStatus;
    TextView otherDetails;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
        checkStatus = (TextView) findViewById(R.id.check_status);
        otherDetails = (TextView) findViewById(R.id.other_details);

        System.out.println("sunday:");
        if (savedInstanceState!= null) {
            Boolean sunday = savedInstanceState.getBoolean("sunday");
            if (sunday != null) {
                System.out.println(sunday);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_debugging) {
            return true;
        }
        else if (id == R.id.action_todays_skate) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processQuery(SkateQuery querier) {
        QueryUrlWithUpdates qu = new QueryUrlWithUpdates(querier);
        qu.execute("");
    }

    private class QueryUrlWithUpdates extends QueryUrl {
        public QueryUrlWithUpdates(SkateQuery skateQuery) {
            super(skateQuery);
        }

        protected void onPostExecute(String[] data) {
            checkStatus.setText(data[0]);
            otherDetails.setText(Html.fromHtml(data[1]));
        }
    }

    public void readLondonSkate(View view) {
        processQuery(new Wednesday());
    }

    public void readLfns(View view) {
        processQuery(new Friday());
    }

    public void readSundayStroll(View view) {
        processQuery(new Sunday());
    }

    public void testAlarm(View view) {
        //new AlarmService(this.getApplicationContext()).startAlarms();
    }

}
