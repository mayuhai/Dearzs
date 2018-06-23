package com.dearzs.app.activity.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.expert.MedicalRecordActivity;
import com.dearzs.app.activity.order.OrderConfirmActivity;
import com.dearzs.app.activity.order.OrderPendingPaymentActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityEvent;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.EntityOrderState;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespOrderCommit;
import com.dearzs.app.util.CommentCustomDialog;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ExpertReplyCustomDialog;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CustomCellViewWithImage;
import com.dearzs.app.widget.LayoutSignTree;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by mayuhai on 2016/7/10.
 */
public class OrderDataDetailActivity extends BaseActivity {

    private EntityOrderInfo mOrderInfo;
    private ImageView mTypeIv; //订单类型，1会诊2转诊
    private TextView mTypeTv;
    private ImageView mOrderStatusTagIv;
    private RelativeLayout mOrderStatusLayout;

//    0	已下单，待支付	立即付款
//    1	已付款，等待应答	应答,拒绝
//    2	已取消，系统自动取消	15分钟内没付款自动取消
//    3	已应答，等待会诊	取消订单
//    4	会诊中	会诊中
//    5	会诊结束，待评价	\N
//    6	已完成，已评价	\N
//    21	已取消，医生拒绝	医生拒绝订单，全额退款
//    22	已取消，下单者取消	等待会诊开始前24小时前取消订单,全额退款
//    23	已取消，下单者取消	等待会诊开始前24小时内取消订单,扣除1%会诊费用
//    24	已取消，下单者在医生应答前取消	下单者在医生应答前取消，全额退款
    private TextView mOrderStatusTv;
    public static Integer ORDERWAITPAY = 0;             //0	已下单，待支付	立即付款
    public static Integer ORDERPAYANDWAITREPLY = 1;     //1	已付款，等待应答	应答,拒绝
    public static Integer ORDERAUTOCANCEL = 2;          //2	已取消，系统自动取消	15分钟内没付款自动取消
    public static Integer ORDERWAITSTART = 3;           //3	已应答，等待会诊	取消订单
    public static Integer ORDERSTARTING = 4;            //4	会诊中	会诊中
    public static Integer ORDERENDWAITCOMMENT = 5;      //5	会诊结束，待评价
    public static Integer ORDERENDANDCOMMENT = 6;       //6	已完成，已评价
    public static Integer ORDEREXPERTREFUSE = 21;       //21	已取消，医生拒绝	医生拒绝订单，全额退款
    public static Integer ORDERSAVECANCEL = 22;         //22	已取消，下单者取消	等待会诊开始前24小时前取消订单,全额退款
    public static Integer ORDERDUNKCANCEL = 23;         //23	已取消，下单者取消	等待会诊开始前24小时内取消订单,扣除1%会诊费用
    public static Integer ORDERBEFOREREPLYCANCEL = 24;  //24	已取消，下单者在医生应答前取消	下单者在医生应答前取消，全额退款

    private TextView mOrderNOTv;
    private TextView mOrderCreateTimeTv;
    private TextView mOrderPayTypeTv;

    private LinearLayout mOrderFeeLayout;

    private TextView mOrderFeeTv;
    private LinearLayout mOrderCommentLayout;

    private RatingBar mRatingBar;
    private TextView mOrderCommentTimeTv;
    private TextView mOrderCommentContentTv;

    private CustomCellViewWithImage mMedical;
    private View mLine2;

    private LinearLayout mOrderExpertLayout;
    private ImageView mOrderExpertPhoto;
    private TextView mOrderExpertName;
    private TextView mOrderExpertJob;
    private TextView mOrderExpertHospital;
    private TextView mOrderExpertDepartment;
    private TextView mOrderExpertTime;
    private TextView mOrderExpertFee;
    private TextView mOrderExpertReply;


    private LinearLayout mOrderPatientLayout;
    private ImageView mOrderPatientPhoto;
    private ImageView mOrderPatientGender;
    private TextView mOrderPatientName;
    private TextView mOrderPatientAge;
    private TextView mOrderPatientTime;

    private ScrollView mScrollLayout;
    private RelativeLayout mOrderBtnLayout;
    private Button mOrderRightBtn;
    private Button mOrderLeftBtn;

    private String mOrderResponsFailStr = "订单取消失败，请重试！";
    private String mOrderResponsSuccStr = "订单取消成功，请等待审核！";

