package com.dearzs.app.chat.utils;

import android.content.Context;
import android.util.Log;

import com.dearzs.app.chat.avcontrollers.QavsdkControl;
import com.tencent.av.sdk.AVVideoCtrl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ExternalCaptureThread extends Thread {
	public volatile boolean canRun = true;
	public final static String TAG = "ExternalCaptureThread";
	private Context applicationCtx;


	public ExternalCaptureThread(Context ctx) {
		this.applicationCtx = ctx;
	}

	public void run() {
		Log.d(TAG, "WL_DEBUG fill capture frame run ");
		int frameBytesNumber = Util.yuvHigh * Util.yuvWide * 3 / 2;
		while (canRun) {

			byte[] bytesInOneFrame = new byte[frameBytesNumber];

			//可以设置成手机里面保存的yuv文件
			String fileName = Util.inputYuvFilePath;

			int byteRead = 0;
			FileInputStream in;
			try {
				in = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				in = null;
			}

			if (in == null) {
				return;
			}

			try {

				// 读入多个字节到字节数组中，byteread为一次读入的字节数
				//这里的读取数据的逻辑不严密,这种方式不能保证一定读取到想读取的字节数，这里仅为测试，因此没有修正逻辑，用了简便方式
				while ((byteRead = in.read(bytesInOneFrame)) != -1) {
					Log.d("TAG", "data len: " + byteRead);
					QavsdkControl qavsdk = QavsdkControl.getInstance();
					AVVideoCtrl avVideoCtrl = qavsdk.getAVContext()
							.getVideoCtrl();
					avVideoCtrl.fillExternalCaptureFrame(bytesInOneFrame, frameBytesNumber,
							Util.yuvWide, Util.yuvHigh, 0, Util.yuvFormat, 1);
					sleep(40);
				}
				Log.d("TAG", "transmit finish ");

			} catch (Exception e1) {
				e1.printStackTrace();
				canRun = false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			}
		}
	}
}
