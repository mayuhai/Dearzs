package com.dearzs.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.MultiItemTypeSupport;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.util.ImageLoaderManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册图片列表适配器
 */
public class AlbumAdapter extends QuickAdapter<String> {
    /**
     * 第一个item打开相机
     **/
    public static final String TYPE_TAKE_PIC_STR = "TakePhoto";
    /**
     * 打开相机item
     **/
    public static final int TYPE_TAKE_PIC = 1;
    /**
     * 普通item
     **/
    public static final int TYPE_CUS = 2;
    /**
     * item类型数量
     **/
    public static final int TYPE_COUNT = 2;

    /**
     * 选中的集合
     **/
    private ArrayList<String> mSelList;

    private OnSelectChangeListener listener;

    public AlbumAdapter(Context context, ArrayList<String> picList,
                        MultiItemTypeSupport<String> multiItemTypeSupport) {
        super(context, picList, multiItemTypeSupport);

        mSelList = new ArrayList<String>();
    }

    @Override
    public void replaceAll(List<String> elem) {
        super.replaceAll(elem);
        cleanTipCount();
    }

    @Override
    protected void convert(final BaseAdapterHelper helper, final String item) {
        final View vContent = helper.getView();
        switch (helper.layoutId) {
            case R.layout.item_gv_album_take_photo:
                break;
            case R.layout.item_gv_album_cus:
                helper.setVisible(R.id.item_gv_album_iv_selected, mSelList.contains(item));
                final ImageView ivPic = helper.getView(R.id.item_gv_album_iv_pic);
                vContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int tag = Integer.parseInt(ivPic.getTag().toString());
                            if (tag < 0) {
                                return;
                            }
                        } catch (Exception e) {
                        }

                        boolean vState = !helper.getVisible(R.id.item_gv_album_iv_selected);
                        if (vState) {
                            mSelList.add(item);
                            // 因含有最大值,所以增加此判断
                            boolean canContinue = true;
                            if (listener != null) {
                                canContinue = listener.onSelectChange(mSelList);
                            }
                            if (!canContinue) {
                                mSelList.remove(item);
                                return;
                            }
                        } else {
                            mSelList.remove(item);
                            if (listener != null) {
                                listener.onSelectChange(mSelList);
                            }
                        }
                        helper.setVisible(R.id.item_gv_album_iv_selected, vState);
                    }
                });

                ImageLoaderManager.getInstance().displayImageNoCache("file://" + item, ivPic, 100, 100, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        view.setTag(-1);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        view.setTag(1);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }
                });
                break;
        }
    }

    /**
     * 清除选中数量提示
     */
    public void cleanTipCount() {
        if (mSelList != null) {
            mSelList.clear();
        }
        if (listener != null) {
            listener.onSelectChange(mSelList);
        }
    }

    public interface OnSelectChangeListener {
        public boolean onSelectChange(List<String> list);
    }

    public void setOnSelectChangeListener(OnSelectChangeListener listener) {
        this.listener = listener;
    }
}
