# StudyForMVP
##写在前面##
又到了周末了，本来这周准备总结一下透明状态栏之类的东西……但是突然群里就吹起了MVP的牛，这让我这个MVC都只有小半桶水的人都不敢说话。但是程序员不会吹牛跟条咸鱼又有什么区别？
![](http://upload-images.jianshu.io/upload_images/1976147-bc51e70906298050.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
所以我毅然决然的打开了搜索引擎，利用我科学上网的本领搜集了一些MVP相关的文章，不过看完也没啥感觉是真的，果然对于coder来说talk is cheap果断撸code才是正道。好了，日常吹牛、唠嗑(1/1)。

##1、MVC与MVP##
对于Android开发者来说，MVC应该是比较熟悉的。首先我自制一副极简风格的MVC图来做说明。

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/1976147-55cd71d2d9c94227.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

MVC是一种软件设计典范，用一种业务逻辑、数据、界面分离的方法组织代码。但是在Android中用MVC有一点明显的不足：View层与Controller层难以明确的划分出去。因为在Android中各种布局文件是View层没错，但是各个Activity和Fragment呢？这些东西既像View又像Controller，但我们一般会将其划分为Controller层，对于View层的更新一般都会放在对应的Activity或者Fragment(或者其他)中，而我们所需要做的仅仅是抽取出一个Model来实现MVC。

可以，这很Android。

![](http://upload-images.jianshu.io/upload_images/1976147-caf2f50caf5e6f8e.gif?imageMogr2/auto-orient/strip)

MVC在Android中看上去不是很标准，那么MVP的出现之后与MVC一对比，相信各个Android开发者都会觉得：

这玩意还不错诶~靠谱！
![算了不自己画了……](http://upload-images.jianshu.io/upload_images/1976147-bacc810e832c9537.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

从图中可以很明显的看书View层不再和Model层互知，View层和Model层通过Presenter层交互。在这了借用hongyang大神的那句话：之所以MVP会让人感觉耳目一新，是因为这次的跳跃是从并不标准的MVC到MVP的一个转变，减少了Activity的职责，简化了Activity中的代码，将复杂的逻辑代码提取到了Presenter中进行处理。与之对应的好处就是，耦合度更低，更方便的进行测试。

##2、MVP小DEMO演练
这个小demo花了我不少时间去想通，让我错过了林丹与杨宗纬的大战！郁闷！

国际惯例先上目录：

![目录.png](http://upload-images.jianshu.io/upload_images/1976147-9ef8b104bd80f7be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我的demo是图片加载的小demo，adapter里的适配器是recyclerview的适配器，ImageBean是图片数据，里面包含了一个Url和一个图片相关的描述。utils里是以前自己以前封装的解析这个接口的工具，Urls里保存的是接口。先把非重点在前面讲了，后面开始介绍一下我写的mvp。

###2.1、Model与View层###
我上来是先写Model层与View层的，因为这俩货在MVP里是解耦的，不能互相感知，所以我可以在设计者两层的时候只考虑需求与实现，不考虑在Presenter里干的事。

首先是Model层：
我要加载图片，我这加载图片用的是Glide，如果你不了解这个强大的开源库，你可以先去百度一下。所以需要我在Model中做的事情就是获取图片信息的集合。首先定义一个Model层的接口：
```java
public interface ImageModel {
    void LoadImageList(ImageModelImp.OnLoadImageListListener listener,int pageIndex);
}
```
图片比较多，采用分页加载，至于这个listener是一个获取数据成功和失败的回调，可以在回调中进行相应的操作。

```java
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
```
获取数据的代码量不是非常的多，因为我用的都是封装的工具进行请求和解析json。这个Model层的类实现了我刚刚定义的接口，执行具体的逻辑。

以上是Model层的代码，接下来分析一下View层需要什么东西。我这个demo只是加载图片而已，我所采用的是recyclerview，那么我需要的仅仅是一个集合，然后将这个集合传入adapter中，剩下的事情adapter都会帮我处理好的。
```java
public interface ImageLoadView {
    void getImageList(List<ImageBean> list);
}
```

让MainActivity实现这个接口
```java
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
    }

    ...
    ...
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



}
```
中间还有很多代码，我这里只是抽取出来一部分代码。可以看出在我这个demo里V层和M层的定义简直不废吹灰之力，但是……
![并没有什么卵用](http://upload-images.jianshu.io/upload_images/1976147-b0f7e456225181b1.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###2.2、Presenter###
Presenter层的逻辑着实让我烧了一会脑子，有些东西就是这样，想通了就很简单，想不通就会很痛苦。如何让M层与V层交互？首先我们需要明确的一点是：获取数据的逻辑在Model层那个实现类里面

你可能说这不废话吗……的确是废话，既然在那个实现类里，那我们肯定需要一个这个类的实例对象，不然怎么去执行那段代码？加载图片需要我们在presenter里做的事情也非常的少，只有获取图片集合这一件事可干而已。所以定义如下接口：
```java
public interface ImagePresenter {
    void loadList(int pageIndex);
}
```


接下来看一下Presenter层的实现类：
```java
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
```

可以看到在这个实现类里，持有了一个M层实现类的对象，持有了一个V层的接口。这个类实现了P层接口和M层请求失败成功的接口。可以看到在loadList()方法中用了M层对象去执行具体的获取数据的代码，而在请求成功的回调方法中用持有的V层接口对象将此list传出。所以在MainActivity实现了V层接口后就可以获取list数据了。

说了那么多你可能会有点被绕糊涂了，很简单，用我极简的作图风格来帮你理解，当然了，如果你对于回调还不十分了解的话建议去看我的这篇文章[Android之回调函数](http://www.jianshu.com/p/7ac60e182449)


![最终图](http://upload-images.jianshu.io/upload_images/1976147-b45145935c21b820.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


可能看了以上的图你也不是很明白，没关系，网上资料很多，同时别忘了自己动手！
对了，放个最终跑起来的样子吧……
![效果图](http://upload-images.jianshu.io/upload_images/1976147-5a13d3cdc368a479.gif?imageMogr2/auto-orient/strip)

对了项目名字写错了，写了MVC……你们懂就好……

