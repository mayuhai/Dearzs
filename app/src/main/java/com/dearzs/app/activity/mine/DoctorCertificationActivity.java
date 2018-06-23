package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.AlbumActivity;
import com.dearzs.app.activity.communtity.GalleryImageActivity;
import com.dearzs.app.adapter.GvUploadPicRecyclerViewAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.base.IPicDelListener;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.entity.EntityPicBase;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.PDynamicImg;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.dearzs.commonlib.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Lyl
 * 医生认证界面
 */
public class DoctorCertificationActivity extends BaseActivity implements IPicDelListener, GvUploadPicRecyclerViewAdapter.OnItemClickLitener {
    private Button mSubmit;
    /**
     * 最大上传图片数量
     */
    public static final int MAX_PIC_COUNT = 4;

    public static final String REQUEST_CODE = "req_code";
    public static final String SELECT_COUNT = "select_count";
    public static final String SEL_PIC_LIST = "sel_pic_list";

    /**
     * 上传图片GridView
     */
    private RecyclerView mRecylerView;
    /**
     * 视图对应的乱配器
     */
    private GvUploadPicRecyclerViewAdapter mAdapter;

    /**
     * 传到下个页面的requestCode,取值为EntityNetPic中的常量
     */
    public int mPicType;
    /**
     * 当前GridView的position
     */
    private int mCurPos;

    /**
     * 对应的图片列表
     */
    private ArrayList<EntityNetPic> mPicList;
    /**
     * 显示最后面的“添加照片”按钮
     */
    private EntityNetPic mEntityPicTip;
    private TextView mViewDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_doctor_certification_introduce);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "医生认证");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mSubmit = getView(R.id.bt_submit);
        mViewDemo = getView(R.id.view_sample_photo);
        mRecylerView = getView(R.id.doctor_certification_recylerview);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(this, 4));

        RecyclerView.ItemAnimator mItemAnimator = new DefaultItemAnimator();
        mItemAnimator.setRemoveDuration(0);
        mRecylerView.setItemAnimator(mItemAnimator);

        LayoutUtil.reMeasureHeight(mRecylerView, (DisplayUtil.getScreenWidth(DoctorCertificationActivity.this) - DimenUtils.dip2px(DoctorCertificationActivity.this, 48)) / 4);

        mSubmit.setOnClickListener(this);
        mViewDemo.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        initAdapter();
    }


    private void initAdapter() {
        // 先创建adapter
        mAdapter = new GvUploadPicRecyclerViewAdapter(DoctorCertificationActivity.this);
        // 直接获取图片操作对列(因拿到的是指针,所以此处操作,等于直接修改集合)
        //TODO  假数据
//        mPicList = (ArrayList<EntityPicBase>) BaseApplication.getInstance().getPicList();
        mPicList = new ArrayList<EntityNetPic>();
        // 生成提示图片
        if (mEntityPicTip == null) {
            mEntityPicTip = new EntityNetPic();
            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
            mPicList.add(mEntityPicTip);
        }
        mAdapter.replaceAll(mPicList);
        mAdapter.setDelListener(this);
        mAdapter.setOnItemClickLitener(this);
        mRecylerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                0) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position

                // 选中最后一个“添加更多”时，，不处理移动
                if (!mAdapter.isSupportDrag(fromPosition)
                        || !mAdapter.isSupportDrag(toPosition)) {
                    return false;
                }

                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mPicList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mPicList, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };
        new ItemTouchHelper(mCallback).attachToRecyclerView(mRecylerView);
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
    public void onLeftBtnClick() {

        doResult();
        super.onLeftBtnClick();
    }

    @Override
    public boolean handleBack() {

        doResult();
        return super.handleBack();
    }

    @Override
    protected void onActivityResult(int mPicType, int resultCode, Intent data) {
        super.onActivityResult(mPicType, resultCode, data);

        this.mPicType = mPicType;
        if (data == null) {
            return;
        }
        //得到新Activity 关闭后返回的数据
        dealResultData(data.getStringArrayExtra(SEL_PIC_LIST));
    }

    /**
     * 处理返回的结果
     *
     * @param selList
     */
    private void dealResultData(String[] selList) {
        if (selList != null && selList.length > 0 && mPicList != null) {
            for (String sel : selList) {
                LogUtil.LogD("selList", "==选中的图片：" + sel);
            }
            switch (mPicType) {
                // 添加照片,则加入到对列末尾
                case EntityNetPic.UPLOAD_PIC_TIP:
                    for (int i = 0; i < selList.length; i++) {
                        EntityNetPic pic = new EntityNetPic();
                        pic.setLocalPath(selList[i]);
                        mPicList.add(pic);
                    }
                    break;
                default:
                    // 替换照片,直接修改指定位置的图标
                    String path = selList[0];
                    EntityNetPic item = (EntityNetPic) mPicList.get(mCurPos);
                    if (item == null) return;
                    item.setLocalPath(path);
                    break;
            }
            uploadAdapter();
        }
    }


    //医生认证
    Callback<EntityBase> reqCertificationDoctorCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast("上传图片失败，请重试");
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
                userInfo.setState(EntityUserInfo.VERIFY_ING);
                if (PersionalDataActivity.mPersionalDataActivity != null) {
                    PersionalDataActivity.mPersionalDataActivity.finish();
                }

                if (DoctorCertificationIntroduceActivity.mDoctorCertificationIntroduceActivity != null) {
                    DoctorCertificationIntroduceActivity.mDoctorCertificationIntroduceActivity.finish();
                }

                BaseApplication.getInstance().setUserInfo(userInfo);
                ToastUtil.showLongToast("发布成功，请等待审核");
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    /**
     * 动态更新列表
     */
    private void uploadAdapter() {
        if (mPicList.contains(mEntityPicTip)) {
            mPicList.remove(mEntityPicTip);
        }
        if (mPicList.size() < MAX_PIC_COUNT) {
            mPicList.add(mEntityPicTip);
        }
        if (mAdapter != null) {
            mAdapter.replaceAll(mPicList);
        }
    }

    private void doResult() {
        if (mPicList != null) {
            mPicList.remove(mEntityPicTip);
        }
        setResult(RESULT_OK, getIntent());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_submit:
                String[] imgs = null;
//                List<PDynamicImg> imgs = new ArrayList<PDynamicImg>();
                boolean isAllSuccess = true;        //是否所有的图片都上传成功了
                if (mPicList != null) {
                    imgs = new String[mPicList.size()];
                    for (int i = 0; i < mPicList.size(); i++) {
                        PDynamicImg img = new PDynamicImg();
                        String imgUrl = mPicList.get(i).getUrl();
                        String localImg = mPicList.get(i).getLocalPath();
                        if (!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")) {
                            img.setImg(imgUrl);
                            imgs[i] = imgUrl;
                        } else if (!TextUtils.isEmpty(localImg)) {
                            isAllSuccess = false;
                        }
                    }
                }
                if (!isAllSuccess) {
                    ToastUtil.showLongToast("您还有图片没有上传成功，所有图片都上传成功后再提交");
                    return;
                }
                ReqManager.getInstance().reqCertificationDoctor(reqCertificationDoctorCallback, Utils.getUserToken(DoctorCertificationActivity.this), imgs);
                break;
            case R.id.view_sample_photo:
                Intent intent = new Intent(DoctorCertificationActivity.this,
                        GalleryImageActivity.class);
                intent.putExtra(GalleryImageActivity.IMAGEPATHS, imageList);
                intent.putExtra(GalleryImageActivity.SELECTED_POS, 0);
                startActivity(intent);
                break;

        }
    }

    //    String[] imageList = {"" + R.drawable.ic_mine_doctor_certificationl_demo_one
