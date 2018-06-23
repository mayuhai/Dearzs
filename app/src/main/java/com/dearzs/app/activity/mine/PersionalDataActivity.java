package com.dearzs.app.activity.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.CustomSpinerAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityUploadPic;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespGetHospitalDepartmentList;
import com.dearzs.app.entity.resp.RespGetHospitalList;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.PersionalGoodAtCustomDialog;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.wheel.test.DateSelector;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.CustomCellView;
import com.dearzs.app.widget.CustomSpinerPopWindow;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.upload.uploadimage.UploadManager;
import com.dearzs.upload.uploadimage.listener.impl.IUploadModel;
import com.dearzs.upload.uploadimage.listener.impl.IUploadProgress;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lyl
 * 个人资料界面
 */
public class PersionalDataActivity extends BaseActivity implements IUploadModel {
    private static final String IMAGE_FILE_LOCATION = Constant.ROOT_APP_DIR + File.separator + "temp.jpg";
    private static final String IMAGE_CROP_FILE_LOCATION = Constant.ROOT_APP_DIR + File.separator + "crop_temp.jpg";
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    private static final int CHOOSE_BIG_PICTURE = 1001;
    private static final int TAKE_BIG_PICTURE = 1002;
    private static final int CROP_BIG_PICTURE = 1003;

    private List<EntityHospitalInfo> mHospitalList = new ArrayList<EntityHospitalInfo>();  //类型列表
    private List<EntityHospitalDepartmentInfo> mDepartmentList = new ArrayList<EntityHospitalDepartmentInfo>();  //类型列表

    private CustomSpinerPopWindow mHospitalSpinerPopWindow;
    private CustomSpinerPopWindow mDpmtSpinerPopWindow;
    private CustomSpinerPopWindow mJobSpinerPopWindow;
    private CustomSpinerPopWindow mSexSpinerPopWindow;
    private CustomSpinerPopWindow mTimeSpinerPopWindow;
    private CustomSpinerPopWindow mDpmtJobSpinerPopWindow;

    private CustomSpinerAdapter mSpinnerAdapter;

    private long mSelectedHospitalId = -1;
    private long mSelectedDepartmentId = -1;
    private List<Long> mHospitalIds;
    private List<Long> mDepartmentIds;
    private List<String> mJobList;
    private List<String> mDpmtJobList;
    private List<String> mSexList;
    private boolean mIsShowPopWin = false;  //请求数据回来后，是否需要展示popwindow来让用户选择

    private LinearLayout mDoctorLayout;
    private LinearLayout mUserPhotoLayout;
    private CircleImageView mUserPhoto;
    private CustomCellView mUserNickName;
    private CustomCellView mUserSex;
    private CustomCellView mUserPhoneNum;
    private CustomCellView mUserCardId;
    private CustomCellView mUserMedicalAge;
    private CustomCellView mUserJob;
    private CustomCellView mUserDepartment;
    private CustomCellView mUserDepartmentJob;
    private CustomCellView mUserHospital;
    private CustomCellView mUserAddress;
    private CustomCellView mUserAge;
    private CustomCellView mUserIntro;
    private CustomCellView mUserIntroDisease;
    private EntityUserInfo mModifyedUserInfo;

    private static final int PERSIONAL_AGE = 0;
    private static final int PERSIONAL_YILLING = 1;

    private String mImgLocalPath = "";
    private boolean mIsCertification;
    public static Activity mPersionalDataActivity;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String dateStr = (String) msg.obj;
            switch (msg.what) {
                case PERSIONAL_AGE:
                    int age = Utils.getTime(dateStr, "yyyy-MM-dd");
                    mUserAge.setDesc(age + "岁");
                    mModifyedUserInfo.setAge(age);
                    break;
                case PERSIONAL_YILLING:
                    int yiling = Utils.getTime(dateStr, "yyyy-MM-dd");
                    mUserMedicalAge.setDesc(yiling + "年");
                    mModifyedUserInfo.setMedicalAge(yiling);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_persional_data);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "个人资料");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
