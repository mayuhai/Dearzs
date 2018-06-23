package com.dearzs.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.util.Utils;

/**
 * Created by luyanlong on 2015/10/12.
 * 公共的TabView
 * 目前支持两种模式：1，背景不变，可改变底部标签2，改变背景
 * <p/>
 * 注：目前还不支持圆角背景，待完善
 */
public class MyTabBar extends HorizontalScrollView {

    public static final int MODE_LINE = 0;  //底部标志的Tab
    public static final int MODE_NONE = 1;  //可变换背景的Tab
    private String mTabViewTag = "TAB_TAg";
    private Context mContext;

    private int mMode = MODE_NONE;
    private String[] mTitles;
    private DisplayMetrics mMetrics;
    private int mLineWidth;
    private boolean mIsScrollable = false;
    private boolean mIsFirst = true;
    private boolean mIsHaveDriver = true;
    private String mTagValue;
    private int mItemNormarBgColor = R.color.white;                     //没有选择情况下的背景颜色
    private int mItemSelectedBgColor = R.color.green;       //选中情况下的背景颜色
    private int mNormarTextColor = R.color.gray_bg;                 //没有选择情况下的字体颜色
    private int mSelectedTextColor = R.color.green;
//    private int mItemSelectedBgResource = R.drawable.bottom_line_bg;
//    private int mItemNormalBgResource = R.drawable.bottom_line_bg;


    private View mDivider;
    private LinearLayout mLayout;
    private OnTabClickListener mTabClickListener;

    public MyTabBar(Context context) {
        super(context);
        init(context);
    }

