package com.reader.myreader.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.myreader.R;
import com.reader.myreader.bean.PopupListBean;

import java.util.List;

/**
 * Created by zhuangwei on 2016/5/28.
 */
public class PopupListAdapter extends BaseAdapter {

    private List<PopupListBean> list;
    private LayoutInflater inflater;

    public PopupListAdapter(Context context, List<PopupListBean> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        if(list!=null){
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.listview_item,parent,false);
            holder = new Holder();
            holder.icon = (ImageView) convertView.findViewById(R.id.popup_icon);
            holder.title = (TextView) convertView.findViewById(R.id.popup_title);
            holder.describe = (TextView) convertView.findViewById(R.id.popup_describe);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(position<list.size()){
            holder.icon.setImageResource(list.get(position).getPopupIcon());
            holder.title.setText(list.get(position).getPopupTitle());
            holder.describe.setText(list.get(position).getPopupDescribe());
        }
        return convertView;
    }

    class Holder{
        ImageView icon;
        TextView title;
        TextView describe;
    }
}
