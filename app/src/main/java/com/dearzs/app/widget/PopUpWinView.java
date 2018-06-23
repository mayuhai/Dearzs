package com.dearzs.app.widget;

import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 自定义PopUpWin
 */
public class PopUpWinView extends PopupWindow {

    public PopUpWinView(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    /**
     * 显示弹框
     */
    public void showPopWinFromBottom(View view) {
        if (view != null) {
            this.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 显示到给的控件的下面
     *
     * @param view
     */
    public void showAtDropDown(View view) {
        if (this != null) {
            this.showAsDropDown(view);
        }
    }

    /**
     * 隐藏弹框
     */
    public void hidePopWin() {
        if (isShowing()) {
            this.dismiss();
        }
    }

    /**
     * 弹框是否可见
     *
     * @return
     */
    public boolean isVisiable() {
        return this.isShowing();
    }

}
