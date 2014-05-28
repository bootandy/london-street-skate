package com.lfns.skateQueries;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Wednesday  implements SkateQuery {
    public String getUrl() { return "http://www.londonskate.com"; }

    // returns: [WAIT, <some html>]
    public String[] getData(Document doc) throws IOException {
        Elements info = doc.select("#status_all_text a");
        Elements status = doc.select("#status_all_text h1 strong a");
        return new String[] {status.get(0).text(), info.html()};
    }
    public String getName() { return "London Skate (Wednesday)"; }
}