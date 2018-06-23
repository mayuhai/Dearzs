package com.dearzs.app.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.dearzs.app.R;

/**
 * 自定义的输入框栏
 * 支持添加左侧icon
 * 支持设置输入类型为明文or密文
 * 支持当内容不为空时，右侧含有删除按钮
 */
public class MInputTextView extends EditText implements View.OnFocusChangeListener, TextWatcher {
    /** 删除按钮的引用 */
    private Drawable mClearDrawable;
    /** 可见或不可见的引用*/
    private Drawable mEyeClose, mEyeOpen;
    private boolean hasFoucs;

    public MInputTextView(Context context) {
        this(context, null);
        init();
    }

    public MInputTextView(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public MInputTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);

        // 设置背景透明
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,2是获得右边的图片  顺序是左上右下（0,1,2,3,）
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.mipmap.search_close);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getMinimumWidth(), mClearDrawable.getMinimumHeight());

        if (mEyeOpen == null) {
            mEyeOpen = getResources().getDrawable(R.mipmap.eye_open);
        }
        mEyeOpen.setBounds(0, 0, mEyeOpen.getMinimumWidth(), mEyeOpen.getMinimumHeight());
        if (mEyeClose == null) {
            mEyeClose = getResources().getDrawable(R.mipmap.eye_close);
        }
        mEyeClose.setBounds(0, 0, mEyeClose.getMinimumWidth(), mEyeClose.getMinimumHeight());

        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(500);
        return translateAnimation;
    }

    /**
     * 设置输入的显示类型
     *
     * @param isCiphertext
     */
    public void setInputCiphertext(boolean isCiphertext) {
        setTransformationMethod(isCiphertext ? PasswordTransformationMethod.getInstance() :
                HideReturnsTransformationMethod.getInstance());
    }

    public boolean isInputCiphertext() {
        return getTransformationMethod() instanceof PasswordTransformationMethod;
    }
}