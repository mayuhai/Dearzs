package com.dearzs.app.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dearzs.app.R;
import com.dearzs.app.activity.expert.ExpertDetailsActivity;
import com.dearzs.app.activity.expert.MedicalRecordActivity;
import com.dearzs.app.activity.expert.MedicalRecordModifyActivity;
import com.dearzs.app.activity.home.EditConsultResultActivity;
import com.dearzs.app.activity.mine.PatientDetailActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.adapter.ChatAdapter;
import com.dearzs.app.chat.model.CurLiveInfo;
import com.dearzs.app.chat.model.ImageMessage;
import com.dearzs.app.chat.model.Message;
import com.dearzs.app.chat.model.MessageFactory;
import com.dearzs.app.chat.model.MySelfInfo;
import com.dearzs.app.chat.model.TextMessage;
import com.dearzs.app.chat.model.VoiceMessage;
import com.dearzs.app.chat.ui.widget.ChatInput;
import com.dearzs.app.chat.ui.widget.VoiceSendingView;
import com.dearzs.app.chat.utils.FileUtil;
import com.dearzs.app.chat.utils.MediaUtil;
import com.dearzs.app.chat.utils.RecorderUtil;
import com.dearzs.app.entity.EntityConsultInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityPatientMedicalRecord;
import com.dearzs.app.entity.resp.RespStartOrEndConsult;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.dearzs.commonlib.utils.PfUtils;
import com.dearzs.tim.presentation.presenter.ChatPresenter;
import com.dearzs.tim.presentation.viewfeatures.ChatView;
import com.dearzs.tim.sdk.IMConstant;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 聊天窗口页
 */
public class ChatActivity extends BaseActivity implements ChatView {
    public final static int IDENTITY_EXPERT_KEY = 1;
    public final static int IDENTITY_DOCTIR_KEY = 2;
    public final static int IDENTITY_PATIENT_KEY = 3;
    public final static String IDENTITY_KEY = "identity";
    public final static String TYPE_KEY = "type";
    public final static String IDENTITY_TYPE_KEY = "identity_type";
    public final static String NOTIFY_FROM_KEY = "notify_from";
    public final static String STATE_KEY = "state";
    public final static String ENTITY_CONSULTATION_KEY = "entity_consultation";
    public final static String IS_CONSULT_KEY = "is_consult";   //是否是会诊室
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;

    private View mRoot;
    private XRecyclerView mRecyclerView;
    private ChatInput mInputPanel;
    private VoiceSendingView mVoiceSendingView;
    private long mConsulId;

    private LinearLayoutManager mLayoutManager;
    private List<Message> mMsgList = new ArrayList<Message>();
    private ChatAdapter mAdapter;
    private ChatPresenter mPresenter;
    private Uri mFileUri;
    private RecorderUtil mRecorder = new RecorderUtil();
    private int mState = -1;
    private int mIdentityType;
    private boolean mIsConsult;                 //标记是否是会诊室
    private String mIdentity;
    private TIMConversationType mType;          //腾讯云TYPE 标记是群组还是单聊
    private EntityConsultInfo mConsultation;
    private boolean isAddedResult = false, isAddMedicalRecord = true;

    private LinearLayout mUserInfoLayout, mPatientLayout, mExpertLayout;
    private CircleImageView mPatientPhoto, mExpertPhoto;
    private ImageView mPatientSex, mExpertCanZhuan;
    private TextView mPatientViewRecord, mExpertViewRecord;
    private TextView mPatientAge, mPatientName, mPatientAppointTime, mPatientReplyTime;
    private TextView mExpertName, mExpertJob, mExpertHosipital, mExpertDepart, mExpertReplyTime;

    public static void startIntent(Context context, String mIdentity, int mIdentityType, boolean isConsult, int state, EntityConsultInfo consultInfo, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(IDENTITY_KEY, mIdentity);
        intent.putExtra(IDENTITY_TYPE_KEY, mIdentityType);
        intent.putExtra(IS_CONSULT_KEY, isConsult);
        intent.putExtra(ENTITY_CONSULTATION_KEY, consultInfo);
        intent.putExtra(STATE_KEY, state);
        intent.putExtra(TYPE_KEY, type);
        context.startActivity(intent);
    }

