package com.example.luo_pc.studyformvc.utils;

import android.util.Log;

import com.example.luo_pc.studyformvc.bean.ImageBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luo-pc on 2016/5/26.
 */
public class JsonUtils {
    static final String TAG = "JsonUtils";

    public static List<ImageBean> readJsonImageBean(String response) {
        List<ImageBean> imageList = new ArrayList<ImageBean>();
        try {

            JSONObject root = new JSONObject(response);
            if (!root.getBoolean("error")) {
                JSONArray results = root.getJSONArray("results");
                if(results.length() > 0) {
                    for (int i = 0; i < results.length(); i++) {
                        ImageBean imageBean = new ImageBean();
                        JSONObject jo = (JSONObject) results.get(i);
                        imageBean.setDesc(jo.getString("desc"));
                        imageBean.setUrl(jo.getString("url"));
                        imageList.add(imageBean);
                    }
                    return imageList;
                }else{
                    return null;
                }

            } else
                return null;

        } catch (Exception e) {

        }
        return null;
    }

}
