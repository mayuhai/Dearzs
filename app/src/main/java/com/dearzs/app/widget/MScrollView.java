package com.dearzs.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Lyl on 2016/6/7.
 * 可监听滑动位置的ScrollView
 */
public class MScrollView extends ScrollView {

    private ScrollListener scrollListener = null;

    public MScrollView(Context context) {
        super(context);
    }

    public MScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollListener(ScrollListener scrollViewListener) {
        this.scrollListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollListener != null) {
            scrollListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollListener {
        void onScrollChanged(MScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}