package com.dearzs.app.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvPatientListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.resp.RespPatientList;
import com.dearzs.app.util.CharacterParser;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.PinyinComparator;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.MyTabBar;
import com.dearzs.app.widget.SideBar;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 患者库界面 2016/5/26.
 */
public class PatientLibraryActivity extends BaseActivity implements SectionIndexer, MyTabBar.OnTabClickListener
        , TextView.OnEditorActionListener {
    private final static int TYPE_PATIENT_HUI = 100;        //会诊
    private final static int TYPE_PATIENT_SUI = TYPE_PATIENT_HUI + 1;       //随诊
    private final String[] mTabTitles = new String[]{"会诊患者", "随诊患者"};
    private ViewGroup mEmptyView;
    private SideBar mSideBar;
    private ListView mLvCustomer;
    private LvPatientListAdapter mCustomerListAdapter;
    private List<EntityPatientInfo> mCustomerList;
    private List<EntityPatientInfo> mNormalCustomerList;
    private List<EntityPatientInfo> mFamilyCustomerList;
    private LinearLayout mLinTitleLayout;
    private TextView mTvNoFriends;
    private TextView mLetterDialog;
    private TextView mLetterTitle;
    private TextView mFootView;
    private int mType = TYPE_PATIENT_HUI;
    private MyTabBar mTabBar;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int mLastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser mCharacterParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_patient_library);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "患者库");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_ADD, null);
    }

    private void initTabBarView() {
        mTabBar = getView(R.id.patient_lib_tabbar);
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
    public void onRightBtnClick() {
        super.onRightBtnClick();
        PatientSearchActivity.startIntent(PatientLibraryActivity.this, mType == TYPE_PATIENT_SUI ? 1 : 0);
    }

    @Override
    public void initView() {
        super.initView();
        initTabBarView();
        // 实例化汉字转拼音类
        mCharacterParser = CharacterParser.getInstance();
        mLvCustomer = getView(R.id.mine_customer_list);

        mEmptyView = getView(R.id.mine_custom_layout_empty);
        mEmptyView.setVisibility(View.GONE);
        //设置患者为空页面
        Utils.setEmptyView(this, mEmptyView, R.mipmap.ic_empty_img, "暂无患者信息", true);

        mSideBar = getView(R.id.mine_customer_sidrbar);
        mLinTitleLayout = getView(R.id.mine_customer_title_layout);
        mLetterTitle = getView(R.id.mine_customer_title_layout_catalog);
        mLetterDialog = getView(R.id.mine_customer_tv_dialog);
        mTvNoFriends = getView(R.id.mine_customer_title_layout_no_friends);
        mSideBar.setTextView(mLetterDialog);
        mLinTitleLayout.setVisibility(mType == TYPE_PATIENT_HUI ? View.VISIBLE : View.GONE);

        // 设置右侧触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (mCustomerListAdapter != null && mLvCustomer != null) {
                    // 该字母首次出现的位置
                    int position = mCustomerListAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        mLvCustomer.setSelection(position);
                    }
                }

            }
        });
        mLvCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EntityPatientInfo item = mCustomerListAdapter.getItem(position);
                if (mCustomerListAdapter != null && item != null) {
                    if (getIntent() != null && getIntent().getStringExtra(Constant.KEY_FROM) != null && getIntent().getStringExtra(Constant.KEY_FROM).equals(Constant.KEY_FROM_ORDER_CONFIRM)) {
                        EventBus.getDefault().post(item);
                        finish();
                    } else {
                        PatientDetailActivity.startIntent(PatientLibraryActivity.this, String.valueOf(item.getId()), mType == TYPE_PATIENT_HUI ? 0 : 1);
                    }
                }
            }
        });

        mLvCustomer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mCustomerList == null || mCustomerList.size() <= 0) {
                    return;
                }
                try {
                    int section = getSectionForPosition(firstVisibleItem);
                    if (firstVisibleItem != mLastFirstVisibleItem) {
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLinTitleLayout
                                .getLayoutParams();
                        params.topMargin = 0;
                        mLinTitleLayout.setLayoutParams(params);
                        mLetterTitle.setText(mCustomerList.get(
                                getPositionForSection(section)).getSortLetters().toUpperCase());
                    }
                    if (mCustomerList.size() > 1) {
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);
                        int nextSecPosition = getPositionForSection(+nextSection);
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = mLinTitleLayout.getHeight();
                                int bottom = childView.getBottom();
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mLinTitleLayout
                                        .getLayoutParams();
                                if (bottom < titleHeight) {
                                    float pushedDistance = bottom - titleHeight;
                                    params.topMargin = (int) pushedDistance;
                                    mLinTitleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        mLinTitleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                    }
                    mLastFirstVisibleItem = firstVisibleItem;
                    mLinTitleLayout.setVisibility(mType == TYPE_PATIENT_HUI ? View.VISIBLE : View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqData();
    }

    private void reqData() {
        ReqManager.getInstance().reqCustomList(reqPatientListCallback, Utils.getUserToken(PatientLibraryActivity.this), mType == TYPE_PATIENT_HUI ? 0 : 1);
    }

    /**
     * 订单列表以及搜索回调
     */
    Callback<RespPatientList> reqPatientListCallback = new Callback<RespPatientList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespPatientList response) {
            closeProgressDialog();
            if (response != null && response.getResult() != null && response.getResult().getList() != null) {
                mCustomerList = response.getResult().getList();
                mNormalCustomerList = new ArrayList<EntityPatientInfo>();
                mFamilyCustomerList = new ArrayList<EntityPatientInfo>();
                //数据返回后，根据是否是新数据，将列表分为两个
                for (int i = 0; i < mCustomerList.size(); i++) {
                    if (mCustomerList.get(i).getIsNew() == 1) {
                        mFamilyCustomerList.add(mCustomerList.get(i));
                    } else {
                        mNormalCustomerList.add(mCustomerList.get(i));
                    }
                }
                mFamilyCustomerList = filledData(mFamilyCustomerList);
                for (int i = 0; i < mFamilyCustomerList.size(); i++) {
                    mFamilyCustomerList.get(i).setSortLetters("#");
                }
                mCustomerList = filledData(mNormalCustomerList);
                mCustomerList.addAll(0, mFamilyCustomerList);

                mEmptyView.setVisibility(mCustomerList == null || mCustomerList.size() == 0 ? View.VISIBLE : View.GONE);
                mCustomerListAdapter = new LvPatientListAdapter(PatientLibraryActivity.this, R.layout.item_lv_patient_library_list, mCustomerList);
                addFootView();
                mFootView.setText(mCustomerList.size() + "位患者");
                mLvCustomer.setAdapter(mCustomerListAdapter);
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };


    private void addFootView() {
        //以免重复添加footView
        if (mLvCustomer.getFooterViewsCount() > 0) return;

        mFootView = new TextView(PatientLibraryActivity.this);
        if (mCustomerList != null) {
            mFootView.setText(mCustomerList.size() + "位患者");
        }
        int padding = DisplayUtil.dip2px(PatientLibraryActivity.this, (float) 15);
        mFootView.setPadding(0, padding, 0, padding);
        mFootView.setGravity(Gravity.CENTER_HORIZONTAL);
        mFootView.setTextSize(17);
        mFootView.setTextColor(getResources().getColor(R.color.gray_1));
        mLvCustomer.addFooterView(mFootView);
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<EntityPatientInfo> filledData(List<EntityPatientInfo> date) {
        if (date != null && date.size() > 0) {
            List<EntityPatientInfo> mSortList = new ArrayList<EntityPatientInfo>();
            for (int i = 0; i < date.size(); i++) {
                EntityPatientInfo sortModel = date.get(i);
                // 汉字转换成拼音
                String pinyin = mCharacterParser.getSelling(date.get(i).getName());
                if (TextUtils.isEmpty(pinyin)) {
                    break;
                }
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (TextUtils.isEmpty(sortString)) {
                    break;
                }
                sortModel.setSortLetters(sortString);
                mSortList.add(sortModel);
            }
            //A-Z排序
            Collections.sort(mSortList, new PinyinComparator());

            return mSortList;
        }
        return new ArrayList<EntityPatientInfo>();
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if (mCustomerList != null && mCustomerList.get(position) != null && mCustomerList.get(position).getSortLetters() != null) {
            return mCustomerList.get(position).getSortLetters().toUpperCase().charAt(0);
        } else {
            return -1;
        }
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        if (mCustomerList != null) {
            for (int i = 0; i < mCustomerList.size(); i++) {
                if (mCustomerList.get(i).getSortLetters() != null) {
                    String sortStr = mCustomerList.get(i).getSortLetters();
                    char firstChar = sortStr.toUpperCase().charAt(0);
                    if (firstChar == section) {
                        return i;
                    }
                } else {
                    return -1;
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

    @Override
    public void onLeftBtnClick() {
        super.onLeftBtnClick();
        finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
        }
    }

    @Override
    public void onTabClick(int position) {
        if (position == 0) {
            mType = TYPE_PATIENT_HUI;
        } else {
            mType = TYPE_PATIENT_SUI;
        }
        reqData();
    }
}
