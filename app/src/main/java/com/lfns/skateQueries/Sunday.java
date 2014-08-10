package com.lfns.skateQueries;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Sunday implements SkateQuery {
    public String getUrl() { return "http://www.lfns.co.uk"; }

    public String[] getData(Document doc) throws IOException {

        String strollDetailsStr = "";
        Elements strollDetails = doc.select("#fp-table-right .details");
        if (strollDetails == null || strollDetails.size() == 0) {
            strollDetails = doc.select("#fp-table-right-go .details");
        }
        if (strollDetails == null || strollDetails.size() == 0) {
            strollDetails = doc.select("#fp-table-right-rain .details");
        }
        // For some weird reason rain on sunday comes up as the left table
        if (strollDetails == null || strollDetails.size() == 0) {
            strollDetails = doc.select("#fp-table-left-rain .details");
        }

        if (strollDetails != null) {
            strollDetailsStr = strollDetails.get(0).text();
        }

        String link = "";
        Elements strollLink = doc.select("#fp-table-right .skatedate a");
        if (strollLink == null || strollLink.size() == 0) {
            strollLink = doc.select("#fp-table-right-go .skatedate a");
        }
        if (strollLink == null || strollLink.size() == 0) {
            strollLink = doc.select("#fp-table-right-rain .skatedate a");
        }
        if (strollLink == null || strollLink.size() == 0) {
            strollLink = doc.select("#fp-table-left-rain .skatedate a");
        }
        if (strollLink != null) {
            link = strollLink.attr("href");
        }

        Elements strollGo = doc.select("#fp-table-right-go .go");
        if (strollGo.size() > 0) {
            return new String[] {strollDetailsStr, strollGo.get(0).text(), link};
        }
        Elements strollNo = doc.select("#fp-table-right-rain .rain");
        if (strollNo.size() > 0) {
            return new String[] {strollDetailsStr, strollNo.get(0).text(), link};
        }
        Elements strollNoLeft = doc.select("#fp-table-left-rain .rain");
        if (strollNoLeft.size() > 0) {
            return new String[] {strollDetailsStr, strollNoLeft.get(0).text(), link};
        }
        return new String[] {"", "", ""};
    }
    public String getName() { return "Sunday Stroll"; }
}