package com.dearzs.app.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.MedicalConsultationWebViewActivity;
import com.dearzs.app.activity.family.FamilyDocListActivity;
import com.dearzs.app.activity.forum.DoctorForumActivity;
import com.dearzs.app.activity.mine.DoctorCertificationIntroduceActivity;
import com.dearzs.app.adapter.HomeSlideAdAdapter;
import com.dearzs.app.adapter.LvConsultationListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.chat.model.Conversation;
import com.dearzs.app.chat.model.EntityEvent;
import com.dearzs.app.chat.model.MessageFactory;
import com.dearzs.app.chat.model.NomalConversation;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.chat.utils.PushUtil;
import com.dearzs.app.entity.EntityBanner;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.entity.resp.RespGetBannerList;
import com.dearzs.app.entity.resp.RespGetConsultationList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.util.ViewScrollConflictUtil;
import com.dearzs.app.widget.viewflow.CircleFlowIndicator;
import com.dearzs.app.widget.viewflow.ViewFlow;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.tim.presentation.presenter.ConversationPresenter;
import com.dearzs.tim.presentation.viewfeatures.ConversationView;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 页面fragment
 */
public class HomeFragment extends BaseFragment implements
        ConversationView {
    private ScrollView mScrollView;
    private ViewFlow mViewFlowSlide;
    private LvConsultationListAdapter mListAdapter;
    private RelativeLayout mRlBuyCarSlideIndi;
    private FrameLayout mFrLaySlide;
    private List<EntityConsultation> mDataList;
    private ListView mListView;

    private RelativeLayout mRlZiXun;
    private LinearLayout mYuYueLayout, mHuiZhenShiLayout, mZhuanZhenLayout, mFamilyDoctorLayout, mShangChengLayout, mJiangTangLayout, mZiXunLayout, mVipLayout, mFengCaiLayout;
//    private TextView mTvYuYue, mTvHuiZhenShi, mTvZhuanZhen, mTvShangCheng, mTvJiangTang, mTvZiXun, mTvVip, mTvFengCai;
//    private ImageView mIvYuYue, mIvHuiZhenShi, mIvZhuanZhen, mIvShangCheng, mIvJiangTang, mIvZiXun, mIvVip, mIvFengCai;

    public HomeSlideAdAdapter mFlowViewAdapter;
    private CircleFlowIndicator mFlowIndicator;

    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationPresenter presenter;

    @Override
    protected int inflateResource() {
        return R.layout.fragment_first;
    }

    @Override
    protected void initViews(View rootView) {
        mScrollView = getView(rootView, R.id.home_scroll_view);
        mViewFlowSlide = getView(rootView, R.id.home_ad_viewpager);
        mRlBuyCarSlideIndi = getView(rootView, R.id.rl_home_slide_indi);
        mFrLaySlide = getView(rootView, R.id.frlay_slide);
        mFlowIndicator = getView(rootView, R.id.indica_buy_car);
        mListView = getView(rootView, R.id.lv_home_consultation);
        mListView.setFocusable(false);      //解决ScrollView嵌套ListView 进入界面后自动滑动到最低端问题

        mYuYueLayout = getView(rootView, R.id.home_item_yuyue_layout);
        mFamilyDoctorLayout = getView(rootView, R.id.home_item_jiatingyisheng_layout);
        mRlZiXun = getView(rootView, R.id.rl_home_zixun);
        mHuiZhenShiLayout = getView(rootView, R.id.home_item_huizhenshi_layout);
        mZhuanZhenLayout = getView(rootView, R.id.home_item_zhuanzhen_layout);
//        mShangChengLayout = getView(rootView, R.id.home_item_family_doctor_layout);
        mJiangTangLayout = getView(rootView, R.id.home_item_jiangtang_layout);
        mZiXunLayout = getView(rootView, R.id.home_item_zixun_layout);
        mVipLayout = getView(rootView, R.id.home_item_vip_layout);
        mFengCaiLayout = getView(rootView, R.id.home_item_fengcai_layout);
//        mTvYuYue = getView(rootView, R.id.tv_home_item_yuyue_title);
//        mTvHuiZhenShi = getView(rootView, R.id.tv_home_item_huizhenshi_title);
//        mTvZhuanZhen = getView(rootView, R.id.home_item_zhuanzhen_tv);
//        mTvShangCheng = getView(rootView, R.id.home_item_family_doctor_tv);
//        mTvJiangTang = getView(rootView, R.id.home_item_jiangtang_tv);
//        mTvZiXun = getView(rootView, R.id.home_item_zixun_tv);
//        mTvVip = getView(rootView, R.id.home_item_vip_tv);
//        mTvFengCai = getView(rootView, R.id.home_item_fengcai_tv);
//        mIvYuYue = getView(rootView, R.id.home_item_yuyue_iv);
//        mIvHuiZhenShi = getView(rootView, R.id.home_item_huizhenshi_iv);
//        mIvZhuanZhen = getView(rootView, R.id.home_item_zhuanzhen_iv);
//        mIvShangCheng = getView(rootView, R.id.home_item_shangcheng_iv);
//        mIvJiangTang = getView(rootView, R.id.home_item_jiangtang_iv);
//        mIvZiXun = getView(rootView, R.id.home_item_zixun_iv);
//        mIvVip = getView(rootView, R.id.home_item_vip_iv);
//        mIvFengCai = getView(rootView, R.id.home_item_fengcai_iv);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDataList != null && mDataList.size() > position) {
                    EntityConsultation consultation = mDataList.get(position);
                    if(consultation !=null){
                        MedicalConsultationWebViewActivity.startIntent(getActivity(), consultation.getId());
                    }
                }
            }
        });

        mRlZiXun.setOnClickListener(this);
        mYuYueLayout.setOnClickListener(this);
        mFamilyDoctorLayout.setOnClickListener(this);
        mHuiZhenShiLayout.setOnClickListener(this);
        mZhuanZhenLayout.setOnClickListener(this);
