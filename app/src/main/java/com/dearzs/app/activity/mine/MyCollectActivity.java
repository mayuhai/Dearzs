package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.dearzs.app.R;
import com.dearzs.app.activity.mine.fragment.FragmentConsultationCollection;
import com.dearzs.app.activity.mine.fragment.FragmentExpertCollection;
import com.dearzs.app.adapter.MFragmentPagerAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.util.Constant;
import com.dearzs.app.widget.MViewPager;
import com.dearzs.app.widget.MyTabBar;
import com.dearzs.app.widget.TitleBarView;

import java.util.ArrayList;

/**
 * Created by Lyl
 * 收藏列表
 */
public class MyCollectActivity extends BaseActivity implements MyTabBar.OnTabClickListener, ViewPager.OnPageChangeListener {
    private MyTabBar mTabBar;
    private String[] mTabTitles;
    private MViewPager mViewPager;
    private ArrayList<BaseFragment> mPageList;
    private MFragmentPagerAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_my_collection);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "我的关注");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void onLeftBtnClick() {
        setResult(Constant.REQUEST_CODE_HOME_ACTIVITY, getIntent());
        super.onLeftBtnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onLeftBtnClick();
    }

    @Override
    public void initView() {
        super.initView();
        initViewPager();
        initTabBarView();
    }

    @Override
    public void initData() {
        super.initData();
    }

    private void initViewPager() {
        mViewPager = (MViewPager) findViewById(R.id.order_carsource_viewpager);
        mPageList = new ArrayList<BaseFragment>();
        mPageList.add(new FragmentExpertCollection());
        mPageList.add(new FragmentConsultationCollection());
        mPageAdapter = new MFragmentPagerAdapter(getSupportFragmentManager(), mPageList);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setScanScroll(true);
    }

    private void initTabBarView() {
        mTabTitles = new String[]{"医学专家", "医学资讯"};
        mTabBar = (MyTabBar) findViewById(R.id.order_carsource_tabbar);
        mTabBar.setNormalBgColor(R.color.white);
        mTabBar.setSelectedBgColor(R.color.holo_orange_dark);
        mTabBar.setSelectedTextColor(R.color.green);
        mTabBar.setNormalTextColor(R.color.gray_1);
        mTabBar.setIsScrollable(true);              //设置可滚动
        mTabBar.setMode(MyTabBar.MODE_LINE);//设置下面有标签的模式
        mTabBar.setTabClickListener(this);//文字显示
        mTabBar.setTitles(mTabTitles);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mTabBar.setSelected(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabClick(int position) {
        mViewPager.setCurrentItem(position);
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, MyCollectActivity.class);
        ctx.startActivity(intent);
    }
}
