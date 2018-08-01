package com.testdemo.testVerticalScrollView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.testdemo.R;

import java.util.List;

public class AvatarListAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> menus;

    public AvatarListAdapter(Context context, List<Integer> menus) {
        this.context = context;
        this.menus = menus;
    }

    @Override
    public int getCount() {
        return menus == null ? 0 : menus.size();
    }

    @Override
    public Object getItem(int position) {
        return menus == null ? "" : menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item_avatar_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //头像顺序要反序加载
        int realPosition = getCount() - 1 - position;
        viewHolder.userIcon.setImageResource(menus.get(realPosition));
        return convertView;
    }

    class ViewHolder {
        ImageView userIcon;
    }
}
