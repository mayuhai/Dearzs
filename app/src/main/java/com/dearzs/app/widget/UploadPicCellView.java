package com.dearzs.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.IPicDelListener;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 上传照片页面,,已选择的照片(此处不支持上传,只显示成功或失败状态)
 *
 * @author zhaoyb
 */
public class UploadPicCellView extends RelativeLayout implements View.OnClickListener {

    /**上下文对象*/
    private Context mContext;

    // 待显示的图片
    private ImageView mIvCar;
    private ViewGroup mLayout;
    // 上传进度提示语
    private TextView mTvErrorTip;
    // 单元格底部提示语
    private TextView mTvTip;
    // 图片删除按钮
    private ImageView mIvDel;

    /** 每一个view都对应一个实体类(可能来源于网络,也可能是本地图片)*/
    private EntityNetPic mEntityPicDetail;

    public UploadPicCellView(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public UploadPicCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    /**
     * 初始化控件
     */
    private void initView(AttributeSet attrs) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        mLayout = (ViewGroup) mInflater.inflate(R.layout.upload_pic_cell_layout, this);

        mTvErrorTip = GetViewUtil.getView(mLayout, R.id.upload_cell_tv_fail_tip);
        mTvErrorTip.setVisibility(View.GONE);

        mIvCar = GetViewUtil.getView(mLayout, R.id.upload_cell_iv_car);

        mIvDel = GetViewUtil.getView(mLayout, R.id.upload_cell_iv_del);
        mIvDel.setOnClickListener(this);

        mTvTip = GetViewUtil.getView(mLayout, R.id.upload_cell_tv_tip);
        if (attrs == null) return;
        String title = "";
        try {
            TypedArray a = mContext.obtainStyledAttributes(attrs,
                    R.styleable.UploadPicCellViewStyle, 0, 0);
            title = a.getString(R.styleable.UploadPicCellViewStyle_cUTip);
        } catch (Exception e) {
        } finally {
            mTvTip.setText(title);
        }
    }

    /**
     * 设置图片
     *
     * @param mEntityPicDetail
     */
    public void updateEntityPicDetail(EntityNetPic mEntityPicDetail, int itemWidth) {
        if (mEntityPicDetail == null) {
            return;
        }
        this.mEntityPicDetail = mEntityPicDetail;

        mIvCar.setLayoutParams(new LayoutParams(itemWidth, itemWidth * 3 / 4));

        // 显示上传失败的按钮
        mTvErrorTip.setVisibility(mEntityPicDetail.getUpload_state() == EntityNetPic.UPLOAD_PIC_FAILED ?
                View.VISIBLE : View.GONE);

        // 显示当前图片
        String picPath = mEntityPicDetail.getUrl();
        if (picPath.startsWith("http")) {
            ImageLoaderManager.getInstance().displayImage(picPath, mIvCar);
        } else {
            ImageLoaderManager.getInstance().displayImageNoCache("file://" + picPath, mIvCar, itemWidth, itemWidth * 3 / 4);
        }

        mIvCar.setVisibility(View.VISIBLE);
        mIvDel.setVisibility(View.VISIBLE);
        mTvTip.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.upload_cell_iv_del) {
            mIvCar.setImageResource(R.mipmap.ic_release_img_add);
            mIvCar.setVisibility(View.GONE);
            mIvDel.setVisibility(View.GONE);
            mTvTip.setVisibility(View.VISIBLE);

            if (mDelListener != null) {
                mDelListener.onPicDel(mEntityPicDetail);
            }
        }
    }

    private IPicDelListener mDelListener;
    public void setDelListener(IPicDelListener mDelListener) {
        this.mDelListener = mDelListener;
    }
}
