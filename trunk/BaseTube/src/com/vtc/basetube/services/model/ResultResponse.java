package com.vtc.basetube.services.model;

import java.util.ArrayList;

public class ResultResponse<T> {
    public String nextPageToken;
    public PageInfo pageInfo;
    public ArrayList<Item<T>> items;

    public class PageInfo {
        public int totalResults;
        public int resultsPerPage;
    }
}
