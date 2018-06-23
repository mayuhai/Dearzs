package com.dearzs.app.activity.family;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.dearzs.app.R;
import com.dearzs.app.activity.CommonWebViewActivity;
import com.dearzs.app.activity.home.AppointmentConsultationActivity;
import com.dearzs.app.activity.home.MessageListActivity;
import com.dearzs.app.activity.mine.OrderDataDetailActivity;
import com.dearzs.app.activity.mine.PatientLibraryActivity;
import com.dearzs.app.activity.mine.PatientSearchActivity;
import com.dearzs.app.adapter.RvFamilyDocAddedPatientListAdapter;
import com.dearzs.app.alipayapi.H5PayDemoActivity;
import com.dearzs.app.alipayapi.PayResult;
import com.dearzs.app.alipayapi.SignUtils;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.entity.resp.RespOrderCommit;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 家庭医生（订单确认）页面
 * Created by lx on 2016/6/15.
 */
public class FamilyDocOrderConfirmActivity extends BaseActivity {
    private EntityExpertInfo mExpertInfo;
    private EntityExpertInfo mAddedDoctorInfo;
    //    private EntityPatientInfo mPatientInfo;
    private boolean mIsDoctor;      //下单者是不是非患者？

    private CircleImageView mExpertPhoto, mDoctorPhoto;
    private TextView mExpertName, mExpertJob, mExpertDepartment, mExpertHospital, mOrderMoney;
    private TextView mDoctorName, mDoctorJob, mDoctorDepartment, mDoctorHospital, mTvChangeDoc;
    private ImageView mZhuanZhenTag, mDoctorZhuanZhenTag, mIvHelp;
    private Button mSearchPatient, mAddPatient, mSubmitOrder, mAddDoctor;
    private EditText mPhoneEt, mRemarksEt;
    private LinearLayout mNoPatientLayout;
    private LinearLayout mAlipayLayout, mWechatLayout, mPatientLayout, mAddDoctorLayout, mDoctorLayout;
    private CheckBox mAlipayCheckbox, mWechatCheckbox;
    private TextView mSelectedPackage;
    private int mConsultType = 1;//默认普通会诊
    private XRecyclerView mPatientRecylerView;
    private RvFamilyDocAddedPatientListAdapter mPatientAdapter;
    private List<EntityPatientInfo> mAddedPatientList;

    //支付参数相关
    private Integer mPayType = 1;    //支付类型，1支付宝2微信
    public static Integer ALIPAY = 1;    //支付类型，1支付宝
    public static Integer WEIXINPAY = 2;    //支付类型，2微信

    public static Integer HUIZHEN = 1;    //1会诊
    public static Integer ZHUANZHEN = 2;  //2转诊

    public static final int SDK_PAY_FLAG = 1;//支付
    private final static int DIALOG = 1;

