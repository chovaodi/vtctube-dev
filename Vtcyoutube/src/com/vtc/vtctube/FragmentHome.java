/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vtc.vtctube;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Picasso;
import com.vtc.vtctube.adpter.MenuHomeAdapter;
import com.vtc.vtctube.model.ItemCategory;
import com.vtc.vtctube.model.ItemPost;
import com.vtc.vtctube.services.AysnRequestHttp;
import com.vtc.vtctube.utils.IResult;
import com.vtc.vtctube.utils.OnDisplayVideo;
import com.vtc.vtctube.utils.Utils;

public class FragmentHome extends SherlockFragment {
    private GridView gridView;
    private View view;
    private FrameLayout frameLayout;
    // private PullToRefreshLayout mPullToRefreshLayout;
    ResultCallBack callBack = null;
    public static List<ItemCategory> listData = null;
    public static String[] cateName;

    private static FragmentHome f = null;
    private OnDisplayVideo mOnDisplayVideo;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    static FragmentHome newInstance(int num) {
        if (f == null) {
            f = new FragmentHome();
        }

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBack = new ResultCallBack();
    }

    @Override
    public void onAttach(Activity activity) {
        if(activity instanceof OnDisplayVideo) {
            mOnDisplayVideo = (OnDisplayVideo) activity;
        }
        super.onAttach(activity);
    }
    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_top);

        MainActivity.progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(100, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinish() {
                MainActivity.progressBar.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                addview();
            }
        }.start();
    }

    public void addview() {
        ImageView img = (ImageView) view.findViewById(R.id.imageView1);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MainActivity.smooth.setVisibility(View.GONE);
                Utils.getVideoView(AcitivityLoadding.itemPost, getActivity(), null);
                if(mOnDisplayVideo != null) {
                    mOnDisplayVideo.display();
                }
            }
        });
        String idPostHome = "xxx";
        if (AcitivityLoadding.itemPost != null) {
            idPostHome = AcitivityLoadding.itemPost.getVideoId();
        }

        Picasso.with(getActivity()).load("http://img.youtube.com/vi/" + idPostHome + "/maxresdefault.jpg")
                .placeholder(R.drawable.bgr_home_video).into(img);

        String url = Utils.getUrlHttp(Utils.host, "get_category_index");
        Log.d("url", url);
        if (GlobalApplication.dataCate.length() > 0) {
            showView(GlobalApplication.dataCate);
        } else {
            if (Utils.isExistFile(Utils.GET_CATE_INDEX)) {
                GlobalApplication.dataCate = Utils.readJsonFile(Utils.GET_CATE_INDEX);
                showView(GlobalApplication.dataCate);
            } else {
                new AysnRequestHttp((ViewGroup) view, Utils.LOAD_FIRST_DATA, MainActivity.smooth, callBack).execute(url);
            }
        }
        new AysnRequestHttp((ViewGroup) view, Utils.REFRESH, null, callBack).execute(url);
    }

    public void showView(String result) {

        try {
            JSONObject jsonObj = new JSONObject(result);
            String status = jsonObj.getString("status");
            if (status.equals("ok")) {
                JSONArray jsonArray = jsonObj.getJSONArray("categories");
                listData = new ArrayList<ItemCategory>();
                cateName = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    ItemCategory item = new ItemCategory();
                    JSONObject json = (JSONObject) jsonArray.get(i);
                    item.setIdCategory(json.getString("id"));
                    item.setTitle(json.getString("title"));
                    item.setSlug(json.getString("slug"));
                    item.setPostcount(json.getInt("post_count"));
                    cateName[i] = json.getString("title");
                    listData.add(item);
                }
            }

            gridView = (GridView) view.findViewById(R.id.list);
            // View header = getActivity().getLayoutInflater().inflate(
            // R.layout.header, null);

            // list.addHeaderView(header);
            gridView.setNumColumns(2);
            gridView.setPadding(Utils.convertDpToPixel(20, getActivity()), 0, Utils.convertDpToPixel(20, getActivity()), 0);

            MenuHomeAdapter adapter = new MenuHomeAdapter(getActivity(), listData);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Utils.hideSoftKeyboard(getActivity());
                    MainActivity.callBackCLickCate.getCate(listData.get(arg2).getTitle(), listData.get(arg2).getIdCategory());

                }

            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public class ResultCallBack implements IResult {

        @Override
        public void getResult(int type, String result) {
            GlobalApplication.dataCate = result;
            if (result.length() > 0)
                Utils.writeJsonFile(result, false, Utils.GET_CATE_INDEX);
            if (type == Utils.LOAD_FIRST_DATA) {
                showView(result);
            }
        }

        @Override
        public void pushResutClickItem(int type, int postion, boolean isLike) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCLickView(ItemPost item) {
            // TODO Auto-generated method stub

        }
    }

}
