package com.vtc.basetube.model;

import java.util.ArrayList;

public class ListData<T> extends ArrayList<T> {
    private static final long serialVersionUID = 1L;

    private String mNextPageToken;

    public String getNextPageToken() {
        return mNextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }
}
