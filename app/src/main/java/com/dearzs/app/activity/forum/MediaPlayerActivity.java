package com.dearzs.app.activity.forum;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.DynamicDetailsActivity;
import com.dearzs.app.activity.communtity.ReleaseCommentActivity;
import com.dearzs.app.activity.expert.ExpertDetailsActivity;
import com.dearzs.app.adapter.GvDoctorForumListAdapter;
import com.dearzs.app.adapter.GvLectureCommentListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.utils.TimeUtil;
import com.dearzs.app.chat.utils.Util;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDoctorForumInfo;
import com.dearzs.app.entity.EntityDoctorForumLecture;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityLectureComment;
import com.dearzs.app.entity.EntityOrg;
import com.dearzs.app.entity.EntityVideo;
import com.dearzs.app.entity.resp.RespDoctorForumComments;
import com.dearzs.app.entity.resp.RespDoctorForumDetail;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.GestureController;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 视频播放页面
 */
public class MediaPlayerActivity extends BaseActivity implements
        UniversalVideoView.VideoViewCallback,
        GestureController.OnGestureControllerListener {
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private GestureController mGestureController;
    private GvLectureCommentListAdapter mLectureCommnetListAdapter;
    private XRecyclerView mLectureCommentRecyclerView;
    private int mPageIndex = 1;
    private int mSeekPosition;//视频当前观看位置
    private int mDuration;//视频时长
    private int mCachedHeight;

    private boolean mIsFullscreen;
    private ViewGroup mLayoutRoot;
    private ViewGroup mVideoLayout;
    private UniversalVideoView mVideoPlayer;
    //视频播放控制器
    private UniversalMediaController mController;
    private ViewGroup mLayoutPassedTime;
    private TextView mTvPassedTime;
    private View mHeaderView;
    private CircleImageView mDoctorPhoto;
    private TextView mDoctorName;
    private TextView mDoctorJob;
    private View mLineView;
    private TextView mDoctorHospital;
    private TextView mDoctorDepartment;
    private TextView mDoctorViewCount;
    private RatingBar mDoctorRating;
    private LinearLayout mRecommendLayout;
    private TextView mViewMore;
    private XRecyclerView mRecommendRecylerView;
    private GvDoctorForumListAdapter mRecommendAdapter;
    private LinearLayout mExpertLayout;
    private LinearLayout mPraiseLayout;
    private LinearLayout mCommentLayout;
    private TextView mTvPraiseCount;
    private TextView mTvCommentCount;
    private Drawable mImgPraised, mImgNotPraised;

    private LinearLayout mBottomLayout;

    private EntityDoctorForumLecture mEntityLecture;

    private boolean mIsPraised = false;
    private long mPraiseCount;

    private long mLectureId;
    private long mExpertId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_media_player);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mLectureId = intent.getLongExtra(Constant.KEY_LECTURE_ID, -1);
        reqData();
    }

    @Override
    public void initView() {
        super.initView();
        mBottomLayout = getView(R.id.media_player_item_bottom_layout);
        mLayoutRoot = getView(R.id.player_root);
        mVideoLayout = getView(R.id.player_video_layout);
        mVideoPlayer = getView(R.id.player_vv);
        mController = getView(R.id.player_controller);
        mLectureCommentRecyclerView = getView(R.id.rv_lecture_recommend);
        mLayoutPassedTime = getView(R.id.player_layout_passed_time);
        mTvPassedTime = getView(R.id.player_tv_passed_time);
        mCommentLayout = getView(R.id.lin_lecture_header_layout_comment_count);
        mPraiseLayout = getView(R.id.lin_lecture_header_layout_praise_count);
        mTvPraiseCount = getView(R.id.tv_lecture_header_layout_praise_count);
        mTvCommentCount = getView(R.id.tv_lecture_header_layout_comment_count);

        Resources res = getResources();
        mImgNotPraised = res.getDrawable(R.mipmap.ic_zan_normal);
        mImgPraised = res.getDrawable(R.mipmap.ic_zan_praised);
        mImgNotPraised.setBounds(0, 0, mImgNotPraised.getMinimumWidth(), mImgNotPraised.getMinimumHeight());
        mImgPraised.setBounds(0, 0, mImgPraised.getMinimumWidth(), mImgPraised.getMinimumHeight());

        mCommentLayout.setOnClickListener(this);
        mPraiseLayout.setOnClickListener(this);

        //设置视频播放控制器
        mVideoPlayer.setMediaController(mController);
        setVideoAreaSize();
        mVideoPlayer.setVideoViewCallback(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLectureCommentRecyclerView.setLayoutManager(layoutManager);
        mLectureCommentRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mLectureCommentRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mLectureCommentRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mHeaderView = LayoutInflater.from(MediaPlayerActivity.this).inflate(R.layout.lecture_recommend_headview_layout, null);
        initHeadView(mHeaderView);
        mLectureCommentRecyclerView.addHeaderView(mHeaderView);

        mLectureCommentRecyclerView.setLoadingMoreEnabled(false);
        mLectureCommentRecyclerView.setPullRefreshEnabled(false);

        mLectureCommentRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                mPageIndex++;
                ReqManager.getInstance().reqDoctorForumCommentList(reqDoctorForumCommentCallback, mPageIndex, Utils.getUserToken(MediaPlayerActivity.this), mLectureId);
            }
        });
        mGestureController = new GestureController(this);
        mGestureController.setListener(this);
        mLayoutRoot.setOnTouchListener(mGestureController);
        mLayoutRoot.setLongClickable(true);
    }

    private void initHeadView(View headerView){
        mDoctorPhoto = getView(headerView, R.id.iv_lecture_header_layout_photo);
        mDoctorName = getView(headerView, R.id.tv_lecture_header_layout_name);
        mDoctorJob = getView(headerView, R.id.tv_lecture_header_layout_user_job);
        mLineView = getView(headerView, R.id.lecture_header_layout_line);
        mDoctorHospital = getView(headerView, R.id.tv_lecture_header_layout_hospital);
        mDoctorDepartment = getView(headerView, R.id.tv_lecture_header_layout_department);
        mDoctorViewCount = getView(headerView, R.id.tv_lecture_header_layout_views);
        mDoctorRating = getView(headerView, R.id.rb_lecture_docture_rating);
        mRecommendLayout = getView(headerView, R.id.lin_dynamic_details_recomment_layout);
        mViewMore = getView(headerView, R.id.tv_lecture_header_layout_view_more_recommend);
        mExpertLayout = getView(headerView, R.id.lin_recommend_expert_layout);
        mRecommendRecylerView = getView(headerView, R.id.rv_lecture_header_recommend);
        mTvCommentCount = getView(headerView, R.id.tv_lecture_comments_count);
        mViewMore.setOnClickListener(this);
        mExpertLayout.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecommendRecylerView.setLayoutManager(layoutManager);
        mRecommendRecylerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecommendRecylerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecommendRecylerView.setLoadingMoreEnabled(false);
        mRecommendRecylerView.setPullRefreshEnabled(false);
        mRecommendAdapter = new GvDoctorForumListAdapter(MediaPlayerActivity.this, new ArrayList<EntityDoctorForumInfo>());
        mRecommendRecylerView.setAdapter(mRecommendAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_lecture_header_layout_view_more_recommend:
                finish();
                break;
            case R.id.lin_recommend_expert_layout:
                if(mExpertId > 0){
                    ExpertDetailsActivity.startIntent(MediaPlayerActivity.this, String.valueOf(mExpertId), Constant.KEY_CONSULT_NORMAL);
                }
                break;
            case R.id.lin_lecture_header_layout_comment_count:
                ReleaseCommentActivity.startIntent(MediaPlayerActivity.this, mEntityLecture.getId(), Constant.KEY_RELEASE_COMMET_TYPE_LECTURE);
                break;
            case R.id.lin_lecture_header_layout_praise_count:
                ReqManager.getInstance().reqDoctorForumPraise(reqDealPraiseCallback, mLectureId, mIsPraised ? 0 : 1, Utils.getUserToken(MediaPlayerActivity.this));
                break;
        }
    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                mCachedHeight = (int) (width * 405f / 720f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = mCachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                mVideoPlayer.requestFocus();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        mLectureId = getIntent().getLongExtra(Constant.KEY_LECTURE_ID, -1);
        mLectureCommnetListAdapter = new GvLectureCommentListAdapter(this, new ArrayList<EntityLectureComment>());
        mLectureCommentRecyclerView.setAdapter(mLectureCommnetListAdapter);
        reqData();
    }

    private void reqCommentData(){
        ReqManager.getInstance().reqDoctorForumCommentList(reqDoctorForumCommentCallback, mPageIndex, Utils.getUserToken(MediaPlayerActivity.this), mLectureId);
    }

    private void reqData(){
        ReqManager.getInstance().reqDoctorForumDetail(reqDoctorForumDetailCallback, mLectureId, Utils.getUserToken(MediaPlayerActivity.this));
    }

    private void bindViewData(EntityDoctorForumLecture lecture){
        mEntityLecture = lecture;
        List<EntityVideo> videos = lecture.getVideos();
        List<EntityDoctorForumInfo> recomments = lecture.getRecommend();
        EntityExpertInfo expertInfo = lecture.getExpert();
        mIsPraised = lecture.getIsPraise() == 1 ? true : false;
        mPraiseCount = lecture.getPraise();
        mTvPraiseCount.setCompoundDrawables(mIsPraised ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
        mTvPraiseCount.setText(String.valueOf(mEntityLecture.getPraise()));

        if(videos != null && videos.size() > 0){
            setVideoPath(videos.get(0).getUrl(), 0);
        } else {
            setVideoPath("www.baidu.com", 0);
        }

        mController.setTitle(lecture.getTitle());

        if(recomments != null){
            bindRecommentData(recomments);
        }

        if(lecture != null){
            bindHeaderViewData(lecture);
        }
    }

     private void bindRecommentData(List<EntityDoctorForumInfo> recomments){
         if(recomments == null || recomments.size() <= 0){
             mRecommendLayout.setVisibility(View.GONE);
         } else {
             mRecommendLayout.setVisibility(View.VISIBLE);
         }
         mRecommendAdapter.notifyData(recomments, true);
         LayoutUtil.setLayout(mRecommendRecylerView, DimenUtils.dip2px(MediaPlayerActivity.this, 200), DisplayUtil.getScreenWidth(MediaPlayerActivity.this) - DimenUtils.dip2px(MediaPlayerActivity.this, 16));
     }

    private void bindHeaderViewData(EntityDoctorForumLecture lecture){
        EntityOrg org = lecture.getOrg();
        EntityExpertInfo expertInfo = lecture.getExpert();
        if(expertInfo !=null){
            mExpertId = expertInfo.getId();
            mDoctorJob.setText(expertInfo.getJob());
            ImageLoaderManager.getInstance().displayImage(expertInfo.getAvatar(), mDoctorPhoto);
            if(expertInfo.getHospital() != null){
                mDoctorHospital.setText(expertInfo.getHospital().getName());
            }
            if(expertInfo.getDepartment() != null){
                mDoctorDepartment.setText(expertInfo.getDepartment().getName());
            }
            mDoctorName.setText(expertInfo.getName());
            mDoctorRating.setRating((float) expertInfo.getStar());
            mDoctorRating.setVisibility(View.VISIBLE);
            mLineView.setVisibility(View.VISIBLE);
        } else if(org != null){
            mDoctorJob.setText(org.getOrgIndustry());
            mDoctorDepartment.setText("");
            ImageLoaderManager.getInstance().displayImage(org.getImg(), mDoctorPhoto);
            mDoctorHospital.setText(org.getOrgCity());
            mDoctorName.setText(org.getOrgName());
            mDoctorRating.setVisibility(View.GONE);
            mLineView.setVisibility(View.GONE);
        }
        mDoctorViewCount.setText(String.valueOf(lecture.getViews()));
    }


    //名医讲堂列表接口回调
    Callback<RespDoctorForumDetail> reqDoctorForumDetailCallback = new Callback<RespDoctorForumDetail>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
            showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        @Override
        public void onResponse(RespDoctorForumDetail response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                hideErrorLayout();
                if(response.getResult() != null && response.getResult().getLecture() != null){
                    bindViewData(response.getResult().getLecture());
                } else {
                    showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    //名医讲堂评论列表接口回调
    Callback<RespDoctorForumComments> reqDoctorForumCommentCallback = new Callback<RespDoctorForumComments>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespDoctorForumComments response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response) && response.getResult() != null && response.getResult().getList() != null) {
                List<EntityLectureComment> list = response.getResult().getList();
                mLectureCommnetListAdapter.notifyData(list, mPageIndex == 1);
                mLectureCommentRecyclerView.setLoadingMoreEnabled(list.size() >= 10);
                mTvCommentCount.setText(String.format("(共%s条)", String.valueOf(response.getResult().getTotal())));
            } else {

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
                mEntityLecture.setIsPraise(mIsPraised ? 1 : 0);
                mPraiseCount = mIsPraised ? ++mPraiseCount : --mPraiseCount;
                mEntityLecture.setPraise(mPraiseCount);       //TODO
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

    private void setVideoPath(String path, int msec) {
        if (TextUtils.isEmpty(path)) return;
        LogUtil.LogD("VIDEO_PATH", "===>" + path);
        mVideoPlayer.setVideoPath(path);
        if (msec > 0) {
            mVideoPlayer.seekTo(msec);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.mIsFullscreen = isFullscreen;
        mGestureController.setEnable(isFullscreen);
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.GONE);
        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.mCachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        mSeekPosition = mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        mDuration = mediaPlayer.getDuration();
        if (mSeekPosition > 0) {
//            mSeekPosition = mDuration * mSeekPosition / 100;
            mVideoPlayer.seekTo(mSeekPosition);
            mSeekPosition = 0;
        }
        if (mIsFullscreen) {
            mGestureController.setData(mSeekPosition, mDuration);
        }
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onControllerFinish() {
        mLayoutPassedTime.setVisibility(View.GONE);
    }

    @Override
    public void onStartProgress(boolean isStart) {
        mLayoutPassedTime.setVisibility(isStart ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onProgressBackward() {
        mTvPassedTime.setBackgroundResource(R.mipmap.ic_backward_arrow);
    }

    @Override
    public void onProgressForward() {
        mTvPassedTime.setBackgroundResource(R.mipmap.ic_forward_arrow);
    }

    @Override
    public void onProgressTo(int progress) {
        if (mVideoPlayer != null) {
            mVideoPlayer.seekTo(progress);
        }
    }

    @Override
    public void onProgressTime(long playTime, long totalTime) {
        String time = String.format("<font color='#ffbf00'>%s</font><font color='#ffffff'> / %s</font>",
                TimeUtil.getTimeStr(playTime / 1000), TimeUtil.getTimeStr(totalTime / 1000));
        mTvPassedTime.setText(Html.fromHtml(time));
    }

    @Override
    public boolean handleBack() {
        if (this.mIsFullscreen) {
            mVideoPlayer.setFullscreen(false);
            return true;
        } else {
            return super.handleBack();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSeekPosition = mVideoPlayer.getCurrentPosition();
        mVideoPlayer.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.start();
        mPageIndex = 1;
        reqCommentData();
        reqData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null)
            mVideoPlayer.stopPlayback();
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, long lectureId) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(ctx, MediaPlayerActivity.class);
        intent.putExtra(Constant.KEY_LECTURE_ID, lectureId);
        ctx.startActivity(intent);
    }
}
