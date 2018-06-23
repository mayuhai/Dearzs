package com.dearzs.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.ArrayList;

/**
 * 图片画廊适配器
 */
public class NewGalleryAdapter extends AbstractViewPagerAdapter<String> {

    private LayoutInflater mInflater;
    private View.OnClickListener mItemClickListener;

    public NewGalleryAdapter(Context ctx, ArrayList<String> mListDatas) {
        this(ctx, mListDatas, null);
    }

    public NewGalleryAdapter(Context ctx, ArrayList<String> mListDatas,
                             View.OnClickListener mItemClickListener) {
        super(mListDatas);
        this.mItemClickListener = mItemClickListener;
        mInflater = LayoutInflater.from(ctx);
    }

    public void refreshData(ArrayList<String> listDatas) {
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }


    @Override
    public View newView(int position) {
        final String picPath = getItem(position);
        View mLayout = mInflater.inflate(R.layout.item_gallery, null);
        if (mItemClickListener != null) {
            mLayout.setOnClickListener(mItemClickListener);
        }
        ImageView mIvPic = GetViewUtil.getView(mLayout, R.id.item_gallery_iv_pic);

        if (picPath != null) {
            if (picPath.startsWith("http")) {
                ImageLoaderManager.getInstance().displayImage(picPath, mIvPic);
            } else {
                ImageLoaderManager.getInstance().displayImage("file://" + picPath, mIvPic, R.mipmap.ic_img_loading);
            }
        }
        return mLayout;
    }
}
