package com.dearzs.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.util.ImageLoaderManager;

import java.util.List;

/**
 * 专家出诊时间适配器
 * Created by Lyl
 */
public class GvVisitTimeAdapter extends QuickAdapter<EntityUserVisits> {
    private String[] weeks = BaseApplication.getInstance().getResources().getStringArray(R.array.week);
    private String[] times = BaseApplication.getInstance().getResources().getStringArray(R.array.day);

    public GvVisitTimeAdapter(Context context, int layoutResId, List<EntityUserVisits> carList){
        super(context, layoutResId, carList);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, EntityUserVisits item) {
        if(item == null){
            return;
        }
        try{
            helper.setText(R.id.expert_visit_week, weeks[item.getWeek() - 1]);
            helper.setText(R.id.expert_visit_time, getStrVisitTime(item.getTime()));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStrVisitTime(String time) {
        if(TextUtils.isEmpty(time)){
            return "";
        }
        try{
            StringBuffer visitTime = new StringBuffer();
                if(!TextUtils.isEmpty(time) && time.contains(",")){
                    String[] timeInteger = time.split(",");
                    for(int t=0;t<timeInteger.length;t++){
                        visitTime.append(times[Integer.valueOf(timeInteger[t]) - 1]);
                        visitTime.append("        ");
                    }
                } else {
                    visitTime.append(times[Integer.valueOf(time) - 1]);
                }
            return visitTime.toString();
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
