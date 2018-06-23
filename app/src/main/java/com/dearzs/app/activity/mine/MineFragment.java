package com.dearzs.app.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.LoginActivity;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.CustomCellViewWithImage;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 子页面fragment
 */
public class MineFragment extends BaseFragment {
    private LinearLayout mTitleLayout;
    private TextView mUserName;
    private TextView mUserYue;
    private CustomCellViewWithImage mPatientLibrary;
    private CustomCellViewWithImage mMyOrder;
    private CustomCellViewWithImage mWallet;
    private CustomCellViewWithImage mVisitSetting;
    private CustomCellViewWithImage mTransferString;
    private CustomCellViewWithImage mCollection;
    private CustomCellViewWithImage mAbout;
    private CustomCellViewWithImage mBackCard;
    private CustomCellViewWithImage mQrCode;
    private CustomCellViewWithImage mHomeDoc;
    private CircleImageView mUserPhoto;
    private Button mLoginOut;
    private TextView mBtAuth;

    private EntityUserInfo mModifyedUserInfo;

    private String mUserId;

    //类型，1用户2医生
    private int mUserType;

    private int mVerifystate;

    public static final int RG_VISIT_REQUEST = 0;      // 判断visitor回调函数的值
    public static final int RG_TRANSFER_REQUEST = 1;    // 判断回调函数的值

