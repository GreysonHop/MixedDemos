package com.necer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.necer.calendar.BaseCalendar;
import com.necer.utils.Attrs;
import com.necer.view.CalendarView;

import org.joda.time.LocalDate;

/**
 * Created by necer on 2017/8/25.
 * QQ群:127278900
 */

public abstract class BaseCalendarAdapter extends RecyclerView.Adapter<BaseCalendarAdapter.MyViewHolder> {

    private BaseCalendar mBaseCalendar;

    protected Context mContext;
    protected int mCount;//总页数
    protected int mCurr;//当前位置
    protected LocalDate mInitializeDate;
    protected Attrs mAttrs;

    private OnDataBoundForPage mOnDataBoundForPage;

    public BaseCalendarAdapter(Context context, LocalDate startDate, LocalDate endDate, LocalDate initializeDate, Attrs attrs, BaseCalendar baseCalendar) {
        this.mContext = context;
        this.mInitializeDate = initializeDate;
        this.mCount = getIntervalCount(startDate, endDate, attrs.firstDayOfWeek) + 1;
        this.mCurr = getIntervalCount(startDate, initializeDate, attrs.firstDayOfWeek);
        this.mAttrs = attrs;
        this.mBaseCalendar = baseCalendar;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }


//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }

    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }*/


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout layout = new FrameLayout(parent.getContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewGroup parent = (ViewGroup) holder.itemView;
        parent.removeAllViews();

        CalendarView view = getCalendarView(mBaseCalendar, position);
        /*ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        view.setLayoutParams(params);*/
        Log.i("greyson", getClass().getSimpleName() + " onBindViewHolder position=" + position);
        view.setTag(position);
        parent.addView(view);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        print("onViewAttachedToWindow()", holder);
        ViewGroup parent = (ViewGroup) holder.itemView;
        View view = parent.getChildAt(0);
        if (view != null) {
            if (mOnDataBoundForPage != null) {
                mOnDataBoundForPage.onPageAttachedToWindow((Integer) view.getTag());
            }
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        print("onViewDetachedFromWindow()", holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        print("onViewRecycled()", holder);
        super.onViewRecycled(holder);
    }

    private void print(String logType, @NonNull MyViewHolder holder) {
        ViewGroup parent = (ViewGroup) holder.itemView;
        View view = parent.getChildAt(0);
        if (view != null) {
            Log.v("greyson", logType + ", tag= " + view.getTag());
        }
    }

    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        CalendarView view = getCalendarView(container, position);
        view.setTag(position);
        container.addView(view);
        return view;
    }*/

    //当前页的位置
    public int getCurrItem() {
        return mCurr;
    }

    protected abstract CalendarView getCalendarView(ViewGroup container, int position);

    protected abstract int getIntervalCount(LocalDate startDate, LocalDate endDate, int weekFirstDayType);

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View view) {
            super(view);

        }
    }

    public interface OnDataBoundForPage {
        void onPageAttachedToWindow(final int position);
    }

    public OnDataBoundForPage getOnDataBoundForPage() {
        return mOnDataBoundForPage;
    }

    public void setOnDataBoundForPage(OnDataBoundForPage onDataBoundForPage) {
        this.mOnDataBoundForPage = onDataBoundForPage;
    }

}

