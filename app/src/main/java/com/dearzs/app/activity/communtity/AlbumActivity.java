package com.dearzs.app.activity.communtity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.AlbumAdapter;
import com.dearzs.app.adapter.GvAlbumChoiceAdapter;
import com.dearzs.app.adapter.basic.MultiItemTypeSupport;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityDirAlbum;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.util.AlbumBrowserManager;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.PopUpWinView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 相册Activity
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class AlbumActivity extends BaseActivity implements View.OnClickListener, AlbumAdapter.OnSelectChangeListener {
    /**
     * 拍照请求码
     **/
    private final int REQ_CODE_TAKE_PICTURE = 1;
    /**
     * 系统相册默认目录名称
     **/
    private final String DIR_DCIM_CAMERA = "DCIM/Camera";

    private GridView mGvAlbum;
    private TextView mBadgeView;
    private View mLayoutBadge;
    /**
     * 相册选择弹框
     **/
    private PopUpWinView mPopWin;

    /**
     * 相册选择适配器
     */
    private GvAlbumChoiceAdapter mAlbumAdapter;
    /**
     * 照片列表适配器
     */
    private AlbumAdapter mAdapter;

    /**
     * 照片列表
     */
    private ArrayList<String> mPicList;
    /**
     * 相册列表
     */
    private ArrayList<EntityDirAlbum> albumList;

    /**
     * 由UploadPicActivity传入的状态,默认为添加多张照片
     */
    private int isReplacePic;
    /**
     * 当前已选中的图片个数
     */
    private int selectCount;

    /**
     * 可以选择的最大图片数
     *
     */
    private int mMaxCount = 4;

    /**
     * 选中的图片列表
     */
    private List<String> mSelList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != AlbumBrowserManager.SCAN_OK) return;
            if (msg.obj != null) {
                HashMap<String, List<String>> mPicMap = (HashMap<String, List<String>>) msg.obj;
                if (mPicMap != null) {
                    albumList = Utils.getPicList(mPicMap);
                }
                changeDataSource(-1);
            } else {
                ToastUtil.showLongToast("访问数据权限被拒绝！~");
            }
        }
    };

    /**
     * 拍照后图片保存路径
     **/
    private String mTakePicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_album);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "相册名称");
    }

    @Override
    public void initView() {
        super.initView();
        albumList = new ArrayList<EntityDirAlbum>();
        mPicList = new ArrayList<String>();

        mGvAlbum = getView(R.id.album_gv);
        mLayoutBadge = getView(R.id.album_layout_badge);
        mBadgeView = getView(R.id.album_btn_text);

        initPopWinView();
    }

    @Override
    public void initListener() {
        super.initListener();
        mGvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPicList != null && AlbumAdapter.TYPE_TAKE_PIC_STR.equals(mPicList.get(0))) {
                    takePicture();
                }
            }
        });
        mLayoutBadge.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mMaxCount = getIntent().getIntExtra(Constant.KEY_MAX_PIC_COUNT, 4);
        selectCount = getIntent().getIntExtra(UploadPicActivity.SELECT_COUNT, 0);
        isReplacePic = getIntent().getIntExtra(UploadPicActivity.REQUEST_CODE, EntityNetPic.UPLOAD_PIC_TIP);
        if (isReplacePic == EntityNetPic.UPLOAD_PIC_TIP) {
            mLayoutBadge.setVisibility(View.VISIBLE);
        } else {
            mLayoutBadge.setVisibility(View.GONE);
        }

        MultiItemTypeSupport<String> multiItemTypeSupport = new MultiItemTypeSupport<String>() {
            @Override
            public int getLayoutId(int position, String item) {
                return AlbumAdapter.TYPE_TAKE_PIC_STR.equals(item) && position == 0 ?
                        R.layout.item_gv_album_take_photo : R.layout.item_gv_album_cus;
            }

            @Override
            public int getViewTypeCount() {
                return AlbumAdapter.TYPE_COUNT;
            }

            @Override
            public int getItemViewType(int position, String item) {
                return AlbumAdapter.TYPE_TAKE_PIC_STR.equals(item) && position == 0 ?
                        AlbumAdapter.TYPE_TAKE_PIC : AlbumAdapter.TYPE_CUS;
            }
        };
        mAdapter = new AlbumAdapter(this, mPicList, multiItemTypeSupport);

        mAdapter.setOnSelectChangeListener(this);
        mGvAlbum.setAdapter(mAdapter);
        AlbumBrowserManager.getInstance(getApplicationContext()).getAlbumPicList(mHandler);
    }

    @Override
    public void onCenterBtnClick() {
        super.onCenterBtnClick();
        if (mPopWin != null) {
            if (mPopWin.isVisiable()) {
                hidePopWin();
            } else {
                showPopWin();
            }
        }
    }

    /**
     * 更新照片列表和相册列表数据源
     */
    private void changeDataSource(int mSelAlbumPosition) {
        if (albumList == null || albumList.size() == 0) return;
        mAlbumAdapter.replaceAll(albumList);

        if (mPicList != null) {
            mPicList.clear();
            EntityDirAlbum album = null;
            if (mSelAlbumPosition < 0) {
                for (int i = 0; i < albumList.size(); i++) {
                    if (albumList.get(i).getTopImgPath().contains(DIR_DCIM_CAMERA)) {
                        album = albumList.get(i);
                        mSelAlbumPosition = i;
                        break;
                    }
                }
                if (mSelAlbumPosition < 0) {
                    mSelAlbumPosition = 0;
                }
            }
            album = albumList.get(mSelAlbumPosition);
            if (album == null) return;
            changeTitleBarCenTxt(album);
            mPicList.addAll(album.getChildList());
            mPicList.remove(AlbumAdapter.TYPE_TAKE_PIC_STR);
            mPicList.add(0, AlbumAdapter.TYPE_TAKE_PIC_STR);
            mAdapter.replaceAll(mPicList);
        }
    }

    /**
     * 初始化弹框
     */
    private void initPopWinView() {
        ListView lvType = (ListView) LayoutInflater.from(this).inflate(
                R.layout.layout_album_choice_popup, null);
        mAlbumAdapter = new GvAlbumChoiceAdapter(this, R.layout.item_gv_album_choice, albumList);
        lvType.setAdapter(mAlbumAdapter);
        mAlbumAdapter.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopWin != null) {
                    mPopWin.hidePopWin();
                }
                changeDataSource((Integer) v.getTag());
            }
        });
        mPopWin = new PopUpWinView(lvType, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                false);
        mPopWin.update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ImageLoaderManager.getInstance().cleanMemoryCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    @Override
    public boolean handleBack() {
        if (mPopWin != null && mPopWin.isShowing()) {
            mPopWin.hidePopWin();
            return true;
        }
        return super.handleBack();
    }

    /**
     * 改变TitleBar中间文字
     *
     * @param album
     */
    private void changeTitleBarCenTxt(EntityDirAlbum album) {
        if (album != null) {
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, album.getDirName() + "(" + album.getChildCount() + ")");
            setCenTxtRightImg(R.mipmap.ic_title_bottom_arr);
        }
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(ctx, AlbumActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.album_layout_badge:
                if (mSelList != null && mSelList.size() > 0) {
                    handReqCodeForResult(mSelList);
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CODE_TAKE_PICTURE) {
                try {
                    if (!TextUtils.isEmpty(mTakePicPath)) {
                        scanMedia(mTakePicPath);
                        BitmapCompressManager.getInstance().compressImage(mTakePicPath);
                        //BitmapCompressManager.getInstance(this).compress(mTakePicPath, 200);
                        if (mSelList == null) {
                            mSelList = new ArrayList<String>();
                        }
                        mSelList.add(mTakePicPath);
                        handReqCodeForResult(mSelList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onSelectChange(List<String> list) {
        if (list != null) {
            mSelList = list;
            if (isReplacePic == EntityNetPic.UPLOAD_PIC_TIP) {
                int curSelectCount = selectCount + list.size();
                if (curSelectCount > mMaxCount) {
                    ToastUtil.showLongToast("最多只能选择" + mMaxCount + "张");
                    return false;
                }
                setBadgeTip(curSelectCount);
            } else {
                handReqCodeForResult(mSelList);
            }
        }
        return true;
    }

    /**
     * 设置选择图片数目提醒
     */
    private void setBadgeTip(int count) {
        if (mBadgeView != null) {
            if (count > 0) {
                mBadgeView.setText(count + "");
                mBadgeView.setVisibility(View.VISIBLE);
            } else {
                mBadgeView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 处理requestCode
     *
     * @param selList
     */
    private void handReqCodeForResult(List<String> selList) {
        if (selList != null && selList.size() > 0) {
            //数据是使用Intent返回
            Intent intent = getIntent();
            //把返回数据存入Intent
            String[] selArr = new String[selList.size()];
            for (int i = 0; i < selList.size(); i++) {
                selArr[i] = selList.get(i);
            }
            intent.putExtra(UploadPicActivity.SEL_PIC_LIST, selArr);
            //设置返回数据
            setResult(RESULT_OK, intent);
            //关闭Activity
            finish();
        }
    }

    /**
     * 隐藏弹框
     */
    private void hidePopWin() {
        if (mPopWin != null) {
            mPopWin.hidePopWin();
            setCenTxtRightImg(R.mipmap.ic_title_bottom_arr);
        }
    }

    /**
     * 显示弹框
     */
    private void showPopWin() {
        if (mPopWin != null) {
            mPopWin.showAtDropDown(getTitleBar());
            setCenTxtRightImg(R.mipmap.ic_title_top_arr);
        }
    }

    /**
     * 打开照相机
     */
    private void takePicture() {
        String state = Environment.getExternalStorageState();
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, state)) {
            mTakePicPath = BitmapCompressManager.getInstance().getTakePhotoFilePath();
            //mTakePicPath = BitmapCompressManager.getInstance(this).getTakePhotoFilePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTakePicPath)));
            startActivityForResult(intent, REQ_CODE_TAKE_PICTURE);
        } else {
            ToastUtil.showLongToast("请确认已经插入sd卡");
        }
    }

    /**
     * 指定图片路径的扫描，使用数据库缓存得到了刷新
     */
    private MediaScannerConnection msc;

    private void scanMedia(final String filePath) {
        msc = new MediaScannerConnection(AlbumActivity.this,
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }

                    public void onMediaScannerConnected() {
                        msc.scanFile(filePath, "image/jpeg");
                    }
                });
        msc.connect();
    }
}
