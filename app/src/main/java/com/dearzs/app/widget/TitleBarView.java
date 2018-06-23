package com.dearzs.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 自定义的标题栏视图
 */
public class TitleBarView extends LinearLayout implements View.OnClickListener {

    /**
     * 左侧返回按钮
     **/
    public static final int TITLE_TYPE_LEFT_IV_BACK = 1012;
    /**
     * 左侧更多按钮
     **/
    public static final int TITLE_TYPE_LEFT_IV_MORE = 1013;
    /**
     * 左侧足迹按钮
     **/
    public static final int TITLE_TYPE_LEFT_IV_FOOT_PRINT = 1014;
    /**
     * 左侧搜索框
     **/
    public static final int TITLE_TYPE_LEFT_ET_SEARCH = 1015;
    /**
     * 右侧文字
     **/
    public static final int TITLE_TYPE_RIGHT_TXT = 1021;
    /**
     * 右侧消息按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_MESSAGE = 1022;
    /**
     * 右侧按钮(开始会诊/结束会诊)
     **/
    public static final int TITLE_TYPE_RIGHT_IV_BUTTON = 1023;
    /**
     * 右侧单个按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_SINGLE_BUTTON = 1024;
    /**
     * 右侧双按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_TWO_BUTTON = 1028;
    /**
     * 右侧收藏按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_COLLECTION = 1034;
    /**
     * 右侧搜索按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_SEARCH = 1025;
    /**
     * 右侧添加按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_ADD = 1026;
    /**
     * 右侧设置和消息按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_SETTING_AND_MESSAGE = 1027;

    /**
     * 右侧历史更新和编辑按钮
     **/
    public static final int TITLE_TYPE_RIGHT_IV_EDIT_AND_HISTORY = 1029;
    /**
     * 中间文字
     **/
    public static final int TITLE_TYPE_CENTER_TXT = 1031;
    /**
     * 中间文字带有描述信息
     **/
    public static final int TITLE_TYPE_CENTER_TXT_DESC = 1032;
    /**
     * 中间文字带有图片
     **/
    public static final int TITLE_TYPE_CENTER_TXT_IV = 1033;
    /**
     * 透明
     **/
    public static final int TITLE_TYPE_NULL = 1099;

    /**
     * title布局
     **/
    private View layout_title;

    /**
     * title左侧按钮
     **/
    private ImageView iv_left;

    /**
     * title左侧搜索框
     **/
    private EditText et_left;

    /**
     * title右侧文字
     **/
    private TextView tv_right;
    /**
     * title右侧按钮
     **/
    private ImageView iv_right;
    /**
     * title右侧按钮2
     **/
    private ImageView iv_right2;

    /**
     * title中间布局
     **/
    private View layout_center;
    /**
     * title中间的文字
     **/
    private TextView tv_center;

    /**
     * 上下文对象
     */
    private Context mContext;
    private OnTitleBarClickListener listener;

    public TitleBarView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        layout_title = mInflater.inflate(R.layout.title_layout, this);

        iv_left = GetViewUtil.getView(layout_title, R.id.base_iv_left);
        et_left = GetViewUtil.getView(layout_title, R.id.base_et_left);
        iv_right = GetViewUtil.getView(layout_title, R.id.base_iv_right);
        iv_right2 = GetViewUtil.getView(layout_title, R.id.base_iv_right_2);
        tv_right = GetViewUtil.getView(layout_title, R.id.base_tv_right);

        iv_left.setOnClickListener(this);

        layout_center = GetViewUtil.getView(layout_title, R.id.base_layout_center);
        tv_center = GetViewUtil.getView(layout_title, R.id.base_tv_center);
        tv_center.setOnClickListener(this);

