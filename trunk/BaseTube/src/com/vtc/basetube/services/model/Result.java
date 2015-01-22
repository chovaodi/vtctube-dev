package com.vtc.basetube.services.model;

import java.util.ArrayList;

public class Result {
    public String nextPageToken;
    public PageInfo pageInfo;
    public ArrayList<Item> items;

    public class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }
}
