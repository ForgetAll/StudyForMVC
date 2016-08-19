package com.example.luo_pc.studyformvc.present;

import com.example.luo_pc.studyformvc.bean.ImageBean;
import com.example.luo_pc.studyformvc.model.ImageModel;
import com.example.luo_pc.studyformvc.model.ImageModelImp;
import com.example.luo_pc.studyformvc.view.ImageLoadView;

import java.util.List;

/**
 * Created by luo-pc on 2016/8/19.
 */
public class ImagePresenterImp implements ImagePresenter,ImageModelImp.OnLoadImageListListener{
    private ImageLoadView imageLoadView;
    private ImageModel imageModel;

    public ImagePresenterImp(ImageLoadView imageLoadView){
        this.imageModel = new ImageModelImp();
        this.imageLoadView = imageLoadView;
    }

    @Override
    public void onSuccess(List<ImageBean> list) {
        imageLoadView.getImageList(list);
    }

    @Override
    public void onFailure(Exception e) {
        //我这里就不做处理了
    }

    @Override
    public void loadList(int pageIndex) {
        imageModel.LoadImageList(this,pageIndex);
    }
}
