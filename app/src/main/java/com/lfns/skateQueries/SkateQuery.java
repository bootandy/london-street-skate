package com.lfns.skateQueries;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface SkateQuery {
    String getUrl();

    String[] getData(Document doc) throws IOException;

    String getName();
}
