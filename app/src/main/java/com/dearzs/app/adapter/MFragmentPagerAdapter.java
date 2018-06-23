package com.dearzs.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import com.dearzs.app.base.BaseFragment;

import java.util.ArrayList;

/**
 * 为解决适配器notifyDataSetChanged不刷新问题，
 * 增加相应位置的flag值，以及重写instantiateItem方法
 *
 * @author zhaoyb
 */
public class MFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private boolean needRefresh = true;
    private ArrayList<BaseFragment> mListDatas;
    private boolean[] fragmentsUpdateFlag;
    private FragmentManager fm;

    public MFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.mListDatas = null;
    }

    public MFragmentPagerAdapter(FragmentManager fm, ArrayList<BaseFragment> mListDatas) {
        super(fm);
        this.fm = fm;
        setFragments(mListDatas);
    }

    private void setFragments(ArrayList<BaseFragment> mListDatas) {
        if (mListDatas == null || mListDatas.size()==0) return;
        this.mListDatas = mListDatas;
        fragmentsUpdateFlag = new boolean[mListDatas.size()];
        needRefresh = true;
        notifyDataSetChanged();
    }

    public void refreshPageFragment(ArrayList<BaseFragment> mListDatas) {
        if (this.mListDatas != null) {
            FragmentTransaction ft = fm.beginTransaction();
            if (ft != null) {
                for (Fragment f : this.mListDatas) {
                    ft.remove(f);
                }
                ft.commit();
                fm.executePendingTransactions();
            }
        }
        setFragments(mListDatas);
    }

    @Override
    public int getCount() {
        return mListDatas == null || mListDatas.isEmpty() ? 0 : mListDatas.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mListDatas == null || mListDatas.isEmpty() ? null : mListDatas.get(position % mListDatas.size());
    }

    @Override
    public int getItemPosition(Object object) {
        if (needRefresh) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // 得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        // 得到tag，这点很重要
        String fragmentTag = fragment.getTag();

        if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
            // 如果这个fragment需要更新
            FragmentTransaction ft = fm.beginTransaction();
            // 移除旧的fragment
            ft.remove(fragment);
            // 换成新的fragment
            fragment = mListDatas.get(position % mListDatas.size());
            // 添加新fragment时必须用前面获得的tag，这点很重要
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();

            // 复位更新标志
            fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
        }
        return fragment;
    }
}
