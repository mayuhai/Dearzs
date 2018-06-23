package com.dearzs.app.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 *
 * 若viewPager用于展示view时，，可复用此viewpager以减少重复使用
 * (因FragmentPagerAdapter以解决Fragment复用问题）
 *
 * @author zhaoyb
 *
 */
public abstract class AbstractViewPagerAdapter<T> extends PagerAdapter {

    public ArrayList<T> mListDatas;
    private SparseArray<View> mViews;

    public AbstractViewPagerAdapter(ArrayList<T> mListDatas) {
        this.mListDatas = mListDatas;
        mViews = new SparseArray<View>(mListDatas.size());
    }

    @Override
    public int getCount() {
        return mListDatas == null || mListDatas.isEmpty() ? 0 : mListDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        if (view == null) {
            view = newView(position);
            mViews.put(position, view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(view);
        return view;
    }

    abstract public View newView(int position);


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View mTempView = mViews.get(position);
        container.removeView(mTempView);
        mViews.remove(position);
    }

    public T getItem(int position) {
        return mListDatas == null || mListDatas.isEmpty() ? null : mListDatas.get(position);
    }
}
