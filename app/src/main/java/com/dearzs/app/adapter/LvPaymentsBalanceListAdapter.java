package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.entity.EntityPaymentsBalance;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 收支明细列表适配器
 */
public class LvPaymentsBalanceListAdapter extends Adapter<LvPaymentsBalanceListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityPaymentsBalance> mDataList;


    public LvPaymentsBalanceListAdapter(Context context, List<EntityPaymentsBalance> carList){
        mCtx = context;
        this.mDataList = carList;
    }


    public void notifyData(List<EntityPaymentsBalance> dataList, boolean isRefresh) {
        if (dataList != null) {
            if (isRefresh) {      //是下拉刷新而不是上拉加载
                this.mDataList.clear();
                this.mDataList.addAll(dataList);
            } else {        //下拉加载更多
                this.mDataList.addAll(dataList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_payments_balance_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityPaymentsBalance entity = mDataList.get(position);
        if(entity != null){
//            ImageLoaderManager.getInstance().displayImage(entity.getImg(), holder.mImage);
            holder.mCostType.setText(entity.getDetail());
            String tag = entity.getType() == 1 ? "+" : "-";
            holder.mMomey.setText(tag + entity.getMoney());
            holder.mMomey.setTextColor(mCtx.getResources().getColor(entity.getType() == 1 ? R.color.red_light : R.color.black_2));
            holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            setListener(holder.mLayout, String.valueOf(entity.getId()));
        }
    }

    private void setListener(View view, final String expertId){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString(Constant.KEY_EXPERT_ID, expertId);
//                ExpertDetailsActivity.startIntent(mCtx, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLayout;
        private ImageView mImage;
        private TextView mCostType;
        private TextView mMomey;
        private TextView mTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.item_payments_balance_layout);
            mImage = GetViewUtil.getView(itemView, R.id.iv_item_cost_img);
            mTime = GetViewUtil.getView(itemView, R.id.tv_cost_time);
            mCostType = GetViewUtil.getView(itemView, R.id.tv_cost_type);
            mMomey = GetViewUtil.getView(itemView, R.id.tv_cost_money);
        }

    }
}
