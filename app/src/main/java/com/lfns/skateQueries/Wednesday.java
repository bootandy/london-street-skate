package com.lfns.skateQueries;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Wednesday  implements SkateQuery {
    public String getUrl() { return "http://www.londonskate.com"; }

    // returns: [WAIT, <some html>]
    public String[] getData(Document doc) throws IOException {
        Elements info = doc.select("#status_all_text a");
        //route name,
        Elements status = doc.select("#status_all_text h1 strong a");
        // route map
        Elements map = doc.select("#status_all_text h1 a");
        return new String[] {
                status.get(0).text(),
                info.html(),
                this.getUrl() + map.get(1).attr("href")
        };
    }
    public String getName() { return "London Skate (Wednesday)"; }
}
