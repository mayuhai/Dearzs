package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.family.FamilyDocDetailActivity;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 家庭医生列表适配器
 */
public class LvFamilyDoctorListAdapter extends Adapter<LvFamilyDoctorListAdapter.ViewHolder> {
    private Context mCtx;
    private List<String> mDataList;


    public LvFamilyDoctorListAdapter(Context context, List<String> carList) {
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<String> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_family_doctor_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDataList == null) return;
        final String entity = mDataList.get(position);
        if (entity != null) {
            holder.mTitle.setText(mDataList.get(position));
            holder.mOrgPrice.setText("原价：" + "￥1999");
            holder.mCurPrice.setText("￥999");
            holder.mBuyedCount.setText("109人已购买");
            Utils.setTextViewMiddleLine(holder.mOrgPrice);
            ImageLoaderManager.getInstance().displayImage("http://a.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=124c783c60d0f703e6b292da30c13600/a9d3fd1f4134970aa8113e0497cad1c8a7865dbe.jpg", holder.mImage);
            setListener(holder.itemView, entity);
        }
    }

    private void setListener(View view, final String consultation) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = 0;
                FamilyDocDetailActivity.startIntent(mCtx, id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mTitle;
        private TextView mCurPrice;
        private TextView mOrgPrice;
        private TextView mBuyedCount;
        private TextView mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = GetViewUtil.getView(itemView, R.id.iv_item_family_doctor);
            mTitle = GetViewUtil.getView(itemView, R.id.tv_item_family_doctor_title);
            mOrgPrice = GetViewUtil.getView(itemView, R.id.tv_item_family_doctor_org_price);
            mCurPrice = GetViewUtil.getView(itemView, R.id.tv_item_family_doctor_cur_price);
            mBuyedCount = GetViewUtil.getView(itemView, R.id.tv_item_family_doctor_buy_count);
            mButton = GetViewUtil.getView(itemView, R.id.btn_item_family_doctor);
        }

    }
}
