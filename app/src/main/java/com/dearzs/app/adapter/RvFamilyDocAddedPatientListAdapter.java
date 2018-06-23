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
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 家庭医生添加患者列表adapter
 */
public class RvFamilyDocAddedPatientListAdapter extends Adapter<RvFamilyDocAddedPatientListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityPatientInfo> mDataList;


    public RvFamilyDocAddedPatientListAdapter(Context context, List<EntityPatientInfo> carList) {
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityPatientInfo> dataList) {
        if (dataList != null) {
            this.mDataList = dataList;
//            this.mDataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_family_doc_order_confirm_patient_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDataList == null) return;
        final EntityPatientInfo patientInfo = mDataList.get(position);
        if (patientInfo != null) {
            holder.mPatientName.setText(patientInfo.getName());
            holder.mTabPatientName.setText(patientInfo.getName());
            holder.mPatientSex.setText(patientInfo.getGender() == 1 ? "男" : "女");
            holder.mPatientAge.setText(patientInfo.getAge() + "岁");
            holder.mPatientPhoneNum.setText(patientInfo.getPhone());
            holder.mPatientCardNum.setText(patientInfo.getCardNo());
            setListener(holder, patientInfo);
        }
    }

    private void setListener(ViewHolder viewHolder, final EntityPatientInfo consultation) {
        viewHolder.mDeletePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataList.remove(consultation);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPatientName;
        private TextView mTabPatientName;
        private TextView mPatientSex;
        private TextView mPatientAge;
        private TextView mPatientPhoneNum;
        private TextView mPatientCardNum;
        private ImageView mDeletePatient;

        public ViewHolder(View itemView) {
            super(itemView);
            mPatientName = GetViewUtil.getView(itemView, R.id.order_confirm_patient_name);
            mTabPatientName = GetViewUtil.getView(itemView, R.id.order_confirm_patient_tab_name);
            mPatientAge = GetViewUtil.getView(itemView, R.id.order_confirm_patient_age);
            mPatientSex = GetViewUtil.getView(itemView, R.id.order_confirm_patient_sex);
            mPatientPhoneNum = GetViewUtil.getView(itemView, R.id.order_confirm_patient_phone);
            mPatientCardNum = GetViewUtil.getView(itemView, R.id.order_confirm_patient_card_num);
            mDeletePatient = GetViewUtil.getView(itemView, R.id.order_confirm_patient_delete_iv);
        }

    }
}
