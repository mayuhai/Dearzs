package com.dearzs.app.wxapi;

import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

	@Override
	public void onReq(BaseReq req) {
		super.onReq(req);
		LogUtil.i("onReq", "==onReq");
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			// goToGetMsg();
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			// goToShowMsg((ShowMessageFromWX.Req) req);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		super.onResp(resp);
	}

	private void goToGetMsg() {

	}
//	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//		MyLogUtil.i("goToShowMsg", "==goToShowMsg");
//
//		WXMediaMessage wxMsg = showReq.message;
//		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
//		msg.append("description: ");
//		msg.append(wxMsg.description);
//		msg.append("\n");
//		msg.append("extInfo: ");
//		msg.append(obj.extInfo);
//		msg.append("\n");
//		msg.append("filePath: ");
//		msg.append(obj.filePath);
//		MyLogUtil.i("msg", "==1>" + msg.toString());
//		MyLogUtil.i("msg", "==2>" + wxMsg.title + "," + wxMsg.toString() + ","
//				+ new String(wxMsg.thumbData));
//
//		Intent intent = getIntent();
//		intent.setAction(NewsWebAcitivity.ACTION_GET_ARTICLE_DETATL);
//		intent.putExtra(NewsWebAcitivity.RIGHT_BTN_STYLE,
//				NewsWebAcitivity.RIGHT_BTN_STYLE_COLLECT_SHARE);
//		intent.putExtra(NewsWebAcitivity.ARTICLE_INFO, "");
//
//		MyLogUtil.i("shareinfo", "==>" + getIntent());
//		NewsWebAcitivity.startIntent(this, getIntent());
//
//		finish();
//	}
}
