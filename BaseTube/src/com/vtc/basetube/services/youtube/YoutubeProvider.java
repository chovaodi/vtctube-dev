package com.vtc.basetube.services.youtube;

import java.util.ArrayList;

import com.vtc.basetube.model.Category;

public class YoutubeProvider {
    private YoutubeController mController;

    public YoutubeProvider() {
        mController = new YoutubeController();
    }
    
    public void getCategoryChannel(OnRequest<ArrayList<Category>> categories) {

    }
}
