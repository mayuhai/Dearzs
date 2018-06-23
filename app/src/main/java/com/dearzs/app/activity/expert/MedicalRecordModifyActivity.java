package com.dearzs.app.activity.expert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.AlbumActivity;
import com.dearzs.app.adapter.GvUploadPicRecyclerViewAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.base.IPicDelListener;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityPatientMedicalRecord;
import com.dearzs.app.entity.EntityPicBase;
import com.dearzs.app.entity.EntityRequestPatientMedicalRecord;
import com.dearzs.app.entity.resp.RespGetPatientMedicalRecord;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.FixGridLayout;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.dearzs.commonlib.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/6/24.
 * 新建或者修改病历界面
 */
public class MedicalRecordModifyActivity extends BaseActivity {
    /**
     * 最大上传图片数量
     */
//    public static final int MAX_PIC_COUNT = 4;

    public static final String REQUEST_CODE = "req_code";
    public static final String SELECT_COUNT = "select_count";
    public static final String SEL_PIC_LIST = "sel_pic_list";

    public static final int mCurrentType = 100;
    public static final int mPastType = mCurrentType + 1;
    public static final int mSpecializedType = mPastType + 1;
    public static final int mAuxiliaryType = mSpecializedType + 1;
    public static final int mTestType = mAuxiliaryType + 1;
    public static final int mDiagnosticType = mTestType + 1;

    public int mWhichFunction = -1;     //点击了哪个GridView，就看做选择了哪个功能

    private TextView mPatientName, mPatientAge;
    private CircleImageView mPatientPhoto;
    private ImageView mPatientSex;
    private EditText mEtCurrentHistory, mEtPastHistory, mEtSpecialized, mEtAuxiliary, mEtTestResult, mEtDiagnosticResult;
    private RecyclerView mGvCurrentHistory, mGvSpecialized, mGvAuxiliary, mGvTestResult;
//    private RecyclerView mGvPastHistory, mGvDiagnosticResult;
    private FixGridLayout mLableLayout;
    private GvUploadPicRecyclerViewAdapter mCurrentImageAdapter;
//    private GvUploadPicRecyclerViewAdapter mPastImageAdapter;
    private GvUploadPicRecyclerViewAdapter mSpecializedImageAdapter;
    private GvUploadPicRecyclerViewAdapter mAuxiliaryImageAdapter;
    private GvUploadPicRecyclerViewAdapter mTestResultImageAdapter;
//    private GvUploadPicRecyclerViewAdapter mDiagnosticResultImageAdapter;

    private EntityPatientMedicalRecord mMedicalRecord;

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
    private ArrayList<EntityNetPic> mCurrentPicList;
    private ArrayList<EntityNetPic> mPastPicList;
    private ArrayList<EntityNetPic> mSpecializedPicList;
    private ArrayList<EntityNetPic> mAuxiliaryPicList;
    private ArrayList<EntityNetPic> mDiagnosticPicList;
    private ArrayList<EntityNetPic> mTestPicList;

