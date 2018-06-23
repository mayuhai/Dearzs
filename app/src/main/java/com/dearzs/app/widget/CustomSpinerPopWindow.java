package com.dearzs.app.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.CustomSpinerAdapter;
import com.dearzs.app.entity.EntityBaseInfo;

import java.util.List;

/**
 * Created by luyanlong on 2016/5/28.
 */
public class CustomSpinerPopWindow  extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private CustomSpinerAdapter mAdapter;
    private CustomSpinerAdapter.IOnItemSelectListener mItemSelectListener;


    public CustomSpinerPopWindow(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public void setItemListener(CustomSpinerAdapter.IOnItemSelectListener listener){
        mItemSelectListener = listener;
    }

    public void setAdatper(CustomSpinerAdapter adapter){
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_spiner_window_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);


        mListView = (ListView) view.findViewById(R.id.spinner_listview);
        view.findViewById(R.id.custom_window_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mListView.setOnItemClickListener(this);
    }


    public void refreshData(List<String> list, int selIndex) {
        if (list != null && selIndex  != -1)
        {
            if (mAdapter != null){
                mAdapter.refreshData(list, selIndex);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (mItemSelectListener != null){
            mItemSelectListener.onItemClick(position);
//            mItemSelectListener.onItemClick(position, view);
        }
    }
}