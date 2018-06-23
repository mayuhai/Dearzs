package com.dearzs.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;

// add by tjt
public class ToastUtil {
	private static Toast mToastInstance;

	public static void showShortToast(String toastText) {
		if (mToastInstance == null) {
			mToastInstance = Toast.makeText(BaseApplication.getInstance(), "",
					Toast.LENGTH_SHORT);
			mToastInstance.setView(makeView());

		}
		((TextView) mToastInstance.getView().findViewById(R.id.message))
				.setText(toastText);
		mToastInstance.show();
	}

	public static void showLongToast(String toastText) {
		if (mToastInstance == null) {
			mToastInstance = Toast.makeText(BaseApplication.getInstance(), "",
					Toast.LENGTH_LONG);
			mToastInstance.setView(makeView());
		}
		((TextView) mToastInstance.getView().findViewById(R.id.message))
				.setText(toastText);
		mToastInstance.show();
	}

	public static void showShortToast(int res) {
		showShortToast(BaseApplication.getInstance().getString(res));
	}

	public static void showLongToast(int res) {
		showLongToast(BaseApplication.getInstance().getString(res));
	}
	public static View makeView(){
		LayoutInflater inflate = (LayoutInflater) BaseApplication.getInstance()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflate.inflate(R.layout.toast_content_view, null);
		return v;
	}

}
