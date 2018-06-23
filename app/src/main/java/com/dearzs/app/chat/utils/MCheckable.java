package com.dearzs.app.chat.utils;

import android.widget.Checkable;

public abstract class MCheckable implements Checkable {
	private boolean mChecked = false;
	
	public MCheckable() {
		this(false);
	}

	public MCheckable(boolean checked) {
		mChecked = checked;
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			onCheckedChanged();
		}
	}

	public void onCheckedChanged() {
		onCheckedChanged(mChecked);
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	protected abstract void onCheckedChanged(boolean checked);
}