    private EntityUserInfo mUserInfo;
    private int mUserType = EntityUserInfo.NORMALUSER;
    private long mUserId;
    public static Activity mOrderDataDetailActivity;
    private String mOrderNO;
    private Boolean mIsOrderCancelOpt;

    private String mTipType = "会诊";

    private LayoutSignTree mLiuCheng;
    private int[] mOrderStatusImgs = new int[]{R.mipmap.ic_order_step_finish_white, R.mipmap.icon_num_second,
            R.mipmap.icon_num_three, R.mipmap.icon_num_four, R.mipmap.icon_num_five, R.mipmap.icon_num_six};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_order_detail);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "订单详情");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        mOrderDataDetailActivity = this;
    }

    @Override
    public void initData() {
        super.initData();

        mUserInfo = BaseApplication.getInstance().getUserInfo();

        if(mUserInfo == null  || mUserInfo.getId() == 0){
            mUserType = PfUtils.getInt(OrderDataDetailActivity.this, Constant.DEARZS_SP, Constant.KEY_USER_TYPE, -1);
            mUserId = PfUtils.getLong(OrderDataDetailActivity.this, Constant.DEARZS_SP, Constant.KEY_USER_ID, -1);
            mUserInfo.setType(mUserType);
            mUserInfo.setId(mUserId);
        } else {
            mUserId = mUserInfo.getId();
            mUserType = mUserInfo.getType();
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            mOrderInfo = (EntityOrderInfo) getIntent().getExtras().getSerializable(Constant.KEY_ORDER_INFO);
            if (mOrderInfo != null) {
                mOrderNO = mOrderInfo.getOrderNo();
                showOrderDataInfo();
            }else {
                mOrderNO = (String) getIntent().getExtras().getSerializable(Constant.KEY_ORDER_NO);
                ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(OrderDataDetailActivity.this), mOrderNO);
            }
        }
    }

    private void showOrderDataInfo(){

        mIsOrderCancelOpt = false;

        int orderType = mOrderInfo.getType();

        if (orderType == OrderConfirmActivity.HUIZHEN) {
            mTypeIv.setImageResource(R.mipmap.ic_order_hui_tag);
            mTypeTv.setText("预约会诊");
        }else{
            mTypeIv.setImageResource(R.mipmap.ic_order_zhuan_tag);
            mTypeTv.setText("预约转诊");
            mTipType = "转诊";
        }

        showOrderUserInfo(mOrderInfo, mUserInfo);

        String orderNO = mOrderInfo.getOrderNo();
        mOrderNOTv.setText("订单号：" + orderNO);

        String orderCreateTime = mOrderInfo.getCreateTime();
        mOrderCreateTimeTv.setText("下单时间：" + Utils.getTimeStamp(Long.parseLong(orderCreateTime)));

        int orderPayType = mOrderInfo.getPayType();
        if (orderPayType == OrderConfirmActivity.ALIPAY) {
            mOrderPayTypeTv.setText("支付方式：支付宝支付");
        }else{
            mOrderPayTypeTv.setText("支付方式：微信支付");
        }

        double orderFee = mOrderInfo.getTotalFee();
        mOrderFeeTv.setText("￥" + orderFee);

        EntityUserInfo orderCreator = mOrderInfo.getCreator();
        EntityUserInfo orderExpert = mOrderInfo.getExpert();
        EntityUserInfo orderPatient = mOrderInfo.getPatient();

        EntityOrderState orderState = mOrderInfo.getOrderState();
        long orderStatus = OrderDataDetailActivity.ORDERWAITPAY;
        if (orderState!= null) {
            orderStatus = orderState.getId();
        }

        boolean isRightBtnShow = false;

        if (orderStatus == OrderDataDetailActivity.ORDERWAITPAY) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.order_wait_reply_color));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            mOrderStatusTv.setText("已下单，待支付");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
            //只有下单者本人才能取消
            if (mUserId == orderCreator.getId()) {
                mOrderBtnLayout.setVisibility(View.VISIBLE);
                mOrderRightBtn.setText("取消");
                mOrderRightBtn.setVisibility(View.VISIBLE);
                mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmDialog(OrderDataDetailActivity.this, "确定要取消该订单吗？", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mOrderResponsFailStr = "订单取消失败，请重试！";
                                mOrderResponsSuccStr = "订单取消成功，请等待审核！";
                                mIsOrderCancelOpt = true;
                                ReqManager.getInstance().reqOrderCancel1TO24(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());

                            }
                        }, null);
                    }
                });


                mOrderLeftBtn.setText("去支付");
                mOrderLeftBtn.setVisibility(View.VISIBLE);
                mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constant.KEY_ORDER_INFO, mOrderInfo);
                        Utils.startIntent(OrderDataDetailActivity.this, OrderPendingPaymentActivity.class, bundle);
                    }
                });
            }

        }else if (orderStatus == OrderDataDetailActivity.ORDERPAYANDWAITREPLY) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.order_wait_reply_color));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            mOrderStatusTv.setText("已付款，等待应答");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());
            //只有下单者本人才能取消
            if (mUserId == orderCreator.getId()) {
                mOrderBtnLayout.setVisibility(View.VISIBLE);
                mOrderRightBtn.setVisibility(View.VISIBLE);
                mOrderRightBtn.setText("取消");
                mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmDialog(OrderDataDetailActivity.this, "确定要取消该订单吗？", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mOrderResponsFailStr = "订单取消失败，请重试！";
                                mOrderResponsSuccStr = "订单取消成功，请等待审核！";
                                mIsOrderCancelOpt = true;
                                ReqManager.getInstance().reqOrderCancel1TO24(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());
                            }
                        }, null);
                    }
                });

                isRightBtnShow = true;
            }

            if (isRightBtnShow) {
                if (mUserId == orderExpert.getId()) {
                    mOrderBtnLayout.setVisibility(View.VISIBLE);
                    mOrderLeftBtn.setVisibility(View.VISIBLE);
                    mOrderLeftBtn.setText("回复");
                    mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showExpertReplyDialog();
                        }
                    });
                }
            } else {
                if (mUserId == orderExpert.getId()) {
                    mOrderBtnLayout.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setText("回复");
                    mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showExpertReplyDialog();
                        }
                    });
                }
            }


            mOrderCommentLayout.setVisibility(View.GONE);

        }else if (orderStatus == OrderDataDetailActivity.ORDERAUTOCANCEL) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.gray_9));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_wait_reply_tag);
