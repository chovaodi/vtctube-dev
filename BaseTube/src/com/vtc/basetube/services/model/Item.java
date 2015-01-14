package com.vtc.basetube.services.model;


public class Item {
    public String id;
    public Snippet snippet;

    public class Snippet {
        public String title;
        public Thumbnails thumbnails;
    }
}