//        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "保存");
    }

    @Override
    public void initView() {
        super.initView();
        mUserPhotoLayout = getView(R.id.lin_persional_photo_layout);
        mDoctorLayout = getView(R.id.ly_doctor);
        mUserPhoto = getView(R.id.iv_persional_photo);
        mUserNickName = getView(R.id.cell_persional_nickname);
        mUserSex = getView(R.id.cell_persional_sex);
        mUserPhoneNum = getView(R.id.cell_persional_phone);
        mUserCardId = getView(R.id.cell_persional_card_num);
        mUserMedicalAge = getView(R.id.cell_persional_medical_age);
        mUserDepartment = getView(R.id.cell_persional_department);
        mUserDepartmentJob = getView(R.id.cell_persional_department_job);
        mUserHospital = getView(R.id.cell_persional_hospital);
        mUserAddress = getView(R.id.cell_persional_address);
        mUserAge = getView(R.id.cell_persional_age);
        mUserIntro = getView(R.id.cell_persional_intro);
        mUserIntroDisease = getView(R.id.cell_persional_intro_disease);
        mUserJob = getView(R.id.cell_persional_job);


        mUserPhotoLayout.setClickable(true);
        mUserNickName.setClickable(true);
        mUserSex.setClickable(true);
        mUserMedicalAge.setClickable(true);
        mUserDepartmentJob.setClickable(true);
        mUserDepartment.setClickable(true);
        mUserAddress.setClickable(true);
        mUserIntro.setClickable(true);
        mUserHospital.setClickable(true);
        mUserAge.setClickable(true);
        mUserJob.setClickable(true);
        mUserIntroDisease.setClickable(true);

        mUserPhotoLayout.setOnClickListener(this);
        mUserNickName.setOnClickListener(this);
        mUserDepartmentJob.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mUserMedicalAge.setOnClickListener(this);
        mUserDepartment.setOnClickListener(this);
        mUserIntro.setOnClickListener(this);
        mUserHospital.setOnClickListener(this);
        mUserAge.setOnClickListener(this);
        mUserJob.setOnClickListener(this);
        mUserAddress.setOnClickListener(this);
        mUserIntroDisease.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            mIsCertification = (boolean) intent.getExtras().getSerializable(Constant.KEY_CERTIFICATION);
        }

        if (mIsCertification) {
            addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "下一步");
            mPersionalDataActivity = this;
        } else {
            addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "保存");
        }

        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        mModifyedUserInfo = new EntityUserInfo();
        if (userInfo != null) {
            ImageLoaderManager.getInstance().displayImageNoCache(userInfo.getAvatar(), mUserPhoto);
            mUserNickName.setDesc(userInfo.getName());
            mUserSex.setDesc(userInfo.getGender() == Constant.MALE ? "男" : "女");
            mUserMedicalAge.setDesc(userInfo.getMedicalAge() + "年");
            if (userInfo.getDepartment() != null) {
                mUserDepartment.setDesc(userInfo.getDepartment().getName());
            }
            if (userInfo.getHospital() != null) {
                mSelectedHospitalId = userInfo.getId();
                mUserHospital.setDesc(userInfo.getHospital().getName());
            }
            mUserDepartmentJob.setDesc(userInfo.getDjob());
            mUserAddress.setDesc(userInfo.getAddress());
            mUserIntro.setDesc(userInfo.getIntro());
            mUserAge.setDesc(userInfo.getAge() + "岁");
            mUserJob.setDesc(userInfo.getJob());
            mUserPhoneNum.setDesc(userInfo.getPhone());
            mUserCardId.setDesc(userInfo.getCardNo());
            mUserIntroDisease.setDesc(userInfo.getLabel());

            EntityHospitalDepartmentInfo departmentInfo = userInfo.getDepartment();
            if (departmentInfo != null && !TextUtils.isEmpty(departmentInfo.getName())) {
                mUserDepartment.setDesc(departmentInfo.getName());
            }

            if (Utils.isDoctoruser() || mIsCertification) {
                mDoctorLayout.setVisibility(View.VISIBLE);

                if (mIsCertification) {
                    mUserCardId.setRightArrorVisiable(true);
                    mUserCardId.setClickable(true);
                    mUserCardId.setOnClickListener(this);
                }
            } else {
                mDoctorLayout.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(mUserCardId.getDesc().toString())) {
                mUserCardId.setRightArrorVisiable(true);
                mUserCardId.setClickable(true);
                mUserCardId.setOnClickListener(this);
            }
        }

        //进入界面，没有设置医院时，请求医院列表
