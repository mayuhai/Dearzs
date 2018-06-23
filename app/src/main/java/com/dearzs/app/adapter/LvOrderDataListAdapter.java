package com.dearzs.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.mine.OrderDataDetailActivity;
import com.dearzs.app.activity.order.OrderConfirmActivity;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.EntityOrderState;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 医学咨询列表适配器
 */
public class LvOrderDataListAdapter extends Adapter<LvOrderDataListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityOrderInfo> mDataList;
    private String mTipType = "会诊";


    public LvOrderDataListAdapter(Context context, List<EntityOrderInfo> carList){
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityOrderInfo> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_order_list_item, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityOrderInfo entity = mDataList.get(position);
        if(entity != null){

            holder.mOrderTitle.setText(entity.getExpert().getName());
            holder.mOrderNO.setText("订单号：" + entity.getOrderNo());
            holder.mOrderFee.setText("￥" + entity.getTotalFee());
            holder.mOrderCreatTime.setText(Utils.getTimeStamp(Long.parseLong(entity.getCreateTime())));
            handleOrderInfo(holder,entity);
            setListener(holder.mLayout, entity);
        }
    }

    private void setListener(View view, final EntityOrderInfo orderInfo){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constant.KEY_ORDER_INFO, orderInfo);
//                Utils.startIntent(mCtx, OrderDataDetailActivity.class, bundle);

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_ORDER_NO, orderInfo.getOrderNo());
                Utils.startIntent(mCtx, OrderDataDetailActivity.class, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLayout;
        private CircleImageView mOrderImage;
        private TextView mOrderTitle;
        private TextView mOrderNO;
        private TextView mOrderCreatTime;
        private TextView mOrderTime;
        private TextView mOrderFee;
        private TextView mOrderStatus;
        private TextView mOrderExpertReply;
        private ImageView mOrderStatusTag;
        private ImageView mOrderTypeTag;
        private ImageView mOrderPatientGender;
        private TextView mOrderPatientAge;
        private View mOrderPatientSplitLine;

        private RelativeLayout mOrderBtnLayout;
        private Button mOrderLeftBtn;
        private Button mOrderRightBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.order_list_item);
            mOrderImage = GetViewUtil.getView(itemView, R.id.iv_icon);
            mOrderTitle = GetViewUtil.getView(itemView, R.id.tv_title);
            mOrderNO = GetViewUtil.getView(itemView, R.id.tv_order_NO);
            mOrderCreatTime = GetViewUtil.getView(itemView, R.id.tv_order_creat_time);
            mOrderTime = GetViewUtil.getView(itemView, R.id.tv_order_time);
            mOrderFee = GetViewUtil.getView(itemView, R.id.tv_order_fee);
            mOrderStatus = GetViewUtil.getView(itemView, R.id.tv_order_status);
            mOrderExpertReply = GetViewUtil.getView(itemView, R.id.tv_expert_reply);
            mOrderStatusTag = GetViewUtil.getView(itemView, R.id.iv_status_tag);
            mOrderTypeTag = GetViewUtil.getView(itemView, R.id.iv_tag_icon);

            mOrderPatientGender = GetViewUtil.getView(itemView, R.id.order_patient_sex);
            mOrderPatientAge = GetViewUtil.getView(itemView, R.id.order_patient_age);
            mOrderPatientSplitLine = GetViewUtil.getView(itemView, R.id.order_split_line);

            mOrderBtnLayout = GetViewUtil.getView(itemView, R.id.ly_btn);
            mOrderLeftBtn = GetViewUtil.getView(itemView, R.id.bt_left);
            mOrderRightBtn = GetViewUtil.getView(itemView, R.id.bt_left);

        }

    }

    private void handleOrderInfo(ViewHolder holder, EntityOrderInfo mOrderInfo) {
        EntityOrderState orderState = mOrderInfo.getOrderState();

        int orderType = mOrderInfo.getType();
        if (orderType == OrderConfirmActivity.HUIZHEN) {
            holder.mOrderTypeTag.setImageResource(R.mipmap.ic_hui_tag);
        }else{
            holder.mOrderTypeTag.setImageResource(R.mipmap.ic_zhuan_tag);
        }

        long orderStatus = OrderDataDetailActivity.ORDERWAITPAY;
        if (orderState!= null) {
            orderStatus = orderState.getId();
        }

        if (isZhuanzhen(mOrderInfo)) {
            mTipType = "转诊";
        }

        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        showOrderUserInfo(mOrderInfo, userInfo, holder);

        if (orderStatus == OrderDataDetailActivity.ORDERWAITPAY) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            holder.mOrderStatus.setText("已下单，待支付");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.VISIBLE);
            holder.mOrderLeftBtn.setVisibility(View.VISIBLE);
            holder.mOrderRightBtn.setVisibility(View.VISIBLE);
            holder.mOrderLeftBtn.setText("去支付");
            holder.mOrderRightBtn.setText("取消");

        }else if (orderStatus == OrderDataDetailActivity.ORDERPAYANDWAITREPLY) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            holder.mOrderStatus.setText("已付款，等待应答");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.VISIBLE);


            int type = EntityUserInfo.NORMALUSER;
            if (userInfo != null) {
                type = userInfo.getType();

            }

            if (type == EntityUserInfo.NORMALUSER) {//如果是患者
                holder.mOrderLeftBtn.setVisibility(View.INVISIBLE);
                holder.mOrderRightBtn.setText("取消");
            }else {//如果是医生
                holder.mOrderLeftBtn.setVisibility(View.VISIBLE);
                holder.mOrderRightBtn.setVisibility(View.VISIBLE);
                holder.mOrderLeftBtn.setText("取消");
                holder.mOrderRightBtn.setText("回复");
            }
        }else if (orderStatus == OrderDataDetailActivity.ORDERAUTOCANCEL) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            holder.mOrderStatus.setText("已取消，系统自动取消");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERWAITSTART) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
            holder.mOrderStatus.setText("已应答，等待会诊");

            if (isZhuanzhen(mOrderInfo)) {
                holder.mOrderStatus.setText("已应答，转诊中");
            }

            holder.mOrderBtnLayout.setVisibility(View.VISIBLE);
            holder.mOrderLeftBtn.setVisibility(View.INVISIBLE);
            holder.mOrderRightBtn.setText("取消");
        }else if (orderStatus == OrderDataDetailActivity.ORDERSTARTING) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
            holder.mOrderStatus.setText("会诊中");

            if (isZhuanzhen(mOrderInfo)) {
                holder.mOrderStatus.setText("转诊中");
            }

        }else if (orderStatus == OrderDataDetailActivity.ORDERENDWAITCOMMENT) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_complete_wait_comment_tag);
            holder.mOrderStatus.setText("会诊结束，待评价");

            if (isZhuanzhen(mOrderInfo)) {
                holder.mOrderStatus.setText("转诊结束，待评价");
            }

                holder.mOrderBtnLayout.setVisibility(View.VISIBLE);
            holder.mOrderRightBtn.setVisibility(View.VISIBLE);
            holder.mOrderRightBtn.setText("去评价");
        }else if (orderStatus == OrderDataDetailActivity.ORDERENDANDCOMMENT) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_complete_tag);
