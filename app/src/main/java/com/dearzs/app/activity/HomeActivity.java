package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.CommuntityFragment;
import com.dearzs.app.activity.expert.ExpertFragment;
import com.dearzs.app.activity.home.HomeFragment;
import com.dearzs.app.activity.home.MessageListActivity;
import com.dearzs.app.activity.home.SearchActivity;
import com.dearzs.app.activity.mine.MineFragment;
import com.dearzs.app.activity.mine.PersionalDataActivity;
import com.dearzs.app.activity.mine.SettingActivity;
import com.dearzs.app.adapter.MFragmentPagerAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.chat.model.EntityEvent;
import com.dearzs.app.chat.model.MySelfInfo;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespAppUpdate;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.MViewPager;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;
import com.dearzs.commonlib.utils.log.LogUtil;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 应用主页
 */
public class HomeActivity extends BaseActivity implements
        View.OnTouchListener, ViewPager.OnPageChangeListener {

    /**
     * 首页页面
     */
    public final static int PAGE_INDEX_HOME = 0;
    /**
     * 社区页面
     */
    public final static int PAGE_INDEX_COMMUNTITY = 1;
    /**
     * 专家页面
     */
    public final static int PAGE_INDEX_EXPERT = 2;
    /**
     * 我的页面
     */
    public final static int PAGE_INDEX_MINE = 3;

    /**
     * 底部tabbar单选按钮组
     */
    private RadioGroup rg_nav;
    /**
     * 底部tabbar按钮，首页，社区，专家，我的
     */
    private RadioButton rb_home, rb_communtity, rb_expert, rb_mine;
    private LinearLayout llNavg;

    /**
     * fragment的容器
     */
    private MViewPager mContainer;

    /**
     * fragment数组
     */
    private ArrayList<BaseFragment> mFragments;
    private HomeFragment mHomeFragment;
    private CommuntityFragment mCommuntityFragment;
    private ExpertFragment mExpertFragment;
    private MineFragment mMineFragment;
    private EditText mEtSearch;

    /**
     * fragment的适配器
     */
    private MFragmentPagerAdapter mFragmentPagerAdapter;
    /**
     * 当前页面索引
     */
    private int mCurPageIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置主页视图
        setContentLayout(R.layout.activity_home);

        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            MySelfInfo.getInstance().setId(userInfo.getPhone());
            MySelfInfo.getInstance().setUserSig(userInfo.getSig());
            MySelfInfo.getInstance().setNickName(userInfo.getName());
            MySelfInfo.getInstance().setAvatar(userInfo.getAvatar());
            TimManager.getInstance().loginIM();
            //如果已经是认证医生且为认证通过状态，且没有设置会诊和转诊，并且没有弹过提示框
            boolean isShow = userInfo.getType() == EntityUserInfo.DOCTORUSER
                    && userInfo.getState() == EntityUserInfo.VERIFY_SUCC
                    && ((userInfo.getVisitState() != null && userInfo.getVisitState() == 0)
                    || (userInfo.getReferralState() != null && userInfo.getReferralState() == 0))
                    && !PfUtils.getBoolean(HomeActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_SHOW_GUIDE_SETTING_ALERT, false);
            if (isShow) {
                showConfirmDialog(HomeActivity.this, "您已经认证成功，需要先进入”我的“模块选择”出诊设置“和”转诊设置“ 才可被搜索到以至预约会诊等操作", "去设置", "稍后设置", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mContainer != null) {
                                    mContainer.setCurrentItem(PAGE_INDEX_MINE);
                                    PfUtils.setBoolean(HomeActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_SHOW_GUIDE_SETTING_ALERT, true);
                                }
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PfUtils.setBoolean(HomeActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_SHOW_GUIDE_SETTING_ALERT, true);
                            }
                        }
                );
            }

            //设置
            if (TextUtils.isEmpty(userInfo.getName()) && !PfUtils.getBoolean(HomeActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_SHOW_NICKNAME_SETTING_ALERT, false)) {
                showConfirmDialog(HomeActivity.this, "您已注册成功，为了能够更好享受第二诊室的服务，需要先去设置一下昵称", "去设置", "稍后设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PersionalDataActivity.startIntent(HomeActivity.this);
                        PfUtils.setBoolean(HomeActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_SHOW_NICKNAME_SETTING_ALERT, true);
                    }
                }, null);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCurPageIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_PAGE, 0);

        if (mContainer != null) {
            mContainer.setCurrentItem(mCurPageIndex);
        }
    }

    @Override
    public void initView() {
        super.initView();
        llNavg = getView(R.id.home_layout_navg);
        rg_nav = getView(R.id.home_rg_nav);
        rb_home = getView(R.id.home_rb_main);
        rb_communtity = getView(R.id.home_rb_community);
        rb_expert = getView(R.id.home_rb_expert);
        rb_mine = getView(R.id.home_rb_mine);
        mEtSearch = getTitleBar().getEtSearch();
        mEtSearch.setFocusable(false);

        mEtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.startIntent(HomeActivity.this);
            }
        });

        mContainer = getView(R.id.home_layout_content);
        mContainer.setOnPageChangeListener(this);
        mContainer.setScanScroll(false);
        mContainer.setOffscreenPageLimit(4);

        mFragmentPagerAdapter = new MFragmentPagerAdapter(getSupportFragmentManager());
        mContainer.setAdapter(mFragmentPagerAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        rg_nav.setOnCheckedChangeListener(rgOncheckedChangeListener);
        llNavg.setOnTouchListener(this);
    }

    @Override
    public void initData() {
        super.initData();

        mCurPageIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_PAGE, 0);

        // 设置默认标题
        setTitleBarState(mCurPageIndex);

        // 初始化Adapter容器
        initFragment();

        checkAppUpdata();


        if (mContainer != null) {
            mContainer.setCurrentItem(mCurPageIndex);
        }
    }

    private void checkAppUpdata() {
        if (!Utils.mIsAPPUpdataing) {
            ReqManager.getInstance().reqCheckAppUpdate(reqCheckAppUpdate);
        }
    }

    Callback<RespAppUpdate> reqCheckAppUpdate = new Callback<RespAppUpdate>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespAppUpdate response) {
            if (onSuccess(response)) {
                Utils.checkSoftwareUpdate(HomeActivity.this, response, false);
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    Callback<RespUserLogin> reqGetLoginInfoCallBack = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response) && response.getResult() != null && response.getResult().getUser() != null) {
                BaseApplication.getInstance().setLoginInfo(response.getResult());
                if (mMineFragment != null) {
//                    mMineFragment.lazyLoad();
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    /**
     * 初始化fragment
     */
    private void initFragment() {
        if (mFragments != null) return;
        mFragments = new ArrayList<BaseFragment>();
        mHomeFragment = new HomeFragment();
        mCommuntityFragment = new CommuntityFragment();
        mExpertFragment = new ExpertFragment();
        mMineFragment = new MineFragment();
        mFragments.add(mHomeFragment);
        mFragments.add(mCommuntityFragment);
        mFragments.add(mExpertFragment);
        mFragments.add(mMineFragment);
        mFragmentPagerAdapter.refreshPageFragment(mFragments);
    }

    /**
     * 设置TitleBar状态
     *
     * @param curPageIndex
     */
    private void setTitleBarState(int curPageIndex) {
        String centerTxt = "";
        switch (curPageIndex) {
            case PAGE_INDEX_HOME:
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, null);
                addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_ET_SEARCH, null);
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_MESSAGE, null);
                break;
            case PAGE_INDEX_COMMUNTITY:
                centerTxt = "全部动态";
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, centerTxt);
                addLeftBtn(TitleBarView.TITLE_TYPE_NULL, null);
                addRightBtn(TitleBarView.TITLE_TYPE_NULL, null);
                break;
            case PAGE_INDEX_EXPERT:
                centerTxt = "专家";
                addLeftBtn(TitleBarView.TITLE_TYPE_NULL, null);
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, centerTxt);
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_SEARCH, null);
                break;
            case PAGE_INDEX_MINE:
                centerTxt = "我的";
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, centerTxt);
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_SETTING_AND_MESSAGE, null);
                addLeftBtn(TitleBarView.TITLE_TYPE_NULL, null);
                break;
        }
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        switch (mCurPageIndex) {
            case PAGE_INDEX_HOME:
                MessageListActivity.startIntent(HomeActivity.this);
                break;
            case PAGE_INDEX_MINE:
                SettingActivity.startIntent(HomeActivity.this);
                break;
            case PAGE_INDEX_EXPERT:
                SearchActivity.startIntent(HomeActivity.this);
                break;
        }
    }

    @Override
    public void onRightBtn2Click() {
        super.onRightBtn2Click();
        switch (mCurPageIndex) {
            case PAGE_INDEX_MINE:
                MessageListActivity.startIntent(HomeActivity.this);
                break;
        }
    }

    @Override
    public void onCenterBtnClick() {
        super.onCenterBtnClick();
        BaseFragment frg = getCurTabFragment(mCurPageIndex);
        if (frg != null) {
            frg.onCenterBtnClick();
        }
    }

    /**
     * 单选切换监听
     */
    private final RadioGroup.OnCheckedChangeListener rgOncheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int index = -1;
            switch (checkedId) {
                case R.id.home_rb_main:
                    index = PAGE_INDEX_HOME;
                    break;
                case R.id.home_rb_community:
                    index = PAGE_INDEX_COMMUNTITY;
                    break;
                case R.id.home_rb_expert:
                    index = PAGE_INDEX_EXPERT;
                    break;
                case R.id.home_rb_mine:
                    index = PAGE_INDEX_MINE;
                    break;
                default:
                    break;
            }
            mCurPageIndex = index;
            if (mContainer != null) {
                mContainer.setCurrentItem(mCurPageIndex);
            }
            setTitleBarState(mCurPageIndex);
        }
    };

    /**
     * 获取当前显示的Fragment
     *
     * @param curPageIndex
     * @return
     */
    public BaseFragment getCurTabFragment(int curPageIndex) {
        if (mFragments == null || mFragments.isEmpty() || curPageIndex < 0 || curPageIndex > mFragments.size() - 1) {
            return null;
        }
        return mFragments.get(curPageIndex);
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(ctx, HomeActivity.class);
        ctx.startActivity(intent);
    }

    private long firstTime = 0;

    public boolean handleBack() {
        long secondTime = System.currentTimeMillis();
        // 如果两次按键时间间隔大于800毫秒，则不退出
        if (secondTime - firstTime > 800) {
            ToastUtil.showLongToast("再按一次退出程序");
            firstTime = secondTime;// 更新firstTime
            return true;
        } else {
            BaseApplication.getInstance().exit();
            finish();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCommuntityFragment != null) {
            mCommuntityFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (mMineFragment != null) {
            mMineFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (mHomeFragment != null) {
            mHomeFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPageSelected(int i) {
        int resId = rb_home.getId();
        switch (i) {
            case PAGE_INDEX_HOME:
                resId = rb_home.getId();
                break;
            case PAGE_INDEX_COMMUNTITY:
                resId = rb_communtity.getId();
                break;
            case PAGE_INDEX_EXPERT:
                resId = rb_expert.getId();
                break;
            case PAGE_INDEX_MINE:
                resId = rb_mine.getId();
                break;
        }
        rg_nav.check(resId);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void updateUserInfo(String update) {
        if (Constant.EVENT_UPDATE_USER_INFO.equals(update)) {
            ReqManager.getInstance().reqUpdateUserInfo(reqGetLoginInfoCallBack, BaseApplication.getInstance().getUserInfo(), Utils.getUserToken(HomeActivity.this));
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void imStateEvent(EntityEvent.IMStateEvent imStateEvent) {
        super.imStateEvent(imStateEvent);
        if (imStateEvent != null) {
            switch (imStateEvent.getState()) {
                case TimManager.STATE_LOGIN_SUC://登录成功
                    if (mCurPageIndex == 0) {
                        HomeFragment fragment = (HomeFragment) getCurTabFragment(mCurPageIndex);
                        if (fragment != null) {
                            fragment.getConversation();
                        }
                    }
                    break;
            }
        }
    }
}