    private long mPatientId = -1;
    private EntityPatientInfo mPatientInfo;
    /**
     * 显示最后面的“添加照片”按钮
     */
    private EntityNetPic mEntityPicTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_modify_medical_record);

        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "病历");
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "完成");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        EntityRequestPatientMedicalRecord medicalRecord = new EntityRequestPatientMedicalRecord();
        if(mEtCurrentHistory != null && TextUtils.isEmpty(mEtCurrentHistory.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if(mEtPastHistory != null && TextUtils.isEmpty(mEtPastHistory.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if(mEtSpecialized != null && TextUtils.isEmpty(mEtSpecialized.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if(mEtAuxiliary != null && TextUtils.isEmpty(mEtAuxiliary.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if(mEtTestResult != null && TextUtils.isEmpty(mEtTestResult.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if(mEtDiagnosticResult != null && TextUtils.isEmpty(mEtDiagnosticResult.getText())){
            ToastUtil.showLongToast("请完善病历信息");
            return;
        }
        if (mCurrentPicList != null && mCurrentPicList.size() > 0) {
            List<String> presentImgs = new ArrayList<String>();
            for (EntityNetPic currentPicList : mCurrentPicList) {
                if (!TextUtils.isEmpty(currentPicList.getUrl())) {
                    if (currentPicList.getUrl().startsWith("http")) {
                        presentImgs.add(currentPicList.getUrl());
                    } else {
                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
                        return;         //有未上传完成的图片
                    }
                }
            }
            medicalRecord.setPresentImgs(presentImgs);
            medicalRecord.setPresent(mEtCurrentHistory.getText().toString());
        }
//        if (mPastPicList != null && mPastPicList.size() > 0) {
//            List<String> pastImgs = new ArrayList<String>();
//            for (EntityNetPic pastPicList : mPastPicList) {
//                if (!TextUtils.isEmpty(pastPicList.getUrl())) {
//                    if (pastPicList.getUrl().startsWith("http")) {
//                        pastImgs.add(pastPicList.getUrl());
//                    } else {
//                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
//                        return;         //有未上传完成的图片
//                    }
//                }
//            }
//            medicalRecord.setPastImgs(pastImgs);
            medicalRecord.setPast(mEtPastHistory.getText().toString());
//        }
        if (mSpecializedPicList != null && mSpecializedPicList.size() > 0) {
            List<String> specializedImgs = new ArrayList<String>();
            for (EntityNetPic specializedPicList : mSpecializedPicList) {
                if (!TextUtils.isEmpty(specializedPicList.getUrl())) {
                    if (specializedPicList.getUrl().startsWith("http")) {
                        specializedImgs.add(specializedPicList.getUrl());
                    } else {
                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
                        return;         //有未上传完成的图片
                    }
                }
            }
            medicalRecord.setSpecialImgs(specializedImgs);
            medicalRecord.setSpecial(mEtSpecialized.getText().toString());
        }
        if (mAuxiliaryPicList != null && mAuxiliaryPicList.size() > 0) {
            List<String> auxiliaryImgs = new ArrayList<String>();
            for (EntityNetPic auxiliaryPicList : mAuxiliaryPicList) {
                if (!TextUtils.isEmpty(auxiliaryPicList.getUrl())) {
                    if (auxiliaryPicList.getUrl().startsWith("http")) {
                        auxiliaryImgs.add(auxiliaryPicList.getUrl());
                    } else {
                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
                        return;         //有未上传完成的图片
                    }
                }
            }
            medicalRecord.setAidImgs(auxiliaryImgs);
            medicalRecord.setAidText(mEtAuxiliary.getText().toString());
        }
        if (mTestPicList != null && mTestPicList.size() > 0) {
            List<String> testImgs = new ArrayList<String>();
            for (EntityNetPic testPicList : mTestPicList) {
                if (!TextUtils.isEmpty(testPicList.getUrl())) {
                    if (testPicList.getUrl().startsWith("http")) {
                        testImgs.add(testPicList.getUrl());
                    } else {
                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
                        return;         //有未上传完成的图片
                    }
                }
            }
            medicalRecord.setLabImgs(testImgs);
            medicalRecord.setLabResult(mEtTestResult.getText().toString());
        }
//        if (mDiagnosticPicList != null && mDiagnosticPicList.size() > 0) {
//            List<String> diagnosticImgs = new ArrayList<String>();
//            for (EntityNetPic diagnosticPicList : mDiagnosticPicList) {
//                if (!TextUtils.isEmpty(diagnosticPicList.getUrl())) {
//                    if (diagnosticPicList.getUrl().startsWith("http")) {
//                        diagnosticImgs.add(diagnosticPicList.getUrl());
//                    } else {
//                        ToastUtil.showLongToast("您还有图片没上传成功，等上传完成再创建病历吧！");
//                        return;         //有未上传完成的图片
//                    }
//                }
//            }
//            medicalRecord.setDiagImgs(diagnosticImgs);
            medicalRecord.setDiagResult(mEtDiagnosticResult.getText().toString());
//        }
        ReqManager.getInstance().reqModifyMedicalRecord(reqMedicalRecordallback, Utils.getUserToken(MedicalRecordModifyActivity.this), String.valueOf(mPatientId), medicalRecord);
    }

    @Override
    public void initView() {
        super.initView();
        mPatientAge = getView(R.id.modify_medical_record_patient_age);
        mPatientName = getView(R.id.modify_medical_record_patient_name);
        mPatientPhoto = getView(R.id.modify_medical_record_patient_photo);
        mPatientSex = getView(R.id.modify_medical_record_patient_sex);

        mLableLayout = getView(R.id.modify_medical_record_auxiliary_lable_layout);

        mEtCurrentHistory = getView(R.id.et_modify_medical_record_current_history);
        mEtPastHistory = getView(R.id.et_modify_medical_record_past_history);
        mEtSpecialized = getView(R.id.et_modify_medical_record_specialized);
        mEtAuxiliary = getView(R.id.et_modify_medical_record_auxiliary);
        mEtTestResult = getView(R.id.et_modify_medical_record_test_result);
        mEtDiagnosticResult = getView(R.id.et_modify_medical_record_diagnostic_result);
        mGvCurrentHistory = getView(R.id.gv_modify_medical_record_current_history);
//        mGvPastHistory = getView(R.id.gv_modify_medical_record_past_history);
        mGvSpecialized = getView(R.id.gv_modify_medical_record_specialized);
        mGvAuxiliary = getView(R.id.gv_modify_medical_record_auxiliary);
        mGvTestResult = getView(R.id.gv_modify_medical_record_test_result);
//        mGvDiagnosticResult = getView(R.id.gv_modify_medical_record_diagnostic_result);
        int height = (DisplayUtil.getScreenWidth(MedicalRecordModifyActivity.this) - DimenUtils.dip2px(MedicalRecordModifyActivity.this, 48)) / 4;
        LayoutUtil.reMeasureHeight(mGvCurrentHistory, height);
//        LayoutUtil.reMeasureHeight(mGvPastHistory, height);
        LayoutUtil.reMeasureHeight(mGvSpecialized, height);
        LayoutUtil.reMeasureHeight(mGvAuxiliary, height);
        LayoutUtil.reMeasureHeight(mGvTestResult, height);
//        LayoutUtil.reMeasureHeight(mGvDiagnosticResult, height);
    }

    @Override
    public void initData() {
        super.initData();
        initAdapters();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPatientInfo = (EntityPatientInfo) bundle.getSerializable(Constant.KEY_PATIENT_INFO);
            if(mPatientInfo !=null){
                mPatientId = mPatientInfo.getId();
            }
            mMedicalRecord = (EntityPatientMedicalRecord) bundle.getSerializable(Constant.KEY_MEDICAL_RECORD_INFO);
            bindViewData();
        }
        setAdapterListener();
    }

    private void initAdapters() {
        GridLayoutManager currentLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
//        //添加分割线
//        mGvCurrentHistory.addItemDecoration(new DividerGridItemDecoration(2));
        mGvCurrentHistory.setLayoutManager(currentLayoutManager);
//        mGvCurrentHistory.setPullRefreshEnabled(false);
//        mGvCurrentHistory.setLoadingMoreEnabled(false);

//        GridLayoutManager pastLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
//        mGvPastHistory.setLayoutManager(pastLayoutManager);
//        //添加分割线
//        mGvPastHistory.addItemDecoration(new DividerGridItemDecoration(2));
//        mGvPastHistory.setPullRefreshEnabled(false);
//        mGvPastHistory.setLoadingMoreEnabled(false);

        GridLayoutManager auxiliaryLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
        mGvAuxiliary.setLayoutManager(auxiliaryLayoutManager);
//        //添加分割线
//        mGvAuxiliary.addItemDecoration(new DividerGridItemDecoration(2));
//        mGvAuxiliary.setPullRefreshEnabled(false);
//        mGvAuxiliary.setLoadingMoreEnabled(false);

        GridLayoutManager specializedLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
        mGvSpecialized.setLayoutManager(specializedLayoutManager);
//        //添加分割线
//        mGvSpecialized.addItemDecoration(new DividerGridItemDecoration(2));
//        mGvSpecialized.setPullRefreshEnabled(false);
//        mGvSpecialized.setLoadingMoreEnabled(false);

        GridLayoutManager testResultLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
        mGvTestResult.setLayoutManager(testResultLayoutManager);
//        //添加分割线
//        mGvTestResult.addItemDecoration(new DividerGridItemDecoration(2));
//        mGvTestResult.setPullRefreshEnabled(false);
//        mGvTestResult.setLoadingMoreEnabled(false);

//        GridLayoutManager diagnosticLayoutManager = new GridLayoutManager(MedicalRecordModifyActivity.this, 4);
//        mGvDiagnosticResult.setLayoutManager(diagnosticLayoutManager);
//        //添加分割线
//        mGvDiagnosticResult.addItemDecoration(new DividerGridItemDecoration(2));
//        mGvDiagnosticResult.setPullRefreshEnabled(false);
//        mGvDiagnosticResult.setLoadingMoreEnabled(false);

        mCurrentImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
        mCurrentPicList = new ArrayList<EntityNetPic>();
        // 生成提示图片
        if (mEntityPicTip == null) {
            mEntityPicTip = new EntityNetPic();
            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
            mCurrentPicList.add(mEntityPicTip);
        } else {
            mCurrentPicList.add(mEntityPicTip);
        }
        mCurrentImageAdapter.replaceAll(mCurrentPicList);
        mGvCurrentHistory.setAdapter(mCurrentImageAdapter);
        mGvCurrentHistory.setEnabled(false);

        //LayoutUtil.reMeasureHeight(MedicalRecordModifyActivity.this, mGvCurrentHistory, 50);

//        mPastImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
//        mPastPicList = new ArrayList<EntityNetPic>();
//        // 生成提示图片
//        if (mEntityPicTip == null) {
//            mEntityPicTip = new EntityNetPic();
//            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
//            mPastPicList.add(mEntityPicTip);
//        } else {
//            mPastPicList.add(mEntityPicTip);
//        }
//        mPastImageAdapter.replaceAll(mPastPicList);
//        mGvPastHistory.setAdapter(mPastImageAdapter);
//        LayoutUtil.setLayout(mGvPastHistory, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72), DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72));

        mAuxiliaryImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
        mAuxiliaryPicList = new ArrayList<EntityNetPic>();
        // 生成提示图片
        if (mEntityPicTip == null) {
            mEntityPicTip = new EntityNetPic();
            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
            mAuxiliaryPicList.add(mEntityPicTip);
        } else {
            mAuxiliaryPicList.add(mEntityPicTip);
        }
        mAuxiliaryImageAdapter.replaceAll(mAuxiliaryPicList);
//        mGvAuxiliary.setAdapter(mCurrentImageAdapter);
        mGvAuxiliary.setAdapter(mAuxiliaryImageAdapter);
//        LayoutUtil.setLayout(mGvAuxiliary, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72), DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72));

        mSpecializedImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
        mSpecializedPicList = new ArrayList<EntityNetPic>();
        // 生成提示图片
        if (mEntityPicTip == null) {
            mEntityPicTip = new EntityNetPic();
            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
            mSpecializedPicList.add(mEntityPicTip);
        } else {
            mSpecializedPicList.add(mEntityPicTip);
        }
        mSpecializedImageAdapter.replaceAll(mSpecializedPicList);
        mGvSpecialized.setAdapter(mSpecializedImageAdapter);
//        LayoutUtil.setLayout(mGvSpecialized, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72), DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72));

        mTestResultImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
        mTestPicList = new ArrayList<EntityNetPic>();
        // 生成提示图片
        if (mEntityPicTip == null) {
            mEntityPicTip = new EntityNetPic();
            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
            mTestPicList.add(mEntityPicTip);
        } else {
            mTestPicList.add(mEntityPicTip);
        }
        mTestResultImageAdapter.replaceAll(mTestPicList);
        mGvTestResult.setAdapter(mTestResultImageAdapter);
//        LayoutUtil.setLayout(mGvTestResult, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72), DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72));

//        mDiagnosticResultImageAdapter = new GvUploadPicRecyclerViewAdapter(MedicalRecordModifyActivity.this);
//        mDiagnosticPicList = new ArrayList<EntityNetPic>();
//        // 生成提示图片
//        if (mEntityPicTip == null) {
//            mEntityPicTip = new EntityNetPic();
//            mEntityPicTip.setUpload_state(EntityNetPic.UPLOAD_PIC_TIP);
//            mDiagnosticPicList.add(mEntityPicTip);
//        } else {
//            mDiagnosticPicList.add(mEntityPicTip);
//        }
//        mDiagnosticResultImageAdapter.replaceAll(mDiagnosticPicList);
////        LayoutUtil.setLayout(mGvDiagnosticResult, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72), DimenUtils.dip2px(MedicalRecordModifyActivity.this, 72));
//        mGvDiagnosticResult.setAdapter(mDiagnosticResultImageAdapter);
    }

    private void setAdapterListener() {
        mCurrentImageAdapter.setDelListener(new IPicDelListener() {
            @Override
            public void onPicDel(Object pic) {
                if (pic == null) return;

                // 根据对象,获取索引位置
                int position = -1;
                if (mCurrentPicList != null) {
                    position = mCurrentPicList.indexOf(pic);
                }
                // 若存在则动态移除
                if (mCurrentImageAdapter != null && position != -1) {
                    mCurrentImageAdapter.notifyItemRemoved(position);
                }
                // 最后从列表中移除
                if (position != -1) {
                    mCurrentPicList.remove(pic);
                }

                // 删除后动态添加“添加更多”
                if (!mCurrentPicList.contains(mEntityPicTip)) {
                    mCurrentPicList.add(mEntityPicTip);
                    mCurrentImageAdapter.notifyItemInserted(mCurrentPicList.size() - 1);
                }
            }
        });
        mCurrentImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mCurrentPicList == null || mCurrentPicList.size() <= 0) return;

                EntityPicBase item = mCurrentPicList.get(position);
                if (item == null) return;

                mWhichFunction = mCurrentType;
                // 点击了哪个位置的视图
                mCurPos = position;
                mPicType = item.getUpload_state();
                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 1);
                intent.putExtra(REQUEST_CODE, mPicType);
                intent.putExtra(SELECT_COUNT, picListSize(mCurrentType));
                startActivityForResult(intent, mPicType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
//        mPastImageAdapter.setDelListener(new IPicDelListener() {
//            @Override
//            public void onPicDel(Object pic) {
//                if (pic == null) return;
//
//                // 根据对象,获取索引位置
//                int position = -1;
//                if (mPastPicList != null) {
//                    position = mPastPicList.indexOf(pic);
//                }
//                // 若存在则动态移除
//                if (mPastImageAdapter != null && position != -1) {
//                    mPastImageAdapter.notifyItemRemoved(position);
//                }
//                // 最后从列表中移除
//                if (position != -1) {
//                    mPastPicList.remove(pic);
//                }
//
//                // 删除后动态添加“添加更多”
//                if (!mPastPicList.contains(mEntityPicTip)) {
//                    mPastPicList.add(mEntityPicTip);
//                    mPastImageAdapter.notifyItemInserted(mPastPicList.size() - 1);
//                }
//            }
//        });
//        mPastImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (mPastPicList == null || mPastPicList.size() <= 0) return;
//
//                EntityPicBase item = mPastPicList.get(position);
//                if (item == null) return;
//
//                mWhichFunction = mPastType;
//                // 点击了哪个位置的视图
//                mCurPos = position;
//                mPicType = item.getUpload_state();
//                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
//                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 0);
//                intent.putExtra(REQUEST_CODE, mPicType);
//                intent.putExtra(SELECT_COUNT, picListSize(mPastType));
//                startActivityForResult(intent, mPicType);
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
        mAuxiliaryImageAdapter.setDelListener(new IPicDelListener() {
            @Override
            public void onPicDel(Object pic) {
                if (pic == null) return;

                // 根据对象,获取索引位置
                int position = -1;
                if (mAuxiliaryPicList != null) {
                    position = mAuxiliaryPicList.indexOf(pic);
                }
                // 若存在则动态移除
                if (mAuxiliaryImageAdapter != null && position != -1) {
                    mAuxiliaryImageAdapter.notifyItemRemoved(position);
                }
                // 最后从列表中移除
                if (position != -1) {
                    mAuxiliaryPicList.remove(pic);
                }

                // 删除后动态添加“添加更多”
                if (!mAuxiliaryPicList.contains(mEntityPicTip)) {
                    mAuxiliaryPicList.add(mEntityPicTip);
                    mAuxiliaryImageAdapter.notifyItemInserted(mAuxiliaryPicList.size() - 1);
                }
            }
        });
        mAuxiliaryImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAuxiliaryPicList == null || mAuxiliaryPicList.size() <= 0) return;

                EntityPicBase item = mAuxiliaryPicList.get(position);
                if (item == null) return;

                mWhichFunction = mAuxiliaryType;
                // 点击了哪个位置的视图
                mCurPos = position;
                mPicType = item.getUpload_state();
                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 14);
                intent.putExtra(REQUEST_CODE, mPicType);
                intent.putExtra(SELECT_COUNT, picListSize(mAuxiliaryType));
                startActivityForResult(intent, mPicType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mSpecializedImageAdapter.setDelListener(new IPicDelListener() {
            @Override
            public void onPicDel(Object pic) {
                if (pic == null) return;

                // 根据对象,获取索引位置
                int position = -1;
                if (mSpecializedPicList != null) {
                    position = mSpecializedPicList.indexOf(pic);
                }
                // 若存在则动态移除
                if (mSpecializedImageAdapter != null && position != -1) {
                    mSpecializedImageAdapter.notifyItemRemoved(position);
                }
                // 最后从列表中移除
                if (position != -1) {
                    mSpecializedPicList.remove(pic);
                }

                // 删除后动态添加“添加更多”
                if (!mSpecializedPicList.contains(mEntityPicTip)) {
                    mSpecializedPicList.add(mEntityPicTip);
                    mSpecializedImageAdapter.notifyItemInserted(mSpecializedPicList.size() - 1);
                }
            }
        });
        mSpecializedImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mSpecializedPicList == null || mSpecializedPicList.size() <= 0) return;

                EntityPicBase item = mSpecializedPicList.get(position);
                if (item == null) return;

                mWhichFunction = mSpecializedType;
                // 点击了哪个位置的视图
                mCurPos = position;
                mPicType = item.getUpload_state();
                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 1);
                intent.putExtra(REQUEST_CODE, mPicType);
                intent.putExtra(SELECT_COUNT, picListSize(mSpecializedType));
                startActivityForResult(intent, mPicType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mTestResultImageAdapter.setDelListener(new IPicDelListener() {
            @Override
            public void onPicDel(Object pic) {
                if (pic == null) return;

                // 根据对象,获取索引位置
                int position = -1;
                if (mTestPicList != null) {
                    position = mTestPicList.indexOf(pic);
                }
                // 若存在则动态移除
                if (mTestResultImageAdapter != null && position != -1) {
                    mTestResultImageAdapter.notifyItemRemoved(position);
                }
                // 最后从列表中移除
                if (position != -1) {
                    mTestPicList.remove(pic);
                }

                // 删除后动态添加“添加更多”
                if (!mTestPicList.contains(mEntityPicTip)) {
                    mTestPicList.add(mEntityPicTip);
                    mTestResultImageAdapter.notifyItemInserted(mTestPicList.size() - 1);
                }
            }
        });
        mTestResultImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mTestPicList == null || mTestPicList.size() <= 0) return;

                EntityPicBase item = mTestPicList.get(position);
                if (item == null) return;

                mWhichFunction = mTestType;
                // 点击了哪个位置的视图
                mCurPos = position;
                mPicType = item.getUpload_state();
                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 8);
                intent.putExtra(REQUEST_CODE, mPicType);
                intent.putExtra(SELECT_COUNT, picListSize(mTestType));
                startActivityForResult(intent, mPicType);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

//        mDiagnosticResultImageAdapter.setDelListener(new IPicDelListener() {
//            @Override
//            public void onPicDel(Object pic) {
//                if (pic == null) return;
//
//                // 根据对象,获取索引位置
//                int position = -1;
//                if (mDiagnosticPicList != null) {
//                    position = mDiagnosticPicList.indexOf(pic);
//                }
//                // 若存在则动态移除
//                if (mDiagnosticResultImageAdapter != null && position != -1) {
//                    mDiagnosticResultImageAdapter.notifyItemRemoved(position);
//                }
//                // 最后从列表中移除
//                if (position != -1) {
//                    mDiagnosticPicList.remove(pic);
//                }
//
//                // 删除后动态添加“添加更多”
//                if (!mDiagnosticPicList.contains(mEntityPicTip)) {
//                    mDiagnosticPicList.add(mEntityPicTip);
//                    mDiagnosticResultImageAdapter.notifyItemInserted(mDiagnosticPicList.size() - 1);
//                }
//            }
//        });
//        mDiagnosticResultImageAdapter.setOnItemClickLitener(new GvUploadPicRecyclerViewAdapter.OnItemClickLitener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (mDiagnosticPicList == null || mDiagnosticPicList.size() <= 0) return;
//
//                EntityPicBase item = mDiagnosticPicList.get(position);
//                if (item == null) return;
//                mWhichFunction = mDiagnosticType;
//
//                // 点击了哪个位置的视图
//                mCurPos = position;
//                mPicType = item.getUpload_state();
//                Intent intent = new Intent(MedicalRecordModifyActivity.this, AlbumActivity.class);
//                intent.putExtra(Constant.KEY_MAX_PIC_COUNT, 0);
//                intent.putExtra(REQUEST_CODE, mPicType);
//                intent.putExtra(SELECT_COUNT, picListSize(mDiagnosticType));
//                startActivityForResult(intent, mPicType);
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        });
    }

    //获取患者病历接口回调
    Callback<RespGetPatientMedicalRecord> reqMedicalRecordallback = new Callback<RespGetPatientMedicalRecord>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetPatientMedicalRecord response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                ToastUtil.showLongToast("创建病历成功");
                setResult(RESULT_OK, getIntent());
                finish();
            } else {
                ToastUtil.showLongToast("创建病历失败，请重试");
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    private void bindViewData() {
        EntityPatientInfo patientInfo = mPatientInfo;
        if (patientInfo != null) {
            mPatientAge.setText(String.valueOf(patientInfo.getAge()));
            mPatientName.setText(patientInfo.getName());
            ImageLoaderManager.getInstance().displayImage(patientInfo.getAvatar(), mPatientPhoto);
            mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
        }
        EntityPatientMedicalRecord medicalRecord = mMedicalRecord;
        if (medicalRecord != null) {
            mEtCurrentHistory.setText(medicalRecord.getPresent());
            mEtPastHistory.setText(medicalRecord.getPast());
            mEtAuxiliary.setText(medicalRecord.getAidText());
            mEtSpecialized.setText(medicalRecord.getSpecial());
            mEtTestResult.setText(medicalRecord.getLabResult());
            mEtDiagnosticResult.setText(medicalRecord.getDiagResult());

//            if (medicalRecord.getPresentImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getPresentImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mCurrentPicList.add(netPic);
//                }
//                mCurrentImageAdapter.replaceAll(mCurrentPicList);
//            }
//            if (medicalRecord.getPastImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getPastImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mPastPicList.add(netPic);
//                }
//                mPastImageAdapter.replaceAll(mPastPicList);
//            }
//            if (medicalRecord.getSpecialImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getSpecialImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mSpecializedPicList.add(netPic);
//                }
//                mSpecializedImageAdapter.replaceAll(mSpecializedPicList);
//            }
//            if (medicalRecord.getAidImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getAidImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mAuxiliaryPicList.add(netPic);
//                }
//                mAuxiliaryImageAdapter.replaceAll(mAuxiliaryPicList);
//            }
//            if (medicalRecord.getLabImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getLabImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mTestPicList.add(netPic);
//                }
//                mTestResultImageAdapter.replaceAll(mTestPicList);
//            }
//            if (medicalRecord.getDiagImgs() != null) {
//                List<EntityMedicalRecordImgs> recordImgs = medicalRecord.getDiagImgs();
//                for (EntityMedicalRecordImgs recordImg : recordImgs) {
//                    EntityNetPic netPic = new EntityNetPic();
//                    netPic.setUrl(recordImg.getImg());
//                    mDiagnosticPicList.add(netPic);
//                }
//                mDiagnosticResultImageAdapter.replaceAll(mDiagnosticPicList);
//            }
            if (mLableLayout.getChildCount() > 0) {
                mLableLayout.removeAllViews();
            }
            //TODO  辅助检查的标签-------------------------------------
//            mLableLayout.setPadding(DimenUtils.dip2px(MedicalRecordModifyActivity.this, 7), 0, DimenUtils.dip2px(MedicalRecordModifyActivity.this, 7), 0);
//            for (int i = 0; i < 5; i++) {
//                String text = "第" + i + "个";
//                View view = getTextView(MedicalRecordModifyActivity.this, text);
//                mLableLayout.addView(view);
//            }
        }
    }

    private View getTextView(Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lable_layout, null);
        final CheckedTextView textView = GetViewUtil.getView(view, R.id.item_title_text);
        textView.setText(text);
        textView.setBackgroundResource(R.mipmap.checkbox_unchecked);
        textView.setChecked(false);
        textView.setTextColor(getResources().getColor(R.color.gray_7));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mLableLayout.getChildCount(); i++) {
                    CheckedTextView checkText = (CheckedTextView)((LinearLayout)mLableLayout.getChildAt(i)).getChildAt(0);
                    checkText.setChecked(false);
                    checkText.setBackgroundResource(R.mipmap.checkbox_unchecked);
                    checkText.setTextColor(getResources().getColor(R.color.gray_7));
                }
                textView.setChecked(true);
                textView.setBackgroundResource(R.mipmap.checkbox_checked);
                textView.setTextColor(getResources().getColor(R.color.green));
