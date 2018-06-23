package com.dearzs.app.activity.communtity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dearzs.app.R;
import com.dearzs.app.adapter.GalleryBaseAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.widget.FlipGallery;


public class GalleryImageActivity extends BaseActivity {

	public static String IMAGEPATHS = "image_paths";
	public static String SELECTED_POS = "selected_pos";

	private FlipGallery mFlipGallery;
	private GalleryBaseAdapter mGalleryBaseAdapter;
	private String imagePaths[];
	private int mSelectedPos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		initUi();
		initData();
		setListener();
	}

	private void setListener() {
		mFlipGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(SELECTED_POS,
						mFlipGallery.getSelectedItemPosition());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initUi() {
		mFlipGallery = (FlipGallery) findViewById(R.id.activity_imageview_gallery);
	}

	public void initData() {
		imagePaths = getIntent().getStringArrayExtra(IMAGEPATHS);
		mSelectedPos = getIntent().getIntExtra(SELECTED_POS, 0);

		if (imagePaths == null) {
			finish();
			return;
		} else {
			mGalleryBaseAdapter = new GalleryBaseAdapter(this, imagePaths);
			mFlipGallery.setAdapter(mGalleryBaseAdapter);
			if (imagePaths.length >= mSelectedPos + 1) {
				mFlipGallery.setSelection(mSelectedPos);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