    public static EntityOrderInfo mCurrentOrderInfo;
    public static Activity mOrderConfirmActivity;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(FamilyDocOrderConfirmActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(FamilyDocOrderConfirmActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(FamilyDocOrderConfirmActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(FamilyDocOrderConfirmActivity.this), FamilyDocOrderConfirmActivity.mCurrentOrderInfo.getOrderNo());
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_family_doc_order_confirm);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "确认家庭医生订单");
        mOrderConfirmActivity = this;
    }

    @Override
    public void initView() {
        super.initView();
        mPatientRecylerView = getView(R.id.family_doc_order_confirm_patient_list);
        mPatientLayout = getView(R.id.lin_add_patient_layout);
        mNoPatientLayout = getView(R.id.order_confirm_no_patient_layout);
        mSelectedPackage = getView(R.id.tv_selected_family_doc_package);

        mExpertPhoto = getView(R.id.order_confirm_expert_photo);
        mExpertName = getView(R.id.order_confirm_expert_name);
        mExpertJob = getView(R.id.order_confirm_expert_job);
        mExpertDepartment = getView(R.id.order_confirm_expert_department);
        mExpertHospital = getView(R.id.order_confirm_expert_hospital);
        mZhuanZhenTag = getView(R.id.order_confirm_can_zhuan);

        mAddDoctorLayout = getView(R.id.lin_add_doctor_layout);
        mDoctorLayout = getView(R.id.expert_details_doctor_layout);
        mDoctorPhoto = getView(R.id.order_confirm_doctor_photo);
        mDoctorName = getView(R.id.order_confirm_doctor_name);
        mDoctorJob = getView(R.id.order_confirm_doctor_job);
        mDoctorDepartment = getView(R.id.order_confirm_doctor_department);
        mDoctorHospital = getView(R.id.order_confirm_doctor_hospital);
        mDoctorZhuanZhenTag = getView(R.id.order_confirm_doctor_can_zhuan);
        mAddDoctor = getView(R.id.order_confirm_add_doctor_bt);

        mSearchPatient = getView(R.id.order_confirm_search_bt);
        mAddPatient = getView(R.id.order_confirm_add_bt);
        mSubmitOrder = getView(R.id.order_confirm_submit_bt);
        mOrderMoney = getView(R.id.order_confirm_order_money);

        mPhoneEt = getView(R.id.order_confirm_phone_et);
        mRemarksEt = getView(R.id.order_confirm_remarks_et);

        mAlipayLayout = getView(R.id.order_payment_alipay_layout);
        mWechatLayout = getView(R.id.order_payment_wechat_layout);
        mIvHelp = getView(R.id.iv_add_doctor_help);
        mTvChangeDoc = getView(R.id.tv_add_doctor_change_doctor);

        mAlipayCheckbox = getView(R.id.payment_alipay_checkbox);
        mWechatCheckbox = getView(R.id.payment_wechat_checkbox);
        mAlipayCheckbox.setChecked(true);//默认选中支付宝支付

        mAlipayLayout.setClickable(true);
        mWechatLayout.setClickable(true);

        mIvHelp.setOnClickListener(this);
        mTvChangeDoc.setOnClickListener(this);

        mAlipayLayout.setOnClickListener(this);
        mWechatLayout.setOnClickListener(this);

        mSubmitOrder.setOnClickListener(this);
        mAddPatient.setOnClickListener(this);
        mAddDoctor.setOnClickListener(this);
        mSearchPatient.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FamilyDocOrderConfirmActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPatientRecylerView.setLayoutManager(layoutManager);

        mPatientRecylerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mPatientRecylerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mPatientRecylerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mPatientRecylerView.setLoadingMoreEnabled(false);
        mPatientRecylerView.setPullRefreshEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //添加患者
    @Subscribe
    public void handleEventBus(EntityPatientInfo patientInfo) {
        if (patientInfo != null && mAddedPatientList != null) {
            if(!contains(patientInfo)){
                mAddedPatientList.add(patientInfo);
                mPatientRecylerView.setVisibility(View.VISIBLE);
                mPatientAdapter.notifyData(mAddedPatientList);
            } else {
                ToastUtil.showLongToast("您已选择该患者，请选择其他患者！");
            }
        } else {
//            mPatientRecylerView.setVisibility(View.GONE);
//            mNoPatientLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean contains (EntityPatientInfo patientInfo){
        for(int i=0;i<mAddedPatientList.size();i++){
            if(mAddedPatientList.get(i).getId() == patientInfo.getId()){
                return true;
            }
        }
        return false;
    }

    //添加主治医生
    @Subscribe
    public void handlerAddDoc(EntityExpertInfo doctorInfo) {
        if (doctorInfo != null) {
            mAddedDoctorInfo = doctorInfo;

            mAddDoctor.setVisibility(View.GONE);
            mTvChangeDoc.setVisibility(View.VISIBLE);
            mDoctorLayout.setVisibility(View.VISIBLE);
            EntityHospitalDepartmentInfo departmentInfo = doctorInfo.getDepartment();
            if (departmentInfo != null) {
                mDoctorDepartment.setText(departmentInfo.getName());
            }
            EntityHospitalInfo hospitalInfo = doctorInfo.getHospital();
            if (hospitalInfo != null) {
                mDoctorHospital.setText(hospitalInfo.getName());
            }
            mDoctorName.setText(doctorInfo.getName());
            mDoctorJob.setText(doctorInfo.getJob());
            ImageLoaderManager.getInstance().displayImage(doctorInfo.getAvatar(), mDoctorPhoto);
            mDoctorZhuanZhenTag.setVisibility(doctorInfo.getReferralState() == 1 ? View.VISIBLE : View.GONE);

        }
    }

    private boolean checkSubmit() {
        boolean check = false;

        if (mIsDoctor) {
            if (mAddedPatientList == null || mAddedPatientList.size() <= 0) {
                ToastUtil.showShortToast("请添加患者");
                return false;
            } else {
                check = true;
            }
        } else {
            if (mAddedDoctorInfo == null) {
                ToastUtil.showShortToast("请添加主治医生");
                return false;
            } else {
                check = true;
            }
        }
        if (!TextUtils.isEmpty(mPhoneEt.getText().toString())) {
            check = true;
        } else {
            ToastUtil.showShortToast("请输入手机号");
            return false;
        }

        return check;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.order_confirm_remarks_et:

                break;
            case R.id.order_confirm_submit_bt:

                if (mPayType == WEIXINPAY && !Utils.isAppInstalled(FamilyDocOrderConfirmActivity.this, Constant.WEIXINPACKAGENAME)) {
                    ToastUtil.showLongToast("您的手机没安装微信，请先安装微信或者更改支付方式");
                    return;
                }
                if (checkSubmit()) {
                    String orderPhone = mPhoneEt.getText().toString();
                    String remark = mRemarksEt.getText().toString();
//                    long patientId = mPatientInfo != null ? mPatientInfo.getId() : Utils.getUserId();       //如果是患者下单，则患者Id是下单者本身的ID
                    long expertId = mExpertInfo != null ? mExpertInfo.getId() : 0;
                    long doctorId = mAddedDoctorInfo != null ? mAddedDoctorInfo.getId() : Utils.getUserId(); //如果是非患者下单，则下单者扮演的就是县级医生的角色
//                    ReqManager.getInstance().reqOrderCommit(reqOrderCommitCall, Utils.getUserToken(FamilyDocOrderConfirmActivity.this),
//                            patientId,
//                            expertId,
//                            doctorId,
//                            orderPhone, remark, mPayType, mConsultType);
                }
                break;
            case R.id.order_confirm_search_bt:
                Intent intentSearch = new Intent(FamilyDocOrderConfirmActivity.this, PatientLibraryActivity.class);
                intentSearch.putExtra(Constant.KEY_FROM, Constant.KEY_FROM_ORDER_CONFIRM);
                startActivityForResult(intentSearch, Constant.REQUEST_CODE_PATIENT_LIBRARY_ACTIVITY);
                break;
            case R.id.order_confirm_add_bt:
                Intent intentAdd = new Intent(FamilyDocOrderConfirmActivity.this, PatientSearchActivity.class);
                startActivityForResult(intentAdd, Constant.REQUEST_CODE_ADD_PATIENT_ACTIVITY);
                break;
//            case R.id.order_confirm_patient_delete_iv:
//                mPatientInfo = null;
//                mNoPatientLayout.setVisibility(View.VISIBLE);
//                mSelectPatientLayout.setVisibility(View.GONE);
//                break;
            case R.id.order_payment_alipay_layout:
                mAlipayCheckbox.setChecked(true);
                mWechatCheckbox.setChecked(false);

                mPayType = ALIPAY;
                break;
            case R.id.order_payment_wechat_layout:
                mAlipayCheckbox.setChecked(false);
                mWechatCheckbox.setChecked(true);

                mPayType = WEIXINPAY;
                break;
            case R.id.order_confirm_add_doctor_bt:
                if (mExpertInfo != null) {
                    AppointmentConsultationActivity.startIntent(FamilyDocOrderConfirmActivity.this, !mIsDoctor, mExpertInfo.getId());
                } else {
                    ToastUtil.showLongToast("数据有误，请退出重试");
                }
                break;
            case R.id.tv_add_doctor_change_doctor:
                //更换主治医生
                if (mExpertInfo != null) {
                    AppointmentConsultationActivity.startIntent(FamilyDocOrderConfirmActivity.this, true, mExpertInfo.getId());//更换主治医生，肯定是选择医生
                } else {
                    ToastUtil.showLongToast("数据有误，请退出重试");
                }
                break;
            case R.id.iv_add_doctor_help:
                //帮助
                CommonWebViewActivity.startIntent(FamilyDocOrderConfirmActivity.this, "www.weibo.com", "添加主治医生");
                break;

        }
    }

    @Override
    public void initData() {
        super.initData();
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPatientAdapter = new RvFamilyDocAddedPatientListAdapter(FamilyDocOrderConfirmActivity.this, mAddedPatientList = new ArrayList<EntityPatientInfo>());
            mPatientRecylerView.setAdapter(mPatientAdapter);
            mExpertInfo = (EntityExpertInfo) getIntent().getSerializableExtra(Constant.KEY_EXPERT_INFO);
            mConsultType = getIntent().getIntExtra(Constant.KEY_CONSULT_TYPE, Constant.KEY_CONSULT_NORMAL);
            mIsDoctor = getIntent().getBooleanExtra(Constant.KEY_IS_DOCTOR, false);
            if (mExpertInfo != null) {
                ImageLoaderManager.getInstance().displayImage(mExpertInfo.getAvatar(), mExpertPhoto);
                mExpertName.setText(mExpertInfo.getName());
                mExpertJob.setText(mExpertInfo.getJob());
                EntityHospitalInfo hospitalInfo = mExpertInfo.getHospital();
                EntityHospitalDepartmentInfo departmentInfo = mExpertInfo.getDepartment();
                if (hospitalInfo != null) {
                    mExpertName.setText(hospitalInfo.getName());
                }
                if (departmentInfo != null) {
                    mExpertDepartment.setText(departmentInfo.getName());
                }

                //TODO通过传进来的标识来判断是会诊还是转诊
//                mOrderMoney.setText(mExpertInfo.getReferralMoney() + "");
                if (mConsultType == Constant.KEY_CONSULT_NORMAL) {
                    mOrderMoney.setText(mExpertInfo.getVisitMoney() + "");
                } else {
                    mOrderMoney.setText(mExpertInfo.getReferralMoney() + "");
                }
                mZhuanZhenTag.setVisibility(mExpertInfo.getReferralState() == 1 ? View.VISIBLE : View.GONE);
            }
        }
    }

    Callback<RespOrderCommit> reqOrderCommitCall = new Callback<RespOrderCommit>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespOrderCommit response) {
            if (onSuccess(response)) {

                RespOrderCommit.EntityOrderCommitResult orderInfoResult = response.getResult();
                mCurrentOrderInfo = orderInfoResult.getOrder();
                if (mCurrentOrderInfo != null) {

                    if (mPayType == WEIXINPAY) {
                        //商户APP工程中引入微信JAR包，调用API前，需要先向微信注册您的APPID
                        final IWXAPI api = WXAPIFactory.createWXAPI(FamilyDocOrderConfirmActivity.this, null);

                        // 将该app注册到微信
                        api.registerApp(Constant.KEY_WEIXIN_APPID);

                        //是否支持微信支付
//                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//                    Toast.makeText(OrderConfirmActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();

//                    int wxSdkVersion = api.getWXAppSupportAPI();
//                    if (wxSdkVersion >= Constant.TIMELINE_SUPPORTED_VERSION) {
//                        Toast.makeText(OrderConfirmActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(OrderConfirmActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
//                    }

                        PayReq request = new PayReq();
                        request.appId = orderInfoResult.getAppid();
                        request.partnerId = orderInfoResult.getPartnerid();
                        request.prepayId = orderInfoResult.getPrepayid();
                        request.packageValue = "Sign=WXPay";
                        request.nonceStr = orderInfoResult.getNoncestr();
                        request.timeStamp = orderInfoResult.getTimestamp();
                        request.sign = orderInfoResult.getSign();
                        api.sendReq(request);
                    } else {
                        aliPay(mCurrentOrderInfo);
                    }


                    ToastUtil.showLongToast("订单提交成功");
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

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
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.KEY_ORDER_INFO, orderInfo);
                    Utils.startIntent(FamilyDocOrderConfirmActivity.this, OrderDataDetailActivity.class, bundle);
                    finish();
                    if (FamilyDocOrderConfirmActivity.mOrderConfirmActivity != null) {
                        FamilyDocOrderConfirmActivity.mOrderConfirmActivity.finish();
                    }
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

    /**
     * call alipay sdk aliPay. 调用SDK支付
     */
    public void aliPay(EntityOrderInfo order) {
        if (TextUtils.isEmpty(Constant.PARTNER) || TextUtils.isEmpty(Constant.RSA_PRIVATE) || TextUtils.isEmpty(Constant.SELLER)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        String tagStr = mExpertInfo.getReferralState() == ZHUANZHEN ? "转诊" : "会诊";
        String productName = mExpertInfo.getName() + tagStr + "费用:" + order.getTotalFee();
        String orderInfo = getOrderInfo(productName, productName, order.getTotalFee() + "", order.getOrderNo());

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(FamilyDocOrderConfirmActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
     *
     * @param v
     */
    public void h5Pay(View v) {
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        /**
         * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
         * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
         * 商户可以根据自己的需求来实现
         */
        String url = "http://m.taobao.com";
        // url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price, String orderNO) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Constant.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Constant.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + orderNO + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + NetConstant.getServerHost() + "/order/notify/alipay" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, Constant.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public static void startIntent(Context context, EntityExpertInfo expertInfo, int consultType, boolean isDoctor) {
        Intent intent = new Intent(context, FamilyDocOrderConfirmActivity.class);
        intent.putExtra(Constant.KEY_EXPERT_INFO, expertInfo);
        intent.putExtra(Constant.KEY_CONSULT_TYPE, consultType);
        intent.putExtra(Constant.KEY_IS_DOCTOR, isDoctor);
        context.startActivity(intent);
    }
}
