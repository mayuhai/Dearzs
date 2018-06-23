package com.dearzs.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.activity.CommonWebViewActivity;
import com.dearzs.app.entity.EntityBanner;
import com.dearzs.app.util.ImageLoaderManager;

import java.util.List;

public class HomeSlideAdAdapter extends BaseAdapter {
	private Context mContext;
	private List<EntityBanner> mList;

	public HomeSlideAdAdapter(Context context, List<EntityBanner> list) {
		this.mContext = context;
		this.mList = list;
	}

	public void changeList(List<EntityBanner> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// return mList.size();
		if (mList == null || mList.size() == 0) {
			return 0;
		} else if (mList.size() == 1) {
			return 1;
		} else {
			return 10000;
		}
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.home_slide_ad_flow_item, null);
		}
		if (mList.size() > 0) {
			final String clickUrl;
			final String imgUrl;
			final String title;
			if (mList.size() == 1){
				clickUrl = mList.get(position).getUrl();
				title = mList.get(position).getTitle();
				imgUrl = mList.get(position).getImg();
			} else {
				clickUrl = mList.get(position % mList.size()).getUrl();
				title = mList.get(position % mList.size()).getTitle();
				imgUrl = mList.get(position % mList.size()).getImg();
			}
			// SenseArticleModel model = mList.get(position);
			ImageLoaderManager.getInstance().displayImage(
					imgUrl,
					(ImageView) convertView
							.findViewById(R.id.imv_home_slide_ad));
			final int clickEvent = position % mList.size() + 1;
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(!TextUtils.isEmpty(clickUrl)){
						CommonWebViewActivity.startIntent(mContext, clickUrl, title);
					}
				}
			});
		}
		return convertView;

	}
}
