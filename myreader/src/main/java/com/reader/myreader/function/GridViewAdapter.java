package com.reader.myreader.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.myreader.R;
import com.reader.myreader.bean.NovelBean;

import java.util.List;

/**
 * Created by zhuangwei on 2016/5/21.
 */
public class GridViewAdapter extends BaseAdapter {

    private List<NovelBean> list;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context,List<NovelBean> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        if(list!=null && position<list.size()){
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
            convertView = inflater.inflate(R.layout.gridview_item,parent,false);
            holder = new Holder();
            holder.novelIcon = (ImageView) convertView.findViewById(R.id.novel_icon);
            holder.novelTitle = (TextView) convertView.findViewById(R.id.novel_title);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(position<list.size()){
            if(position==list.size()-1){
                holder.novelIcon.setScaleType(ImageView.ScaleType.CENTER);
                holder.novelIcon.setImageResource(R.drawable.plus);
                holder.novelIcon.setBackgroundResource(R.drawable.plus_bg_seletor);
                holder.novelTitle.setText("");
            } else {
                holder.novelIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.novelIcon.setImageBitmap(list.get(position).getNovelIcon());
                holder.novelTitle.setText(list.get(position).getNovelTitle());
            }
        }
        return convertView;
    }

    class Holder{
        ImageView novelIcon;
        TextView novelTitle;
    }
}
