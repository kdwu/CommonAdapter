package com.sunday.imoocrecyclerview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sunday.imoocrecyclerview.adapter.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);

        // 线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
////        横向排列ItemView
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
////        数据反向展示
//        linearLayoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        itemView点击事件监听
        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity2.this, "第" + position + "数据被点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 添加数据
     *
     * @param v
     */
    public void onAddDataClick(View v) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String s = "第" + i + "条数据";
            data.add(s);
        }

        mAdapter.setDataSource(data);
    }

    /**
     * 切换布局
     *
     * @param v
     */
    public void onChangeLayoutClick(View v) {
//        从线性布局 切换为 网格布局
        if (mRecyclerView.getLayoutManager().getClass() == LinearLayoutManager.class) {
//            网格布局
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (mRecyclerView.getLayoutManager().getClass() == GridLayoutManager.class) {
//            瀑布流布局
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        } else {
            // 线性布局
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    /**
     * 插入一条数据
     *
     * @param v
     */
    public void onInsertDataClick(View v) {
        mAdapter.addData(1);
    }

    /**
     * 删除一条数据
     *
     * @param v
     */
    public void onRemoveDataClick(View v) {
        mAdapter.removeData(1);
    }
}
