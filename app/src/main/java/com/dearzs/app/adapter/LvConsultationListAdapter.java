package com.dearzs.app.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;

import java.util.List;

/**
 * 咨询列表适配器
 */
public class LvConsultationListAdapter extends QuickAdapter<EntityConsultation> {
    private Context mCtx;

    @Override
    protected void convert(BaseAdapterHelper helper, EntityConsultation item) {
        if(item == null){
            return;
        }

        ImageView icon = helper.getView(R.id.iv_item_consultation_icon);
        ImageLoaderManager.getInstance().displayImage(item.getImg(), icon);
        helper.setText(R.id.tv_consultation_time, Utils.getTimeStamp(item.getCreateTime()));
        helper.setText(R.id.tv_consultation_title, item.getTitle());
        helper.setText(R.id.tv_consultation_type, item.getTypeName());
        helper.setText(R.id.tv_consultation_praise, item.getPraise() + "");
    }

    public LvConsultationListAdapter(Context context, int layoutResId, List<EntityConsultation> carList){
        super(context, layoutResId, carList);
        mCtx = context;
    }
}