//                textView.toggle();
            }
        });
        return view;
    }


    /**
     * Activity跳转
     *
     * @param medicalRecord
     */
    public static void startIntent(Context ctx, EntityPatientMedicalRecord medicalRecord, EntityPatientInfo patientInfo) {
        Intent intent = new Intent();
        intent.setClass(ctx, MedicalRecordModifyActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(Constant.KEY_MEDICAL_RECORD_INFO, medicalRecord);
        b.putSerializable(Constant.KEY_PATIENT_INFO, patientInfo);
        intent.putExtras(b);
        ctx.startActivity(intent);
    }

    @Override
    public void onLeftBtnClick() {

        doResult(mWhichFunction);
        super.onLeftBtnClick();
    }

    @Override
    public boolean handleBack() {

        doResult(mWhichFunction);
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
        String[] selectedList = selList;
        if (selectedList != null && selectedList.length > 0 && mCurrentPicList != null) {
            for (String sel : selectedList) {
                LogUtil.LogD("selList", "==选中的图片：" + sel);
            }
            switch (mWhichFunction) {
                case mCurrentType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mCurrentPicList.add(pic);
                                int height = (DisplayUtil.getScreenWidth(MedicalRecordModifyActivity.this) - DimenUtils.dip2px(MedicalRecordModifyActivity.this, 48)) / 4;
                                LayoutUtil.reMeasureHeight(mGvCurrentHistory, height);
                            }
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mCurrentPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
                case mPastType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mPastPicList.add(pic);
                            }
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mPastPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
                case mSpecializedType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mSpecializedPicList.add(pic);
                            }
                            int height = (DisplayUtil.getScreenWidth(MedicalRecordModifyActivity.this) - DimenUtils.dip2px(MedicalRecordModifyActivity.this, 48)) / 4;
                            LayoutUtil.reMeasureHeight(mGvSpecialized, height);
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mSpecializedPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
                case mAuxiliaryType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mAuxiliaryPicList.add(pic);
                            }
                            int height = (DisplayUtil.getScreenWidth(MedicalRecordModifyActivity.this) - DimenUtils.dip2px(MedicalRecordModifyActivity.this, 48)) / 4;
                            int auxiLine = ((int)(mAuxiliaryPicList.size() / 4)) + (mAuxiliaryPicList.size() % 4 == 0 ? 0 : 1);
                            int marginHeight = auxiLine > 1 ? (auxiLine - 1) * DimenUtils.dip2px(MedicalRecordModifyActivity.this, 10) : 0;
                            LayoutUtil.reMeasureHeight(mGvAuxiliary, height * auxiLine + marginHeight);
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mAuxiliaryPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
                case mTestType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mTestPicList.add(pic);
                            }
                            int height = (DisplayUtil.getScreenWidth(MedicalRecordModifyActivity.this) - DimenUtils.dip2px(MedicalRecordModifyActivity.this, 48)) / 4;
                            int testLine = ((int)(mTestPicList.size() / 4)) + (mTestPicList.size() % 4 == 0 ? 0 : 1);
                            int marginHeight = testLine > 1 ? (testLine - 1) * DimenUtils.dip2px(MedicalRecordModifyActivity.this, 10) : 0;
