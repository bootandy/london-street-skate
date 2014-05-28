package com.lfns.skateQueries;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Sunday implements SkateQuery {
    public String getUrl() { return "http://www.lfns.co.uk"; }

    public String[] getData(Document doc) throws IOException {

        Elements strollDetails = doc.select("#fp-table-right .details");
        if (strollDetails == null || strollDetails.size() == 0) {
            strollDetails = doc.select("#fp-table-right-go .details");
        }
        String strollDetailsStr = "";
        if (strollDetails != null) {
            strollDetailsStr = strollDetails.get(0).text();
        }

        Elements strollGo = doc.select("#fp-table-right-go .go");
        if (strollGo.size() > 0) {
            return new String[] {strollDetailsStr, strollGo.get(0).text()};
        }
        Elements strollNo = doc.select("#fp-table-right-rain .rain"); //guessing
        if (strollNo.size() > 0) {
            return new String[] {strollDetailsStr, strollNo.get(0).text()};
        }
        return new String[] {"",""};
    }
    public String getName() { return "Sunday Stroll"; }
}