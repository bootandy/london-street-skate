package com.lfns.util;

import com.lfns.skateQueries.Friday;
import com.lfns.skateQueries.SkateQuery;
import com.lfns.skateQueries.Sunday;
import com.lfns.skateQueries.Wednesday;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;

import static java.lang.Thread.sleep;


public class Util {
    public static String getUrlForToday() {
        return getQueryHandlerForToday().getUrl();
    }

    public static SkateQuery getQueryHandlerForToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.MONDAY || day == Calendar.TUESDAY || day == Calendar.WEDNESDAY) {
            return new Wednesday();
        }else if (day == Calendar.THURSDAY || day == Calendar.FRIDAY) {
            return new Friday();
        }else if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return new Sunday();
        }else {
            throw new RuntimeException("Bad day of the week");
        }
    }

    public static Document callTheUrl(String url) throws IOException {
        IOException except = null;
        for (int i = 0; i < 3; i++) {
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e) {
                try {
                    sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                except = e;
            }
        }
        throw(except);
    }
}