//                            LayoutUtil.reMeasureHeight(mLinTestResult, height * testLine + marginHeight);
                            LayoutUtil.reMeasureHeight(mGvTestResult, height * testLine + marginHeight);
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mTestPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
                case mDiagnosticType:
                    switch (mPicType) {
                        // 添加照片,则加入到对列末尾
                        case EntityNetPic.UPLOAD_PIC_TIP:
                            for (int i = 0; i < selectedList.length; i++) {
                                EntityNetPic pic = new EntityNetPic();
                                pic.setLocalPath(selectedList[i]);
                                mDiagnosticPicList.add(pic);
                            }
                            break;
                        default:
                            // 替换照片,直接修改指定位置的图标
                            String path = selectedList[0];
                            EntityNetPic item = (EntityNetPic) mDiagnosticPicList.get(mCurPos);
                            if (item == null) return;
                            item.setLocalPath(path);
                            break;
                    }
                    break;
            }

            uploadAdapter(mWhichFunction);
        }
    }

    /**
     * 动态更新列表
     */
    private void uploadAdapter(int type) {
        switch (type) {
            case mCurrentType:
                if (mCurrentPicList.contains(mEntityPicTip)) {
                    mCurrentPicList.remove(mEntityPicTip);
                }
                if (mCurrentPicList.size() < 1) {
                    mCurrentPicList.add(mEntityPicTip);
                }
                if (mCurrentImageAdapter != null) {
                    mCurrentImageAdapter.replaceAll(mCurrentPicList);
                }
                break;
            case mPastType:
                if (mPastPicList.contains(mEntityPicTip)) {
                    mPastPicList.remove(mEntityPicTip);
                }
                if (mPastPicList.size() < 0) {
                    mPastPicList.add(mEntityPicTip);
                }
//                if (mPastImageAdapter != null) {
//                    mPastImageAdapter.replaceAll(mPastPicList);
//                }
                break;
            case mSpecializedType:
                if (mSpecializedPicList.contains(mEntityPicTip)) {
                    mSpecializedPicList.remove(mEntityPicTip);
                }
                if (mSpecializedPicList.size() < 1) {
                    mSpecializedPicList.add(mEntityPicTip);
                }
                if (mSpecializedImageAdapter != null) {
                    mSpecializedImageAdapter.replaceAll(mSpecializedPicList);
                }
                break;
            case mAuxiliaryType:
                if (mAuxiliaryPicList.contains(mEntityPicTip)) {
                    mAuxiliaryPicList.remove(mEntityPicTip);
                }
                if (mAuxiliaryPicList.size() < 14) {
                    mAuxiliaryPicList.add(mEntityPicTip);
                }
                if (mAuxiliaryImageAdapter != null) {
                    mAuxiliaryImageAdapter.replaceAll(mAuxiliaryPicList);
                }
                break;
            case mTestType:
                if (mTestPicList.contains(mEntityPicTip)) {
                    mTestPicList.remove(mEntityPicTip);
                }
                if (mTestPicList.size() < 8) {
                    mTestPicList.add(mEntityPicTip);
                }
                if (mTestResultImageAdapter != null) {
                    mTestResultImageAdapter.replaceAll(mTestPicList);
                }
                break;
            case mDiagnosticType:
                if (mDiagnosticPicList.contains(mEntityPicTip)) {
                    mDiagnosticPicList.remove(mEntityPicTip);
                }
                if (mDiagnosticPicList.size() < 0) {
                    mDiagnosticPicList.add(mEntityPicTip);
                }
//                if (mDiagnosticResultImageAdapter != null) {
//                    mDiagnosticResultImageAdapter.replaceAll(mDiagnosticPicList);
//                }
                break;
        }
    }

    private void doResult(int type) {
        switch (type) {
            case mCurrentType:
                if (mCurrentPicList != null) {
                    mCurrentPicList.remove(mEntityPicTip);
                }
                break;
            case mPastType:
                if (mPastPicList != null) {
                    mPastPicList.remove(mEntityPicTip);
                }
                break;
            case mAuxiliaryType:
                if (mAuxiliaryPicList != null) {
                    mAuxiliaryPicList.remove(mEntityPicTip);
                }
                break;
            case mSpecializedType:
                if (mSpecializedPicList != null) {
                    mSpecializedPicList.remove(mEntityPicTip);
                }
                break;
            case mTestType:
                if (mTestPicList != null) {
                    mTestPicList.remove(mEntityPicTip);
                }
                break;
            case mDiagnosticType:
                if (mDiagnosticPicList != null) {
                    mDiagnosticPicList.remove(mEntityPicTip);
                }
                break;
        }
        setResult(RESULT_OK, getIntent());
    }

    private int picListSize(int type) {
        switch (type) {
            case mCurrentType:
                if (mCurrentPicList == null || mCurrentPicList.isEmpty()) {
                    return 0;
                }
                if (mCurrentPicList.contains(mEntityPicTip)) {
                    return mCurrentPicList.size() - 1;
                } else {
                    return mCurrentPicList.size();
                }
            case mPastType:
                if (mPastPicList == null || mPastPicList.isEmpty()) {
                    return 0;
                }
                if (mPastPicList.contains(mEntityPicTip)) {
                    return mPastPicList.size() - 1;
                } else {
                    return mPastPicList.size();
                }
            case mAuxiliaryType:
                if (mAuxiliaryPicList == null || mAuxiliaryPicList.isEmpty()) {
                    return 0;
                }
                if (mAuxiliaryPicList.contains(mEntityPicTip)) {
                    return mAuxiliaryPicList.size() - 1;
                } else {
                    return mAuxiliaryPicList.size();
                }
            case mSpecializedType:
                if (mSpecializedPicList == null || mSpecializedPicList.isEmpty()) {
                    return 0;
                }
                if (mSpecializedPicList.contains(mEntityPicTip)) {
                    return mSpecializedPicList.size() - 1;
                } else {
                    return mSpecializedPicList.size();
                }
            case mTestType:
                if (mTestPicList == null || mTestPicList.isEmpty()) {
                    return 0;
                }
                if (mTestPicList.contains(mEntityPicTip)) {
                    return mTestPicList.size() - 1;
                } else {
                    return mTestPicList.size();
                }
            case mDiagnosticType:
                if (mDiagnosticPicList == null || mDiagnosticPicList.isEmpty()) {
                    return 0;
                }
                if (mDiagnosticPicList.contains(mEntityPicTip)) {
                    return mDiagnosticPicList.size() - 1;
                } else {
                    return mDiagnosticPicList.size();
                }
        }
        return 0;
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
}
