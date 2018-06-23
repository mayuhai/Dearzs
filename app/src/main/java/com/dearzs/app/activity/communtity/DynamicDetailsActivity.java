package com.dearzs.app.activity.communtity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.GvDynamicImageAdapter;
import com.dearzs.app.adapter.LvDynamicCommentListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDynamicComment;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespGetDynamicCommentList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/8/28.
 * 动态详情页面
 */
public class DynamicDetailsActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private List<EntityDynamicComment> mDataList;
    private LvDynamicCommentListAdapter mListAdapter;
    private int mPageIndex;
    private View mHeaderView;
    private EntityDynamicInfo mDynamicInfo;
    private long mDynamicId;
    private Drawable mImgPraised, mImgNotPraised;
    private HeadViewHolder mHeadViewHolder;
    private boolean mIsPraised = false;
    private long mPraiseCount;
    private TextView mTvPraiseCount;
    private TextView mTvCommentCount;
    private LinearLayout mPraiseCountLayout;
    private LinearLayout mCommentCountLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_dynamic_details);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "动态详情");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    public void initView() {
        super.initView();
        mCommentCountLayout = getView(R.id.lin_community_dynamic_comment_count);
        mTvCommentCount = getView(R.id.tv_community_dynamic_comment_count);
        mPraiseCountLayout = getView(R.id.lin_community_dynamic_praise_count);
        mTvPraiseCount = getView(R.id.tv_community_dynamic_praise_count);

        mRecyclerView = getView(R.id.dynamic_details_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DynamicDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);
        mHeaderView = LayoutInflater.from(DynamicDetailsActivity.this).inflate(R.layout.item_lv_communtity_dynamic_list, null);
        initHeadView();
        mRecyclerView.addHeaderView(mHeaderView);

        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(true);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPageIndex = 1;
                reqData(mPageIndex);
            }

            @Override
            public void onLoadMore() {
                reqData(++mPageIndex);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        Resources res = getResources();
        mImgNotPraised = res.getDrawable(R.mipmap.ic_zan_normal);
        mImgPraised = res.getDrawable(R.mipmap.ic_zan_praised);
        mImgNotPraised.setBounds(0, 0, mImgNotPraised.getMinimumWidth(), mImgNotPraised.getMinimumHeight());
        mImgPraised.setBounds(0, 0, mImgPraised.getMinimumWidth(), mImgPraised.getMinimumHeight());

        if(getIntent() != null){
            mDynamicInfo = (EntityDynamicInfo) getIntent().getSerializableExtra(Constant.KEY_DYNAMIC_INFO);
            mDynamicId = getIntent().getLongExtra(Constant.KEY_DYNAMIC_ID, -1);
        }

        bindHeaderView(mHeadViewHolder, mDynamicInfo);

        mListAdapter = new LvDynamicCommentListAdapter(DynamicDetailsActivity.this, mDataList = new ArrayList<EntityDynamicComment>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    private void initHeadView(){
        mHeadViewHolder = new HeadViewHolder();
        mHeadViewHolder.mLayout = GetViewUtil.getView(mHeaderView, R.id.communitity_dynamic_list_item);
        mHeadViewHolder.mBottomLayout = GetViewUtil.getView(mHeaderView, R.id.dynamic_item_bottom_layout);
        mHeadViewHolder.mUserPhoto = GetViewUtil.getView(mHeaderView, R.id.tv_community_dynamic_photo);
        mHeadViewHolder.mUserJob = GetViewUtil.getView(mHeaderView, R.id.tv_community_dynamic_user_job);
        mHeadViewHolder.mName = GetViewUtil.getView(mHeaderView, R.id.tv_community_dynamic_name);
        mHeadViewHolder.mContent = GetViewUtil.getView(mHeaderView, R.id.tv_community_dynamic_content);
        mHeadViewHolder.mTime = GetViewUtil.getView(mHeaderView, R.id.tv_community_dynamic_time);
        mHeadViewHolder.mTvCommentCount = GetViewUtil.getView(mHeaderView, R.id.tv_dynamic_details_comments_count);
        mHeadViewHolder.mCommentTitleLayout = GetViewUtil.getView(mHeaderView, R.id.lin_dynamic_details_comment_title_layout);
        mHeadViewHolder.mRecycleView = GetViewUtil.getView(mHeaderView, R.id.lin_community_dynamic_img_layout);
        mHeadViewHolder.mDelImg = GetViewUtil.getView(mHeaderView, R.id.iv_delect_dynamic);

        mHeadViewHolder.mBottomLayout.setVisibility(View.GONE);
        mHeadViewHolder.mCommentTitleLayout.setVisibility(View.VISIBLE);
    }

    private class HeadViewHolder{
        private LinearLayout mLayout;
        private LinearLayout mBottomLayout;
        private ImageView mUserPhoto;
        private TextView mName;
        private TextView mUserJob;
        private TextView mContent;
        private TextView mTime;
        private LinearLayout mCommentTitleLayout;
        private TextView mTvCommentCount;
        private XRecyclerView mRecycleView;
        private ImageView mDelImg;

        public void bindData(EntityDynamicInfo entity) {
            if (entity == null || entity.getImages() == null || entity.getImages().size() == 0) {
                mRecycleView.setVisibility(View.GONE);
                return;
            } else {
                mRecycleView.setVisibility(View.VISIBLE);
            }
            int imageSize = entity.getImages().size();
            int columns = imageSize > 3 ? 2 : imageSize;     //小于三张，有几张就就列，否则（4张） 2列
            int line = imageSize > 3 ? 2 : 1;

            GridLayoutManager layoutManager = new GridLayoutManager(DynamicDetailsActivity.this, columns);
            mRecycleView.setLayoutManager(layoutManager);

            //100 位行高
            LayoutUtil.setLayout(mRecycleView, DimenUtils.dip2px(DynamicDetailsActivity.this, 80) * line , DimenUtils.dip2px(DynamicDetailsActivity.this, 85) * columns);
            mRecycleView.setAdapter(new GvDynamicImageAdapter(DynamicDetailsActivity.this, entity.getImages()));
        }
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().getDynamicCommentList(reqCommentListCall, mDynamicId, pageIndex, Utils.getUserToken(DynamicDetailsActivity.this));
    }

    private void bindHeaderView(HeadViewHolder holder, EntityDynamicInfo entity){
        if (entity != null) {
            EntityDynamicInfo.EntityDynamicUser user = entity.getUser();
            if (user != null) {
                holder.mName.setText(user.getName());
                holder.mUserJob.setText(TextUtils.isEmpty(user.getJob()) ? "" : user.getJob());
                ImageLoaderManager.getInstance().displayImage(user.getAvatar(), holder.mUserPhoto);
            }
            holder.mContent.setText(entity.getContent());
//            mTvCommentCount.setText(entity.getComments() + "");
            mTvPraiseCount.setText(entity.getPraise() + "");
            mTvPraiseCount.setCompoundDrawables(entity.getIsPraise() == 1 ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
            holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            holder.bindData(entity);
            EntityUserInfo currentUserInfo = BaseApplication.getInstance().getUserInfo();
            if(currentUserInfo != null && user != null){
                holder.mDelImg.setVisibility(currentUserInfo.getId() == user.getId() ? View.VISIBLE : View.INVISIBLE);
            }
            setListener(entity, holder);
        }
    }

    private void setListener(final EntityDynamicInfo entity, final HeadViewHolder holder) {
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item点击事件
            }
        });

        holder.mDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除动态处理
                showConfirmDialog(DynamicDetailsActivity.this, "确定要删除您的这条动态吗？", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ReqManager.getInstance().reqDelDynamicPraise(reqDelDynamicCallback, Utils.getUserToken(DynamicDetailsActivity.this), entity.getId());
                    }
                }, null);
            }
        });

        mPraiseCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPraised = entity.getIsPraise() == 1;
                mPraiseCount = entity.getPraise();
                //点赞处理
                ReqManager.getInstance().reqDealDynamicPraise(reqDealPraiseCallback, Utils.getUserToken(DynamicDetailsActivity.this), entity.getId(), mIsPraised ? 0 : 1);
            }
        });

        mCommentCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReleaseCommentActivity.startIntent(DynamicDetailsActivity.this, entity.getId(), Constant.KEY_RELEASE_COMMET_TYPE_DYNAMIC);
            }
        });
    }


    //删除动态接口回调
    Callback<EntityBase> reqDelDynamicCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                ToastUtil.showShortToast("删除动态成功");
                finish();
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    //赞和取消赞处理接口回调
    Callback<EntityBase> reqDealPraiseCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                mIsPraised = !mIsPraised;
                mDynamicInfo.setIsPraise(mIsPraised ? 1 : 0);
                mPraiseCount = mIsPraised ? ++mPraiseCount : --mPraiseCount;
                mDynamicInfo.setPraise(mPraiseCount);
                mTvPraiseCount.setCompoundDrawables(mIsPraised ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
                mTvPraiseCount.setText(mPraiseCount + "");
                String toast = mIsPraised ? "点赞成功" : "取消赞成功";
                ToastUtil.showShortToast(toast);
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    //动态评论列表接口回调
    Callback<RespGetDynamicCommentList> reqCommentListCall = new Callback<RespGetDynamicCommentList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(final RespGetDynamicCommentList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHeadViewHolder.mTvCommentCount.setText("(" + response.getResult().getTotal() + ")");
                        }
                    });
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    @Subscribe
    public void handleEventBus(String msg){
        if(!TextUtils.isEmpty(msg) && "delete".equals(msg)){
            mPageIndex = 1;
            reqData(mPageIndex);
        }
    }


    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, long dynamic, EntityDynamicInfo entity) {
        Intent intent = new Intent();
        intent.setClass(ctx, DynamicDetailsActivity.class);
        intent.putExtra(Constant.KEY_DYNAMIC_ID, dynamic);
        intent.putExtra(Constant.KEY_DYNAMIC_INFO, entity);
        ctx.startActivity(intent);
    }
}
