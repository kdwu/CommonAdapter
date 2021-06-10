package com.sunday.imoocrecyclerview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sunday.imoocrecyclerview.common.CommonAdapter;
import com.sunday.imoocrecyclerview.common.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CommonAdapter<String> mCommonAdapter;

    List<String> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 20; i++) {
            String s = "第" + i + "条数据";
            mList.add(s);
        }

        mRecyclerView = findViewById(R.id.recycler_view);

        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mCommonAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<String>() {
            @Override
            public void onBindViewHolder(String model, CommonViewHolder viewHolder, int type, final int position) {
                viewHolder.setText(R.id.tv, model);
                switch (position % 5) {
                    case 0:
                        viewHolder.setImageResource(R.id.iv, R.mipmap.a);
                        break;
                    case 1:
                        viewHolder.setImageResource(R.id.iv, R.mipmap.b);
                        break;
                    case 2:
                        viewHolder.setImageResource(R.id.iv, R.mipmap.c);
                        break;
                    case 3:
                        viewHolder.setImageResource(R.id.iv, R.mipmap.d);
                        break;
                    case 4:
                        viewHolder.setImageResource(R.id.iv, R.mipmap.e);
                        break;
                }
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.item_layout;
            }
        });

        mRecyclerView.setAdapter(mCommonAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void onChangeLayoutClick(View v) {
        // 从线性布局 切换为 网格布局
        if (mRecyclerView.getLayoutManager().getClass() == LinearLayoutManager.class) {
            // 网格布局
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (mRecyclerView.getLayoutManager().getClass() == GridLayoutManager.class) {
            // 瀑布流布局
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        } else {
            // 线性布局
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    public void onInsertDataClick(View v) {
        mCommonAdapter.insert("插入的数据", 1);
    }

    public void onRemoveDataClick(View v) {
        mCommonAdapter.remove(1);
    }

}
