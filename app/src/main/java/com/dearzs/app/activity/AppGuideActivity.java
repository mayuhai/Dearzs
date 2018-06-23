package com.dearzs.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.commonlib.utils.PfUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangchunlei on 2015/1/19. 应用启动介绍页面
 */
public class AppGuideActivity extends BaseActivity implements OnClickListener {

    private ViewPager mViewPager;
    private String[] mGuideTitle;
    private int[] appGuideArray;
    private List<View> mGuidePage;
    private boolean mIsFirstStart = true;
    private LinearLayout mPointLatout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_guide_layout);
        mIsFirstStart = PfUtils.getBoolean(AppGuideActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_FIRST_RUN, true);
        initView();
        initData();
        setListeners();
    }

    @Override
    public void initView() {
        super.initView();
        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        mPointLatout = (LinearLayout) findViewById(R.id.guide_point_layout);
    }

    @Override
    public void initData() {
        super.initData();
        /* 引导图片数组 */
        if (mIsFirstStart) {
            appGuideArray = new int[]{R.mipmap.ic_guide_one,
                    R.mipmap.ic_guide_two};
            mGuideTitle = new String[]{
                    "海量名医\n为您的健康保驾护航", "在线会诊\n" +
                    "与专家的距离从未如此贴近"
            };
        } else {
            //升级时候的引导图
            appGuideArray = new int[]{R.mipmap.ic_guide_one,
                    R.mipmap.ic_guide_two};
        }
        mGuidePage = new ArrayList<View>();
        /**
         * 遍历图片数组，生成相应数目的ImageView对象并添加到imageViews集合中
         */
        for (int i = 0; i < appGuideArray.length; i++) {
            View view = LayoutInflater.from(AppGuideActivity.this).inflate(R.layout.layout_guide_item, null);
            ImageView guideImage = (ImageView) view.findViewById(R.id.iv_guide);
            TextView guideText = (TextView) view.findViewById(R.id.tv_guide);
            Button guideBtn = (Button) view.findViewById(R.id.bt_guide);
            guideImage.setImageResource(appGuideArray[i]);
            guideText.setText(mGuideTitle[i]);
            guideBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AppGuideActivity.this,
                            LoginActivity.class));
                    PfUtils.setBoolean(AppGuideActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_FIRST_RUN, false);
                    finish();
                }
            });
            if (i == appGuideArray.length - 1) {
                guideBtn.setVisibility(View.VISIBLE);
            } else {
                guideBtn.setVisibility(View.GONE);
            }
            mGuidePage.add(view);
        }
        AppGuideViewPagerAdapter adapter = new AppGuideViewPagerAdapter(
                mGuidePage);
        mViewPager.setAdapter(adapter);
    }

    /* 设置监听器 */
    private void setListeners() {
        mViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                        for (int j = 0; j < mPointLatout.getChildCount(); j++) {
                            ((CheckBox) mPointLatout.getChildAt(j)).setChecked(j == i);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_start_app:
//                startActivity(new Intent(AppGuideActivity.this, HomeActivity.class));
//                PfUtils.setBoolean(AppGuideActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_FIRST_RUN, false);
//                finish();
//                break;
//
//            default:
//                break;
        }
    }

    public class AppGuideViewPagerAdapter extends PagerAdapter {
        private List<View> views;

        public AppGuideViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }
}