        iv_right.setOnClickListener(this);
        iv_right2.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    /**
     * 添加标题左侧按钮
     * @param type 按钮样式
     */
    public void addLeftBtn(int type, String msg) {
        showTitle();
        iv_left.setVisibility(View.VISIBLE);
        switch (type) {
            case TITLE_TYPE_LEFT_IV_BACK:
                iv_left.setImageResource(R.mipmap.ic_title_back);
                break;
            case TITLE_TYPE_LEFT_IV_MORE:
                iv_left.setImageResource(R.mipmap.ic_title_back);
                break;
            case TITLE_TYPE_LEFT_IV_FOOT_PRINT:
                iv_left.setImageResource(R.mipmap.ic_title_back);
                break;
            case TITLE_TYPE_LEFT_ET_SEARCH:
                iv_left.setVisibility(View.GONE);
                et_left.setVisibility(View.VISIBLE);
                break;
            case TITLE_TYPE_NULL:
            default:
                iv_left.setVisibility(View.GONE);
                et_left.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 添加标题右侧按钮
     * @param type 按钮样式
     */
    public void addRightBtn(int type, String msg) {
        showTitle();
        switch (type) {
            case TITLE_TYPE_RIGHT_TXT:
                tv_right.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.GONE);
                tv_right.setText(Html.fromHtml(msg));
                break;
            case TITLE_TYPE_RIGHT_IV_MESSAGE:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right2.setVisibility(View.GONE);
                iv_right.setImageResource(R.mipmap.ic_title_message);
                break;
            case TITLE_TYPE_RIGHT_IV_BUTTON:
                tv_right.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.GONE);
                tv_right.setBackgroundResource(R.mipmap.consultation_room_user_job_bg_green);
                tv_right.setText(Html.fromHtml(msg));
                tv_right.setTextColor(Color.WHITE);
                break;
            case TITLE_TYPE_RIGHT_IV_COLLECTION:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right.setImageResource(R.mipmap.ic_collection_normal);
                break;
            case TITLE_TYPE_RIGHT_IV_SINGLE_BUTTON:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right2.setVisibility(View.GONE);
//                iv_right.setImageResource(R.mipmap.ic_title_back);
                break;
            case TITLE_TYPE_RIGHT_IV_SEARCH:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right2.setVisibility(View.GONE);
                iv_right.setImageResource(R.mipmap.ic_title_search);
                break;
            case TITLE_TYPE_RIGHT_IV_ADD:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right2.setVisibility(View.GONE);
                iv_right.setImageResource(R.mipmap.ic_title_add);
                break;
            case TITLE_TYPE_RIGHT_IV_TWO_BUTTON:
                tv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.VISIBLE);
//                iv_right.setImageResource(R.mipmap.ic_collection_normal);
//                iv_right2.setImageResource(R.mipmap.ic_title_share);
                break;
            case TITLE_TYPE_RIGHT_IV_EDIT_AND_HISTORY:
                tv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right.setImageResource(R.mipmap.ic_title_edit);
                iv_right2.setImageResource(R.mipmap.ic_title_history);
                break;
            case TITLE_TYPE_RIGHT_IV_SETTING_AND_MESSAGE:
                tv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.VISIBLE);
                iv_right.setVisibility(View.VISIBLE);
                iv_right.setImageResource(R.mipmap.ic_title_setting);
                iv_right2.setImageResource(R.mipmap.ic_title_message);
                break;
            case TITLE_TYPE_NULL:
            default:
                tv_right.setVisibility(View.GONE);
                iv_right.setVisibility(View.GONE);
                iv_right2.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 添加中间标题
     *
     * @param type 标题样式
     * @param msg  标题文字内容
     */
    public void addCenter(int type, String msg) {
        showTitle();
        switch (type) {
            case TITLE_TYPE_CENTER_TXT:
                tv_center.setVisibility(View.VISIBLE);
                tv_center.setText(msg);
                break;
            case TITLE_TYPE_CENTER_TXT_DESC:
                tv_center.setVisibility(View.GONE);
                tv_center.setText(msg);
                break;
            case TITLE_TYPE_CENTER_TXT_IV:
                tv_center.setVisibility(View.GONE);
                tv_center.setText(msg);
                break;
            case TITLE_TYPE_NULL:
                layout_center.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void setRightIvResource(int resource){
        iv_right.setImageResource(resource);
    }

    public void setRightIv2Resource(int resource){
        iv_right2.setImageResource(resource);
    }

    /**
     * 去除右侧按钮
     *
     */
    public void removeRightBtn() {
        iv_right.setVisibility(View.INVISIBLE);
        iv_right2.setVisibility(View.INVISIBLE);
        tv_right.setVisibility(View.INVISIBLE);
    }

    public void setTitleRightTxtBackground(int backgroundResource){
        tv_right.setBackgroundResource(backgroundResource);
    }

    public void setTitleRightTxt(String rightTxt){
        tv_right.setText(rightTxt);
    }

    /**
     * 去除左侧按钮
     */
    public void removeLeftBtn() {
        iv_left.setVisibility(View.GONE);
        et_left.setVisibility(View.GONE);
    }

    /**
     * 显示标题
     */
    private void showTitle() {
        layout_title.setVisibility(View.VISIBLE);
    }

    /**
     * titleBar按钮监听
     */
    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.base_iv_left:
            case R.id.base_et_left:
                listener.onLeftBtnClick();
                break;
            case R.id.base_iv_right:
            case R.id.base_tv_right:
                listener.onRightBtnClick();
                break;
            case R.id.base_iv_right_2:
                listener.onRightBtn2Click();
                break;
            case R.id.base_tv_center:
                listener.onCenterBtnClick();
                break;
            default:
                break;
        }
    }

    public EditText getEtSearch() {
        return et_left;
    }

    public interface OnTitleBarClickListener {
        public void onLeftBtnClick();

        public void onRightBtnClick();

        public void onCenterBtnClick();

        public void onRightBtn2Click();

        public void onLeftBtnClick(View view);
    }

    public void setOnTitleBtnClickListener(OnTitleBarClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置中间文字右侧图片
     *
     * @param resId
     */
    public void setCenTxtRightImg(int resId) {
        if (resId > 0) {
            try {
                Drawable rightDrawable = mContext.getResources().getDrawable(resId);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                tv_center.setCompoundDrawables(null, null, rightDrawable, null);
            } catch (Exception e) {
            }
        }
    }
}
