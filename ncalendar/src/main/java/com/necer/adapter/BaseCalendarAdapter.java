package com.necer.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.necer.calendar.BaseCalendar;
import com.necer.mynew.CalendarData;
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
    protected int mCurr;//当前位置，今天的位置。比如今天是2020/3/10，离startDate有mCurr个月或周
    protected LocalDate mInitializeDate;
    protected Attrs mAttrs;

    private SparseArray<CalendarData> mDataList = new SparseArray<>();

    public BaseCalendarAdapter(Context context, LocalDate startDate, LocalDate endDate, LocalDate initializeDate, Attrs attrs, BaseCalendar baseCalendar) {
        this.mContext = context;
        this.mInitializeDate = initializeDate;
        this.mCount = getIntervalCount(startDate, endDate, attrs.firstDayOfWeek) + 1;
        this.mCurr = getIntervalCount(startDate, initializeDate, attrs.firstDayOfWeek);
        this.mAttrs = attrs;
        this.mBaseCalendar = baseCalendar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getCalendarView(mBaseCalendar);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CalendarData data = getDataByPosition(position);

        CalendarView calendarView = (CalendarView) holder.itemView;
        calendarView.setCalendarData(data.getInitialDate(), data.getDateList());

        Log.i("greyson", getClass().getSimpleName() + " onBindViewHolder position=" + position);
    }

    public CalendarData getDataByPosition(int position) {
        CalendarData data = mDataList.get(position);
        if (data == null) {
            data = getCalendarData(position);
            mDataList.put(position, data);
        }
        return data;
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
        /*ViewGroup parent = (ViewGroup) holder.itemView;
        View view = parent.getChildAt(0);
        if (view != null) {
            Log.v("greyson", logType + ", tag= " + view.getTag());
        }*/
    }

    //当前页的位置
    public int getCurrItem() {
        return mCurr;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    protected abstract CalendarView getCalendarView(ViewGroup container);

    protected abstract CalendarData getCalendarData(int position);

    protected abstract int getIntervalCount(LocalDate startDate, LocalDate endDate, int weekFirstDayType);

    class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View view) {
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