//            , "" + R.drawable.ic_mine_doctor_certificationl_demo_two
//            , "" + R.drawable.ic_mine_doctor_certificationl_demo_three
//            , "" + R.drawable.ic_mine_doctor_certificationl_demo_four};
    String[] imageList = {
            "drawable://" + R.mipmap.ic_mine_doctor_certificationl_demo_one
            , "drawable://" + R.mipmap.ic_mine_doctor_certificationl_demo_two
            , "drawable://" + R.mipmap.ic_mine_doctor_certificationl_demo_three
            , "drawable://" + R.mipmap.ic_mine_doctor_certificationl_demo_four
    };

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, DoctorCertificationActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onPicDel(Object pic) {
        if (pic == null) return;

        // 根据对象,获取索引位置
        int position = -1;
        if (mPicList != null) {
            position = mPicList.indexOf(pic);
        }
        // 若存在则动态移除
        if (mAdapter != null && position != -1) {
            mAdapter.notifyItemRemoved(position);
        }
        // 最后从列表中移除
        if (position != -1) {
            mPicList.remove(pic);
        }

        // 删除后动态添加“添加更多”
        if (!mPicList.contains(mEntityPicTip)) {
            mPicList.add(mEntityPicTip);
            mAdapter.notifyItemInserted(mPicList.size() - 1);
        }
    }

    private int picListSize() {
        if (mPicList == null || mPicList.isEmpty()) {
            return 0;
        }
        if (mPicList.contains(mEntityPicTip)) {
            return mPicList.size() - 1;
        } else {
            return mPicList.size();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mPicList == null || mPicList.size() <= 0) return;

        EntityPicBase item = mPicList.get(position);
        if (item == null) return;

        // 点击了哪个位置的视图
        mCurPos = position;
        mPicType = item.getUpload_state();
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra(REQUEST_CODE, mPicType);
        intent.putExtra(SELECT_COUNT, picListSize());
        intent.putExtra(SEL_PIC_LIST, mPicList);
        startActivityForResult(intent, mPicType);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }
}
