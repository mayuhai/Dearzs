package com.dearzs.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.File;

public class ShareUtil {
	private static ShareUtil mShareUtil;
	private static Context mContext;
	String mBody;
	String mTitle;
	String mClickUrl;
	String mOtherBody;
	UMImage mUMImgBitmap;
	
	public static ShareUtil getInstence(Context context){
		mContext = context;
		if(mShareUtil == null){
			mShareUtil = new ShareUtil();
		}
		return mShareUtil;
	}

	public void openShare(String shareTitle, String shareBody, String clickUrl, String imageUrl){
			final String mIconUrl = imageUrl;
			mBody = shareBody;
			mTitle = shareTitle;
			mClickUrl = clickUrl;
			mOtherBody = shareBody;

			if (isNumeric(mIconUrl)) {
				mUMImgBitmap = new UMImage(mContext, Integer.parseInt(mIconUrl));
			} else {
				mUMImgBitmap = new UMImage(mContext, mIconUrl);
			}

			// ** 其他平台的分享内容.除了上文中已单独设置了分享内容的微信、朋友圈、腾讯微博平台，
			// 剩下的其他平台的分享内容都为如下文字和UMImage **

			final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[] {
					SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA};
			new ShareAction((Activity) mContext).setDisplayList(displaylist)
					.setShareboardclickCallback(shareBoardlistener)
					.setCallback(umShareListener)
					.open();
	}

	private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {
		@Override
		public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
			if(snsPlatform.mPlatform.equals(SHARE_MEDIA.WEIXIN)){
				new ShareAction((Activity) mContext)
						.setPlatform(share_media)
						.withTitle(mTitle)
						.withText(mBody)
						.withTargetUrl(mClickUrl)
						.withMedia(mUMImgBitmap)
						.setCallback(umShareListener)
						.share();
			} else if(snsPlatform.mPlatform.equals(SHARE_MEDIA.WEIXIN_CIRCLE)){
				new ShareAction((Activity) mContext)
						.setPlatform(share_media)
						.withTitle(mBody)
						.withText(mBody)
						.withTargetUrl(mClickUrl)
						.withMedia(mUMImgBitmap)
						.setCallback(umShareListener)
						.share();
			} else if(snsPlatform.mPlatform.equals(SHARE_MEDIA.QQ)){
				new ShareAction((Activity) mContext)
						.setPlatform(share_media)
						.withTitle(mTitle)
						.withText(mBody)
						.withMedia(mUMImgBitmap)
						.withTargetUrl(mClickUrl)
						.setCallback(umShareListener)
						.share();
			} else if(snsPlatform.mPlatform.equals(SHARE_MEDIA.QZONE)){
				new ShareAction((Activity) mContext)
						.setPlatform(share_media)
						.withTitle(mTitle)
						.withText(mBody)
						.withTargetUrl(mClickUrl)
						.withMedia(mUMImgBitmap)
						.setCallback(umShareListener)
						.share();
			} else if(snsPlatform.mPlatform.equals(SHARE_MEDIA.SINA)){
				new ShareAction((Activity) mContext)
						.setPlatform(share_media)
						.withTargetUrl(mClickUrl)
						.withMedia(mUMImgBitmap)
						.withTitle(mTitle)
						.withText(mBody)
						.setCallback(umShareListener)
						.share();
			}
		}
	};


	public void init() {
		String qqAppId = Constant.KEY_QQ_APPID;
		String qqAppkey = Constant.KEY_QQ_APPKEY;
		String weChatAppId = Constant.KEY_WEIXIN_APPID;
		String weChatAppSecret = Constant.KEY_WEIXIN_APPSECRET;
		String sinaAppId = Constant.KEY_SINA_APPID;
		String sinaAppSecret = Constant.KEY_SINA_APPSECRET;

		//新浪微博 appkey appsecret
		PlatformConfig.setSinaWeibo(sinaAppId,sinaAppSecret);
		//微信 appid appsecret
		PlatformConfig.setWeixin(weChatAppId, weChatAppSecret);
		// QQ和Qzone appid appkey     
		PlatformConfig.setQQZone(qqAppId, qqAppkey);
	}

	private static UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
			if(!platform.equals(SHARE_MEDIA.EMAIL) && !platform.equals(SHARE_MEDIA.SMS)){
				Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT).show();
			}
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
			if(!platform.equals(SHARE_MEDIA.EMAIL) && !platform.equals(SHARE_MEDIA.SMS)){
            	Toast.makeText(mContext," 分享失败", Toast.LENGTH_SHORT).show();
			}


        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
			if(!platform.equals(SHARE_MEDIA.EMAIL) && !platform.equals(SHARE_MEDIA.SMS)) {
				Toast.makeText(mContext, " 分享取消", Toast.LENGTH_SHORT).show();
			}
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(mContext).onActivityResult(requestCode, resultCode, data);
    }
    
	public static boolean isNumeric(String str) {
		if (!Utils.isNull(str) && str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

}
