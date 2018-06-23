package com.dearzs.app.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.NewGalleryAdapter;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.ArrayList;

/**
 * 自定义画廊控件
 */
public class GalleryView extends RelativeLayout
        implements ViewPager.OnPageChangeListener {

    private ViewGroup mLayout;
    private MViewPager mVpGallery;
    private ViewGroup mLayoutCount;
    private TextView mTvCurPage;
    private TextView mTvTotalPage;

    private OnClickListener mItemClickListener;
    /**
     * 上下文对象
     */
    private Context mContext;
    private NewGalleryAdapter mAdapter;
    private ArrayList<String> mPicList;

    public GalleryView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mLayout = (ViewGroup) mInflater.inflate(R.layout.gallery_layout, this);

        mLayoutCount = GetViewUtil.getView(mLayout, R.id.gallery_layout_count);
        mTvCurPage = GetViewUtil.getView(mLayout, R.id.gallery_tv_cur_page);
        mTvTotalPage = GetViewUtil.getView(mLayout, R.id.gallery_tv_total_page);

        mPicList = new ArrayList<String>();

        mVpGallery = GetViewUtil.getView(mLayout, R.id.gallery_vp);
        mVpGallery.setScanScroll(true);
        mVpGallery.setOnPageChangeListener(this);
        setCurrentItem(0);
        mAdapter = new NewGalleryAdapter(mContext, mPicList, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener == null) return;
                mItemClickListener.onClick(v);
            }
        });
        mVpGallery.setAdapter(mAdapter);
        refreshData(mPicList);
    }

    public void setItemClickListener(OnClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * 设置图片列表
     *
     * @param mPicList
     */
    public void refreshData(ArrayList<String> picList) {
        if (picList == null) {
            picList = new ArrayList<String>();
        }
        this.mPicList = picList;
        int size = this.mPicList == null ? 0 : this.mPicList.size();
        if(mAdapter != null){
            mAdapter.refreshData(mPicList);
        }
        // 删除元素了,下标调为1
        if (mPicList.size() < size) {
            setCurrentItem(0);
        } else {
            setCurPage(mVpGallery.getCurrentItem() + 1 + "");
        }
        setTotalPage("/" + mPicList.size());
    }

    public void setCurPage(String curPage) {
        mTvCurPage.setText(curPage);
    }

    public void setCurrentItem(int pos) {
        if (pos < 0 || mPicList == null || pos > mPicList.size() - 1) {
            return;
        }
        mVpGallery.setCurrentItem(pos, true);
    }

    public void setTotalPage(String totalPage) {
        mTvTotalPage.setText(totalPage);
    }

    public void hideCountView() {
        mLayoutCount.setVisibility(View.GONE);
    }

    public void showCountView() {
        mLayoutCount.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageSelected(int pos) {
        if (pos < 0 || mPicList == null || pos > mPicList.size() - 1) {
            return;
        }
        mTvCurPage.setText(pos + 1 + "");
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
}