//            holder.mOrderStatus.setText("已完成");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }else if (orderStatus == OrderDataDetailActivity.ORDEREXPERTREFUSE) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            holder.mOrderStatus.setText("已取消，医生拒绝");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERSAVECANCEL) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            holder.mOrderStatus.setText("已取消，用户取消(不扣钱)");
//            holder.mOrderStatus.setText("已取消，用户取消");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERDUNKCANCEL) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            holder.mOrderStatus.setText("已取消，用户取消(扣钱)");
//            holder.mOrderStatus.setText("已取消，用户取消");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERBEFOREREPLYCANCEL) {
//            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            holder.mOrderStatus.setText("已取消，用户在医生应答前取消");
            holder.mOrderStatus.setText(mOrderInfo.getOrderState().getDes());

            holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
        }

        holder.mOrderBtnLayout.setVisibility(View.INVISIBLE);
    }

    private boolean isZhuanzhen(EntityOrderInfo orderInfo){
        int orderType = orderInfo.getType();

        if (orderType == OrderConfirmActivity.HUIZHEN) {
            return false;
        }else{
            return true;
        }
    }

    public void refreshItemView(EntityOrderInfo object, int pos) {
        if (mDataList != null && object != null) {
            if (pos >= 0 && pos < mDataList.size()) {
                mDataList.set(pos, object);
            }
            notifyItemChanged(pos + 1);
        }
    }

    public void delItemView(int pos) {
        if (mDataList != null) {
            if (pos >= 0 && pos < mDataList.size()) {
                mDataList.remove(pos);
                notifyItemRemoved(pos + 1);
            }
            if (mDataList.size() <= 0) {
                notifyDataSetChanged();
            }
        }
    }

    public EntityOrderInfo getItem(int position) {
        if (mDataList == null || mDataList.size() == 0 || position < 0 || position >= mDataList.size()) {
            return null;
        }
        return mDataList.get(position);
    }

    private void showOrderUserInfo(EntityOrderInfo orderInfo, EntityUserInfo userInfo, ViewHolder holder){
        int userType = EntityUserInfo.NORMALUSER;
        long userId = -1;
        if (userInfo != null) {
            userType = userInfo.getType();//是医生还是非医生
            userId = userInfo.getId();    //当前用户的id
        }

        EntityUserInfo creator = orderInfo.getCreator();  //订单创建者信息，一般为县级医生
        EntityUserInfo expert = orderInfo.getExpert(); //专家信息
        EntityUserInfo patient = orderInfo.getPatient(); //患者信息
        boolean isPatientOrder = false; //是否是患者下单
        if(patient != null && creator != null){
            isPatientOrder = patient.getId() == creator.getId();
        }
        EntityUserInfo doctor = isPatientOrder ? orderInfo.getDoctor() : creator;

        if(creator == null || expert == null || patient == null || doctor == null) {
            return;
        }

        if (userId == doctor.getId()) {//县级医生显示的信息

            ImageLoaderManager.getInstance().displayImage(expert.getAvatar(), holder.mOrderImage);
            holder.mOrderTitle.setText(expert.getName());
//            mOrderExpertJob.setText(expert.getJob());
//            mOrderExpertDepartment.setText(expert.getDepartment().getName());
//            mOrderExpertHospital.setText(expert.getHospital().getName());

            if (!TextUtils.isEmpty(orderInfo.getExpertRemark())) {
                holder.mOrderExpertReply.setVisibility(View.VISIBLE);
                holder.mOrderExpertReply.setText("专家回复：" + orderInfo.getExpertRemark() +
                        "[" + mTipType + "时间:" + orderInfo.getOrderTimeHour() + ":" + orderInfo.getOrderTimeMinute() + "]");
            }else {
                holder.mOrderExpertReply.setVisibility(View.GONE);
            }

            String orderTime = orderInfo.getOrderTime();
            String orderTimeStr = "全天";
            if (orderTime.equalsIgnoreCase(EntityOrderInfo.AM + "")) {
                orderTimeStr = "上午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.PM + "")){
                orderTimeStr = "下午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.NIGHT + "")){
                orderTimeStr = "晚上";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.ALLDAY + "")){
                orderTimeStr = "全天";
            }
            holder.mOrderTime.setText(orderInfo.getOrderDate() + " " + orderTimeStr);


        }else if (userId == expert.getId()) {//专家
            holder.mOrderPatientGender.setVisibility(View.VISIBLE);
            holder.mOrderPatientAge.setVisibility(View.VISIBLE);
            holder.mOrderPatientSplitLine.setVisibility(View.VISIBLE);
            if (patient != null) {
                ImageLoaderManager.getInstance().displayImage(patient.getAvatar(), holder.mOrderImage);
                holder.mOrderTitle.setText(patient.getName());
                int gender = patient.getGender();
                if (gender == Constant.FEMALE) {
                    holder.mOrderPatientGender.setImageResource(R.mipmap.ic_female);
                }else{
                    holder.mOrderPatientGender.setImageResource(R.mipmap.ic_male);
                }
                holder.mOrderPatientAge.setText("年龄:" + patient.getAge());
            }


            if (!TextUtils.isEmpty(orderInfo.getExpertRemark())) {
                holder.mOrderExpertReply.setVisibility(View.VISIBLE);
                holder.mOrderExpertReply.setText("我的回复：" + orderInfo.getExpertRemark());
            } else {
                holder.mOrderExpertReply.setVisibility(View.GONE);
            }

            String orderTime = orderInfo.getOrderTime();
            String orderTimeStr = "全天";
            if (orderTime.equalsIgnoreCase(EntityOrderInfo.AM + "")) {
                orderTimeStr = "上午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.PM + "")){
                orderTimeStr = "下午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.NIGHT + "")){
                orderTimeStr = "晚上";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.ALLDAY + "")){
                orderTimeStr = "全天";
            }
            holder.mOrderTime.setText(orderInfo.getOrderDate() + " " + orderTimeStr);
        }else if (userId == patient.getId()) {//患者
            ImageLoaderManager.getInstance().displayImage(expert.getAvatar(), holder.mOrderImage);
            holder.mOrderTitle.setText(expert.getName());
//            mOrderExpertJob.setText(expert.getJob());
//            mOrderExpertDepartment.setText(expert.getDepartment().getName());
//            mOrderExpertHospital.setText(expert.getHospital().getName());

            if (!TextUtils.isEmpty(orderInfo.getExpertRemark())) {
                holder.mOrderExpertReply.setVisibility(View.VISIBLE);
                holder.mOrderExpertReply.setText("专家回复：" + orderInfo.getExpertRemark());
            } else {
                holder.mOrderExpertReply.setVisibility(View.GONE);
            }

            String orderTime = orderInfo.getOrderTime();
            String orderTimeStr = "全天";
            if (orderTime.equalsIgnoreCase(EntityOrderInfo.AM + "")) {
                orderTimeStr = "上午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.PM + "")){
                orderTimeStr = "下午";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.NIGHT + "")){
                orderTimeStr = "晚上";
            }else if (orderTime.equalsIgnoreCase(EntityOrderInfo.ALLDAY + "")){
                orderTimeStr = "全天";
            }
            holder.mOrderTime.setText(orderInfo.getOrderDate() + " " + orderTimeStr);
        }
    }
}
