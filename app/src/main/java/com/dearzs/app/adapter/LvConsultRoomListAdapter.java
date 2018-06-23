package com.dearzs.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.expert.ExpertDetailsActivity;
import com.dearzs.app.activity.expert.MedicalRecordActivity;
import com.dearzs.app.activity.mine.PatientDetailActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.entity.EntityConsultInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.tencent.TIMConversationType;

import java.util.List;

/**
 * 会诊室列表适配器
 */
public class LvConsultRoomListAdapter extends Adapter<LvConsultRoomListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityConsultInfo> mDataList;
    private long mCurrentUserId;

    public LvConsultRoomListAdapter(Context context, List<EntityConsultInfo> carList) {
        mCtx = context;
        this.mDataList = carList;
        this.mCurrentUserId = BaseApplication.getInstance().getUserInfo().getId();
    }

    public void notifyData(List<EntityConsultInfo> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_consult_room_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList == null) return;
        EntityConsultInfo entity = mDataList.get(position);
        if (entity != null) {
            int bgResource = 0;
            int state = entity.getState();
            String stateStr = "";
            String stateTips = "";
            if (state == 0) {
                stateTips = "需要处理";
                bgResource = R.mipmap.consultation_room_start_title_bg;
                stateStr = "会诊未开始";
            } else if (state == 1) {
                stateTips = "立即进入";
                bgResource = R.mipmap.consultation_room_no_start_title_bg;
                stateStr = "会诊进行中";
            } else {
                stateTips = "查看记录";
                bgResource = R.mipmap.consultation_room_finish_title_bg;
                stateStr = "会诊结束，点击查看详情";
            }
            holder.mConsultRoomTitleLayout.setBackgroundResource(bgResource);
            holder.mConsultRoomStsteTips.setText(stateTips);
            holder.mConsultRoomStste.setText(stateStr);
            EntityUserInfo createrInfo = entity.getCreator();
            EntityExpertInfo doctorInfo = entity.getDoctor();
            EntityPatientInfo patientInfo = entity.getPatient();
            EntityExpertInfo expertInfo = entity.getExpert();
            if (createrInfo == null || expertInfo == null || patientInfo == null || doctorInfo == null) {
                return;
            }
            if (mCurrentUserId == doctorInfo.getId()) {  //县级医生
                holder.mPatientLayout.setVisibility(View.VISIBLE);
                holder.mExpertLayout.setVisibility(View.VISIBLE);
                holder.mLeftDoctorLayout.setVisibility(View.GONE);
                holder.mRightDoctorLayout.setVisibility(View.GONE);
                ImageLoaderManager.getInstance().displayImage(patientInfo.getAvatar(), holder.mPatientHeader);
                holder.mPatientName.setText(patientInfo.getName());
                holder.mPatientAge.setText(patientInfo.getAge() + "岁");
                holder.mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
                holder.mPatientAnalysis.setText(entity.getCaseAnalysis());

                ImageLoaderManager.getInstance().displayImage(expertInfo.getAvatar(), holder.mExpertHeader);
                holder.mExpertName.setText(expertInfo.getName());
                holder.mExpertJob.setText(expertInfo.getJob());
                EntityHospitalInfo hospitalInfo = expertInfo.getHospital();
                if (hospitalInfo != null) {
                    holder.mExpertHosiptal.setText(hospitalInfo.getName());
                }
                EntityHospitalDepartmentInfo departmentInfo = expertInfo.getDepartment();
                if (departmentInfo != null) {
                    holder.mExpertDepartment.setText(departmentInfo.getName());
                }

                holder.mPatientUserType.setText("患者");
                holder.mExpertUserType.setText("专家");
            } else if (mCurrentUserId == expertInfo.getId()) {  //专家
                holder.mPatientLayout.setVisibility(View.VISIBLE);
                holder.mRightDoctorLayout.setVisibility(View.VISIBLE);
                holder.mLeftDoctorLayout.setVisibility(View.GONE);
                holder.mExpertLayout.setVisibility(View.GONE);

                ImageLoaderManager.getInstance().displayImage(patientInfo.getAvatar(), holder.mPatientHeader);
                holder.mPatientName.setText(patientInfo.getName());
                holder.mPatientAge.setText(patientInfo.getAge() + "岁");
                holder.mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
                holder.mPatientAnalysis.setText(entity.getCaseAnalysis());
                if(createrInfo.getId() == patientInfo.getId()){ //患者下单
                    ImageLoaderManager.getInstance().displayImage(doctorInfo.getAvatar(), holder.mRightDoctorHeader);
                    holder.mRightDoctorName.setText(doctorInfo.getName());
                    holder.mRightDoctorJob.setText(doctorInfo.getJob());
                    EntityHospitalInfo hospitalInfo = doctorInfo.getHospital();
                    if (hospitalInfo != null) {
                        holder.mRightDoctorHosiptal.setText(hospitalInfo.getName());
                    }
                    EntityHospitalDepartmentInfo departmentInfo = doctorInfo.getDepartment();
                    if (departmentInfo != null) {
                        holder.mRightDoctorDepartment.setText(departmentInfo.getName());
                    }
                } else {    //主治医生下单
                    ImageLoaderManager.getInstance().displayImage(createrInfo.getAvatar(), holder.mRightDoctorHeader);
                    holder.mRightDoctorName.setText(createrInfo.getName());
                    holder.mRightDoctorJob.setText(createrInfo.getJob());
                    EntityHospitalInfo hospitalInfo = createrInfo.getHospital();
                    if (hospitalInfo != null) {
                        holder.mRightDoctorHosiptal.setText(hospitalInfo.getName());
                    }
                    EntityHospitalDepartmentInfo departmentInfo = createrInfo.getDepartment();
                    if (departmentInfo != null) {
                        holder.mRightDoctorDepartment.setText(departmentInfo.getName());
                    }
                }

                holder.mPatientUserType.setText("患者");
                holder.mRightDoctorUserType.setText("主治医师");

            } else if (mCurrentUserId == patientInfo.getId()) {   //患者
                holder.mLeftDoctorLayout.setVisibility(View.VISIBLE);
                holder.mExpertLayout.setVisibility(View.VISIBLE);
                holder.mPatientLayout.setVisibility(View.GONE);
                holder.mRightDoctorLayout.setVisibility(View.GONE);

                if(createrInfo.getId() == patientInfo.getId()){ //患者下单
                    ImageLoaderManager.getInstance().displayImage(doctorInfo.getAvatar(), holder.mLeftDoctorHeader);
                    holder.mLeftDoctorName.setText(doctorInfo.getName());
                    holder.mLeftDoctorJob.setText(doctorInfo.getJob());
                    EntityHospitalInfo hospitalInfo = doctorInfo.getHospital();
                    if (hospitalInfo != null) {
                        holder.mLeftDoctorHosiptal.setText(hospitalInfo.getName());
                    }
                    EntityHospitalDepartmentInfo departmentInfo = doctorInfo.getDepartment();
                    if (departmentInfo != null) {
                        holder.mLeftDoctorDepartment.setText(departmentInfo.getName());
                    }
                } else {
                    ImageLoaderManager.getInstance().displayImage(createrInfo.getAvatar(), holder.mLeftDoctorHeader);
                    holder.mLeftDoctorName.setText(createrInfo.getName());
                    holder.mLeftDoctorJob.setText(createrInfo.getJob());
                    EntityHospitalInfo hospitalInfo = createrInfo.getHospital();
                    if (hospitalInfo != null) {
                        holder.mLeftDoctorHosiptal.setText(hospitalInfo.getName());
                    }
                    EntityHospitalDepartmentInfo departmentInfo = createrInfo.getDepartment();
                    if (departmentInfo != null) {
                        holder.mLeftDoctorDepartment.setText(departmentInfo.getName());
                    }
                }

                ImageLoaderManager.getInstance().displayImage(expertInfo.getAvatar(), holder.mExpertHeader);
                holder.mExpertName.setText(expertInfo.getName());
                holder.mExpertJob.setText(expertInfo.getJob());
                EntityHospitalInfo expertHospitalInfo = expertInfo.getHospital();
                if (expertHospitalInfo != null) {
                    holder.mExpertHosiptal.setText(expertHospitalInfo.getName());
                }
                EntityHospitalDepartmentInfo expertDepartmentInfo = expertInfo.getDepartment();
                if (expertDepartmentInfo != null) {
                    holder.mExpertDepartment.setText(expertDepartmentInfo.getName());
                }

                holder.mLeftDoctorUserType.setText("主治医师");
                holder.mExpertUserType.setText("专家");
            }
            setListener(holder, entity);
        }
    }

    private void setListener(ViewHolder viewHolder, final EntityConsultInfo consultation) {
        viewHolder.mPatientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (consultation != null && consultation.getPatient() != null) {
                    bundle.putString(Constant.KEY_PATIENT_ID, String.valueOf(consultation.getPatient().getId()));
                }
                Utils.startIntent(mCtx, PatientDetailActivity.class, bundle);
            }
        });
        viewHolder.mLeftDoctorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultation != null && consultation.getCreator() != null) {
                    String doctorId = consultation.getCreator().getId() == consultation.getPatient().getId() ? String.valueOf(consultation.getDoctor().getId()) : String.valueOf(consultation.getCreator().getId());
                    ExpertDetailsActivity.startIntent(mCtx, doctorId, Constant.KEY_CONSULT_NORMAL);
                }
            }
        });
        viewHolder.mRightDoctorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultation != null && consultation.getCreator() != null) {
                    String doctorId = consultation.getCreator().getId() == consultation.getPatient().getId() ? String.valueOf(consultation.getDoctor().getId()) : String.valueOf(consultation.getCreator().getId());
                    ExpertDetailsActivity.startIntent(mCtx, doctorId, Constant.KEY_CONSULT_NORMAL);
                }
            }
        });
        viewHolder.mExpertLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultation != null && consultation.getExpert() != null) {
                    ExpertDetailsActivity.startIntent(mCtx, String.valueOf(consultation.getExpert().getId()), Constant.KEY_CONSULT_NORMAL);
                }
            }
        });
        viewHolder.mViewMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_PATIENT_DETAILS);
                bundle.putSerializable(Constant.KEY_PATIENT_INFO, consultation.getPatient());
                Utils.startIntent(mCtx, MedicalRecordActivity.class, bundle);
            }
        });
        viewHolder.mConsultRoomTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultation == null) return;
                if (consultation.getState() == 0 && (consultation.getExpert() == null || consultation.getExpert().getId() != mCurrentUserId)) {
                    ToastUtil.showLongToast("会诊还未开始，请联系咨询会诊专家");
                } else {
                    applyJoinGroup(consultation);
                }
            }
        });
    }

    private void applyJoinGroup(final EntityConsultInfo consultInfo) {
        if (consultInfo == null) return;
        int identityType = -1;
        if(consultInfo != null && consultInfo.getPatient() != null && consultInfo.getExpert() != null && consultInfo.getCreator() != null){
            identityType = consultInfo.getExpert().getId() == mCurrentUserId ? ChatActivity.IDENTITY_EXPERT_KEY : consultInfo.getCreator().getId() == mCurrentUserId ? ChatActivity.IDENTITY_DOCTIR_KEY : ChatActivity.IDENTITY_PATIENT_KEY;
        }
        ChatActivity.startIntent(mCtx, consultInfo.getGroupId(),identityType, true, consultInfo.getState(), consultInfo, TIMConversationType.Group);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mPatientLayout, mLeftDoctorLayout, mExpertLayout, mRightDoctorLayout;
        private CircleImageView mPatientHeader, mLeftDoctorHeader, mExpertHeader, mRightDoctorHeader;
        private TextView mPatientUserType, mLeftDoctorUserType, mExpertUserType, mRightDoctorUserType;
        private TextView mPatientName, mLeftDoctorName, mExpertName, mRightDoctorName;
        private TextView mLeftDoctorJob, mRightDoctorJob, mExpertJob, mLeftDoctorHosiptal, mRightDoctorHosiptal, mExpertHosiptal, mLeftDoctorDepartment, mRightDoctorDepartment, mExpertDepartment;
        private TextView mPatientAge, mPatientAnalysis;
        private ImageView mPatientSex;
        private TextView mConsultRoomStste;         //0待会诊1会诊中2会诊结束
        private TextView mConsultRoomStsteTips;
        private LinearLayout mConsultRoomTitleLayout;
        private TextView mViewMedicalRecord;

        public ViewHolder(View itemView) {
            super(itemView);
            mPatientLayout = GetViewUtil.getView(itemView, R.id.lin_patient_layout);
            mLeftDoctorLayout = GetViewUtil.getView(itemView, R.id.lin_left_doctor_layout);
            mRightDoctorLayout = GetViewUtil.getView(itemView, R.id.lin_right_doctor_layout);
            mExpertLayout = GetViewUtil.getView(itemView, R.id.lin_expert_layout);
            mViewMedicalRecord = GetViewUtil.getView(itemView, R.id.tv_consult_room_patient_view_record);
            mConsultRoomStste = GetViewUtil.getView(itemView, R.id.tv_consult_room_state);
            mConsultRoomStsteTips = GetViewUtil.getView(itemView, R.id.tv_consult_room_state_tips);
            mConsultRoomTitleLayout = GetViewUtil.getView(itemView, R.id.consultation_room_state_layout);

            mPatientHeader = GetViewUtil.getView(itemView, R.id.iv_consult_room_patient_header);
            mLeftDoctorHeader = GetViewUtil.getView(itemView, R.id.iv_consult_room_left_doctor_header);
            mRightDoctorHeader = GetViewUtil.getView(itemView, R.id.iv_consult_room_right_doctor_header);
            mExpertHeader = GetViewUtil.getView(itemView, R.id.iv_consult_room_expert_header);

            mPatientUserType = GetViewUtil.getView(itemView, R.id.tv_consult_room_patient_tag);
            mLeftDoctorUserType = GetViewUtil.getView(itemView, R.id.tv_consult_room_left_doctor_tag);
            mRightDoctorUserType = GetViewUtil.getView(itemView, R.id.tv_consult_room_right_doctor_tag);
            mExpertUserType = GetViewUtil.getView(itemView, R.id.tv_consult_room_expert_tag);

            mPatientName = GetViewUtil.getView(itemView, R.id.tv_consult_room_patient_name);
            mLeftDoctorName = GetViewUtil.getView(itemView, R.id.tv_consult_room_left_doctor_name);
            mRightDoctorName = GetViewUtil.getView(itemView, R.id.tv_consult_room_right_doctor_name);
            mExpertName = GetViewUtil.getView(itemView, R.id.tv_consult_room_expert_name);

            mLeftDoctorJob = GetViewUtil.getView(itemView, R.id.tv_consult_room_left_doctor_job);
            mRightDoctorJob = GetViewUtil.getView(itemView, R.id.tv_consult_room_right_doctor_job);
            mExpertJob = GetViewUtil.getView(itemView, R.id.tv_consult_room_expert_job);

            mLeftDoctorHosiptal = GetViewUtil.getView(itemView, R.id.tv_consult_room_left_doctor_hospital);
            mRightDoctorHosiptal = GetViewUtil.getView(itemView, R.id.tv_consult_room_right_doctor_hospital);
            mExpertHosiptal = GetViewUtil.getView(itemView, R.id.tv_consult_room_expert_hospital);

            mLeftDoctorDepartment = GetViewUtil.getView(itemView, R.id.tv_consult_room_left_doctor_department);
            mRightDoctorDepartment = GetViewUtil.getView(itemView, R.id.tv_consult_room_right_doctor_department);
            mExpertDepartment = GetViewUtil.getView(itemView, R.id.tv_consult_room_expert_department);

            mPatientAge = GetViewUtil.getView(itemView, R.id.tv_consult_room_patient_age);
            mPatientAnalysis = GetViewUtil.getView(itemView, R.id.tv_consult_room_patient_case_analysis);

            mPatientSex = GetViewUtil.getView(itemView, R.id.iv_consult_room_patient_sex);
        }

    }
}
