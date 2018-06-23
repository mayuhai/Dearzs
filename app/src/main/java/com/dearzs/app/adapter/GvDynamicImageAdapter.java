package com.dearzs.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.GalleryImageActivity;
import com.dearzs.app.entity.EntityDynamicImage;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 动态Item中的图片列表adapter
 * Created by luyanlong on 2016/6/6.
 */
public class GvDynamicImageAdapter extends RecyclerView.Adapter<GvDynamicImageAdapter.ViewHolder> {
    private Context mContext;
    private List<EntityDynamicImage> mDataList;

    public GvDynamicImageAdapter(Context context, List<EntityDynamicImage> dataList){
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_dynamic_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mDataList == null ){
            return;
        }
        EntityDynamicImage entity = mDataList.get(position);
        if(entity != null){
            ImageLoaderManager.getInstance().displayImage(entity.getImg(), holder.mDynamicImage);
        }
        holder.mDynamicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        GalleryImageActivity.class);
                intent.putExtra(GalleryImageActivity.IMAGEPATHS, getStrFromEntity(mDataList));
                intent.putExtra(GalleryImageActivity.SELECTED_POS, position);
                mContext.startActivity(intent);
            }
        });

    }

    private String[] getStrFromEntity(List<EntityDynamicImage> list){
        String[] images = null;
        if(list != null && list.size() > 0){
            images = new String[list.size()];
            for(int i=0;i<list.size();i++){
                images[i] = list.get(i).getImg();
            }
        } else {
            images = new String[0];
        }
        return images;
    }

    @Override
    public int getItemCount() {
        return (mDataList == null ? 0 : mDataList.size());
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mDynamicImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mDynamicImage = GetViewUtil.getView(itemView, R.id.item_dynamic_iv_pic);
        }
    }
}
