package com.dearzs.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 通用的单元格布局控件<br/>
 * 布局左边文字，右边是文字，下面有一条线。
 */
public class CustomCellView extends LinearLayout {
    /**
     * 默认资源ID
     **/
    private final int DEFAULT_RES_ID = -1;
    private final int DEFAULT_DIS = 12;

    private ViewGroup mLayoutCell;
    private ViewGroup mLayoutContent;
    private TextView mTvTitle;
    private ImageView mIvIcon;
    private TextView mTvDesc;
    private ImageView mIvDesc;
    private TextView mTvBottonLine;
    private TextView mTvTopLine;

    /**
     * 上下文对象
     */
    private Context mContext;

    public CustomCellView(Context context) {
        super(context);
        mContext = context;
    }

    public CustomCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    /**
     * 初始化控件
     */
    private void initView(AttributeSet attrs) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mLayoutCell = (ViewGroup) mInflater.inflate(R.layout.custom_cell_layout, this);

        mLayoutContent = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_layout_content);
        mTvTitle = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_tv_title);
        mTvDesc = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_tv_title_desc);
        mIvDesc = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_iv_title_desc);
        mTvBottonLine = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_tv_bottom_line);
        mTvTopLine = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_tv_top_line);
        mIvIcon = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_iv_icon);

        String title = "";
        String desc = "";
        boolean showArror = true;
        boolean showLine = true;
        boolean showTopLine = true;
        int paddingLeft = DEFAULT_DIS;
        int marginRight = DEFAULT_DIS;
        int arrorResId = DEFAULT_RES_ID;
        int titleColorResId = DEFAULT_RES_ID;
        int descColorResId = DEFAULT_RES_ID;
        int iconResId = DEFAULT_RES_ID;
        boolean showIcon = true;
        try {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomCellViewStyle, 0, 0);
            title = a.getString(R.styleable.CustomCellViewStyle_cTitle);
            desc = a.getString(R.styleable.CustomCellViewStyle_cDesc);
            showArror = a.getBoolean(R.styleable.CustomCellViewStyle_cShowArror, true);
            showLine = a.getBoolean(R.styleable.CustomCellViewStyle_cShowLine, true);
            showTopLine = a.getBoolean(R.styleable.CustomCellViewStyle_cShowTopLine, true);
            paddingLeft = a.getInteger(R.styleable.CustomCellViewStyle_cPaddingLeft, DEFAULT_DIS);
            marginRight = a.getInteger(R.styleable.CustomCellViewStyle_cMarginRight, DEFAULT_DIS);
            arrorResId = a.getResourceId(R.styleable.CustomCellViewStyle_cArrorResId, DEFAULT_RES_ID);
            titleColorResId = a.getResourceId(R.styleable.CustomCellViewStyle_cTitleTextColor, DEFAULT_RES_ID);
            descColorResId = a.getResourceId(R.styleable.CustomCellViewStyle_cDescTextColor, DEFAULT_RES_ID);
            iconResId = a.getResourceId(R.styleable.CustomCellViewStyle_cIcon, DEFAULT_RES_ID);
            showIcon = a.getBoolean(R.styleable.CustomCellViewStyle_cShowIcon, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setTitle(title);
            setDesc(desc);
            setRightArrorVisiable(showArror);
            setArrorResId(arrorResId);
            setBottomLineVisiable(showLine);
            setTopLineVisiable(showTopLine);
            setPaddingMarginSize(paddingLeft, marginRight);
            setTitleColor(titleColorResId);
            setDescColor(descColorResId);
            setIconImage(iconResId);
            setIconVisiable(showIcon);
        }
    }

    public void setPaddingMarginSize(int paddingLeft, int marginRight) {
        paddingLeft = DisplayUtil.dip2px(mContext, paddingLeft);
        marginRight = DisplayUtil.dip2px(mContext, marginRight);

        if (paddingLeft < 0 || marginRight < 0) {
            return;
        }

        try {
            //mLayoutCell设置paddingLeft
            mLayoutContent.setPadding(paddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            //mLayoutContent设置marginRight
            MarginLayoutParams lp = (MarginLayoutParams) mLayoutContent.getLayoutParams();
            lp.setMargins(lp.leftMargin, lp.topMargin, marginRight, lp.bottomMargin);
            mLayoutContent.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置左侧icon是否现在
     */
    public void setIconVisiable(boolean show) {
        mIvIcon.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置左侧IconResouce
     */
    public void setIconImage(int resId) {
        if (resId != DEFAULT_RES_ID && resId > 0) {
            try {
                mIvIcon.setImageResource(resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setTitleColor(int resId) {
        if (resId != DEFAULT_RES_ID && resId > 0) {
            try {
                mTvTitle.setTextColor(mContext.getResources().getColor(resId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDescMaxLength(int ems) {
        mTvDesc.setEms(ems);
    }

    public void setDescColor(int resId) {
        if (resId != DEFAULT_RES_ID && resId > 0) {
            try {
                mTvDesc.setTextColor(mContext.getResources().getColor(resId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置左边提示信息
     *
     * @param title
     * @author 张海龙
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 设置右边提示信息
     *
     * @param desc
     * @author 张海龙
     */
    public void setDesc(String desc) {
        mTvDesc.setText(desc);
    }

    /**
     * 设置右边提示信息
     *
     * @param desc
     * @author 张海龙
     */
    public void setDesc(String desc, int maxLen) {
        if (desc != null) {
            if (desc.length() > maxLen && maxLen > 0) {
                desc = desc.substring(0, maxLen - 1) + "...";
            }
        }
        mTvDesc.setText(desc);
    }

    /**
     * 获取右边提示信息
     *
     * @return
     */
    public String getDesc() {
        return mTvDesc.getText().toString();
    }

    /**
     * 设置右边提示信息
     *
     * @param htmlDesc
     * @author 张海龙
     */
    public void setHtmlDesc(String htmlDesc) {
        mTvDesc.setText(Html.fromHtml(htmlDesc));
    }

    /**
     * 设置整个布局点击事件
     *
     * @param listener
     */
    public void setCellClickListener(OnClickListener listener) {
        if (listener != null) {
            setClickable(true);
            mLayoutCell.setOnClickListener(listener);
        }
    }

    public void setClickable(boolean clickable) {
//        setRightArrorVisiable(clickable);
        super.setClickable(clickable);
    }

    /**
     * 设置右边箭头显示状态
     */
    public void setRightArrorVisiable(boolean show) {
        mIvDesc.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右边箭头资源Id
     *
     * @param resId
     */
    public void setArrorResId(int resId) {
        if (resId == DEFAULT_RES_ID || resId < 0) {
            resId = R.mipmap.ic_arrow_right;
        }
        mIvDesc.setImageResource(resId);
    }

    /**
     * 设置下划线显示状态
     */
    public void setBottomLineVisiable(boolean show) {
        mTvBottonLine.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置下划线显示状态
     */
    public void setTopLineVisiable(boolean show) {
        mTvTopLine.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