    @Override
    protected int inflateResource() {
        return R.layout.fragment_mine;
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    public void initData() {
        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        mModifyedUserInfo = new EntityUserInfo();
        if (userInfo != null) {
            if(TextUtils.isEmpty(userInfo.getQr()) || TextUtils.isEmpty(userInfo.getInviteCode())){
                mQrCode.setVisibility(View.GONE);
            } else {
                mQrCode.setVisibility(View.VISIBLE);
            }

            mModifyedUserInfo.setUserVisits(userInfo.getUserVisits());
            String userName = userInfo.getName();
            if (TextUtils.isEmpty(userName)) {
                userName = userInfo.getPhone();
            }
            mUserName.setText(userName);
            mUserYue.setText(userInfo.getBalance() + "￥");
            mWallet.setDesc(userInfo.getBalance() + "￥");
            ImageLoaderManager.getInstance().displayImage(userInfo.getAvatar(), mUserPhoto);

            int visitState = userInfo.getVisitState() == null ? 0 : userInfo.getVisitState();
            if(visitState == 1) {
                mVisitSetting.setDesc("可会诊:" + userInfo.getVisitMoney() + "￥");
            }else {
                mVisitSetting.setDesc("不可会诊");
            }

            int referralState = userInfo.getReferralState() == null ? 0 : userInfo.getReferralState();
            if(referralState == 1) {
                mTransferString.setDesc("可转诊:" + userInfo.getReferralMoney() + "￥");
            }else {
                mTransferString.setDesc("不可转诊");
            }

            mUserType = EntityUserInfo.NORMALUSER;
            if (userInfo != null) {
                mUserType = userInfo.getType();

            }

            if (mUserType == EntityUserInfo.DOCTORUSER) {//如果是医生
                mBtAuth.setVisibility(View.INVISIBLE);
            }else {
                mVisitSetting.setDesc("");
                mTransferString.setDesc("");
            }

            mVerifystate = userInfo.getState();

            if (mVerifystate == EntityUserInfo.WAIT_VERIFY) {//0待审核，可以提交
                mBtAuth.setVisibility(View.VISIBLE);
                mBtAuth.setText("认证医生");
            }else if (mVerifystate == EntityUserInfo.VERIFY_SUCC) {//1审核通过，不可再次提交
                mBtAuth.setVisibility(View.INVISIBLE);
                mBtAuth.setText("认证成功");
            }else if (mVerifystate == EntityUserInfo.VERIFY_FAIL) {//2审核失败，可以再次提交
                mBtAuth.setVisibility(View.VISIBLE);
                mBtAuth.setText("认证失败");
            }else if (mVerifystate == EntityUserInfo.VERIFY_ING) {//3审核中，不可提交
                mBtAuth.setVisibility(View.VISIBLE);
                mBtAuth.setText("认证审核中");
                mBtAuth.setTextColor(getResources().getColor(R.color.orange_txt));
            }
        }
    }

    @Override
    protected void initViews(View rootView) {
        mUserName = getView(rootView, R.id.tv_persional_user_name);
        mTitleLayout = getView(rootView, R.id.mine_title_layout);
        mUserYue = getView(rootView, R.id.tv_persional_yue);
        mUserPhoto = getView(rootView, R.id.iv_persional_user_photo);
        mPatientLibrary = getView(rootView, R.id.iv_persional_patient_library);
        mMyOrder = getView(rootView, R.id.iv_persional_user_order);
        mWallet = getView(rootView, R.id.iv_persional_user_wallet);
//        mPhoneNum = getView(rootView, R.id.iv_persional_user_photo);
        mVisitSetting = getView(rootView, R.id.iv_persional_visit_setting);
        mTransferString = getView(rootView, R.id.iv_persional_transfer_setting);
        mCollection = getView(rootView, R.id.iv_persional_user_collection);
        mAbout = getView(rootView, R.id.iv_persional_user_about);
        mBackCard = getView(rootView, R.id.iv_persional_back_card);
        mQrCode = getView(rootView, R.id.iv_persional_user_qr_code);
        mHomeDoc = getView(rootView, R.id.iv_persional_home_doc);
        mLoginOut = getView(rootView, R.id.bt_persional_logout);
        mBtAuth = getView(rootView, R.id.bt_doctor_certification);

        mPatientLibrary.setOnClickListener(this);
//        mPhoneNum.setOnClickListener(this);
        mWallet.setOnClickListener(this);
        mMyOrder.setOnClickListener(this);
        mVisitSetting.setOnClickListener(this);
        mTransferString.setOnClickListener(this);
        mCollection.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mQrCode.setOnClickListener(this);
        mHomeDoc.setOnClickListener(this);
        mBackCard.setOnClickListener(this);
        mTitleLayout.setOnClickListener(this);
        mLoginOut.setOnClickListener(this);
        mBtAuth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();

        int viewId = v.getId();
        switch (viewId) {
            case R.id.mine_title_layout:
                PersionalDataActivity.startIntent(getActivity());
                break;
            case R.id.iv_persional_patient_library:
                Utils.startIntent(getActivity(), PatientLibraryActivity.class, new Bundle());
                break;
            case R.id.iv_persional_user_order:
                OrderDataListActivity.startIntent(getActivity(), new Bundle());
                break;
            case R.id.iv_persional_user_wallet:
                MyWalletActivity.startIntent(getActivity(), new Bundle());
                break;
            case R.id.iv_persional_visit_setting:

                if (mVerifystate == EntityUserInfo.WAIT_VERIFY) {//0待审核，可以提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_SUCC) {//1审核通过，不可再次提交
                    Intent intent = new Intent(getActivity(), VisitCostSettingActivity.class);
                    intent.putExtra("userInfo", userInfo);
                    getActivity().startActivityForResult(intent, MineFragment.RG_VISIT_REQUEST);
                }else if (mVerifystate == EntityUserInfo.VERIFY_FAIL) {//2审核失败，可以再次提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_ING) {//3审核中，不可提交
                    ToastUtil.showShortToast("认证审核中，请耐心等待！！！");
                }
//                if (mUserType != EntityUserInfo.DOCTORUSER) {//如果是医生
//                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
//                }else {
//                    Intent intent = new Intent(getActivity(), VisitCostSettingActivity.class);
//                    intent.putExtra("userInfo", userInfo);
//                    getActivity().startActivityForResult(intent, MineFragment.RG_VISIT_REQUEST);
//                }
                break;
            case R.id.iv_persional_transfer_setting:

                if (mVerifystate == EntityUserInfo.WAIT_VERIFY) {//0待审核，可以提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_SUCC) {//1审核通过，不可再次提交
                    Intent intent = new Intent(getActivity(), TransferSettingActivity.class);
                    intent.putExtra("userInfo", userInfo);
                    getActivity().startActivityForResult(intent, MineFragment.RG_TRANSFER_REQUEST);
                }else if (mVerifystate == EntityUserInfo.VERIFY_FAIL) {//2审核失败，可以再次提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_ING) {//3审核中，不可提交
                    ToastUtil.showShortToast("认证审核中，请耐心等待！！！");
                }

//                if (mUserType != EntityUserInfo.DOCTORUSER) {//如果是医生
//                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
//                }else {
//                    Intent transferIntent = new Intent(getActivity(), TransferSettingActivity.class);
//                    transferIntent.putExtra("userInfo", userInfo);
//                    getActivity().startActivityForResult(transferIntent, MineFragment.RG_TRANSFER_REQUEST);
//                }
                break;
            case R.id.iv_persional_user_collection:
                Intent intent = new Intent(getActivity(), MyCollectActivity.class);
//                startActivityForResult(intent, Constant.REQUEST_CODE_HOME_ACTIVITY);
                startActivity(intent);
                break;
            case R.id.iv_persional_user_about:
                AboutActivity.startIntent(getActivity());
                break;
            case R.id.bt_persional_logout:
                String tokenid = PfUtils.getStr(getActivity(), Constant.DEARZS_SP, Constant.KEY_TOKENID, null);
                if (!TextUtils.isEmpty(tokenid)) {
                    ReqManager.getInstance().reqUserLoginOut(reqUserLoginOutCall, tokenid);
                } else {
                    BaseApplication.getInstance().setLoginInfo(null);
                    BaseApplication.getInstance().setPicList(null);
                    BaseApplication.getInstance().setUserInfo(null);
                    PfUtils.setLong(getActivity(), Constant.DEARZS_SP, Constant.KEY_USER_ID, -1);
                    PfUtils.setInt(getActivity(), Constant.DEARZS_SP, Constant.KEY_USER_TYPE, -1);
                    TimManager.getInstance().logoutIM();
                    LoginActivity.startIntent(getActivity());
                }
                break;
            case R.id.bt_doctor_certification:
                if (mVerifystate == EntityUserInfo.WAIT_VERIFY) {//0待审核，可以提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_SUCC) {//1审核通过，不可再次提交
                }else if (mVerifystate == EntityUserInfo.VERIFY_FAIL) {//2审核失败，可以再次提交
                    DoctorCertificationIntroduceActivity.startIntent(getActivity());
                }else if (mVerifystate == EntityUserInfo.VERIFY_ING) {//3审核中，不可提交
                    ToastUtil.showShortToast("认证审核中，请耐心等待！！！");
                }
                break;
            case R.id.iv_persional_back_card:
                BankCardActivity.startIntent(getActivity());
                break;
            case R.id.iv_persional_user_qr_code:
                if(userInfo != null){
                    QRCodeActivity.startIntent(getActivity(), userInfo.getQr(), userInfo.getInviteCode());
                }
                break;
            case R.id.iv_persional_home_doc:
                MyFamilyDoctorActivity.startIntent(getActivity());
                break;
        }
    }

    Callback<EntityBase> reqUserLoginOutCall = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("登出成功");
                PfUtils.setStr(getActivity(), Constant.DEARZS_SP, Constant.KEY_TOKENID, null);
                getActivity().finish();
                TimManager.getInstance().logoutIM();
                LoginActivity.startIntent(getActivity());
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String cost = data.getStringExtra("cost");
            boolean isOn = data.getBooleanExtra("isOn", false);

            int weekDay = (int) data.getIntExtra("weekday", 0);
            String timeStr = data.getStringExtra("time");

            if (requestCode == RG_VISIT_REQUEST) {
                if (resultCode == getActivity().RESULT_CANCELED) {

                } else if (resultCode == getActivity().RESULT_OK) {
                    if(isOn) {
                        mVisitSetting.setDesc("可会诊");
                    }else {
                        mVisitSetting.setDesc("不可会诊");
                    }

                    if (!TextUtils.isEmpty(cost)) {
                        mModifyedUserInfo.setVisitMoney(Double.parseDouble(cost));
                    }

                    mModifyedUserInfo.setVisitState(isOn ? 1 : 0);

                    List<EntityUserVisits> visitsmUserInfo = mModifyedUserInfo.getUserVisits();
                    for (EntityUserVisits entityUserVisits : visitsmUserInfo ) {
                        if (entityUserVisits.getWeek() == weekDay) {
                            entityUserVisits.setTime(timeStr);
                        }
                    }
//                    mModifyedUserInfo.setWeek(weekDay);
//                    mModifyedUserInfo.setTime(timeStr);
                }
            }

            if (requestCode == RG_TRANSFER_REQUEST) {
                if (resultCode == getActivity().RESULT_CANCELED) {

                } else if (resultCode == getActivity().RESULT_OK) {
                    if(isOn) {
                        mTransferString.setDesc("可转诊");
                    }else {
                        mTransferString.setDesc("不可转诊");
                    }
                    if (!TextUtils.isEmpty(cost)) {
                        mModifyedUserInfo.setReferralMoney(Double.parseDouble(cost));
                    }

                    mModifyedUserInfo.setReferralState(isOn ? 1 : 0);
                }
            }

            ReqManager.getInstance().reqUpdateUserInfo(reqCheckAppUpdate, mModifyedUserInfo, Utils.getUserToken(getActivity()));
        }
    }

    //
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == resultCode){
//            if(requestCode == Constant.REQUEST_CODE_PERSIONAL_ACTIVITY){
//                initData();
//            }
//        }
//    }

    /**
     * 更新用户信息接口回调
     */
    Callback<RespUserLogin> reqCheckAppUpdate = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            ToastUtil.showLongToast("更新失败，请重试");
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                if(response != null && response.getResult() !=null){
                    BaseApplication.getInstance().setLoginInfo(response.getResult());
                    initData();
                }
                ToastUtil.showLongToast("更新成功");
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };
}
