package com.example.luo_pc.studyformvc.model;

import com.example.luo_pc.studyformvc.bean.ImageBean;
import com.example.luo_pc.studyformvc.utils.JsonUtils;
import com.example.luo_pc.studyformvc.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

/**
 * Created by luo-pc on 2016/8/19.
 */
public class ImageModelImp implements ImageModel {

    @Override
    public void LoadImageList(final OnLoadImageListListener listener,int pageIndex) {
        OkHttpUtils.get().url(Urls.IMAGE_URL+pageIndex).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.onFailure(e);
            }

            @Override
            public void onResponse(String response, int id) {
                List<ImageBean> imageBeen = JsonUtils.readJsonImageBean(response);
                listener.onSuccess(imageBeen);
            }
        });
    }



    public interface OnLoadImageListListener {

        void onSuccess(List<ImageBean> list);

        void onFailure(Exception e);
    }
}
