package com.dearzs.app.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dearzs.app.R;
import com.dearzs.app.activity.mine.OrderDataDetailActivity;
import com.dearzs.app.activity.order.OrderConfirmActivity;
import com.dearzs.app.activity.order.OrderPendingPaymentActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.resp.RespOrderCommit;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import okhttp3.Call;
import okhttp3.Request;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler{

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, Constant.KEY_WEIXIN_APPID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();

//			Bundle bundle = new Bundle();
//			bundle.putSerializable(Constant.KEY_ORDER_INFO, OrderConfirmActivity.mCurrentOrderInfo);
//			Utils.startIntent(WXPayEntryActivity.this, OrderDataDetailActivity.class, bundle);
//			finish();
//			if (OrderConfirmActivity.mOrderConfirmActivity != null) {
//				OrderConfirmActivity.mOrderConfirmActivity.finish();
//			}

//			ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(WXPayEntryActivity.this),OrderConfirmActivity.mCurrentOrderInfo.getOrderNo());

			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					ReqManager.getInstance().reqOrderDetail(reqOrderDetailCall, Utils.getUserToken(WXPayEntryActivity.this),OrderConfirmActivity.mCurrentOrderInfo.getOrderNo());
					break;
				case BaseResp.ErrCode.ERR_COMM:
					finish();
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					finish();
					break;
				case BaseResp.ErrCode.ERR_SENT_FAILED:
					finish();
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					finish();
					break;
				case BaseResp.ErrCode.ERR_UNSUPPORT:
					finish();
					break;
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

					if (OrderConfirmActivity.mOrderConfirmActivity != null) {
						OrderConfirmActivity.mOrderConfirmActivity.finish();
					}

					if (OrderDataDetailActivity.mOrderDataDetailActivity != null) {
						OrderDataDetailActivity.mOrderDataDetailActivity.finish();
					}

					if (OrderPendingPaymentActivity.mOrderPendingPaymentActivity != null) {
						OrderPendingPaymentActivity.mOrderPendingPaymentActivity.finish();
					}

					Bundle bundle = new Bundle();
					bundle.putSerializable(Constant.KEY_ORDER_INFO, orderInfo);
					Utils.startIntent(WXPayEntryActivity.this, OrderDataDetailActivity.class, bundle);
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
}