//        mShangChengLayout.setOnClickListener(this);
        mJiangTangLayout.setOnClickListener(this);
        mZiXunLayout.setOnClickListener(this);
        mVipLayout.setOnClickListener(this);
        mFengCaiLayout.setOnClickListener(this);
        mViewFlowSlide.setOnTouchListener(new ViewScrollConflictUtil(mScrollView));
    }

    @Override
    public void initData() {
        mListAdapter = new LvConsultationListAdapter(getActivity(), R.layout.item_lv_consultation_list, mDataList);
        mListView.setAdapter(mListAdapter);
        //服务器返回图片地址后再调用
        ReqManager.getInstance().reqBannerList(new ReqGetBannerListCallback());
        ReqManager.getInstance().reqConsultationList(new ReqGetConsultationListCallback(), 1, 3, Utils.getUserToken(getActivity()));

        presenter = new ConversationPresenter(this);
        getConversation();
    }

    public void getConversation() {
        if (presenter != null) {
            presenter.getConversation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        PushUtil.getInstance().reset();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReqManager.getInstance().reqConsultationList(new ReqGetConsultationListCallback(), 1, 3, Utils.getUserToken(getActivity()));
    }

    //获取Banner列表回调
    private class ReqGetBannerListCallback extends Callback<RespGetBannerList> {
        @Override
        public void onError(Call call, Exception e) {
//            closeProgressDialog();
        }

        @Override
        public void onResponse(RespGetBannerList response) {
//            closeProgressDialog();
            setSlideAdData(response.getResult().getList());
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
//            showProgressDialog();
        }
    }

    //获取咨询列表回调
    private class ReqGetConsultationListCallback extends Callback<RespGetConsultationList> {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespGetConsultationList response) {
            mDataList = new ArrayList<EntityConsultation>();
            mDataList.add(new EntityConsultation());
            mListAdapter.replaceAll(mDataList);
            mListView.getLayoutParams().height = Utils.getListViewHeight(mListView);
            closeProgressDialog();
            if (response != null && response.getResult() != null) {
                mDataList = response.getResult().getList();
                mListAdapter.replaceAll(mDataList);
                mListView.getLayoutParams().height = Utils.getListViewHeight(mListView);
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    }

    public void setSlideAdData(List<EntityBanner> adImageUrl) {
//        listSlideAd = resp.getData().getSlideList();

        mFlowViewAdapter = new HomeSlideAdAdapter(getActivity(), adImageUrl);
        mViewFlowSlide.setmSideBuffer(adImageUrl.size());
        mViewFlowSlide.setAdapter(mFlowViewAdapter);
        mViewFlowSlide.setFlowIndicator(mFlowIndicator);
        mViewFlowSlide.stopAutoFlowTimer();
        mViewFlowSlide.setTimeSpan(5000);
        mFlowIndicator.requestLayout();
        mFlowIndicator.invalidate();
        if (adImageUrl == null || adImageUrl.size() == 0) {
            mFrLaySlide.setVisibility(View.GONE);
        } else {
            mFrLaySlide.setVisibility(View.VISIBLE);
            if (adImageUrl.size() == 1) {
                mRlBuyCarSlideIndi.setVisibility(View.GONE);
            } else {
                mRlBuyCarSlideIndi.setVisibility(View.VISIBLE);
                mViewFlowSlide.startAutoFlowTimer(); // 启动自动播放
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {

            case R.id.frg_first_tv_confirm:
                showConfirmDialog("请确认购买信息？", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                break;
            case R.id.home_item_yuyue_layout:
//                if (Utils.isDoctoruser()) {
                    AppointmentConsultationActivity.startIntent(getActivity(), false);
//                } else {
//                    final String hotLine = PfUtils.getStr(getActivity(), Constant.DEARZS_SP, Constant.KEY_HOT_LINE, "");
////                    if(!TextUtils.isEmpty(hotLine)){
//                        ((BaseActivity)getActivity()).showConfirmDialog(getActivity(), "尊敬的用户，您好:\n" +
//                                "若您为医生用户，请先认证为医生，即可进行预约。\n" +
//                                "若您为非医生用户，请找您的主治医生代为预约，或者致电客服" + hotLine + "预约。\n" +
//                                "谢谢您的理解与支持，我们也会不断的提升使用体验。", "认证医生", "致电客服", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                DoctorCertificationIntroduceActivity.startIntent(getActivity());
//                            }
//                        }, new View.OnClickListener(){
//
//                            @Override
//                            public void onClick(View v) {
//                                if(!TextUtils.isEmpty(hotLine)){
//                                    Utils.intentDial(getActivity(), hotLine);
//                                }
//                            }
//                        });
////                    }
//                }
                break;
            case R.id.home_item_jiatingyisheng_layout:
                FamilyDocListActivity.startIntent(getActivity());
                break;
            case R.id.home_item_huizhenshi_layout:
                ConsultationRoomActivity.startIntent(getActivity());
                break;
            case R.id.home_item_zhuanzhen_layout:
                break;
//            case R.id.home_item_family_doctor_layout:
//
//                break;
            case R.id.home_item_jiangtang_layout:
                DoctorForumActivity.startIntent(getActivity());
                break;
            case R.id.home_item_zixun_layout:
                break;
            case R.id.home_item_vip_layout:
                break;
            case R.id.home_item_fengcai_layout:
                break;
            case R.id.rl_home_zixun:
                //医学咨询
                MedicalConsultationActivity.startIntent(getActivity(), new Bundle());
                break;
        }
    }

    /**
     * 初始化界面或刷新界面
     *
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        for (TIMConversation item : conversationList) {
            switch (item.getType()) {
                case C2C:
                case Group:
                    this.conversationList.add(new NomalConversation(item));
                    break;
            }
        }
    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null) {
            //adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System) {
            return;
        }
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation c = iterator.next();
            if (conversation.equals(c)) {
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        Collections.sort(conversationList);
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(identify)) {
                iterator.remove();
                //adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (Conversation conversation : conversationList) {
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                String name = info.getGroupInfo().getGroupName();
                if (name.equals("")) {
                    name = info.getGroupInfo().getGroupId();
                }
                conversation.setName(name);
                //adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        LogUtil.LogD("IM", "==unreadnum:" + getTotalUnreadNum());
        List<NomalConversation> c2cCnvList = new ArrayList<NomalConversation>();
        if (conversationList != null && conversationList.size() > 0) {
            for (Conversation cnv : conversationList) {
                if (cnv instanceof NomalConversation) {
                    if (((NomalConversation) cnv).getType() == TIMConversationType.C2C) {
                        c2cCnvList.add((NomalConversation) cnv);
                    }
                }
            }
        }
        TimManager.getInstance().setConversationList(c2cCnvList);
        EventBus.getDefault().post(new EntityEvent.ConversationChangeEvent());
    }

    private long getTotalUnreadNum() {
        long num = 0;
        for (Conversation conversation : conversationList) {
            num += conversation.getUnreadNum();
        }
        return num;
    }
}
