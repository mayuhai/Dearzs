package com.dearzs.app.download;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dearzs.app.R;
import com.dearzs.app.util.Utils;

import java.io.File;

/**22-08
 * 下载
 * @author mayuhai
 * 2013.1.30
 */
public class DownlodeActivity extends Activity {
	protected ProgressBar downloadbar;
	protected TextView resultView;
	protected FileDownloader loader;
    protected Button cancellDownloadBtn;
    //它用于往消息队列发送消息，当Handler被创建时会自动绑定到Handler被创建时所在的线程所绑定的消息队列
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int fileTotal;
			int downProgress;
			switch (msg.what) {
			case 1:
				fileTotal = downloadbar.getMax();
				downProgress = downloadbar.getProgress();
				downloadbar.setProgress(msg.getData().getInt("size"));//把当前已经下载的数据长度设置为进度条的当前刻度
				float num = (float)downProgress / (float)fileTotal;
				int result = (int)(num * 100);
				resultView.setText(result + "%");
				Utils.mIsAPPUpdataing = true;
				if(downProgress == fileTotal){
					Toast.makeText(DownlodeActivity.this, R.string.downlode_complete, Toast.LENGTH_LONG).show();
					Utils.closeNotification(DownlodeActivity.this, Utils.DOWNLOAD_NEWS_NF_ID);
					Utils.installApk(DownlodeActivity.this, loader.saveFile);
					Utils.mIsAPPUpdataing = false;
				}
				break;

			case -1:
				Toast.makeText(DownlodeActivity.this, R.string.downlode_error, Toast.LENGTH_LONG).show();
				Utils.closeNotification(DownlodeActivity.this, Utils.DOWNLOAD_NEWS_NF_ID);
				Utils.mIsAPPUpdataing = false;
				break;
			}
		}    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        downloadbar = (ProgressBar) findViewById(R.id.downloadbar);
        resultView = (TextView) findViewById(R.id.result);
        cancellDownloadBtn = (Button) findViewById(R.id.cancell_download);
        cancellDownloadBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Utils.closeNotification(DownlodeActivity.this, Utils.DOWNLOAD_NEWS_NF_ID);
				if (loader != null) {
					loader.stopFileloader();
					loader = null;
				}
				finish();
			}
		});
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	File downlodeFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + Utils.DOWNLOAD_DIR);
        	if (!downlodeFile.exists()) {
        		downlodeFile.mkdirs();
        	}
        	download(null, downlodeFile);
        }else{
        	Toast.makeText(DownlodeActivity.this,  R.string.no_sdcard, Toast.LENGTH_LONG).show();
        }
    }
    
    //对UI控件的更新只能由主线程(UI线程)负责,如果不在UI线程更新控件，更新后的值不会被重绘到屏幕上
	private void download(final String path, final File saveDir) {
		new Thread(new Runnable() {			
			public void run() {
				try {
//					loader = new FileDownloader(DownlodeActivity.this, path, saveDir, 3);
					loader = DownloadNotification.loader;
					downloadbar.setMax(loader.getFileSize());//设置进度条的最大刻度为文件的大小
					loader.download(new DownloadProgressListener(){
						public void onDownloadSize(int size) {
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);
							handler.sendMessage(msg);
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