package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.family.FamilyDocDetailActivity;
import com.dearzs.app.entity.EntityFamilyDoctorPackage;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 家庭医生套餐列表适配器
 */
public class LvFamilyDoctorPackageListAdapter extends Adapter<LvFamilyDoctorPackageListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityFamilyDoctorPackage> mDataList;


    public LvFamilyDoctorPackageListAdapter(Context context, List<EntityFamilyDoctorPackage> carList) {
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityFamilyDoctorPackage> dataList) {
        if (dataList != null) {
            this.mDataList.clear();
            this.mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_family_doctor_package_check, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDataList == null) return;
        final EntityFamilyDoctorPackage entity = mDataList.get(position);
        if (entity != null) {
            holder.mCheckBox.setBackgroundResource(entity.isChecked() ? R.mipmap.checkbox_checked : R.mipmap.checkbox_unchecked);
//            holder.mCheckBox.setBackgroundResource(entity.isChecked() ? R.drawable.rectangle_green_line_white_bg : R.drawable.rounded_rectangle_gray_line_white_bg);
            holder.mCheckBox.setTextColor(entity.isChecked() ? mCtx.getResources().getColor(R.color.green) : mCtx.getResources().getColor(R.color.gray_7));
            holder.mCheckBox.setText(entity.getPackageName());
            setListener(holder.itemView, position);
        }
    }

    private void setListener(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mDataList.size(); i++){
                    mDataList.get(i).setChecked(position == i);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mCheckBox = GetViewUtil.getView(itemView, R.id.tv_package_check_box);
        }

    }
}
