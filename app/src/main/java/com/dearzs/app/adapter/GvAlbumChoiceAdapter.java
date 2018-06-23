package com.dearzs.app.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.entity.EntityDirAlbum;
import com.dearzs.app.util.ImageLoaderManager;

import java.util.List;

/**
 * 相册选择适配器
 */
public class GvAlbumChoiceAdapter extends QuickAdapter<EntityDirAlbum> {

    public GvAlbumChoiceAdapter(Context context, int layoutResId, List<EntityDirAlbum> msgList) {
        super(context, layoutResId, msgList);
    }

    @Override
    protected void convert(final BaseAdapterHelper helper, final EntityDirAlbum item) {
        if (mItemClickListener != null) {
            View picParent = helper.getView(R.id.item_album_iv_pic_parent);
            picParent.setTag(helper.getPosition());
            picParent.setOnClickListener(mItemClickListener);
        }
        helper.setText(R.id.item_album_tv_folder_name, item.getDirName() + "(" + item.getChildCount() + ")");
        ImageView ivPic = helper.getView(R.id.item_album_iv_pic);

        if (!item.getTopImgPath().equals(ivPic.getTag())) {
            ivPic.setTag(item.getTopImgPath());
            ImageLoaderManager.getInstance().displayImageNoCache("file://" + item.getTopImgPath(), ivPic, 100, 100);
        }
    }

    private View.OnClickListener mItemClickListener;
    public void setItemClickListener(View.OnClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