    public static void startIntent(Context context, String mIdentity, boolean isConsult, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(IDENTITY_KEY, mIdentity);
        intent.putExtra(IS_CONSULT_KEY, isConsult);
        intent.putExtra(TYPE_KEY, type);
        context.startActivity(intent);
    }

    public static void startIntent(Context context, long consultId, boolean isConsult, boolean isFromOrder) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constant.KEY_CONSULTION_ID, String.valueOf(consultId));
        intent.putExtra(IS_CONSULT_KEY, isConsult);
        intent.putExtra(Constant.KEY_IS_FROM_ORDER, isFromOrder);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mRoot = GetViewUtil.getView(this, R.id.chat_root);
        mInputPanel = GetViewUtil.getView(this, R.id.chat_input_panel);
        mRecyclerView = getView(R.id.chat_list);
        mVoiceSendingView = GetViewUtil.getView(this, R.id.chat_voice_sending);
        mInputPanel.setChatView(this);

        mUserInfoLayout = getView(R.id.consult_chat_user_layout);
        mPatientLayout = getView(R.id.consult_chat_patient_info_layout);
        mExpertLayout = getView(R.id.consult_chat_expert_info_layout);
        mPatientPhoto = getView(R.id.consult_chat_expert_photo);
        mPatientSex = getView(R.id.consult_chat_patient_sex);
        mPatientName = getView(R.id.consult_chat_patient_name);
        mPatientAge = getView(R.id.consult_chat_patient_age);
        mPatientAppointTime = getView(R.id.consult_chat_patient_time);
        mPatientReplyTime = getView(R.id.consult_chat_patient_reply_time);
        mExpertPhoto = getView(R.id.consult_chat_expert_photo);
        mExpertName = getView(R.id.consult_chat_expert_name);
        mExpertJob = getView(R.id.consult_chat_expert_job);
        mExpertHosipital = getView(R.id.consult_chat_expert_hospital);
        mExpertDepart = getView(R.id.consult_chat_expert_department);
        mExpertReplyTime = getView(R.id.consult_chat_expert_reply_time);
        mExpertCanZhuan = getView(R.id.consult_chat_expert_can_zhuan);
        mPatientViewRecord = getView(R.id.consult_chat_patient_view_medicalrecord);
        mExpertViewRecord = getView(R.id.consult_chat_expert_view_medicalrecord);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mPatientLayout.setOnClickListener(this);
        mExpertLayout.setOnClickListener(this);
        mPatientViewRecord.setOnClickListener(this);
        mExpertViewRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.consult_chat_expert_info_layout:
                if (mConsultation.getExpert() != null) {
                    ExpertDetailsActivity.startIntent(ChatActivity.this, String.valueOf(mConsultation.getExpert().getId()), Constant.KEY_CONSULT_NORMAL);
                }
                break;
            case R.id.consult_chat_patient_info_layout:
                if (mConsultation.getPatient() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_PATIENT_ID, String.valueOf(mConsultation.getPatient().getId()));
                    Utils.startIntent(ChatActivity.this, PatientDetailActivity.class, bundle);
                }
                break;
            case R.id.consult_chat_patient_view_medicalrecord:
            case R.id.consult_chat_expert_view_medicalrecord:
                if (mConsultation != null && mConsultation.getPatient() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.KEY_PATIENT_INFO, mConsultation.getPatient());
                    bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_CONSULT_RESULT);
                    Utils.startIntent(ChatActivity.this, MedicalRecordActivity.class, bundle);
                }
                break;
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mInputPanel.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });

        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //如果拉到顶端读取更多消息
                mPresenter.getMessage(mMsgList.size() > 0 ? mMsgList.get(0).getMessage() : null);
            }

            @Override
            public void onLoadMore() {
            }
        });

        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mRoot.getRootView().getHeight() - mRoot.getHeight();
                if (heightDiff > 100) {
                    if (mAdapter != null && mAdapter.getItemCount() > 0) {
                        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mAdapter != null && mMsgList != null) {
            mMsgList.clear();
            mAdapter.notifyDataSetChanged();
        }
        setIntent(intent);
        initData();
    }

    @Override
    public void initData() {
        super.initData();
        boolean isFromNotice = getIntent().getBooleanExtra(Constant.KEY_IS_FROM_NOTIFY, false);
        boolean isFromOrder = getIntent().getBooleanExtra(Constant.KEY_IS_FROM_ORDER, false);       //区分是否来自订单列表或者订单详情
        mIsConsult = getIntent().getBooleanExtra(IS_CONSULT_KEY, false);

        TIMConversationType timType = null;
        if (isFromNotice || isFromOrder) {
            timType = TIMConversationType.Group;
            mConsulId = Long.valueOf(getIntent().getStringExtra(Constant.KEY_CONSULTION_ID));
            ReqManager.getInstance().reqConsultDetail(reqGetConsulDetailCall, Utils.getUserToken(ChatActivity.this), mConsulId);
        } else {
            //数据是否来自腾讯云通知栏
            boolean isFromImNotice = getIntent().getBooleanExtra(NOTIFY_FROM_KEY, false);
            int identityType = getIntent().getIntExtra(IDENTITY_TYPE_KEY, -1);
            int state = getIntent().getIntExtra(STATE_KEY, -1);
            EntityConsultInfo consultation = (EntityConsultInfo) getIntent().getSerializableExtra(ENTITY_CONSULTATION_KEY);
            String identity = isFromImNotice ? Constant.NOTIFY_IDENTITY_KEY : getIntent().getStringExtra(IDENTITY_KEY);
            timType = isFromImNotice ? Constant.NOTIFY_TYPE_KEY : (TIMConversationType) getIntent().getSerializableExtra(TYPE_KEY);
            if(consultation != null){
                mConsulId = consultation.getId();
            }
            dealConsul(mIsConsult, state, identityType, consultation, identity, timType, false);
        }
        if (timType == TIMConversationType.C2C) {
            mInputPanel.setViewType(new int[]{ChatInput.VIEW_TYPE_TEXT, ChatInput.VIEW_TYPE_EMO, ChatInput.VIEW_TYPE_VOICE, ChatInput.VIEW_TYPE_MORE_NO_FACE_VIDEO});
        } else {
            mInputPanel.setViewType(new int[]{ChatInput.VIEW_TYPE_TEXT, ChatInput.VIEW_TYPE_EMO, ChatInput.VIEW_TYPE_VOICE, ChatInput.VIEW_TYPE_MORE});
        }
    }

    /**
     *
     * @param isConsult
     * @param state
     * @param identityType
     * @param consultInfo
     * @param identity
     * @param timType
     * @param isRefreshState        是否只是刷新会诊室状态（专家修改会诊室状态后推送，点击刷新）
     */
    private void dealConsul(boolean isConsult, int state, int identityType, EntityConsultInfo consultInfo, String identity, TIMConversationType timType, boolean isRefreshState) {
        mIdentityType = identityType;
        mState = state;
        mConsultation = consultInfo;
        mIdentity = identity;
        mType = timType;

        if (isConsult) {
            if (consultInfo == null) {
                isAddedResult = false;
            } else {
                if (TextUtils.isEmpty(consultInfo.getCaseAnalysis()) && TextUtils.isEmpty(consultInfo.getDiseaseDiagnosis()) && TextUtils.isEmpty(consultInfo.getTreatmentPrograms())) {
                    isAddedResult = false;
                } else {
                    isAddedResult = true;
                }
            }
            bindConsulViewData(state, identity);
        } else {
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "在线咨询");
            mInputPanel.setVisibility(View.VISIBLE);
        }
        if(mPresenter == null || mAdapter == null){
            mPresenter = new ChatPresenter(this, identity, timType);
            mPresenter.start();
            mAdapter = new ChatAdapter(this, mMsgList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void bindConsulViewData(int state, String identity) {
        if (mState == 0) {        //未开始
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊未开始");
            mInputPanel.setVisibility(View.GONE);
            ToastUtil.showLongToast("会诊还未开始，点击右上角“开始会诊”按钮来开始会诊");
            if (mIdentityType == IDENTITY_EXPERT_KEY) {          //专家
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_BUTTON, " ");
                setTitleRightTxt("开始会诊");
                setTitleRightTxtBackground(R.mipmap.consultation_room_user_job_bg_green);
            } else {
                //donothing   患者和主治医师该状态不可进入
            }
        } else if (mState == 1) {     //会诊进行中
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "正在会诊");

            if (mIdentityType == IDENTITY_EXPERT_KEY) {
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_BUTTON, " ");
                setTitleRightTxt("结束会诊");
                setTitleRightTxtBackground(R.mipmap.consultation_room_user_job_bg_orange);
            }
            mInputPanel.setVisibility(View.VISIBLE);
        } else {            //会诊已结束
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊已结束");
            ToastUtil.showLongToast("会诊已经结束");
            mInputPanel.setVisibility(View.GONE);
            //TODO  两个boolean型的变量  需要根据服务器添加的字段来处理
            showDialog(mIdentityType, isAddMedicalRecord, isAddedResult);
        }
        bindViewDataByIdentity(mIdentityType);
    }

    private void bindViewDataByIdentity(int identity) {
        if (mConsultation == null) return;
        if (identity == IDENTITY_PATIENT_KEY) {          //患者
            EntityExpertInfo expertInfo = mConsultation.getExpert();
            if (expertInfo != null) {
                mUserInfoLayout.setVisibility(View.VISIBLE);
                mPatientLayout.setVisibility(View.GONE);
                mExpertLayout.setVisibility(View.VISIBLE);
                mExpertName.setText(expertInfo.getName());
                mExpertJob.setText(expertInfo.getJob());
                if (expertInfo.getHospital() != null) {
                    mExpertHosipital.setText(expertInfo.getHospital().getName());
                }
                if (expertInfo.getDepartment() != null) {
                    mExpertDepart.setText(expertInfo.getDepartment().getName());
                }
                mExpertReplyTime.setText("医生回复：" + Utils.getDateYmd(expertInfo.getCreateTime()));
                mExpertCanZhuan.setVisibility(expertInfo.getVisitState() == 1 ? View.VISIBLE : View.GONE);
                ImageLoaderManager.getInstance().displayImageNoCache(expertInfo.getAvatar(), mExpertPhoto);
            } else {
                mUserInfoLayout.setVisibility(View.GONE);
            }
        } else {           //主治医师 或者 专家
            EntityPatientInfo patientInfo = mConsultation.getPatient();
            if (patientInfo != null) {
                mUserInfoLayout.setVisibility(View.VISIBLE);
                mPatientLayout.setVisibility(View.VISIBLE);
                mExpertLayout.setVisibility(View.GONE);
                mPatientName.setText(patientInfo.getName());
                mPatientAge.setText("年龄：" + patientInfo.getAge());
                mPatientAppointTime.setText(Utils.getDateYmd(mConsultation.getCreateTime()));
                mPatientReplyTime.setText((mIdentityType == IDENTITY_EXPERT_KEY ? "我的回复：" : "医生回复：") + Utils.getDateYmd(mConsultation.getCtime()));
                mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
                ImageLoaderManager.getInstance().displayImageNoCache(patientInfo.getAvatar(), mPatientPhoto);
            } else {
                mUserInfoLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPresenter != null){
            mPresenter.readMessages();
        }
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null){
            mPresenter.stop();
        }
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        if (mState == 0) {      //0待会诊1会诊中2会诊结束
            if (mConsultation != null) {
                showConfirmDialog(ChatActivity.this, "是否要开始会诊", "开始会诊", "暂不开始", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReqManager.getInstance().reqStartConsult(reqDelConsultationCall, Utils.getUserToken(ChatActivity.this), mConsultation.getId());
                    }
                }, null);
            }
        } else {
            showConfirmDialog(ChatActivity.this, "是否要结束会诊", "结束会诊", "暂不结束", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReqManager.getInstance().reqEndConsult(reqDelConsultationCall, Utils.getUserToken(ChatActivity.this), Long.valueOf(mIdentity), "", "", "", "");
                }
            }, null);
        }
    }


    /**
     * //     * @param state              会诊室状态
     *
     * @param identity           是专家 主治医师还是患者
     * @param isAddMedicalRecord 是否已填写病历
     * @param isAddResult        是否已填写会诊结果
     */
    private void showDialog(final int identity, final boolean isAddMedicalRecord, final boolean isAddResult) {
        String confirmText = identity == IDENTITY_EXPERT_KEY ? isAddMedicalRecord ? "查看病历" : "填写病历" : "查看病历";
        String calcelText = identity == IDENTITY_EXPERT_KEY ? isAddResult ? "查看会诊结果" : "填写会诊结果" : "查看会诊结果";
        String constantText = identity == IDENTITY_EXPERT_KEY ? "<font color='#ff472e'>*请及时填写病历，预约转诊等操作，若退出后也可到会诊结果页面进行填写病历，预约转诊等操作</font>"
                : "<font color='#ff472e'>病历：是该会诊室结束后专家填写的；\n会诊结果：是该会诊室结束后专家填写的对患者的综合建议</font>";
        showConfirmDialog(ChatActivity.this, "会诊结束", constantText, confirmText, calcelText,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAddMedicalRecord) {       //已添加病历，所有身份都去患者病历界面
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constant.KEY_PATIENT_INFO, mConsultation.getPatient());
                            bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_CONSULT_RESULT);
                            Utils.startIntent(ChatActivity.this, MedicalRecordActivity.class, bundle);
                        } else {
                            if (identity == IDENTITY_EXPERT_KEY) {       //没有添加病历，专家需要条状到编辑病历界面，其他两种身份的人弹提示
                                EntityPatientMedicalRecord medicalRecord = new EntityPatientMedicalRecord();
                                EntityPatientInfo patientInfo = new EntityPatientInfo();
                                if (mConsultation != null) {
                                    patientInfo = mConsultation.getPatient();
                                }
                                if (patientInfo != null) {
                                    MedicalRecordModifyActivity.startIntent(ChatActivity.this, medicalRecord, patientInfo);
                                }
                            } else {        //未编写病历时，患者和主治医生不可进入会诊结果页
                                ToastUtil.showLongToast("专家正在编写病历，请稍后再进入查看");
                            }
                        }
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long expertId = mConsultation.getExpert() == null ? -1 : mConsultation.getExpert().getId();
                        boolean isCanZhuan = mConsultation.getExpert() == null ? false : mConsultation.getExpert().getReferralState() == 1 ? true : false;
                        if (identity == IDENTITY_EXPERT_KEY) {        //身份是专家可直接进入会诊结果页
                            EditConsultResultActivity.startIntent(ChatActivity.this, isAddResult, expertId, mConsultation, mConsultation.getState(), Long.valueOf(mIdentity), mIdentityType, isCanZhuan);
                        } else {        //未编写会诊结果时，患者和主治医生不可进入会诊结果页
                            if (!isAddResult) {
                                ToastUtil.showLongToast("专家正在编写会诊结果，请稍后再进入查看");
                            } else {
                                EditConsultResultActivity.startIntent(ChatActivity.this, isAddResult, expertId, mConsultation, mConsultation.getState(), Long.valueOf(mIdentity), mIdentityType, isCanZhuan);
                            }
                        }
                    }
                });
    }

    //处理会诊室接口回调
    Callback<RespStartOrEndConsult> reqDelConsultationCall = new Callback<RespStartOrEndConsult>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespStartOrEndConsult response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getConsult() != null) {
                    mState = response.getResult().getConsult().getState();
                    if (mState == 2) {
                        removeRightBtn();
                        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊结束");
                        setTitleRightTxt(mState == 1 ? "结束会诊" : "开始会诊");
                        ToastUtil.showLongToast("会诊结束");
                        mInputPanel.setVisibility(View.GONE);
                    } else if (mState == 1) {
                        setTitleRightTxt("开始会诊");
                        ToastUtil.showLongToast("会诊开始");
                        mInputPanel.setVisibility(View.VISIBLE);
                        setTitleRightTxt(mState == 1 ? "结束会诊" : "开始会诊");
                        setTitleRightTxtBackground(mState == 1 ? R.mipmap.consultation_room_user_job_bg_orange : R.mipmap.consultation_room_user_job_bg_green);
                        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "正在会诊");
                    } else {
                        mInputPanel.setVisibility(View.GONE);
                        setTitleRightTxtBackground(mState == 1 ? R.mipmap.consultation_room_user_job_bg_orange : R.mipmap.consultation_room_user_job_bg_green);
                        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊未开始");
                    }

                    //这里从正在会诊转换为会诊结束，病历和会诊结果肯定都没有填，所以这里需要传两个false
                    if (mState == 2) showDialog(mIdentityType, false, false);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    //获取会诊室详情接口回调
    Callback<RespStartOrEndConsult> reqGetConsulDetailCall = new Callback<RespStartOrEndConsult>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespStartOrEndConsult response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getConsult() != null) {
                    int state = response.getResult().getConsult().getState();
                    EntityConsultInfo consultInfo = response.getResult().getConsult();
                    int identityType = -1;
                    if (consultInfo != null && consultInfo.getPatient() != null && consultInfo.getExpert() != null && consultInfo.getCreator() != null) {
                        long currentUserId = PfUtils.getLong(ChatActivity.this, Constant.DEARZS_SP, Constant.KEY_USER_ID, -1);
                        identityType = consultInfo.getExpert().getId() == currentUserId ? ChatActivity.IDENTITY_EXPERT_KEY : consultInfo.getCreator().getId() == currentUserId ? ChatActivity.IDENTITY_DOCTIR_KEY : ChatActivity.IDENTITY_PATIENT_KEY;
                    }
                    dealConsul(true, state, identityType, consultInfo, consultInfo.getGroupId(), TIMConversationType.Group, true);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        mRecyclerView.refreshComplete();
        if (message == null) {
            mAdapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMsgList.size() == 0) {
                    mMessage.setHasTime(null);
                } else {
                    mMessage.setHasTime(mMsgList.get(mMsgList.size() - 1).getMessage());
                }
                mMsgList.add(mMessage);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        mRecyclerView.refreshComplete();
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                mMsgList.add(0, mMessage);
            } else {
                mMsgList.add(0, mMessage);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc) {

    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                mFileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(mInputPanel.getText());
        mPresenter.sendMessage(message.getMessage());
        mInputPanel.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }

    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        mVoiceSendingView.setVisibility(View.VISIBLE);
        mVoiceSendingView.showRecording();
        mRecorder.startRecording();
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        mVoiceSendingView.release();
        mVoiceSendingView.setVisibility(View.GONE);
        mRecorder.stopRecording();
        if (mRecorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(mRecorder.getTimeInterval(), mRecorder.getFilePath());
            mPresenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        //Message message = new VideoMessage(fileName);
        //mPresenter.sendMessage(message.getMessage());
    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {
    }

    @Override
    public void sendFaceVideo() {
        if (!TextUtils.isEmpty(mIdentity)) {
            int mRoomId = Integer.valueOf(mIdentity) + 100000;
            MySelfInfo.getInstance().setMyRoomNum(mRoomId);

            if (mConsultation != null && mConsultation.getExpert() != null && mConsultation.getPatient() != null && mConsultation.getCreator() != null) {
                if (mIdentityType == IDENTITY_EXPERT_KEY) {//专家
                    Intent intent = new Intent(this, LiveActivity.class);
                    intent.putExtra(IMConstant.ID_STATUS, IMConstant.HOST);
                    MySelfInfo.getInstance().setIdStatus(IMConstant.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(true);
                    CurLiveInfo.setTitle("会诊室");
                    CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                    CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                    CurLiveInfo.setHostAvator(mConsultation.getExpert().getAvatar());
                    CurLiveInfo.setPatientId(mConsultation.getPatient().getId());
                    CurLiveInfo.setDoctorId(mConsultation.getCreator().getId());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, LiveActivity.class);
                    intent.putExtra(IMConstant.ID_STATUS, IMConstant.MEMBER);
                    MySelfInfo.getInstance().setIdStatus(IMConstant.MEMBER);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(mConsultation.getExpert().getPhone());
                    CurLiveInfo.setHostName(mConsultation.getExpert().getName());
                    CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                    CurLiveInfo.setHostAvator(mConsultation.getExpert().getAvatar());
                    CurLiveInfo.setPatientId(mConsultation.getPatient().getId());
                    CurLiveInfo.setDoctorId(mConsultation.getCreator().getId());
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && mFileUri != null) {
                showImagePreview(mFileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getImageFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra(ImagePreviewActivity.ISORI_KEY, false);
                String path = data.getStringExtra(ImagePreviewActivity.PATH_KEY);
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        mPresenter.sendMessage(message.getMessage());
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.PATH_KEY, path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void updateData(String update){
        if(Constant.EVENT_UPDATE_CONSUL_STATE.equals(update) && mConsulId > 0){
            ReqManager.getInstance().reqConsultDetail(reqGetConsulDetailCall, Utils.getUserToken(ChatActivity.this), mConsulId);
        }
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                //Message message = new FileMessage(path);
                //mPresenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }
    }
}
