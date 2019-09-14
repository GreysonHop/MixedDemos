package com.testdemo.testFlipView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.testdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Greyson on 2018/1/25.
 */
public class TestFlipperActivity extends Activity {
    private final static String TAG = "greyson_TestFlipper";

    ViewFlipper viewFlipper;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_flipper);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        initViewFlipper();
        initPager();
    }

    private void initPager() {
        final ArrayList<String> dataList = new ArrayList<>();
        dataList.add("墨鱼");
        dataList.add("死鱼");
        dataList.add("金烂鱼");
        dataList.add("北目鱼");
        dataList.add("哇哈哈鱼");

        viewPager.setAdapter(new TestAdapter(this, dataList));

    }

    public void initViewFlipper() {
        TranslateAnimation inAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        inAnimation.setDuration(1000);
        TranslateAnimation outAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        outAnimation.setDuration(1000);

        viewFlipper.setFlipInterval(2000);
        viewFlipper.setInAnimation(inAnimation);
        viewFlipper.setOutAnimation(outAnimation);

        for (int i = 0; i < 5; i++) {
            TextView textView = new TextView(this);
            textView.setText("test lalala of == " + i);
            textView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            viewFlipper.addView(textView);
        }

//        viewFlipper.startFlipping();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                viewFlipper.showNext();
                break;
            case R.id.previous:
                viewFlipper.showPrevious();
                break;
        }
    }

    private class TestAdapter extends PagerAdapter {

        private ArrayList<String> dataList;
        private Context context;
        private SparseArray<View> mViews;
        private HashMap<Integer, Integer> selectedMap;//被选中的菜单：位置-数量

        public TestAdapter(Context context, ArrayList<String> dataList) {
            this.dataList = dataList;
            this.context = context;

            int count = dataList.size();
            if (count % 2 == 0) {
                mViews = new SparseArray<>(count / 2);
            } else {
                mViews = new SparseArray<>(count / 2 + 1);
            }
            selectedMap = new HashMap<>();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mViews.get(position);
            final int dataIndex = position * 2;

            if (view == null) {
                view = View.inflate(context, R.layout.test_pager_item, null);
                ((TextView) view.findViewById(R.id.nameTV)).setText(dataList.get(dataIndex));
                final TextView numTV = (TextView) view.findViewById(R.id.numTV);
                view.findViewById(R.id.addTV).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != selectedMap.get(dataIndex)) {
                            int num = selectedMap.get(dataIndex);
                            num++;
                            selectedMap.put(dataIndex, num);
                            numTV.setText(String.valueOf(num));
                        } else {
                            selectedMap.put(dataIndex, 1);
                            numTV.setText(String.valueOf(1));
                        }
                    }
                });
                view.findViewById(R.id.subTV).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != selectedMap.get(dataIndex)) {
                            int num = selectedMap.get(dataIndex);
                            num--;
                            if (num > 0) {
                                selectedMap.put(dataIndex, num);
                                numTV.setText(String.valueOf(num));
                            } else if (num == 0) {
                                selectedMap.remove(dataIndex);
                                numTV.setText(String.valueOf(num));
                            }
                        }
                    }
                });


                if (position * 2 + 1 < dataList.size()) {
                    final int dataIndex2 = position * 2 + 1;
                    view.findViewById(R.id.secondItemLayout).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.nameTV2)).setText(dataList.get(dataIndex2));
                    final TextView numTV2 = (TextView) view.findViewById(R.id.numTV2);
                    view.findViewById(R.id.addTV2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != selectedMap.get(dataIndex2)) {
                                int num = selectedMap.get(dataIndex2);
                                num++;
                                selectedMap.put(dataIndex2, num);
                                numTV2.setText(String.valueOf(num));
                            } else {
                                selectedMap.put(dataIndex2, 1);
                                numTV2.setText(String.valueOf(1));
                            }
                        }
                    });
                    view.findViewById(R.id.subTV2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != selectedMap.get(dataIndex2)) {
                                int num = selectedMap.get(dataIndex2);
                                num--;
                                if (num > 0) {
                                    selectedMap.put(dataIndex2, num);
                                    numTV2.setText(String.valueOf(num));
                                } else if (num == 0) {
                                    selectedMap.remove(dataIndex2);
                                    numTV2.setText(String.valueOf(num));
                                }
                            }
                        }
                    });
                } else {
                    view.findViewById(R.id.secondItemLayout).setVisibility(View.INVISIBLE);
                }

                mViews.put(position, view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            int count = dataList.size();
            if (count % 2 == 0) {
                return count / 2;
            } else {
                return count / 2 + 1;
            }
//            return dataList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
}
