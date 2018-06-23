package com.dearzs.app.activity.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.dearzs.app.R;
import com.dearzs.app.activity.mine.OrderDataDetailActivity;
import com.dearzs.app.alipayapi.H5PayDemoActivity;
import com.dearzs.app.alipayapi.PayResult;
import com.dearzs.app.alipayapi.SignUtils;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.resp.RespOrderCommit;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/6/18.
 * 代付款界面
 */
public class OrderPendingPaymentActivity extends BaseActivity {
    private TextView mVisitTime, mOrderMoney;
    private LinearLayout mAlipayLayout, mWechatLayout;
    private CheckBox mAlipayCheckbox, mWechatCheckbox;
    private Button mPayBt;

    private EntityOrderInfo mOrderInfo;
    private Integer mOldPayType;
    private Integer mPayType = 1;    //支付类型，1支付宝2微信

    public static Activity mOrderPendingPaymentActivity;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConfirmActivity.SDK_PAY_FLAG: {
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
                        Toast.makeText(OrderPendingPaymentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderPendingPaymentActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderPendingPaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }

                    ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(OrderPendingPaymentActivity.this), mOrderInfo.getOrderNo());
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_order_pending_payment);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "等待付款");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        mOrderPendingPaymentActivity = this;
    }

    @Override
    public void initView() {
        super.initView();
        mAlipayLayout = getView(R.id.order_payment_alipay_layout);
        mWechatLayout = getView(R.id.order_payment_wechat_layout);

        mAlipayCheckbox = getView(R.id.payment_alipay_checkbox);
        mWechatCheckbox = getView(R.id.payment_wechat_checkbox);
        mOrderMoney = getView(R.id.order_payment_order_money);
        mPayBt = getView(R.id.order_payment_to_pay_bt);

        mAlipayLayout.setClickable(true);
        mWechatLayout.setClickable(true);

        mPayBt.setOnClickListener(this);
        mAlipayLayout.setOnClickListener(this);
        mWechatLayout.setOnClickListener(this);

        mAlipayCheckbox.setChecked(true);//默认选中支付宝支付
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        finish();
    }

    @Override
    public void initData() {
        super.initData();
        if (getIntent() != null && getIntent().getExtras() != null) {
            mOrderInfo = (EntityOrderInfo) getIntent().getExtras().getSerializable(Constant.KEY_ORDER_INFO);
            if (mOrderInfo != null) {
                mOldPayType = mOrderInfo.getPayType();

//                mOldPayType = mOrderInfo.getPayType();
//                mPayType = mOrderInfo.getPayType();
//                if (String.valueOf(mOldPayType).equalsIgnoreCase(String.valueOf(OrderConfirmActivity.WEIXINPAY))) {
//                    mAlipayCheckbox.setChecked(false);
//                    mWechatCheckbox.setChecked(true);
//                }else {
//                    mAlipayCheckbox.setChecked(true);
//                    mWechatCheckbox.setChecked(false);
//                }


                mOrderMoney.setText("￥" + mOrderInfo.getTotalFee());
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.order_payment_alipay_layout:
                mAlipayCheckbox.setChecked(true);
                mWechatCheckbox.setChecked(false);
                mPayType = 1;
                break;
            case R.id.order_payment_wechat_layout:
                mAlipayCheckbox.setChecked(false);
                mWechatCheckbox.setChecked(true);
                mPayType = 2;
                break;
            case R.id.order_payment_to_pay_bt:
                if (String.valueOf(mPayType).equalsIgnoreCase(String.valueOf(mOldPayType))
                        && String.valueOf(mPayType).equalsIgnoreCase(String.valueOf(OrderConfirmActivity.ALIPAY))){
                    aliPay(mOrderInfo);
                }else {
//                    ToastUtil.showLongToast("支付方式不同，请修改！！！");
                    ReqManager.getInstance().reqOrderPayTypeModify(reqOrderPayTypeModifyCallback, Utils.getUserToken(OrderPendingPaymentActivity.this),
                            mOrderInfo.getOrderNo(), mPayType);
                }
                break;
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
                if (orderInfoResult != null && mOrderInfo != null) {

                    if (OrderDataDetailActivity.mOrderDataDetailActivity != null) {
                        OrderDataDetailActivity.mOrderDataDetailActivity.finish();
                    }

                    mOrderInfo = orderInfoResult.getOrder();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.KEY_ORDER_INFO, mOrderInfo);
                    Utils.startIntent(OrderPendingPaymentActivity.this, OrderDataDetailActivity.class, bundle);
                    finish();

                }else {
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

    Callback<RespOrderCommit> reqOrderPayTypeModifyCallback = new Callback<RespOrderCommit>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespOrderCommit response) {
            if (onSuccess(response)) {

                RespOrderCommit.EntityOrderCommitResult orderInfoResult = response.getResult();
                if (orderInfoResult != null) {

                    if (mPayType == OrderConfirmActivity.WEIXINPAY) {
                        //商户APP工程中引入微信JAR包，调用API前，需要先向微信注册您的APPID
                        final IWXAPI api = WXAPIFactory.createWXAPI(OrderPendingPaymentActivity.this, null);

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
                        request.prepayId= orderInfoResult.getPrepayid();
                        request.packageValue = "Sign=WXPay";
                        request.nonceStr= orderInfoResult.getNoncestr();
                        request.timeStamp = orderInfoResult.getTimestamp();
                        request.sign= orderInfoResult.getSign();
                        api.sendReq(request);
                    }
                }else {
                    if (mOrderInfo != null) {
                        aliPay(mOrderInfo);
                    }
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
     *
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

        String tagStr = order.getExpert().getReferralState() == OrderConfirmActivity.ZHUANZHEN ? "转诊":"会诊";
        String productName = order.getExpert().getName() + tagStr + "诊费用:" + order.getTotalFee();
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
                PayTask alipay = new PayTask(OrderPendingPaymentActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = OrderConfirmActivity.SDK_PAY_FLAG;
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
     *
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
     *
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
     *
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
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, Constant.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

}
