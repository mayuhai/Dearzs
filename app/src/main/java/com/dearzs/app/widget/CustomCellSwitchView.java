package com.dearzs.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 通用的单元格布局控件(带有复选按钮)<br/>
 * 布局左边文字，右边是文字，下面有一条线。
 */
public class CustomCellSwitchView extends LinearLayout {
    /**
     * 默认资源ID
     **/
    private final int DEFAULT_RES_ID = -1;

    private View mLayoutCell;
    private TextView mTvTitle;
    private TextView mTvDesc;
    private CheckBox mCbxState;
    private TextView mTvBottomLine;
    private TextView mTvTopLine;
    private ImageView mIvIcon;

    /**
     * 上下文对象
     */
    private Context mContext;

    private OnSwitchClickListener switchListener;

    public CustomCellSwitchView(Context context) {
        super(context);
        mContext = context;
    }

    public CustomCellSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    /**
     * 初始化控件
     */
    private void initView(AttributeSet attrs) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mLayoutCell = mInflater.inflate(R.layout.custom_cell_radio_layout, this);

        mTvTitle = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_radio__tv_title);
        mTvDesc = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_radio_tv_title_desc);
        mCbxState = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_radio_rb);
        mTvBottomLine = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_radio_tv_bottom_line);
        mTvTopLine = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_radio_tv_top_line);
        mIvIcon = GetViewUtil.getView(mLayoutCell, R.id.custom_cell_iv_icon);

        String title = "";
        String desc = "";
        boolean checkState = false;
        boolean showLine = true;
        boolean showTopLine = true;
        boolean showIcon = true;
        int titleColor = DEFAULT_RES_ID;
        int descColor = DEFAULT_RES_ID;
        int rbResId = DEFAULT_RES_ID;
        int iconResId = DEFAULT_RES_ID;
        try {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomCellRadioViewStyle, 0, 0);
            title = a.getString(R.styleable.CustomCellRadioViewStyle_cRTitle);
            desc = a.getString(R.styleable.CustomCellRadioViewStyle_cRDesc);
            checkState = a.getBoolean(R.styleable.CustomCellRadioViewStyle_cRChecked, false);
            showLine = a.getBoolean(R.styleable.CustomCellRadioViewStyle_cRShowLine, true);
            showTopLine = a.getBoolean(R.styleable.CustomCellRadioViewStyle_cRShowTopLine, true);
            titleColor = a.getResourceId(R.styleable.CustomCellRadioViewStyle_cRTitleTextColor, DEFAULT_RES_ID);
            descColor = a.getResourceId(R.styleable.CustomCellRadioViewStyle_cRDescTextColor, DEFAULT_RES_ID);
            rbResId = a.getResourceId(R.styleable.CustomCellRadioViewStyle_cRbtResId, DEFAULT_RES_ID);
            iconResId = a.getResourceId(R.styleable.CustomCellRadioViewStyle_cRIcon, DEFAULT_RES_ID);
            showIcon = a.getBoolean(R.styleable.CustomCellRadioViewStyle_cRShowIcon, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setTitle(title);
            setDesc(desc);
            setBottomLineVisiable(showLine);
            setTopLineVisiable(showTopLine);
            setTitleTextColor(titleColor);
            setDescTextColor(descColor);
            setRadioBtnResId(rbResId);
            initListener();
            setRadiaButtonState(checkState);
            setIconImage(iconResId);
            setIconVisiable(showIcon);
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

    public void initListener() {
        mCbxState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchListener != null) {
                    switchListener.onSwitchClick(isChecked);
                }
            }
        });
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        mCbxState.setClickable(clickable);
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
     * 设置中间提示信息
     *
     * @param desc
     * @author 张海龙
     */
    public void setDesc(String desc) {
        mTvDesc.setText(desc);
    }

    /**
     * 设置整个布局点击事件
     *
     * @param listener
     */
    public void setCellClickListener(OnClickListener listener) {
        if (listener != null) {
            mLayoutCell.setOnClickListener(listener);
        }
    }

    public void removeCheckedChangeListener() {
        mCbxState.setOnCheckedChangeListener(null);
        mCbxState.setClickable(false);
    }

    /**
     * 设置单选按钮状态显示状态
     */
    public void setRadiaButtonState(boolean state) {
        mCbxState.setChecked(state);
    }

    /**
     * 获取单选按钮状态显示状态
     *
     * @return
     */
    public boolean getRadioBtnState() {
        return mCbxState.isChecked();
    }

    /**
     * 设置下划线显示状态
     */
    public void setBottomLineVisiable(boolean show) {
        mTvBottomLine.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取单选按钮是否可以点击
     *
     * @return
     */
    public boolean getCheckBoxClickable() {
        return mCbxState.isClickable();
    }


    /**
     * 设置单选按钮是否可点击
     *
     * @param clickable
     */
    public void setCheckBoxClickable(boolean clickable) {
        mCbxState.setClickable(clickable);
    }

    /**
     * 设置Title字体颜色
     *
     * @param colorResId
     */
    public void setTitleTextColor(int colorResId) {
        if (colorResId != DEFAULT_RES_ID && colorResId > 0) {
            try {
                mTvTitle.setTextColor(mContext.getResources().getColor(colorResId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置Desc字体颜色
     *
     * @param colorResId
     */
    public void setDescTextColor(int colorResId) {
        if (colorResId != DEFAULT_RES_ID && colorResId > 0) {
            try {
                mTvDesc.setTextColor(mContext.getResources().getColor(colorResId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置单选按钮
     *
     * @param resId
     */
    public void setRadioBtnResId(int resId) {
        if (resId != DEFAULT_RES_ID && resId > 0) {
            try {
                mCbxState.setButtonDrawable(mContext.getResources().getDrawable(resId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开关是否打开的监听
     */
    public interface OnSwitchClickListener {
        public void onSwitchClick(boolean on);
    }

    public void setOnSwitchListener(OnSwitchClickListener listener) {
        this.switchListener = listener;
    }


    /**
     * 设置下划线显示状态
     */
    public void setTopLineVisiable(boolean show) {
        mTvTopLine.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