    public MyTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mMetrics = getResources().getDisplayMetrics();
        mLayout = new LinearLayout(context);
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setIsScrollable(true);
        setHorizontalScrollBarEnabled(false);
    }

    private void initTab() {
        switch (mMode) {
            case MODE_LINE:
                addLineTab();
                break;
            case MODE_NONE:
                addNoneTab();
                break;
            default:
                break;
        }
    }

    private void addLineTab() {
        setBackgroundColor(getResources().getColor(mItemNormarBgColor));
        mLayout.removeAllViews();
        removeAllViews();
        for (int i = 0; i < mTitles.length; i++) {
            final View tabView = LayoutInflater.from(mContext).inflate(R.layout.mytabbar_view, null);
            final TextView titleTxt = (TextView) tabView.findViewById(R.id.txt_title);
            final ImageView bottomSign = (ImageView) tabView.findViewById(R.id.img_tab);
            titleTxt.setText(mTitles[i]);
            titleTxt.setTag(i);
            tabView.setTag("TAB_TAg");
            bottomSign.getLayoutParams().width = mLineWidth / 4 * 3;
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(mTabViewTag, titleTxt.getText().toString());
                    scrollToCenter(tabView);
                    final int position = Utils.indexOfArr(mTitles, titleTxt.getText().toString());
                    setSelected(tabView);
                    mTabClickListener.onTabClick(position);
                }
            });
            mLayout.addView(tabView, new LayoutParams(mLineWidth, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//            addDivider(mLayout);
        }
//        if(mIsHaveDriver){
//            mLayout.removeViewAt(2 * mTitles.length - 1);
//        }
        addView(mLayout);
        hideBottomLine();
        resetTitleColor();
    }

    private void addNoneTab() {
        setBackgroundColor(getResources().getColor(mItemNormarBgColor));
//        setBackgroundResource(R.drawable.bottom_line_bg);
//        setBackgroundResource(mItemNormarBgColor);
        mLayout.removeAllViews();
        removeAllViews();
        for (int i = 0; i < mTitles.length; i++) {
            final View tabView = LayoutInflater.from(mContext).inflate(R.layout.mytabbar_view, null);
            final TextView titleTxt = (TextView) tabView.findViewById(R.id.txt_title);
            final ImageView bottomSign = (ImageView) tabView.findViewById(R.id.img_tab);
            titleTxt.setText(mTitles[i]);
            titleTxt.setTag(i);
            tabView.setTag("TAB_TAg");
            bottomSign.getLayoutParams().width = mLineWidth / 2;
            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = Utils.indexOfArr(mTitles, titleTxt.getText().toString());
                    setSelected(tabView);
                    mTabClickListener.onTabClick(position);
                }
            });
            mLayout.addView(tabView, new LayoutParams(mLineWidth, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//            addDivider(mLayout);
        }
//        if(mIsHaveDriver){
//            mLayout.removeViewAt(2 * mTitles.length - 1);
//        }
        addView(mLayout);
        resetBgColor();
        hideBottomLine();
        resetTitleColor();
    }

    public void showBottomLine(View v) {
        v.findViewById(R.id.img_tab).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.txt_title)).setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue((v.findViewById(R.id.txt_title)).getTag().toString());
    }

    public void showBottomLine(int position) {
        TextView titleTxt = (TextView) mLayout.getChildAt(position).findViewById(R.id.txt_title);
        ImageView imageview = (ImageView) mLayout.getChildAt(position).findViewById(R.id.img_tab);
        imageview.setVisibility(View.VISIBLE);
        titleTxt.setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue(titleTxt.getTag().toString());
    }

    public void setItemTitle(int position, String title) {
        ((TextView) mLayout.getChildAt(position).findViewById(R.id.txt_title)).setText(title);
    }

    public void showBgColor(View v) {
        TextView titleTxt = (TextView) v.findViewById(R.id.txt_title);
        v.setBackgroundColor(getResources().getColor(mItemSelectedBgColor));
//        v.setBackgroundResource(R.drawable.bottom_line_bg);
        titleTxt.setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue(titleTxt.getTag().toString());
    }

    public void showBgColor(int position) {
        TextView titleTxt = (TextView) mLayout.findViewById(R.id.txt_title);
        mLayout.getChildAt(position).setBackgroundColor(getResources().getColor(mItemSelectedBgColor));
//        v.setBackgroundResource(R.drawable.bottom_line_bg);
        titleTxt.setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue(titleTxt.getTag().toString());
    }

    public void showBgResource(int position) {
        TextView titleTxt = (TextView) mLayout.findViewById(R.id.txt_title);
        mLayout.getChildAt(position).setBackgroundColor(getResources().getColor(mItemSelectedBgColor));
//        v.setBackgroundResource(R.drawable.bottom_line_bg);
        titleTxt.setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue(titleTxt.getTag().toString());
    }

    public void showBgResource(View v) {
        TextView titleTxt = ((TextView) v.findViewById(R.id.txt_title));
        v.setBackgroundColor(getResources().getColor(mItemSelectedBgColor));
//        v.setBackgroundResource(R.drawable.bottom_line_bg);
        titleTxt.setTextColor(mContext.getResources().getColor(mSelectedTextColor));
        setTagValue(titleTxt.getTag().toString());
    }

    public void hideBottomLine() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            if (((mLayout.getChildAt(i)).getTag()) != null) {
                String tag = ((mLayout.getChildAt(i)).getTag()).toString();
                ImageView imageview = (ImageView) (mLayout.getChildAt(i)).findViewById(R.id.img_tab);
                if (mIsFirst && i == 0 && mMode == MODE_LINE) {
                    if (tag.equals(mTabViewTag)) {
                        imageview.setVisibility(View.VISIBLE);
                        mTabClickListener.onTabClick(0);
                    }
                } else {
                    if (tag.equals(mTabViewTag)) {
                        imageview.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    public void resetBgColor() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            if ((mLayout.getChildAt(i)).getTag() != null) {
                String tag = (mLayout.getChildAt(i)).getTag().toString();
                View view = mLayout.getChildAt(i);
                if (mIsFirst && i == 0 && mMode == MODE_NONE) {
                    if (tag.equals(mTabViewTag)) {
                        view.setBackgroundColor(getResources().getColor(mItemSelectedBgColor));
                        mTabClickListener.onTabClick(0);
                    }
                } else {
                    if (tag.equals(mTabViewTag)) {
                        view.setBackgroundColor(getResources().getColor(mItemNormarBgColor));
                    }
                }
            }
        }
    }

    public void resetTitleColor() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            String tag = ((mLayout.getChildAt(i)).getTag()).toString();
            TextView textview = (TextView) mLayout.getChildAt(i).findViewById(R.id.txt_title);
            if (tag != null) {
                if (mIsFirst && i == 0) {
                    if (tag.equals(mTabViewTag)) {
                        textview.setTextColor(getResources().getColor(mSelectedTextColor));
                    }
                } else {
                    if (tag.equals(mTabViewTag)) {
                        textview.setTextColor(getResources().getColor(mNormarTextColor));
                    }
                }
            }
        }
    }

    private void addDivider(View v) {
        if (mIsHaveDriver) {
            mDivider = LayoutInflater.from(mContext).inflate(R.layout.vertical_line, null);
            ((LinearLayout) v).addView(mDivider, new LayoutParams(1,
                    LayoutParams.MATCH_PARENT,
                    Gravity.CENTER));
        }
    }

    private void scrollToCenter(View v) {
        if (mIsScrollable) {
            if (v.getLeft() >= (mLineWidth / 2 + mLineWidth)) {
                smoothScrollTo(v.getLeft() - mLineWidth / 2 - mLineWidth, 0);
            } else {
                smoothScrollTo(-v.getLeft(), 0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsScrollable) {
            return super.onTouchEvent(ev);
        }
        return mIsScrollable;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public String[] getTitles() {
        return mTitles;
    }

    public void setTitles(String[] titles) {
        if (titles == null || titles.length == 0) {
            return;
        }
        mTitles = titles;
        mLineWidth = mMetrics.widthPixels / (mTitles.length < 4 ? mTitles.length : 4);
        initTab();
    }

    public void refreshTitles(String[] titles) {
        if (titles == null || titles.length == 0) {
            return;
        }
        mTitles = titles;
        for (int i = 0; i < titles.length; i++) {
            ((TextView) mLayout.getChildAt(i).findViewById(R.id.txt_title)).setText(titles[i]);
        }
    }

    public void setTabClickListener(OnTabClickListener mTabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    public void setIsScrollable(boolean isScrollable) {
        this.mIsScrollable = isScrollable;
    }

    public OnTabClickListener getTabClickListener() {
        return mTabClickListener;
    }

    public void setFirst(boolean isFirst) {
        this.mIsFirst = isFirst;
    }

    public boolean getFirst() {
        return mIsFirst;
    }

    public void setIsHaveDriver(boolean isHaveDriver) {
        this.mIsHaveDriver = isHaveDriver;
    }

    public void setTagValue(String mTagValue) {
        this.mTagValue = mTagValue;
    }

    public void setNormalBgColor(int bgColor) {
        this.mItemNormarBgColor = bgColor;
    }

    public void setSelectedBgColor(int bgColor) {
        this.mItemSelectedBgColor = bgColor;
    }

    public void setNormalTextColor(int textColor) {
        this.mNormarTextColor = textColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.mSelectedTextColor = textColor;
    }

    /**
     * 设置红点提醒个数
     *
     * @param position
     * @param count
     * @param showSmallTip 是否显示没有数字的红点
     */
    public void setTitleTipsCount(int position, int count, boolean showSmallTip) {
        TextView mTextTip = (TextView) (mLayout.getChildAt(position).findViewById(R.id.txt_title_tips));
        mTextTip.setVisibility(View.VISIBLE);
        mTextTip.setLayoutParams(new LayoutParams(-2, -2, Gravity.TOP | Gravity.RIGHT));
        //红点变小点
        if (showSmallTip) {
            mTextTip.setLayoutParams(new LayoutParams(20, 20, Gravity.TOP | Gravity.RIGHT));
        } else if (count <= 0) {
            // 如果 count <= 0 不显示
            mTextTip.setVisibility(View.INVISIBLE);
        } else {
            // 如果 count > 0 不做处理
            mTextTip.setText(count + "");
        }
    }

    /**
     * 设置红点提醒个数
     *
     * @param position
     * @param count
     */
    public void setTitleTipsCount(int position, int count) {
        setTitleTipsCount(position, count, false);
    }

    public void setSelected(View view) {
        setFirst(false);
        resetTitleColor();
        if (mMode == MODE_NONE) {
            resetBgColor();
            showBgColor(view);
        } else {
            hideBottomLine();
            showBottomLine(view);
        }
    }

    public void setSelected(int position) {
        setFirst(false);
        resetTitleColor();
        if (mMode == MODE_NONE) {
            resetBgColor();
            showBgColor(position);
        } else {
            hideBottomLine();
            showBottomLine(position);
        }
    }

    public interface OnTabClickListener {
        void onTabClick(int position);
    }
}