//            mOrderStatusTv.setText("已取消，系统自动取消");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderFeeLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERWAITSTART) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.order_wait_reply_color));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);

            mOrderBtnLayout.setVisibility(View.GONE);
            //只有下单者本人才能取消
            if (mUserId == orderCreator.getId()) {
                mOrderBtnLayout.setVisibility(View.VISIBLE);
                mOrderRightBtn.setVisibility(View.VISIBLE);
                mOrderRightBtn.setText("取消");
                mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showConfirmDialog(OrderDataDetailActivity.this, "确定要取消该订单吗？", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mOrderResponsFailStr = "订单取消失败，请重试！";
                                mOrderResponsSuccStr = "订单取消成功，请等待审核！";
                                mIsOrderCancelOpt = true;
                                ReqManager.getInstance().reqOrderCancel3TO22(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());

                            }
                        }, null);
                    }
                });

                isRightBtnShow = true;
            }

            mOrderStatusTv.setText("已应答，等待会诊");
            if (isZhuanzhen()) {
                mOrderStatusTv.setText("已应答，等待转诊");

                if (mUserId == orderExpert.getId()) {
                    mOrderBtnLayout.setVisibility(View.VISIBLE);

                    if (isRightBtnShow) {
                        mOrderLeftBtn.setVisibility(View.VISIBLE);
                        mOrderLeftBtn.setText("完成转诊");
                        mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                showConfirmDialog(OrderDataDetailActivity.this, "确定要完成转诊吗？", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        mOrderResponsFailStr = "完成转诊失败，请重试！";
                                        mOrderResponsSuccStr = "完成转诊成功，请等待审核！";
                                        ReqManager.getInstance().reqOrderReferralEnd(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());

                                    }
                                }, null);
                            }
                        });
                    } else {
                        mOrderRightBtn.setVisibility(View.VISIBLE);
                        mOrderRightBtn.setText("完成转诊");
                        mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                showConfirmDialog(OrderDataDetailActivity.this, "确定要完成转诊吗？", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        mOrderResponsFailStr = "完成转诊失败，请重试！";
                                        mOrderResponsSuccStr = "完成转诊成功，请等待审核！";
                                        ReqManager.getInstance().reqOrderReferralEnd(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());

                                    }
                                }, null);
                            }
                        });
                    }

                }
            }else {
                mOrderBtnLayout.setVisibility((mOrderInfo.getConsultId() > 0) ? View.VISIBLE : View.GONE);

                if (isRightBtnShow) {
                    mOrderLeftBtn.setText("会诊室");
                    mOrderLeftBtn.setVisibility(View.VISIBLE);
                    mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOrderInfo != null || mOrderInfo.getConsultId() > 0){
                                ChatActivity.startIntent(OrderDataDetailActivity.this, mOrderInfo.getConsultId(), true, true);
                            } else {
                                ToastUtil.showLongToast("没有该会诊室");
                            }
                        }
                    });
                } else {
                    mOrderRightBtn.setText("会诊室");
                    mOrderRightBtn.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOrderInfo != null || mOrderInfo.getConsultId() > 0){
                                ChatActivity.startIntent(OrderDataDetailActivity.this, mOrderInfo.getConsultId(), true, true);
                            } else {
                                ToastUtil.showLongToast("没有该会诊室");
                            }
                        }
                    });
                }

            }



            mOrderCommentLayout.setVisibility(View.GONE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERSTARTING) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.green));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
            mOrderBtnLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);

            mOrderStatusTv.setText("会诊中");
            if (isZhuanzhen()) {
//                mOrderStatusTv.setText("已应答，等待转诊");
                mOrderStatusTv.setText("转诊中");

                if (mUserId == orderExpert.getId()) {
                    mOrderBtnLayout.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setText("完成转诊");
                    mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showConfirmDialog(OrderDataDetailActivity.this, "确定要完成转诊吗？", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    mOrderResponsFailStr = "完成转诊失败，请重试！";
                                    mOrderResponsSuccStr = "完成转诊成功，请等待审核！";
                                    ReqManager.getInstance().reqOrderReferralEnd(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo());

                                }
                            }, null);
                        }
                    });
                }
            }else {
                mOrderBtnLayout.setVisibility((mOrderInfo.getConsultId() > 0) ? View.VISIBLE : View.GONE);
                mOrderRightBtn.setText("会诊室");
                mOrderRightBtn.setVisibility(View.VISIBLE);
                mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOrderInfo != null || mOrderInfo.getConsultId() > 0){
                            ChatActivity.startIntent(OrderDataDetailActivity.this, mOrderInfo.getConsultId(), true, true);
                        } else {
                            ToastUtil.showLongToast("没有该会诊室");
                        }
                    }
                });
            }


        }else if (orderStatus == OrderDataDetailActivity.ORDERENDWAITCOMMENT) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.order_wait_reply_color));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_complete_wait_comment_tag);
            mOrderStatusTv.setText("会诊结束，待评价");
            mOrderBtnLayout.setVisibility(View.GONE);
            if (isZhuanzhen()) {
                mOrderStatusTv.setText("转诊结束，待评价");
            }else {
                mOrderBtnLayout.setVisibility((mOrderInfo.getConsultId() > 0) ? View.VISIBLE : View.GONE);
                mOrderRightBtn.setText("会诊室");
                mOrderRightBtn.setVisibility(View.VISIBLE);
                mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOrderInfo != null || mOrderInfo.getConsultId() > 0){
                            ChatActivity.startIntent(OrderDataDetailActivity.this, mOrderInfo.getConsultId(), true, true);
                        } else {
                            ToastUtil.showLongToast("没有该会诊室");
                        }
                    }
                });

                isRightBtnShow = true;
            }

            mOrderCommentLayout.setVisibility(View.GONE);
            //只有下单者本人才能评价
            if (mUserId == orderCreator.getId()) {
                mOrderBtnLayout.setVisibility(View.VISIBLE);

                if (isRightBtnShow) {
                    mOrderLeftBtn.setVisibility(View.VISIBLE);
                    mOrderLeftBtn.setText("评价");
                    mOrderLeftBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCommentDialog();
                        }
                    });
                } else {
                    mOrderRightBtn.setVisibility(View.VISIBLE);
                    mOrderRightBtn.setText("评价");
                    mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCommentDialog();
                        }
                    });
                }

            }
        }else if (orderStatus == OrderDataDetailActivity.ORDERENDANDCOMMENT) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.green));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_complete_tag);
