package com.example.luo_pc.studyformvc.model;

/**
 * Created by luo-pc on 2016/8/19.
 */
public interface ImageModel {
    void LoadImageList(ImageModelImp.OnLoadImageListListener listener,int pageIndex);
}
