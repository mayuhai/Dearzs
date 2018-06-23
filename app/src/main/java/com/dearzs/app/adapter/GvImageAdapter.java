package com.dearzs.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.GalleryImageActivity;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.entity.EntityDynamicImage;
import com.dearzs.app.entity.EntityMedicalRecordImgs;
import com.dearzs.app.entity.EntityPatientMedicalRecord;
import com.dearzs.app.util.ImageLoaderManager;

import java.util.List;

/**
 * 病历中图片的适配器
 * Created by Lyl
 */
public class GvImageAdapter extends QuickAdapter<EntityMedicalRecordImgs> {
    private List<EntityMedicalRecordImgs> mImageList;
    private Context mContext;

    public GvImageAdapter(Context context, int layoutResId, List<EntityMedicalRecordImgs> imageList){
        super(context, layoutResId, imageList);
        mImageList = imageList;
        mContext = context;
    }

    @Override
    public void replaceAll(List<EntityMedicalRecordImgs> elem) {
        mImageList = elem;
        super.replaceAll(elem);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, EntityMedicalRecordImgs item) {
        if(item == null){
            return;
        }
        try{
            ImageView imageView = helper.getView(R.id.item_dynamic_iv_pic);
            ImageLoaderManager.getInstance().displayImage(item.getImg(), imageView);
            setListener(helper, imageView);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setListener(final BaseAdapterHelper helper, final View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = helper.getPosition();
                Intent intent = new Intent(mContext,
                        GalleryImageActivity.class);
                intent.putExtra(GalleryImageActivity.IMAGEPATHS, getStrFromEntity(mImageList));
                intent.putExtra(GalleryImageActivity.SELECTED_POS, position);
                mContext.startActivity(intent);
            }
        });
    }

    private String[] getStrFromEntity(List<EntityMedicalRecordImgs> list){
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
}
