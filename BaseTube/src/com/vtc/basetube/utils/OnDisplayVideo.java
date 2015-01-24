package com.vtc.basetube.utils;

import com.vtc.basetube.BaseTubeApplication;
import com.vtc.basetube.model.ItemVideo;

public interface OnDisplayVideo {
    public void display(ItemVideo videoId);
    public BaseTubeApplication getTubeApplication();
}
