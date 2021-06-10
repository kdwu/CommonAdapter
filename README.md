---
theme: condensed-night-purple
highlight: tomorrow-night
---

# 传统的适配器
在 Android 项目中，基本上都会有列表功能，而现在的列表功能都是通过 RecyclerView 实现的，当项目中列表功能比较多的时候，每一个 RecyclerView 都需要一个 Adapter 适配器，这样会使得项目中的 Adapter 类非常的多。所以，封装一个万能的 RecyclerView 适配器是可以提高我们的开发效率的。在这之前，我们先来看一下传统适配器配合 RecyclerView 是怎样使用的。

我们先来看一下我们要实现的例子的样子，如下所示：
![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f51dd545cdb0481eab39b5b9d0e20d1c~tplv-k3u1fbpfcp-watermark.image)

适配器 Adapter 的代码如下所示：
```java
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private OnItemClickListener onItemClickListener;
    private RecyclerView mRv;
    private List<String> dataSource;
    private Context mContext;
    private int addDataPosition = -1;

    public MyRecyclerViewAdapter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.dataSource = new ArrayList<>();
        this.mRv = recyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        myViewHolder.mIv.setImageResource(getIcon(position));
        myViewHolder.mTv.setText(dataSource.get(position));
        
        // 只在瀑布流布局中使用随机高度
        if (mRv.getLayoutManager().getClass() == StaggeredGridLayoutManager.class) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getRandomHeight());
            myViewHolder.mTv.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myViewHolder.mTv.setLayoutParams(params);
        }

        // 改变ItemView背景颜色
        if (addDataPosition == position) {
            myViewHolder.mItemView.setBackgroundColor(Color.RED);
        } else {
            myViewHolder.mItemView.setBackgroundColor(Color.parseColor("#A4D3EE"));
        }

        myViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用接口的回调方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private int getIcon (int position) {
        switch (position % 5) {
            case 0:
                return R.mipmap.a;
            case 1:
                return R.mipmap.b;
            case 2:
                return R.mipmap.c;
            case 3:
                return R.mipmap.d;
            case 4:
                return R.mipmap.e;
        }
        return 0;
    }

    private int getRandomHeight () {
        return (int)(Math.random() * 1000);
    }

    public void addData (int position) {
        addDataPosition = position;
        dataSource.add(position, "插入的数据");
        notifyItemInserted(position);

        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    public void removeData (int position) {
        addDataPosition = -1;
        dataSource.remove(position);
        notifyItemRemoved(position);

        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        ImageView mIv;
        TextView mTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIv = itemView.findViewById(R.id.iv);
            mTv = itemView.findViewById(R.id.tv);
            mItemView = itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
```

上面这个适配器虽然添加了点击事件、插入数据等功能，但是还是比较简单的，Adapter 适配器的实现主要就是三个步骤：
1. 继承 RecyclerView.Adapter
2. 创建 ViewHolder
3. 绑定数据

有了这个适配器后，RecyclerView 只要绑定这个适配器就可以显示列表了，具体代码如下所示：
```java
mRecyclerView = findViewById(R.id.recycler_view);

// 线性布局
LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
mRecyclerView.setLayoutManager(linearLayoutManager);
mAdapter = new MyRecyclerViewAdapter(this, mRecyclerView);
mRecyclerView.setAdapter(mAdapter);

// itemView点击事件监听
mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(int position) {
        Toast.makeText(MainActivity2.this, "第" + position + "条数据被点击", Toast.LENGTH_SHORT).show();
    }
});
```

这样一个列表功能就实现了，但是上面这个 Adapter 只能适配这个列表，无法做到通用性。现在我们来实现通用的 RecyclerView 适配器。


# 封装通用 Adapter
由于 RecyclerView 的 Adapter 必须继承自 RecyclerView.Adapter，并且指定我们写的 ViewHolder 为泛型，为了达到万能的效果，我们把需要传入的 Java Bean 属性直接用一个泛型 T 指代。这里我是利用接口来封装，没有使用抽象类，并且还实现了多 type 类型的适配器的封装，具体代码如下所示：
```java
public class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private final List<T> mList;

    private OnBindDataListener<T> onBindDataListener;
    private OnMoreBindDataListener<T> onMoreBindDataListener;

    // 单类型
    public CommonAdapter(List<T> mList, OnBindDataListener<T> onBindDataListener) {
        this.mList = mList;
        this.onBindDataListener = onBindDataListener;
    }

    // 多类型
    public CommonAdapter(List<T> mList, OnMoreBindDataListener<T> onMoreBindDataListener) {
        this.mList = mList;
        this.onBindDataListener = onMoreBindDataListener;
        this.onMoreBindDataListener = onMoreBindDataListener;
    }

    //绑定数据
    public interface OnBindDataListener<T> {
        void onBindViewHolder(T model, CommonViewHolder viewHolder, int type, int position);

        int getLayoutId(int type);
    }

    //绑定多类型的数据
    public interface OnMoreBindDataListener<T> extends OnBindDataListener<T> {
        int getItemType(int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (onMoreBindDataListener != null) {
            return onMoreBindDataListener.getItemType(position);
        }
        return 0;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = onBindDataListener.getLayoutId(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return CommonViewHolder.getCommonViewHolder(parent.getContext(), view);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBindDataListener.onBindViewHolder(mList.get(position), holder, getItemViewType(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    // 插入一项数据
    public void insert(T item, int position) {
        mList.add(position, item);
        notifyItemInserted(position);
    }

    // 删除一项数据
    public void remove(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }
}
```
这样一个通用 Adapter 适配器就写好了，**核心思想就是通过接口将 onCreateViewHolder() 和 onBindViewHolder() 交给调用者处理**。

我们都知道 ListView 中 ViewHolder 是需要自定义的，在 RecyclerView 中 ViewHolder 是已经封装好的，所以我们还需要封装一个通用的 ViewHolder，具体代码如下所示：
```java
public class CommonViewHolder extends RecyclerView.ViewHolder {

    // 子View的集合
    private SparseArray<View> mViews;
    private Context context;

    public CommonViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        mViews = new SparseArray<>();
    }

    public static CommonViewHolder getCommonViewHolder(Context context, View itemView) {
        return new CommonViewHolder(context, itemView);
    }

    /**
     * 提供给外部访问 View 的方法
     *
     * @param viewId id
     * @param <T>    泛型
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置文本
     *
     * @param viewId id
     * @param text   文本
     * @return this
     */
    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片
     *
     * @param viewId id
     * @param resId  图片id
     * @return this
     */
    public CommonViewHolder setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
        return this;
    }
}
```


# 使用万能的 RecyclerView 适配器
我们已经将通用的 Adapter 和通用的 ViewHolder 都封装好了，现在我们来看一下是如何使用的，具体代码如下所示：
```java
mRecyclerView = findViewById(R.id.recycler_view);

// 线性布局
LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
mRecyclerView.setLayoutManager(linearLayoutManager);
mCommonAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<String>() {
    @Override
    public void onBindViewHolder(String model, CommonViewHolder viewHolder, int type, final int position) {
        viewHolder.setText(R.id.tv, model);
    }

    @Override
    public int getLayoutId(int type) {
        return R.layout.item_layout;
    }
});

mRecyclerView.setAdapter(mCommonAdapter);
```
这个使用是非常简单的，通过 getLayoutId() 传入 Item 的布局，通过 onBindViewHolder() 方法设置数据。以后我们项目中 RecyclerView 的适配器只需要上述两个类就可以了。

# 源码

