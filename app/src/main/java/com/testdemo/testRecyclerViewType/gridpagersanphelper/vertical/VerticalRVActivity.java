package com.testdemo.testRecyclerViewType.gridpagersanphelper.vertical;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testdemo.R;
import com.testdemo.testRecyclerViewType.gridpagersanphelper.DataSourceUtils;
import com.testdemo.testRecyclerViewType.gridpagersanphelper.ScreenUtils;
import com.testdemo.testRecyclerViewType.gridpagersanphelper.gridpagersnaphelper.GridPagerSnapHelper;
import com.testdemo.testRecyclerViewType.gridpagersanphelper.gridpagersnaphelper.GridPagerUtils;
import com.testdemo.testRecyclerViewType.gridpagersanphelper.gridpagersnaphelper.transform.VerticalDataTransform;

import java.util.List;

public class VerticalRVActivity extends AppCompatActivity {

    private static final int row = 5;
    private static final int spanCount = 5;

    // spanCount和column是相等的
    private static final int column = spanCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_rv);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_vertical);
        recyclerView.setHasFixedSize(true);

        //setLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        //attachToRecyclerView
        GridPagerSnapHelper gridPagerSnapHelper = new GridPagerSnapHelper();
        gridPagerSnapHelper.setRow(row).setColumn(column);
        gridPagerSnapHelper.attachToRecyclerView(recyclerView);

        int screenWidth = ScreenUtils.getScreenWidth(this);
        int itemWidth = screenWidth / spanCount;

        int totoalHeight = ScreenUtils.dip2px(this, 400);
        int itemHeight = totoalHeight / row;

        //getDataSource
        List<DataSourceUtils.ItemData> dataList = DataSourceUtils.getDataSource();
        dataList = GridPagerUtils.transformAndFillEmptyData(
                new VerticalDataTransform<DataSourceUtils.ItemData>(row, column), dataList);

        //setAdapter
        VerticalRNAdapter adapter = new VerticalRNAdapter(this, dataList, itemWidth, itemHeight);
        recyclerView.setAdapter(adapter);
    }
}
