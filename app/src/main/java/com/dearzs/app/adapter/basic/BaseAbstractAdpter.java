package com.dearzs.app.adapter.basic;

import android.database.DataSetObserver;
import android.widget.BaseAdapter;

public abstract class BaseAbstractAdpter extends BaseAdapter {
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}
}
