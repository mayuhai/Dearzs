package com.dearzs.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.dearzs.app.R;
import com.dearzs.app.base.IPicDelListener;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.widget.UploadPicCellView;
import com.dearzs.app.widget.UploadVehiclePicView;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.upload.uploadimage.UploadManager;

import java.util.ArrayList;

/**
 * 上传图片所需要的适配器
 *
 * @author Lyl
 */
public class GvUploadPicRecyclerViewAdapter extends
        RecyclerView.Adapter<GvUploadPicRecyclerViewHolder> {
    private Context mContext;

    public static final byte UPLOAD_TYPE_PIC = 0x1;
    public static final byte UPLOAD_TYPE_MORE = 0x2;

    /**
     * 数据源
     */
    private ArrayList<EntityNetPic> datas;
    private LayoutInflater mInflater;

    public GvUploadPicRecyclerViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 替换所有数据,重新布局界面
     *
     * @param datas
     */
    public void replaceAll(ArrayList<EntityNetPic> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        EntityNetPic item = getItem(position);
        if (item == null) return -1;
        // 根据数据类型,返回不同的布局
        return item.getUpload_state() == EntityNetPic.UPLOAD_PIC_TIP ?
                UPLOAD_TYPE_MORE : UPLOAD_TYPE_PIC;
    }

    // 创建对应的viewHolder
    @Override
    public GvUploadPicRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        int PADDING_LR = DisplayUtil.dip2px(mContext, 1);
        int mItemWidth = (DisplayUtil.getScreenWidth(mContext) - 48 * PADDING_LR) / 4;
        switch (viewType) {
            case UPLOAD_TYPE_MORE:
                itemView = mInflater.inflate(R.layout.item_gv_upload_pic_more, parent, false);
                itemView.setVisibility(View.INVISIBLE);
                LayoutUtil.setLayout(itemView, mItemWidth, mItemWidth);
                break;
            case UPLOAD_TYPE_PIC:
                UploadVehiclePicView picView = (UploadVehiclePicView) mInflater.inflate(R.layout.item_gv_upload_pic_cus2,
                        parent, false);
                if (this.mDelListener != null)
                    picView.setDelListener(mDelListener);
                LayoutUtil.setLayout(picView, mItemWidth, mItemWidth);
                itemView = picView;
                break;
        }

        return new GvUploadPicRecyclerViewHolder(itemView, viewType);
    }

    /**
     * 生成上传任务,提交上传
     */
    private void updatePic(UploadVehiclePicView picView, EntityNetPic mIvReasonModel) {
        if (mIvReasonModel == null) {
            return;
        }
        // 重新绑定新图片路径
        //mIvReasonModel.setLocalPath(mIvReasonModel.getLocalPath());
        // 上传前会绑定显示视图
        mIvReasonModel.bindUploadProgress(picView);
        // 绑定数据实体类
        picView.updateEntityPic(mIvReasonModel);
        // 上传
        UploadManager.getInstance().execute(ReqManager.getInstance().
                getUploadPic(), mIvReasonModel);
    }

    // 绑定对应的ViewHolder
    @Override
    public void onBindViewHolder(final GvUploadPicRecyclerViewHolder holder, int position) {
        final EntityNetPic item = getItem(position);
        if (item == null) return;

        holder.itemView.setVisibility(View.VISIBLE);
        switch (holder.getViewType()) {
            case UPLOAD_TYPE_MORE:
                break;
            case UPLOAD_TYPE_PIC:
                UploadVehiclePicView picView = (UploadVehiclePicView) holder.itemView;
                picView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                picView.setDelListener(new IPicDelListener() {
                    @Override
                    public void onPicDel(Object pic) {
                        item.setLocalPath(null);//TODO   此处处理只要修改了本地图片,则将相应字段清空
                    }
                });
//                ((UploadPicCellView)holder.itemView).updateEntityPicDetail((EntityNetPic)item, itemWidth);
                updatePic((UploadVehiclePicView) holder.itemView, item);
                break;
        }
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickLitener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null || datas.isEmpty() ? 0 : datas.size();
    }

    public boolean isSupportDrag(int position) {
        return getItemViewType(position) == UPLOAD_TYPE_PIC;
    }

    private EntityNetPic getItem(int position) {
        if (datas == null || datas.isEmpty()) {
            return null;
        }
        return datas.get(position);
    }

    // 添加点击删除事件
    private IPicDelListener mDelListener;

    public void setDelListener(IPicDelListener mDelListener) {
        this.mDelListener = mDelListener;
    }

    // 为视图添加点击事件
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}