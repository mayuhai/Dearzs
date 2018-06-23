package com.dearzs.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 上传照片固定的viewHolder
 * 分两种,正常和添加更多
 *
 * @author zhaoyb
 */
public class GvUploadPicRecyclerViewHolder extends RecyclerView.ViewHolder {

    private int viewType;

    public GvUploadPicRecyclerViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }
}