package com.dearzs.app.activity.communtity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.mine.PersionalDataActivity;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 发表动态界面
 * Created by luyanlong on 2016/6/5.
 */
public class ReleaseDynamicActivity extends BaseActivity implements
        IPicDelListener, GvUploadPicRecyclerViewAdapter.OnItemClickLitener {

    /**
     * 最大上传图片数量
     */
    public static final int MAX_PIC_COUNT = 4;

    public static final String REQUEST_CODE = "req_code";
    public static final String SELECT_COUNT = "select_count";
    public static final String SEL_PIC_LIST = "sel_pic_list";

    private String mDynamicContent;
    private EditText mEtContent;

    /**
     * 上传图片GridView
     */
    private RecyclerView mGvUploadPic;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_release_dynamic);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "完成");
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "发表动态");

        mGvUploadPic = getView(R.id.release_dynamic_pic_gv);
        mGvUploadPic.setHasFixedSize(true);
        mGvUploadPic.setLayoutManager(new GridLayoutManager(this, 4));

        RecyclerView.ItemAnimator mItemAnimator = new DefaultItemAnimator();
        mItemAnimator.setRemoveDuration(0);
        mGvUploadPic.setItemAnimator(mItemAnimator);

        LayoutUtil.reMeasureHeight(mGvUploadPic, (DisplayUtil.getScreenWidth(ReleaseDynamicActivity.this) - DimenUtils.dip2px(ReleaseDynamicActivity.this, 48)) / 4);

        initAdapter();
    }

    @Override
    public void initView() {
        super.initView();
        mEtContent = getView(R.id.et_release_content);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();

        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        if(TextUtils.isEmpty(userInfo.getName())){
            PersionalDataActivity.startIntent(ReleaseDynamicActivity.this);
            LogUtil.showToast(ReleaseDynamicActivity.this, "请填写昵称后再发表动态！");
            return;
        }

        mDynamicContent = mEtContent.getText().toString();
        List<PDynamicImg> imgs = new ArrayList<PDynamicImg>();
        boolean isAllSuccess = true;        //是否所有的图片都上传成功了
        if(mPicList != null ){
            for(int i=0;i<mPicList.size();i++){
                PDynamicImg img = new PDynamicImg();
                String imgUrl = mPicList.get(i).getUrl();
                String localImg = mPicList.get(i).getLocalPath();
                if(!TextUtils.isEmpty(imgUrl) && imgUrl.startsWith("http")){
                    img.setImg(imgUrl);
                    imgs.add(img);
                } else if(!TextUtils.isEmpty(localImg)){
                    isAllSuccess = false;
                }
            }
        }
        if(!isAllSuccess){
            ToastUtil.showLongToast("您还有照片没有上传成功，请等候或重传");
            return;
        }
        if(!TextUtils.isEmpty(mDynamicContent)){
            ReqManager.getInstance().reqReleaseDynamic(reqReleaseDynamicCallback, mDynamicContent, imgs, Utils.getUserToken(ReleaseDynamicActivity.this));
        } else {
            ToastUtil.showLongToast("请输入内容");
            return;
        }
    }

    //发布动态接口回调
    Callback<EntityBase> reqReleaseDynamicCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast("发布动态失败，请重试");
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                ToastUtil.showLongToast("发布动态成功");
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

    private void initAdapter() {
        // 先创建adapter
        mAdapter = new GvUploadPicRecyclerViewAdapter(ReleaseDynamicActivity.this);
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
        mGvUploadPic.setAdapter(mAdapter);

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
        new ItemTouchHelper(mCallback).attachToRecyclerView(mGvUploadPic);
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
//        doResult();
        super.onLeftBtnClick();
    }

    @Override
    public boolean handleBack() {

//        doResult();
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
        startActivityForResult(intent, mPicType);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, ReleaseDynamicActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}

