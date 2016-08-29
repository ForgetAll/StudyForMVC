package com.example.luo_pc.studyformvc;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.luo_pc.studyformvc.R;
import com.example.luo_pc.studyformvc.adapter.ImageListAdapter;
import com.example.luo_pc.studyformvc.bean.ImageBean;
import com.example.luo_pc.studyformvc.present.ImagePresenter;
import com.example.luo_pc.studyformvc.present.ImagePresenterImp;
import com.example.luo_pc.studyformvc.utils.Urls;
import com.example.luo_pc.studyformvc.view.ImageLoadView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageLoadView {

    private RecyclerView image_list;
    private ImagePresenter imagePresenter;
    private List<ImageBean> imageList = null;
    private ImageListAdapter adapter;
    private int pageIndex = 1;
    private GridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        adapter = new ImageListAdapter(this);
        imagePresenter = new ImagePresenterImp(this);
        imagePresenter.loadList(pageIndex);
        staggeredGridLayoutManager = new GridLayoutManager(this, 2);
//        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        image_list.setLayoutManager(staggeredGridLayoutManager);
        image_list.setAdapter(adapter);
        adapter.setisShow(false);
        //间隔
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        image_list.addItemDecoration(decoration);
        image_list.addOnScrollListener(onScrollListener);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        private int position;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            position = staggeredGridLayoutManager.findLastVisibleItemPosition();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //SCROLL_STATE_IDLE
            //The RecyclerView is not currently scrolling.
            if (adapter.getisShow() && newState == RecyclerView.SCROLL_STATE_IDLE &&
                    position + 1 == adapter.getItemCount()) {
                //加载更多
                imagePresenter.loadList(pageIndex);
            }
        }
    };


    private void initView() {
        image_list = (RecyclerView) findViewById(R.id.rc_image_list);
    }

    @Override
    public void getImageList(List<ImageBean> list) {
        adapter.setisShow(true);
        if (imageList == null) {
            imageList = new ArrayList<ImageBean>();
        }
        imageList.addAll(list);
        if (pageIndex == 1) {
            adapter.setData(list);
        } else {
            if (list == null || list.size() == 0) {
                //如果没有更多数据则隐藏脚布局
                adapter.setisShow(false);
            }
        }
        //setData方法中含有刷新，无需刷新了。
        adapter.setData(imageList);
        pageIndex += 1;
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        imagePresenter = null;
        super.onDestroy();
        

    }
}
