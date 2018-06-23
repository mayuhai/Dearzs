package com.dearzs.app.download;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dearzs.app.R;
import com.dearzs.app.util.Utils;

import java.io.File;

/**
 * 下载新闻 通知栏
 * @author mayuhai
 * 2013.1.30
 */
public class DownloadNotification {
	public static FileDownloader loader;
	protected Context mContext;
	public static boolean isDownLoadingNews;
	
    public DownloadNotification(Context mContext) {
		super();
		this.mContext = mContext;
	}

	//它用于往消息队列发送消息，当Handler被创建时会自动绑定到Handler被创建时所在的线程所绑定的消息队列
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int fileTotal;
			int downProgress;
			switch (msg.what) {
			case 1:
				fileTotal = msg.arg1;
				downProgress = msg.arg2;
				Utils.showDownloadNotification(mContext, R.mipmap.ic_launcher, mContext.getString(R.string.download_news_title), fileTotal, downProgress);
				Utils.mIsAPPUpdataing = true;
				if(downProgress == fileTotal){
					isDownLoadingNews = false;
					Toast.makeText(mContext, R.string.downlode_complete, 1).show();
					Utils.closeNotification(mContext, Utils.DOWNLOAD_NEWS_NF_ID);
					Utils.installApk(mContext, loader.saveFile);
					Utils.mIsAPPUpdataing = false;
				}
				break;

			case -1:
				Toast.makeText(mContext, R.string.downlode_error, 1).show();
				Utils.closeNotification(mContext, Utils.DOWNLOAD_NEWS_NF_ID);
				Utils.mIsAPPUpdataing = false;
				break;
			}
		}    	
    };
    
    public void downloadNews(String url) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	File downlodeFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + Utils.DOWNLOAD_DIR);
        	if (!downlodeFile.exists()) {
        		downlodeFile.mkdirs();
        	}
        	download(url, downlodeFile);
        }else{
        	Toast.makeText(mContext,  R.string.no_sdcard, 1).show();
        }
    }
    
    //对UI控件的更新只能由主线程(UI线程)负责,如果不在UI线程更新控件，更新后的值不会被重绘到屏幕上
	private void download(final String path, final File saveDir) {
		new Thread(new Runnable() {			
			public void run() {
				try {
					loader = new FileDownloader(mContext, path, saveDir, 3);
					loader.download(new DownloadProgressListener(){
						public void onDownloadSize(int size) {
							Message msg = new Message();
							msg.what = 1;
							msg.arg1 = loader.getFileSize();
							msg.arg2 = size;
							handler.sendMessage(msg);
							isDownLoadingNews = true;
							//msg.target = handler;							
						}});
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

}