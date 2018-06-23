package com.dearzs.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.entity.EntityBaseInfo;

import java.util.List;

/**
 * Created by luyanlong on 2016/5/28.
 */
public class CustomSpinerAdapter extends BaseAdapter {

    public static interface IOnItemSelectListener{
//        public void onItemClick(int pos, View view);
        public void onItemClick(int pos);
    };

    private List<String> mObjects;

    private LayoutInflater mInflater;

    public CustomSpinerAdapter(Context context,List<String> mObjects){
        this.mObjects = mObjects;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void refreshData(List<String> objects, int selIndex){
        mObjects = objects;
        if (selIndex < 0){
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()){
            selIndex = mObjects.size() - 1;
        }
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_custom_spiner_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.item_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTextView.setText(mObjects.get(pos));
        return convertView;
    }


    public static class ViewHolder
    {
        public TextView mTextView;
    }

}