//            mOrderStatusTv.setText("已完成");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

//            mOrderBtnLayout.setVisibility(View.GONE);
            if (mOrderInfo != null && mOrderInfo.getComment() != null) {
                EntityComment comment = mOrderInfo.getComment();
                mOrderCommentContentTv.setText(comment.getComment());
                mOrderCommentTimeTv.setText(Utils.getTimeStamp(comment.getCreateTime()));
                mRatingBar.setRating(comment.getStar());
            }else{
                mOrderCommentLayout.setVisibility(View.GONE);
            }

            mOrderBtnLayout.setVisibility((mOrderInfo.getConsultId() > 0) ? View.VISIBLE : View.GONE);
            mOrderRightBtn.setText("会诊室");
            mOrderRightBtn.setVisibility(View.VISIBLE);
            mOrderRightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOrderInfo != null || mOrderInfo.getConsultId() > 0){
                        ChatActivity.startIntent(OrderDataDetailActivity.this, mOrderInfo.getConsultId(), true, true);
                    } else {
                        ToastUtil.showLongToast("没有该会诊室");
                    }
                }
            });

        }else if (orderStatus == OrderDataDetailActivity.ORDEREXPERTREFUSE) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.gray_9));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            mOrderStatusTv.setText("已取消，医生拒绝");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderBtnLayout.setVisibility(View.GONE);
            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderFeeLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERSAVECANCEL) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.gray_9));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            mOrderStatusTv.setText("已取消，用户取消(不扣钱)");
