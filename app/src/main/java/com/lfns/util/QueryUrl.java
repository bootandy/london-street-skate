package com.lfns.util;

import android.os.AsyncTask;

import com.lfns.skateQueries.SkateQuery;

import org.jsoup.nodes.Document;

import java.io.IOException;


public class QueryUrl extends AsyncTask<String, Integer, String[]> {

    private SkateQuery skateQuery;

    public QueryUrl(SkateQuery skateQuery) {
        this.skateQuery = skateQuery;
    }

    // may raise: java.net.UnknownHostException on bad url
    protected String[] doInBackground(String... urlStr){
        try {
            Document d = Util.callTheUrl(skateQuery.getUrl());
            String[] data = skateQuery.getData(d);
            return data;
        } catch(Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            return new String[] {"","",""};
        }
    }

}
