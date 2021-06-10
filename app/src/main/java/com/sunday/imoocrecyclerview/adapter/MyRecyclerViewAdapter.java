package com.sunday.imoocrecyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sunday.imoocrecyclerview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 1、继承RecyclerView.Adapter
 * 2、绑定ViewHolder
 * 3、实现Adapter的相关方法
 */
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

    /**
     * 创建并且返回ViewHolder
     * @param viewGroup
     * @param position
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, viewGroup, false));
    }

    /**
     * ViewHolder 绑定数据
     * @param myViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {
        myViewHolder.mIv.setImageResource(getIcon(position));
        myViewHolder.mTv.setText(dataSource.get(position));

        /**
         * 只在瀑布流布局中使用随机高度
         */
        if (mRv.getLayoutManager().getClass() == StaggeredGridLayoutManager.class) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getRandomHeight());
            myViewHolder.mTv.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            myViewHolder.mTv.setLayoutParams(params);
        }

//        改变ItemView背景颜色
        if (addDataPosition == position) {
            myViewHolder.mItemView.setBackgroundColor(Color.RED);
        } else {
            myViewHolder.mItemView.setBackgroundColor(Color.parseColor("#A4D3EE"));
        }

        myViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                调用接口的回调方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 返回数据数量
     * @return
     */
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

    /**
     * 返回不同的ItemView高度
     * @return
     */
    private int getRandomHeight () {
        return (int)(Math.random() * 1000);
    }

    /**
     * 添加一条数据
     * @param position
     */
    public void addData (int position) {
        addDataPosition = position;
        dataSource.add(position, "插入的数据");
        notifyItemInserted(position);

//        刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    /**
     * 删除一条数据
     * @param position
     */
    public void removeData (int position) {
        addDataPosition = -1;
        dataSource.remove(position);
        notifyItemRemoved(position);

//        刷新ItemView
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

    /**
     * ItemView点击事件回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