//            mOrderStatusTv.setText("已取消，用户取消");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderBtnLayout.setVisibility(View.GONE);
            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderFeeLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERDUNKCANCEL) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.gray_9));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            mOrderStatusTv.setText("已取消，用户取消(扣钱)");
//            mOrderStatusTv.setText("已取消，用户取消");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderBtnLayout.setVisibility(View.GONE);
            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderFeeLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
        }else if (orderStatus == OrderDataDetailActivity.ORDERBEFOREREPLYCANCEL) {
            mOrderStatusLayout.setBackgroundColor(getResources().getColor(R.color.gray_9));
            mOrderStatusTagIv.setImageResource(R.mipmap.ic_order_pay_wait_start_tag);
//            mOrderStatusTv.setText("已取消，用户在医生应答前取消");
            mOrderStatusTv.setText(mOrderInfo.getOrderState().getDes());

            mOrderBtnLayout.setVisibility(View.GONE);
            mOrderPayTypeTv.setVisibility(View.GONE);
            mOrderFeeLayout.setVisibility(View.GONE);
            mOrderCommentLayout.setVisibility(View.GONE);
        }

        handleOrderStatus(orderStatus);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mScrollLayout.getLayoutParams();
        int height = mOrderBtnLayout.getVisibility() == View.VISIBLE ? DimenUtils.dip2px(OrderDataDetailActivity.this, 50) : 0;
        layoutParams.setMargins(0, 0, 0, height);
        mScrollLayout.setLayoutParams(layoutParams);
    }

    private boolean isZhuanzhen(){
        int orderType = mOrderInfo.getType();

        if (orderType == OrderConfirmActivity.HUIZHEN) {
            return false;
        }else{
            return true;
        }
    }


    private void handleOrderStatus(long orderStatus) {

        int orderType = mOrderInfo.getType();
        String[] tips = new String[]{"订单已取消","待付款","待应答","待会诊","待评论","订单未完成"};
        int statusIndex = 0;

        if (orderType == OrderConfirmActivity.HUIZHEN) {
            if (orderStatus == ORDERWAITPAY){
                statusIndex = 2;
                tips = new String[]{"已下单","待付款","待应答","待会诊","待评论","订单未完成"};
            }

            if (orderStatus == ORDERPAYANDWAITREPLY){
                statusIndex = 3;
                tips = new String[]{"已下单","已付款","待应答","待会诊","待评论","订单未完成"};
            }

            if (orderStatus == ORDERWAITSTART){
                statusIndex = 4;
                tips = new String[]{"已下单","已付款","已应答","待会诊","待评论","订单未完成"};
            }

            if (orderStatus == ORDERSTARTING){
                statusIndex = 4;
                tips = new String[]{"已下单","已付款","已应答","会诊中","待评论","订单未完成"};
            }

            if (orderStatus == ORDERENDWAITCOMMENT){
                statusIndex = 5;
                tips = new String[]{"已下单","已付款","已应答","会诊结束","待评论","订单未完成"};
            }

            if (orderStatus == ORDERENDANDCOMMENT){
                statusIndex = 6;
                tips = new String[]{"已下单","已付款","已应答","会诊结束","已评论","订单完成"};
            }
        }else{
            tips = new String[]{"订单已取消","待付款","待应答","待转诊","待评论","订单未完成"};

            if (orderStatus == ORDERWAITPAY){
                statusIndex = 2;
                tips = new String[]{"已下单","待付款","待应答","待转诊","待评论","订单未完成"};
            }

            if (orderStatus == ORDERPAYANDWAITREPLY){
                statusIndex = 3;
                tips = new String[]{"已下单","已付款","待应答","待转诊","待评论","订单未完成"};
            }

            if (orderStatus == ORDERWAITSTART){
                statusIndex = 4;
                tips = new String[]{"已下单","已付款","已应答","转珍中","待评论","订单未完成"};
            }

            if (orderStatus == ORDERSTARTING){
                statusIndex = 4;
                tips = new String[]{"已下单","已付款","已应答","转诊中","待评论","订单未完成"};
            }

            if (orderStatus == ORDERENDWAITCOMMENT){
                statusIndex = 5;
                tips = new String[]{"已下单","已付款","已应答","转诊结束","待评论","订单未完成"};
            }

            if (orderStatus == ORDERENDANDCOMMENT){
                statusIndex = 6;
                tips = new String[]{"已下单","已付款","已应答","转诊结束","已评论","订单完成"};
            }
        }

        mLiuCheng.setTreeNodeNum(tips.length, tips, mOrderStatusImgs);
        mLiuCheng.setSelectIndex(statusIndex);
    }


    @Override
    public void initView() {
        super.initView();
        mTypeIv = getView(R.id.tv_order_type_tag); //订单类型，1会诊2转诊
        mTypeTv = getView(R.id.tv_order_type_text);
        mOrderStatusTagIv = getView(R.id.iv_order_status_tag);
        mOrderStatusLayout = getView(R.id.tv_order_status_layout);
        mOrderStatusTv = getView(R.id.tv_order_status);
        mOrderNOTv = getView(R.id.tv_order_NO);
        mOrderCreateTimeTv = getView(R.id.tv_order_creat_time);
        mOrderPayTypeTv = getView(R.id.tv_order_pay_type);

        mOrderFeeLayout = getView(R.id.ly_order_fee);
        mOrderFeeTv = getView(R.id.tv_order_fee);

        mMedical = getView(R.id.iv_medical);
        mMedical.setOnClickListener(this);
        mLine2 = getView(R.id.line2);

        mOrderCommentLayout = getView(R.id.ly_comment);
        mRatingBar = getView(R.id.comment_rating);
        mOrderCommentTimeTv = getView(R.id.tv_order_comment_time);
        mOrderCommentContentTv = getView(R.id.tv_order_comment_content);

        mScrollLayout = getView(R.id.lin_order_detail_scroll_layout);

        mOrderBtnLayout = getView(R.id.ly_btn);
        mOrderRightBtn = getView(R.id.bt_right);
        mOrderLeftBtn = getView(R.id.bt_left);

        mOrderExpertLayout = getView(R.id.ly_expert);
        mOrderExpertPhoto = getView(R.id.order_photo);
        mOrderExpertName = getView(R.id.order_expert_name);
        mOrderExpertJob = getView(R.id.order_expert_job);
        mOrderExpertHospital = getView(R.id.order_expert_hospital);
        mOrderExpertDepartment = getView(R.id.order_expert_department);
        mOrderExpertTime = getView(R.id.order_expert_time);
        mOrderExpertFee = getView(R.id.tv_order_expert_fee);
        mOrderExpertReply = getView(R.id.tv_expert_reply);

        mOrderPatientLayout = getView(R.id.ly_patient);
        mOrderPatientPhoto = getView(R.id.order_patient_photo);
        mOrderPatientGender = getView(R.id.order_patient_sex);
        mOrderPatientName = getView(R.id.order_patient_name);
        mOrderPatientAge = getView(R.id.order_patient_age);
        mOrderPatientTime = getView(R.id.order_patient_time);

        mLiuCheng = getView(R.id.order_liucheng);

    }

    Callback<EntityBase> reqOrderCommentCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast(mOrderResponsFailStr);
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                ToastUtil.showLongToast("评论成功");
//                initData();
                finish();
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };


    Callback<EntityBase> reqOrderCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast(mOrderResponsFailStr);
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功
            if (onSuccess(response)) {
                if (mIsOrderCancelOpt) {
                    mIsOrderCancelOpt = false;
                    showErrorDialog(TextUtils.isEmpty(response.getMsg()) ? mOrderResponsSuccStr : response.getMsg(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(OrderDataDetailActivity.this), mOrderNO);
                        }
                    });
                }else {
                    ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(OrderDataDetailActivity.this), mOrderNO);
                    ToastUtil.showShortToast(mOrderResponsSuccStr);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    public void showCommentDialog() {

        final CommentCustomDialog.Builder builder = new CommentCustomDialog.Builder(this);

        builder.setTitle("评论");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                RatingBar ratingBar = builder.getRatingBar();
                EditText editText = builder.getEditText();

                String comment = null;

                if (editText != null && editText.getText() != null && !TextUtils.isEmpty(editText.getText().toString().trim())) {
                    comment = editText.getText().toString().trim();
                    ToastUtil.showLongToast(comment);
                }

                ReqManager.getInstance().reqOrderComment(reqOrderCommentCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo(), Double.parseDouble(ratingBar.getRating() + ""), comment);
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    public void showExpertReplyDialog() {

        final ExpertReplyCustomDialog.Builder builder = new ExpertReplyCustomDialog.Builder(this);

        String message = "设置建议会诊时间";
        if (isZhuanzhen()) {
            message = "设置建议转诊时间";
        }
        builder.setMessage(message);
        builder.setTitle("专家回复");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
                EditText editText = builder.getEditText();
                NumberPicker hourPicker = builder.getHourPicker();
                NumberPicker minutePicker = builder.getMinutePicker();

                String expertRemark = null;

                if (editText != null && editText.getText() != null && !TextUtils.isEmpty(editText.getText().toString().trim())) {
                    expertRemark = editText.getText().toString().trim();
                    ToastUtil.showLongToast(expertRemark);
                }

                int hour = hourPicker.getValue();
                int minute = minutePicker.getValue();

                mOrderResponsFailStr = "订单回复失败，请重试！";
                mOrderResponsSuccStr = "订单回复成功，请准时" + mTipType + "！";
                ReqManager.getInstance().reqOrderExperReply(reqOrderCallback, Utils.getUserToken(OrderDataDetailActivity.this), mOrderInfo.getOrderNo(), Constant.EXPERT_AGREE, hour,minute,expertRemark);
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

        if (mOrderInfo != null) {
            String timeStr = mOrderInfo.getOrderTime();

            int time = Integer.parseInt(timeStr);

            if (!TextUtils.isEmpty(timeStr)) {
                Integer.parseInt(timeStr);
            }

            builder.setPickerTimeShow(time);
        }

    }

    private void showOrderUserInfo(EntityOrderInfo orderInfo, EntityUserInfo userInfo){
        int userType = EntityUserInfo.NORMALUSER;
        long userId = -1;
        if (userInfo != null) {
            userType = userInfo.getType();//是医生还是非医生
            userId = userInfo.getId();    //当前用户的id
        }

//        EntityUserInfo creator = orderInfo.getCreator();  //订单创建者信息，一般为县级医生
        EntityUserInfo expert = orderInfo.getExpert(); //专家信息
        EntityUserInfo patient = orderInfo.getPatient(); //患者信息
        boolean isPatientOrder = orderInfo.getCreator().getId() == patient.getId(); //是否是患者下单
        EntityUserInfo doctor = (isPatientOrder) ? orderInfo.getDoctor() : orderInfo.getCreator(); //县级医生信息（如果是患者下单则是Doctor如果不是患者下单则是Creator）

        int orderType = mOrderInfo.getType();
        if (orderType == Constant.KEY_CONSULT_NORMAL) {
            mOrderExpertFee.setText("￥" + expert.getVisitMoney());
        }else {
            mOrderExpertFee.setText("￥" + expert.getReferralMoney());
        }

        if (userId == doctor.getId()) {//县级医生端显示的信息

            mOrderExpertLayout.setVisibility(View.VISIBLE);
            mOrderPatientLayout.setVisibility(View.GONE);
            mMedical.setVisibility(View.GONE);
            mLine2.setVisibility(View.GONE);
            ImageLoaderManager.getInstance().displayImage(expert.getAvatar(), mOrderExpertPhoto);
            mOrderExpertName.setText(expert.getName());
            mOrderExpertJob.setText(expert.getJob());
            mOrderExpertDepartment.setText(expert.getDepartment().getName());
            mOrderExpertHospital.setText(expert.getHospital().getName());


            String orderTime = mOrderInfo.getOrderTime();
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
            mOrderExpertTime.setText("预约时间：" + orderInfo.getOrderDate() + " " + orderTimeStr);

            if (!TextUtils.isEmpty(orderInfo.getExpertRemark())) {
                mOrderExpertReply.setVisibility(View.VISIBLE);
                mOrderExpertReply.setText("专家回复：" + orderInfo.getExpertRemark() +
                        "[" + mTipType + "时间:" + mOrderInfo.getOrderTimeHour() + ":" + mOrderInfo.getOrderTimeMinute() + "]");
            }else {
                mOrderExpertReply.setVisibility(View.GONE);
            }


        }else if (userId == expert.getId()) {//专家端显示的信息
            mOrderExpertLayout.setVisibility(View.GONE);
            mOrderPatientLayout.setVisibility(View.VISIBLE);
            if(patient != null) {
                ImageLoaderManager.getInstance().displayImage(patient.getAvatar(), mOrderPatientPhoto);
                mOrderPatientName.setText(patient.getName());

                int gender = patient.getGender();
                if (gender == Constant.FEMALE) {
                    mOrderPatientGender.setImageResource(R.mipmap.ic_female);
                }else{
                    mOrderPatientGender.setImageResource(R.mipmap.ic_male);
                }

                mOrderPatientAge.setText(patient.getAge() + "");
            }else {
                ToastUtil.showShortToast("订单错误");
                finish();
                return;
            }

            String orderTime = mOrderInfo.getOrderTime();
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
            mOrderPatientTime.setText("预约时间：" + orderInfo.getOrderDate() + " " + orderTimeStr);
        }else if (userId == patient.getId()) {//患者端显示的信息
            mOrderExpertLayout.setVisibility(View.VISIBLE);
            mOrderPatientLayout.setVisibility(View.GONE);
            mMedical.setVisibility(View.GONE);
            mLine2.setVisibility(View.GONE);
            ImageLoaderManager.getInstance().displayImage(expert.getAvatar(), mOrderExpertPhoto);
            mOrderExpertName.setText(expert.getName());
            mOrderExpertJob.setText(expert.getJob());
            mOrderExpertDepartment.setText(expert.getDepartment() != null ? expert.getDepartment().getName() : null);
            mOrderExpertHospital.setText(expert.getHospital().getName());

            String orderTime = mOrderInfo.getOrderTime();
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
            mOrderExpertTime.setText("预约时间：" + orderInfo.getOrderDate() + " " + orderTimeStr);

            if (!TextUtils.isEmpty(orderInfo.getExpertRemark())) {
                mOrderExpertReply.setVisibility(View.VISIBLE);
                mOrderExpertReply.setText("专家回复：" + orderInfo.getExpertRemark());
            }
        }
    }

    //订单详情请求回掉
    Callback<RespOrderCommit> reqOrderDetailCall = new Callback<RespOrderCommit>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
            ToastUtil.showLongToast("订单查询失败，请重试！");
        }

        @Override
        public void onResponse(RespOrderCommit response) {
            if (onSuccess(response)) {

                RespOrderCommit.EntityOrderCommitResult orderInfoResult = response.getResult();
                EntityOrderInfo orderInfo = orderInfoResult.getOrder();
                if (orderInfoResult != null && orderInfo != null) {
                    mOrderInfo = orderInfo;
                    EventBus.getDefault().post(new EntityEvent.EventOrderRefresh(mOrderInfo, EntityEvent.EventOrderRefresh.TYPE_ORDER_REFRESH));
                    showOrderDataInfo();
                } else {
                    ToastUtil.showLongToast("订单查询失败，请重试！");
                }

            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        switch (viewId) {
            case R.id.iv_medical:

                EntityUserInfo patientUserInfo =  mOrderInfo.getPatient();
                if (patientUserInfo != null) {
                    EntityPatientInfo patientInfo = new EntityPatientInfo();
                    patientInfo.setAvatar(patientUserInfo.getAvatar());
                    patientInfo.setName(patientUserInfo.getName());
                    patientInfo.setAge(patientUserInfo.getAge());
                    patientInfo.setId(patientUserInfo.getId());
                    patientInfo.setGender(patientUserInfo.getGender());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.KEY_PATIENT_INFO, patientInfo);
                    bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_PATIENT_DETAILS);
                    Utils.startIntent(OrderDataDetailActivity.this, MedicalRecordActivity.class, bundle);
                }
                break;
        }
    }
}