//        if (userInfo == null || userInfo.getHospital() == null || userInfo.getHospital().getId() <= 0) {
//            ReqManager.getInstance().reqHospitalList(reqHospitalListCall);
//        }
        String[] jobs = getResources().getStringArray(R.array.doctor_job);
        mJobList = Arrays.asList(jobs);
        String[] sex = getResources().getStringArray(R.array.sex);
        mSexList = Arrays.asList(sex);
        String[] dpmtJobs = getResources().getStringArray(R.array.department_job);
        mDpmtJobList = Arrays.asList(dpmtJobs);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        if (mIsCertification) {
            String nickName = mUserNickName.getDesc();
            String gender = mUserSex.getDesc();
            String phone = mUserPhoneNum.getDesc();
            String cardId = mUserCardId.getDesc();
            String medicalAge = mUserMedicalAge.getDesc();
            String job = mUserJob.getDesc();
            String hospital = mUserHospital.getDesc();
            String department = mUserDepartment.getDesc();
            String address = mUserAddress.getDesc();
            String age = mUserAge.getDesc();
            String intro = mUserIntro.getDesc();

            if (!TextUtils.isEmpty(nickName) && !TextUtils.isEmpty(gender) &&
                    !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(cardId) &&
                    !TextUtils.isEmpty(medicalAge) && !TextUtils.isEmpty(job) && !TextUtils.isEmpty(hospital) &&
                    !TextUtils.isEmpty(department) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(age) &&
                    !TextUtils.isEmpty(intro)) {

                ReqManager.getInstance().reqUpdateUserInfo(reqPersionalDataUpdate, mModifyedUserInfo, Utils.getUserToken(PersionalDataActivity.this));
            } else {
                ToastUtil.showShortToast("请先完善个人信息！！！");
            }
        } else {
            ReqManager.getInstance().reqUpdateUserInfo(reqPersionalDataUpdate, mModifyedUserInfo, Utils.getUserToken(PersionalDataActivity.this));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.lin_persional_photo_layout:
                //选择头像
                initChosePicPopwindow();
                break;
            case R.id.cell_persional_nickname:
                //昵称
                showConfirmDialog(PersionalDataActivity.this, "修改昵称", "", InputType.TYPE_CLASS_TEXT, mUserNickName.getDesc().toString(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSweetAlertDialog != null) {
                            if (TextUtils.isEmpty(mSweetAlertDialog.getInputTextView().getText().toString())) {
                                LogUtil.showToast(PersionalDataActivity.this, "昵称不能为空");
                            } else {
                                mUserNickName.setDesc(mSweetAlertDialog.getInputTextView().getText().toString());
                                mModifyedUserInfo.setName(mSweetAlertDialog.getInputTextView().getText().toString());
                            }
                        }
                    }
                });
                break;
            case R.id.cell_persional_sex:
                //性别
                initSexPopwindow(mSexList);
                break;
            case R.id.cell_persional_medical_age:
                //医龄
                DateSelector yilingDateSelector = new DateSelector();
                yilingDateSelector.init(PersionalDataActivity.this, mHandler, PERSIONAL_YILLING, true);
                yilingDateSelector.showDaySelectorDialog();
                break;
            case R.id.cell_persional_department:
                //科室
                if (mSelectedHospitalId > 0) {
                    if (mDepartmentList != null && mDepartmentList.size() > 0) {
                        //科室列表不为空说明肯定设置了医院
                        mDpmtSpinerPopWindow.showAsDropDown(getTitleBar());
                    } else {
                        ReqManager.getInstance().reqHospitalDepartmentList(reqHospitalDpmtListCall, mSelectedHospitalId);
                    }
                } else {
                    ToastUtil.showLongToast("请先选择医院");
                }
                break;
            case R.id.cell_persional_department_job:
                //科室职务
                initDepartmentJobPopwindow(mDpmtJobList);
                break;
            case R.id.cell_persional_hospital:
                //医院
                if (mHospitalList != null && mHospitalList.size() > 0) {
                    mSpinnerAdapter.refreshData(getHospitalNamesListFromEntity(mHospitalList), 0);
                    mHospitalSpinerPopWindow.showAsDropDown(getTitleBar());
                } else {
//                    mIsShowPopWin = true;
                    ReqManager.getInstance().reqHospitalList(reqHospitalListCall, "");
                }
                break;
            case R.id.cell_persional_address:
                //地址
                showConfirmDialog(PersionalDataActivity.this, "修改地址", "", InputType.TYPE_CLASS_TEXT, mUserAddress.getDesc().toString(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSweetAlertDialog != null) {
                            if (TextUtils.isEmpty(mSweetAlertDialog.getInputTextView().getText().toString())) {
                                LogUtil.showToast(PersionalDataActivity.this, "地址不能为空");
                            } else {
                                mUserAddress.setDesc(mSweetAlertDialog.getInputTextView().getText().toString());
                                mModifyedUserInfo.setAddress(mSweetAlertDialog.getInputTextView().getText().toString());
                            }
                        }
                    }
                });
                break;
            case R.id.cell_persional_age:
                //年龄
                DateSelector ageDateSelector = new DateSelector();
                ageDateSelector.init(PersionalDataActivity.this, mHandler, PERSIONAL_AGE, true);
                ageDateSelector.showDaySelectorDialog();
                break;
            case R.id.cell_persional_intro:
                //个人介绍
                showPersionalGoodAtDialog();
                break;
            case R.id.cell_persional_intro_disease:
                //擅长疾病
                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_DISEASE_STR, mUserIntroDisease.getDesc());
                GoodAtActivity.startIntent(PersionalDataActivity.this, bundle);
                break;
            case R.id.cell_persional_job:
                //职称
                initJobPopwindow(mJobList);
                break;

            case R.id.cell_persional_card_num:
                if (mIsCertification || TextUtils.isEmpty(mUserCardId.getDesc().toString())) {
                    //身份证
                    showConfirmDialog(PersionalDataActivity.this, "修改身份证", "", InputType.TYPE_CLASS_TEXT, mUserCardId.getDesc().toString(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mSweetAlertDialog != null) {
                                if (TextUtils.isEmpty(mSweetAlertDialog.getInputTextView().getText().toString())) {
                                    LogUtil.showToast(PersionalDataActivity.this, "职称不能为身份证");
                                } else if (!Utils.isIdentityCard(mSweetAlertDialog.getInputTextView().getText().toString())) {
                                    LogUtil.showToast(PersionalDataActivity.this, "身份证格式不正确");
                                } else {
                                    mUserCardId.setDesc(mSweetAlertDialog.getInputTextView().getText().toString());
                                    mModifyedUserInfo.setCardNo(mSweetAlertDialog.getInputTextView().getText().toString());
                                }
                            }
                        }
                    });
                }
                break;
        }
    }

    public void initHospitalPopwindow(final List<String> dataList) {

        if (mSpinnerAdapter != null && mDpmtSpinerPopWindow != null) {
            mSpinnerAdapter.refreshData(dataList, 0);
            mHospitalSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, dataList);
            mSpinnerAdapter.refreshData(dataList, 0);

            //初始化PopWindow
            mHospitalSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mHospitalSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mHospitalSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    mSelectedDepartmentId = -1;
                    mDepartmentList.clear();
                    //医院列表请求回来，清空科室数据，以免数据错乱
                    if (mHospitalIds != null && mHospitalIds.size() > pos) {
                        mSelectedHospitalId = mHospitalIds.get(pos);
                    }
                    EntityHospitalInfo hospitalInfo = new EntityHospitalInfo();
                    hospitalInfo.setId(mSelectedHospitalId);
                    hospitalInfo.setName(dataList.get(pos));
                    mModifyedUserInfo.setHospital(hospitalInfo);
                    EntityHospitalDepartmentInfo departInfo = new EntityHospitalDepartmentInfo();
                    departInfo.setHid(mSelectedHospitalId);
                    departInfo.setName("");
                    departInfo.setId(-1);
                    mModifyedUserInfo.setDepartment(departInfo);
                    mUserHospital.setDesc(dataList.get(pos));
                    mUserDepartment.setDesc("");
                    ReqManager.getInstance().reqHospitalDepartmentList(reqHospitalDpmtListCall, mSelectedHospitalId);
                }
            });
            mHospitalSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }

    public void initDpmtPopwindow(final List<String> dataList) {
        if (mSpinnerAdapter != null && mDpmtSpinerPopWindow != null) {
            mSpinnerAdapter.refreshData(dataList, 0);
            mDpmtSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.refreshData(dataList, 0);
            } else {
                mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, dataList);
                mSpinnerAdapter.refreshData(dataList, 0);
            }

            //初始化PopWindow
            mDpmtSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mDpmtSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mDpmtSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    mUserDepartment.setDesc(dataList.get(pos));
                    if (mDepartmentIds != null && mDepartmentIds.size() > pos) {
                        mSelectedDepartmentId = mDepartmentIds.get(pos);
                    }
                    EntityHospitalDepartmentInfo departInfo = new EntityHospitalDepartmentInfo();
                    departInfo.setHid(mSelectedHospitalId);
                    departInfo.setName(dataList.get(pos));
                    departInfo.setId(mSelectedDepartmentId);
                    mModifyedUserInfo.setDepartment(departInfo);
                }
            });
            mDpmtSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }

    public void initJobPopwindow(final List<String> dataList) {
        if (mJobSpinerPopWindow != null && mSpinnerAdapter != null) {
            mSpinnerAdapter.refreshData(dataList, 0);
            mJobSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.refreshData(dataList, 0);
            } else {
                mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, dataList);
                mSpinnerAdapter.refreshData(dataList, 0);
            }

            //初始化PopWindow
            mJobSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mJobSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mJobSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    mUserJob.setDesc(dataList.get(pos));
                    mModifyedUserInfo.setJob(dataList.get(pos));
                }
            });
            mJobSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }

    public void initDepartmentJobPopwindow(final List<String> dataList) {
        if (mDpmtJobSpinerPopWindow != null && mSpinnerAdapter != null) {
            mSpinnerAdapter.refreshData(dataList, 0);
            mDpmtJobSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.refreshData(dataList, 0);
            } else {
                mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, dataList);
                mSpinnerAdapter.refreshData(dataList, 0);
            }

            //初始化PopWindow
            mDpmtJobSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mDpmtJobSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mDpmtJobSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    mUserDepartmentJob.setDesc(dataList.get(pos));
                    mModifyedUserInfo.setDjob(dataList.get(pos));
                }
            });
            mDpmtJobSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }

    public void initSexPopwindow(final List<String> dataList) {
        if (mSpinnerAdapter != null && mSexSpinerPopWindow != null) {
            mSpinnerAdapter.refreshData(dataList, 0);
            mSexSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.refreshData(dataList, 0);
            } else {
                mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, dataList);
                mSpinnerAdapter.refreshData(dataList, 0);
            }

            //初始化PopWindow
            mSexSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mSexSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mSexSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    if (pos == 0) {
                        mModifyedUserInfo.setGender(Constant.MALE);
                    } else if (pos == 1) {
                        mModifyedUserInfo.setGender(Constant.FEMALE);
                    }
                    mUserSex.setDesc(dataList.get(pos));
                }
            });
            mSexSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }


    public void initChosePicPopwindow() {
        List<String> list = new ArrayList<String>();
        list.add("从相册选择");
        list.add("拍照");
        list.add("取消");
        if (mSpinnerAdapter != null && mTimeSpinerPopWindow != null) {
            mSpinnerAdapter.refreshData(list, 0);
            mTimeSpinerPopWindow.showAsDropDown(getTitleBar());
        } else {
            if (mSpinnerAdapter != null) {
                mSpinnerAdapter.refreshData(list, 0);
            } else {
                mSpinnerAdapter = new CustomSpinerAdapter(PersionalDataActivity.this, list);
                mSpinnerAdapter.refreshData(list, 0);
            }

            //初始化PopWindow
            mTimeSpinerPopWindow = new CustomSpinerPopWindow(PersionalDataActivity.this);
            mTimeSpinerPopWindow.setAdatper(mSpinnerAdapter);
            mTimeSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
                @Override
                public void onItemClick(int pos) {
                    if (pos == 0) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, CHOOSE_BIG_PICTURE);
                    } else if (pos == 1) {
                        takePicture();
                    } else {

                    }
                }
            });
            mTimeSpinerPopWindow.showAsDropDown(getTitleBar());
        }
    }

    //医院科室列表接口回调
    Callback<RespGetHospitalDepartmentList> reqHospitalDpmtListCall = new Callback<RespGetHospitalDepartmentList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespGetHospitalDepartmentList response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                mDepartmentList = response.getResult().getList();
                initDpmtPopwindow(getStrListFromDepartmentEntity(mDepartmentList));
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    //医院列表接口回调
    Callback<RespGetHospitalList> reqHospitalListCall = new Callback<RespGetHospitalList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetHospitalList response) {
            if (onSuccess(response)) {
                mHospitalList = response.getResult().getList();
                initHospitalPopwindow(getHospitalNamesListFromEntity(mHospitalList));
//                if (mIsShowPopWin) {
//                    mHospitalSpinerPopWindow.showAsDropDown(getTitleBar());
//                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    private List<String> getHospitalNamesListFromEntity(List<EntityHospitalInfo> list) {
        List<String> listStr = new ArrayList<String>();
        if (mHospitalIds != null) {
            mHospitalIds.clear();
        } else {
            mHospitalIds = new ArrayList<Long>();
        }
//        mHospitalIds.add((long) -1);
//        listStr.add("全部医院");
        for (int i = 0; i < list.size(); i++) {
            listStr.add(list.get(i).getName());
            mHospitalIds.add(list.get(i).getId());
        }
        return listStr;
    }

    private List<String> getStrListFromDepartmentEntity(List<EntityHospitalDepartmentInfo> list) {
        List<String> listStr = new ArrayList<String>();
        if (mDepartmentIds != null) {
            mDepartmentIds.clear();
        } else {
            mDepartmentIds = new ArrayList<Long>();
        }
//        mDepartmentIds.add((long) -1);
//        listStr.add("全部科室");
        for (int i = 0; i < list.size(); i++) {
            mDepartmentIds.add(list.get(i).getId());
            listStr.add(list.get(i).getName());
        }
        return listStr;
    }

    /**
     * 打开照相机
     */
    private void takePicture() {
        String state = Environment.getExternalStorageState();
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(IMAGE_FILE_LOCATION)));
            startActivityForResult(intent, TAKE_BIG_PICTURE);
        } else {
            LogUtil.showToast(this, "请确认已经插入sd卡");
        }
    }

    Callback<RespUserLogin> reqPersionalDataUpdate = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            ToastUtil.showLongToast("更新失败，请重试");
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                if (response != null && response.getResult() != null) {
                    BaseApplication.getInstance().setLoginInfo(response.getResult());
                    initData();

                    if (mIsCertification) {
                        DoctorCertificationActivity.startIntent(PersionalDataActivity.this);
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_BIG_PICTURE:
                if (data != null) {
                    imageUri = data.getData();
                    cropImage(imageUri, 500, 500, CROP_BIG_PICTURE);
                }
                break;
            case TAKE_BIG_PICTURE:
                cropImage(Uri.fromFile(new File(IMAGE_FILE_LOCATION)), 500, 500, CROP_BIG_PICTURE);
                break;
            case CROP_BIG_PICTURE:
//                if (data == null) {
//                return;
//            }
                Bitmap photo = null;
                try {
//                    Uri photoUri = data.getData();
//                    if (photoUri != null) {
//                        photo = BitmapFactory.decodeFile(photoUri.getPath());
//                    } else {
                    photo = BitmapFactory.decodeFile(IMAGE_CROP_FILE_LOCATION);
//                    }
                    if (photo != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        saveBitmap(photo, "");
                        mImgLocalPath = IMAGE_CROP_FILE_LOCATION;
                        LogUtil.LogE("mImgLocalPath = " + mImgLocalPath);
                        ImageLoaderManager.getInstance().displayImageNoCache("file://" + mImgLocalPath, mUserPhoto);
                        UploadManager.getInstance().execute(ReqManager.getInstance().getUploadPic(), this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (photo != null) {
                        photo.recycle();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void reSetLable(String msg){
//        if("lable".equals(msg)){
        mModifyedUserInfo.setLabel(msg);
        mUserIntroDisease.setDesc(msg);
//        }
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm, String picName) {
        Log.e(TAG, "保存图片");
        File file = new File(IMAGE_FILE_LOCATION);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //截取图片
    public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            String url = getPath(PersionalDataActivity.this, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        } else {
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        intent.putExtra("output", Uri.fromFile(new File(IMAGE_CROP_FILE_LOCATION)));
        startActivityForResult(intent, requestCode);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * 创建单选按钮对话框
     */
//    @Override
//    protected Dialog onCreateDialog(int id) {
//        Dialog dialog = null;
//        switch (id) {
//            case DIALOG:
//                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
//                //设置对话框的图标
////                builder.setIcon(R.drawable.header);
//                //设置对话框的标题
////                builder.setTitle("单选按钮对话框");
//                //0: 默认第一个单选按钮被选中
//                int sexIndex = 0;
//
//                EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
//                if (userInfo.getGender() == Constant.MALE) {
//                    sexIndex = 0;
//                } else {
//                    sexIndex = 1;
//                }
//                builder.setSingleChoiceItems(R.array.sex, sexIndex, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String sexStr = getResources().getStringArray(R.array.sex)[which];
//                        mUserSex.setDesc(sexStr);
//                        if (which == 0) {
//                            mModifyedUserInfo.setGender(Constant.MALE);
//                        } else if (which == 1) {
//                            mModifyedUserInfo.setGender(Constant.FEMALE);
//                        }
//
//                        dialog.dismiss();
//                    }
//                });
//
//                //添加一个确定按钮
////                builder.setPositiveButton(" 确 定 ", new DialogInterface.OnClickListener(){
////                    public void onClick(DialogInterface dialog, int which) {
////
////                    }
////                });
//                //创建一个单选按钮对话框
//                dialog = builder.create();
//                break;
//        }
//        return dialog;
//    }
    public void showPersionalGoodAtDialog() {

        final PersionalGoodAtCustomDialog.Builder builder = new PersionalGoodAtCustomDialog.Builder(this);

        builder.setTitle("个人简介");
        builder.setMessage("请设选择标签");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String intro = null;

                EditText editText = null;
                editText = builder.getEditText();

                if (editText != null && editText.getText() != null && !TextUtils.isEmpty(editText.getText().toString().trim())) {
                    intro = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(intro)) {
                        mUserIntro.setDesc(intro);
                        mModifyedUserInfo.setIntro(intro);
//                        ToastUtil.showLongToast(intro);
                    }
                }

            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
        builder.setEditTextMessage(mUserIntro.getDesc());
    }

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, PersionalDataActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public int doResult(int uploadResult, String newFilePath, Object resultMessage) {
        LogUtil.LogE(resultMessage.toString());
        try {
            Gson gson = new Gson();
            EntityUploadPic netPic = gson.fromJson(resultMessage.toString(), EntityUploadPic.class);
            if (netPic != null && netPic.getResult() != null) {
                String url = netPic.getResult().getUrl();
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    mModifyedUserInfo.setAvatar(url);
                    File file = new File(IMAGE_FILE_LOCATION);
                    File cropFile = new File(IMAGE_CROP_FILE_LOCATION);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (cropFile.exists()) {
                        cropFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            ToastUtil.showLongToast("图片上传失败，请重新上传");
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isNeedUpload() {
        return true;
    }

    @Override
    public String getUploadFilePath() {
        return mImgLocalPath;
    }

    @Override
    public void bindUploadProgress(IUploadProgress mIUploadProgress) {

    }

    @Override
    public IUploadProgress getUploadProgress() {
        return null;
    }
}
