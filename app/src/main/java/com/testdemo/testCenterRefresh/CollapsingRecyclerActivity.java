package com.testdemo.testCenterRefresh;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;


import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.testdemo.R;

import java.util.ArrayList;

public class CollapsingRecyclerActivity extends Activity {

    private View collapseView;
    private int originHeight;
    private int offset;

    private TwinklingRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<String> listData;
    private int refreshTime = 0;
    private int times = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsing_recycler_view);
        collapseView = findViewById(R.id.collapse_view);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);

        setupGridView();
        collapseView.post(new Runnable() {
            @Override
            public void run() {
                offset = originHeight = collapseView.getHeight();
            }
        });
    }

    private void setupGridView() {
        final TwinklingRefreshLayout refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);
        SinaRefreshView headerView = new SinaRefreshView(this);
        headerView.setArrowResource(R.drawable.ic_arrow);
        headerView.setTextColor(0xff745D5C);
//        TextHeaderView headerView = (TextHeaderView) View.inflate(this,R.layout.header_tv,null);
        refreshLayout.setHeaderView(headerView);

        LoadingView loadingView = new LoadingView(this);
        refreshLayout.setBottomView(loadingView);

        listData = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            listData.add("item" + i);
        }
        mAdapter = new MyAdapter(listData);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if (originHeight != 0) {
                    Log.i("greyson", "dx = " + dx + " - dy = " + dy + "\nrecycler: " + recyclerView.getScrollY() + " - " + recyclerView.getTranslationY());
                    if (dy > 0) {
                        offset -= dy;
                        if (offset <= originHeight) {
                            collapseView.setTranslationY(-originHeight+offset);
//                            ViewGroup.LayoutParams params = collapseView.getLayoutParams();
//                            params.height = offset;
//                            collapseView.setLayoutParams(params);
                        }
                    } else {
                        offset -= dy;
                        if (offset >= 0) {
                            collapseView.setTranslationY(-originHeight+offset);
//                            ViewGroup.LayoutParams params = collapseView.getLayoutParams();
//                            params.height = offset;
//                            collapseView.setLayoutParams(params);
                        }
                    }
                }*/
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Log.i("greyson", "不动");
                        break;
                    case 1:
                        Log.i("greyson", "拖拽");
                        break;
                    case 2:
                        Log.i("greyson", "惯性滚动");
                }*/
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dy = (int) (y - lastY);

                        if (originHeight > 0) {
                            Log.i("greyson", "dy = " + dy);
                            if (dy > 0) {
                                offset += dy;
                                if (offset <= originHeight) {
//                                    collapseView.setTranslationY(-originHeight + offset);
                            ViewGroup.LayoutParams params = collapseView.getLayoutParams();
                            params.height = offset;
                            collapseView.setLayoutParams(params);
                                }
                            } else {
                                offset += dy;
                                if (offset >= 0) {
//                                    collapseView.setTranslationY(-originHeight + offset);
                            ViewGroup.LayoutParams params = collapseView.getLayoutParams();
                            params.height = offset;
                            collapseView.setLayoutParams(params);
                                }
                            }
                        }
                        lastY = y;
                        break;

                    case MotionEvent.ACTION_UP:


                }

                return false;
            }
        });

        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshTime++;
                times = 0;
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        listData.clear();
                        for (int i = 0; i < 15; i++) {
                            listData.add("item" + i + "after " + refreshTime + " times of refresh");
                        }
                        mAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefreshing();
                    }

                }, 1000);            //refresh data here
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                if (times < 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 15; i++) {
                                listData.add("item" + (i + listData.size()));
                            }
                            mAdapter.notifyDataSetChanged();
                            refreshLayout.onFinishLoadMore();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                listData.add("item" + (1 + listData.size()));
                            }
                            mAdapter.notifyDataSetChanged();
                            refreshLayout.setEnableLoadmore(false);
                        }
                    }, 1000);
                }
                times++;
            }
        });
    }

    private float lastY;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
