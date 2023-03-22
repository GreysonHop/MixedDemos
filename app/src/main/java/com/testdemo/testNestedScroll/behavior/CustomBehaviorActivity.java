package com.testdemo.testNestedScroll.behavior;

import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.testdemo.BaseCommonActivity;
import com.testdemo.R;
import com.testdemo.util.CommonTextAdapter;
import com.testdemo.util.CommonImgAdapter;

import java.util.ArrayList;

public class CustomBehaviorActivity extends BaseCommonActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView mRV_Horizon;
    private CommonTextAdapter mAdapter;
    private ArrayList<String> listData;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_custom_behavior;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView)this.findViewById(R.id.rv_vertical);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        listData = new ArrayList<>();
        for(int i = 0; i < 18 ;i++){
            listData.add("item" + i);
        }
        mAdapter = new CommonTextAdapter(listData);
        mRecyclerView.setAdapter(mAdapter);


        mRV_Horizon = (RecyclerView) this.findViewById(R.id.rv_horizontal);
        mRV_Horizon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<String> listData2 = new ArrayList<>();
        for(int i = 0; i < 6 ;i++){
            listData2.add("image");
        }
        mRV_Horizon.setAdapter(new CommonImgAdapter(listData2));

        animate();
    }

    int imageOriginHeight;
    private void animate() {
        CollapsingToolbarLayout ctl = findViewById(R.id.collapsing_toolbar);

        AppBarLayout abl = findViewById(R.id.appbar);
        abl.setExpanded(false);

        ImageView imageView = (ImageView) findViewById(R.id.iv_demo);

        abl.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {



            // 第一次滑动的时候记录图片的原始高度
            if (imageOriginHeight == 0) {
                imageOriginHeight = mRV_Horizon.getMeasuredHeight();
            }

            // 根据滑动的距离缩放图片
            float newHeight = imageOriginHeight + verticalOffset;
            float scale = newHeight / imageOriginHeight;
            ViewCompat.setScaleY(mRV_Horizon, scale);
            ViewCompat.setScaleX(mRV_Horizon, scale);

        });
    }
}
