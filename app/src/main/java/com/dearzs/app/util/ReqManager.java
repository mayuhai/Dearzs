package com.dearzs.app.util;

import android.content.Context;
import android.text.TextUtils;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityRequestPatientMedicalRecord;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.PDynamicImg;
import com.dearzs.app.entity.ServiceParam;
import com.dearzs.app.entity.resp.RespAppUpdate;
import com.dearzs.app.entity.resp.RespDoctorForumComments;
import com.dearzs.app.entity.resp.RespDoctorForumDetail;
import com.dearzs.app.entity.resp.RespDoctorForumList;
import com.dearzs.app.entity.resp.RespDoctorForumTypes;
import com.dearzs.app.entity.resp.RespGetBannerList;
import com.dearzs.app.entity.resp.RespGetCityList;
import com.dearzs.app.entity.resp.RespGetCollectionConsultationList;
import com.dearzs.app.entity.resp.RespGetCollectionExpertList;
import com.dearzs.app.entity.resp.RespGetConsultList;
import com.dearzs.app.entity.resp.RespGetConsultationDetail;
import com.dearzs.app.entity.resp.RespGetConsultationList;
import com.dearzs.app.entity.resp.RespGetDiseaseCategory;
import com.dearzs.app.entity.resp.RespGetDynamicCommentList;
import com.dearzs.app.entity.resp.RespGetDynamicList;
import com.dearzs.app.entity.resp.RespGetExpertCommentList;
import com.dearzs.app.entity.resp.RespGetExpertDetails;
import com.dearzs.app.entity.resp.RespGetExpertList;
import com.dearzs.app.entity.resp.RespGetHospitalDepartmentList;
import com.dearzs.app.entity.resp.RespGetHospitalList;
import com.dearzs.app.entity.resp.RespGetMedicalRecordHistoryList;
import com.dearzs.app.entity.resp.RespGetMessageList;
import com.dearzs.app.entity.resp.RespGetPatientMedicalRecord;
import com.dearzs.app.entity.resp.RespGetPaymentsBalanceList;
import com.dearzs.app.entity.resp.RespOrderCommit;
import com.dearzs.app.entity.resp.RespOrderInfoList;
import com.dearzs.app.entity.resp.RespPatientDetails;
import com.dearzs.app.entity.resp.RespPatientList;
import com.dearzs.app.entity.resp.RespStartOrEndConsult;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.commonlib.okhttp.OkHttpUtils;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.okhttp.callback.FileCallBack;
import com.dearzs.commonlib.okhttp.callback.HandleEncryptCallback;
import com.dearzs.commonlib.utils.MD5;

import java.util.List;

/**
 * 网络请求管理类
 */
public class ReqManager implements HandleEncryptCallback {

    public static final int KEY_PAGE_SIZE = 10;
    private Context ctx;
    private static ReqManager mInstance;

    /**
     * 登录接口返回字段
     **/
    private String token;
    /**
     * 设备唯一识别码
     **/
    private String devicetoken;
    /**
     * app当前版本
     **/
    private String version;

    /**
     * 更新公告请求信息值
     *
     * @param devicetoken
     * @param version
     */
    public void updatePublicParams(String devicetoken, String version) {
        this.devicetoken = devicetoken;
        this.version = version;
    }

    /**
     * 更新公告请求信息值
     *
     * @param token
     */
    public void updatePublicParams(String token) {
        this.token = token;
    }

    @Override
    public String handleEncrypt(String url, String paramStr) {
        return url + "&sign=" + MD5.hexdigestForUpper("?" + paramStr + NetConstant.secretKey);
    }

    private ReqManager(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 初始化管理器
     */
    synchronized public static boolean init(Context mContext) {
        boolean result = true;
        if (mInstance != null) {
            return true;
        }
        try {
            mInstance = new ReqManager(mContext);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static synchronized ReqManager getInstance() {
        return mInstance;
    }

    /**
     * 获取客户列表
     */
    public void reqCustomList(Callback<RespPatientList> callBack, String tokenId, int isQr) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        if(isQr > 0){
            param.setIsQr(isQr);
        }
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_PATIENT_LIBRARY_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 找回密码接口
     */
    public void reqResetPwd(Callback<EntityBase> callBack, String phone, String password, String code) {
        ServiceParam param = new ServiceParam();
        param.setPassword(password);
        param.setPhone(phone);
        param.setCode(code);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.USER_FORGET_PWD))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取版本信息接口
     */
    public void reqMyWalletYue(Callback<RespOrderInfoList> callBack, String orderId) {
        ServiceParam param = new ServiceParam();
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_MY_WALLET_YUE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 验证手机验证码接口
     */
    public void reqValidPhoneCode(Callback<EntityBase> callBack, String phone, int type) {
        ServiceParam param = new ServiceParam();
        param.setPhone(phone);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_VALID_PHONE_CODE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取Banner列表接口
     */
    public void reqBannerList(Callback<RespGetBannerList> callBack) {
        ServiceParam param = new ServiceParam();
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_BANNER_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取咨询列表接口
     */
    public void reqConsultationList(Callback<RespGetConsultationList> callBack, int page, int pageSize, String token) {
        ServiceParam param = new ServiceParam();
        param.setRows(pageSize);
        param.setPage(page);
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_CONSULTATATION_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取咨询详情接口
     */
    public void reqConsultationDetail(Callback<RespGetConsultationDetail> callBack, long newsId, String token) {
        ServiceParam param = new ServiceParam();
        param.setNewsId(newsId);
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_CONSULTATATION_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取会诊室列表接口
     */
    public void reqConsultList(Callback<RespGetConsultList> callBack, int page, int pageSize, String token) {
        ServiceParam param = new ServiceParam();
        param.setRows(pageSize);
        param.setPage(page);
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_CONSULTA_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 开始会诊接口
     */
    public void reqStartConsult(Callback<RespStartOrEndConsult> callBack, String token, long roomId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setConsultId(roomId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.START_CONSULTA))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 结束会诊接口
     */
    public void reqEndConsult(Callback<RespStartOrEndConsult> callBack, String token, long roomId, String caseAnalysis, String diseaseDiagnosis, String treatmentPrograms, String result) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setConsultId(roomId);
        if(!TextUtils.isEmpty(caseAnalysis)){
            param.setCaseAnalysis(caseAnalysis);
        }
        if(!TextUtils.isEmpty(diseaseDiagnosis)){
            param.setDiseaseDiagnosis(diseaseDiagnosis);
        }
        if(!TextUtils.isEmpty(treatmentPrograms)){
            param.setTreatmentPrograms(treatmentPrograms);
        }
        if(!TextUtils.isEmpty(result)){
            param.setResult(result);
        }
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.END_CONSULTA))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取会诊结果接口
     */
    public void reqConsultDetail(Callback<RespStartOrEndConsult> callBack, String token, long consulId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setConsultId(consulId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_CONSULTA_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取收支明细列表接口
     */
    public void reqPaymentsBalanceList(Callback<RespGetPaymentsBalanceList> callBack, int page, String token) {
        ServiceParam param = new ServiceParam();
        param.setRows(KEY_PAGE_SIZE);
        param.setPage(page);
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_PAYMENTS_BALANCE_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }
//
//    /**
//     * 获取短信验证码接口
//     */
//    public void reqMobileCode(Callback<RespMsgValidCode> callBack, String mobile) {
//        OkHttpUtils
//                .postJson()
//                .url(NetConstant.getReqUrl(NetConstant.GET_MOBILE_CODE))
//                .addParams("mobile", mobile)
//                .paramEncrypt(this)
//                .content(param)
//                .build(getPublicParam())
//                .execute(callBack);
//    }

//    /**
//     * 获取医院列表接口
//     * */
//    public void reqHospitalList(Callback<EntityBase> callBack, long hospitalId){
//        ServiceParam param = new ServiceParam();
//        param.setHospitalId(hospitalId);
//        OkHttpUtils
//                .postJsonJson()
//                .url(NetConstant.getReqUrl(NetConstant.GET_HOSPITAL_LIST))
//                .content(param)
//                .build()
//                .execute(callBack);
//    }

    /**
     * 获取收藏专家列表接口
     */
    public void reqCollectionExpertList(Callback<RespGetCollectionExpertList> callBack, int page, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setPage(page);
        param.setRows(KEY_PAGE_SIZE);
        param.setType(Constant.COLLECTION_EXPERT);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_COLLECTION_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取收藏医学咨询列表接口
     */
    public void reqCollectionConsultationList(Callback<RespGetCollectionConsultationList> callBack, int page, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setPage(page);
        param.setRows(KEY_PAGE_SIZE);
        param.setType(Constant.COLLECTION_CONSULTATION);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_COLLECTION_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取城市列表接口
     */
    public void reqCityList(Callback<RespGetCityList> callBack) {
        ServiceParam param = new ServiceParam();
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_HOSPITAL_CITY_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取医院列表接口
     */
    public void reqHospitalList(Callback<RespGetHospitalList> callBack, String city) {
        ServiceParam param = new ServiceParam();
        if(!TextUtils.isEmpty(city))param.setCity(city);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_HOSPITAL_LIST) + "city=" + city)
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取疾病分类列表接口
     */
    public void getDiseaseCategoryList(Callback<RespGetDiseaseCategory> callBack, String token) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_DISEASE_CATEGORY_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取医院科室列表接口
     */
    public void reqHospitalDepartmentList(Callback<RespGetHospitalDepartmentList> callBack, long hospitalId) {
        ServiceParam param = new ServiceParam();
        param.setHid(hospitalId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_HOSPITAL_DPMT_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取找回密码接口
     */
    public void reqModifyPassword(Callback<EntityBase> callBack, String mobile, String userName, String code, String password) {
        ServiceParam param = new ServiceParam();
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_MODIFY_PASSWORD))
                .content(param)
                .build()
                .execute(callBack);
    }


    /**
     * 下载文件
     */
    public void reqDownloadFile(FileCallBack callBack,
                                String fileUrl) {
        OkHttpUtils
                .postJson()
                .url(fileUrl)
                .build()
                .execute(callBack);
    }

    /**
     * 获取图片上传接口地址
     *
     * @return
     */
    /**
     * 获取图片上传接口地址
     *
     * @return
     */
    public String getUploadPic() {
        String url = NetConstant.getReqUrl(NetConstant.GET_UPLOAD_PIC);
        return url;
    }

    /**
     * 软件更新接口
     */
    public void reqCheckAppUpdate(Callback<RespAppUpdate> callBack) {
        ServiceParam param = new ServiceParam();
//        param.setPhone(phone);
//        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_APP_UPDATA))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取专家列表
     */
    public void reqExpertList(Callback<RespGetExpertList> callBack, int page, String tokenId, long hospitalId,
                              long departmentId, String date, String city, int time, String sortName, String sortOrder, String searchCode, int pageSize, boolean isSelectDoctor) {
        ServiceParam param = new ServiceParam();
        if(!TextUtils.isEmpty(searchCode)) param.setName(searchCode);
        if(hospitalId > 0) param.setHospitalId(hospitalId);
        if(departmentId > 0 && hospitalId > 0) param.setDepartmentId(departmentId);
        if(!TextUtils.isEmpty(date)) param.setDate(date);
        if(time > 0) param.setTime(time + "");
        if(!TextUtils.isEmpty(sortName)) param.setSortName(sortName);
        if(!TextUtils.isEmpty(sortOrder)) param.setSortOrder(sortOrder);
        if(!TextUtils.isEmpty(city)) param.setCity(city);

        param.setTokenId(tokenId);
        param.setPage(page);
        param.setRows(pageSize);
        if(isSelectDoctor){
            OkHttpUtils
                    .postJson()
                    .url(NetConstant.getReqUrl(NetConstant.GET_EXPERT_LIST_2))
                    .content(param)
                    .build()
                    .execute(callBack);
        } else {
            OkHttpUtils
                    .postJson()
                    .url(NetConstant.getReqUrl(NetConstant.GET_EXPERT_LIST))
                    .content(param)
                    .build()
                    .execute(callBack);
        }
    }

//    /**
//     * 获取专家列表
//     */
//    public void reqExpertList(Callback<RespGetExpertList> callBack, int page, String tokenId, long hospitalId,
//                              long departmentId, String date, int time, String sortName, String sortOrder, String searchCode, int pageSize) {
//        ServiceParam param = new ServiceParam();
//        if(!TextUtils.isEmpty(searchCode)) param.setName(searchCode);
//        if(hospitalId > 0) param.setHospitalId(hospitalId);
//        if(departmentId > 0 && hospitalId > 0) param.setDepartmentId(departmentId);
//        if(!TextUtils.isEmpty(date)) param.setDate(date);
//        if(time > 0) param.setTime(time + "");
//        if(!TextUtils.isEmpty(sortName)) param.setSortName(sortName);
//        if(!TextUtils.isEmpty(sortOrder)) param.setSortOrder(sortOrder);
//
//        param.setTokenId(tokenId);
//        param.setPage(page);
//        param.setRows(pageSize);
//        OkHttpUtils
//                .postJson()
//                .url(NetConstant.getReqUrl(NetConstant.GET_EXPERT_LIST))
//                .content(param)
//                .build()
//                .execute(callBack);
//    }

    /**
     * 获取专家详情
     */
    public void reqExpertDetail(Callback<RespGetExpertDetails> callBack, String tokenId, String uid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_EXPERT_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取专家评论列表
     */
    public void reqExpertCommentList(Callback<RespGetExpertCommentList> callBack, String tokenId, String uid, int pageIndex) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setUid(uid);
        param.setPage(pageIndex);
        param.setRows(KEY_PAGE_SIZE);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_EXPERT_COMMENT_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取社区动态列表
     */
    public void reqCommuntityList(Callback<RespGetDynamicList> callBack, String tokenId, int page) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setPage(page);
        param.setRows(KEY_PAGE_SIZE);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_COMMUNTITY_DYNAMIC_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 动态赞和取消赞
     */
    public void reqDealDynamicPraise(Callback<EntityBase> callBack, String tokenId, long dynamicId, int type) {     //操作类型，1赞0取消赞
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setDynamicId(dynamicId);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DEAL_DYNAMIC_PRAISE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 新闻资讯详情赞和取消赞
     */
    public void reqDealConsultationPraise(Callback<EntityBase> callBack, String tokenId, long newsId, int type) {     //操作类型，1赞0取消赞
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setNewsId(newsId);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DEAL_MEDICAL_CONSULTATION_PRAISE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 删除动态
     */
    public void reqDelDynamicPraise(Callback<EntityBase> callBack, String tokenId, long dynamicId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setDynamicId(dynamicId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DELETE_DYNAMIC))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 收藏和取消收藏
     */
    public void reqDealCollection(Callback<EntityBase> callBack, String tokenId, long fid, int type, int optsType) {     //收藏类型，1医生专家 2医学资讯
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setOptsType(optsType);
        param.setFid(fid);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DEAL_COLLECTION))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 用户注册
     */
    public void reqUserRegister(Callback<EntityBase> callBack, String phone, String securityCode, String password) {
        ServiceParam param = new ServiceParam();
        param.setPhone(phone);
        param.setCode(securityCode);
        param.setPassword(password);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.USER_REGISTER))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 更新用户信息
     */
    public void reqUpdateUserInfo(Callback<RespUserLogin> callBack, EntityUserInfo userInfo, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        if(!TextUtils.isEmpty(userInfo.getAvatar())){
            param.setAvatar(userInfo.getAvatar());
        }
        if(!TextUtils.isEmpty(userInfo.getName())){
            param.setName(userInfo.getName());
        }
        if(!TextUtils.isEmpty(userInfo.getJob())){
            param.setJob(userInfo.getJob());
        }
        if(!TextUtils.isEmpty(userInfo.getDjob())){
            param.setDjob(userInfo.getDjob());
        }
        if(!TextUtils.isEmpty(userInfo.getAddress())){
            param.setAddress(userInfo.getAddress());
        }
        if(!TextUtils.isEmpty(userInfo.getIntro())){
            param.setIntro(userInfo.getIntro());
        }
        if(!TextUtils.isEmpty(userInfo.getLabel())){
            param.setLabel(userInfo.getLabel());
        }
        if(userInfo.getHospital() != null){
            param.setHospitalId(userInfo.getHospital().getId());
            //只有选择了医院才能选择科室
            if(userInfo.getDepartment() != null){
                param.setDepartmentId(userInfo.getDepartment().getId());
            }
        }
        if(userInfo.getAge() > 0){
            param.setAge(userInfo.getAge());
        }
        if(userInfo.getMedicalAge() > 0){
            param.setMedicalAge(userInfo.getMedicalAge());
        }
        if(userInfo.getGender() > 0){
            param.setGender(userInfo.getGender());
        }

        if (userInfo.getVisitMoney() > 0) {
            param.setVisitMoney(userInfo.getVisitMoney());
        }

        if (userInfo.getVisitState()  != null) {
            param.setVisitState(userInfo.getVisitState());
        }

        if (userInfo.getReferralMoney() > 0) {
            param.setReferralMoney(userInfo.getReferralMoney());
        }

        if (userInfo.getReferralState() != null) {
            param.setReferralState(userInfo.getReferralState());
        }

        if (userInfo.getUserVisits() != null) {
            param.setUserVisits(userInfo.getUserVisits());
        }

        if(!TextUtils.isEmpty(userInfo.getIntro())){
            param.setIntro(userInfo.getIntro());
        }

        if(!TextUtils.isEmpty(userInfo.getCardNo())){
            param.setCardNo(userInfo.getCardNo());
        }

        if(!TextUtils.isEmpty(userInfo.getBankAccountBranch())){
            param.setBankAccountBranch(userInfo.getBankAccountBranch());
        }
        if(!TextUtils.isEmpty(userInfo.getBankAccount())){
            param.setBankAccount(userInfo.getBankAccount());
        }
        if(!TextUtils.isEmpty(userInfo.getBankAccountNumber())){
            param.setBankAccountNumber(userInfo.getBankAccountNumber());
        }
        if(!TextUtils.isEmpty(userInfo.getBankAccountName())){
            param.setBankAccountName(userInfo.getBankAccountName());
        }

//        if(!TextUtils.isEmpty(userInfo.getLabel())){
            param.setLabel(userInfo.getLabel());
//        }

        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.UPDATE_USER_INFO))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 发布动态
     */
    public void reqReleaseDynamic(Callback<EntityBase> callBack, String content, List<PDynamicImg> dynamicImgs, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setDynamicImgs(dynamicImgs);
        param.setContent(content);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_RELEASE_DYNAMIC))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 发布动态评论
     */
    public void reqReleaseDynamicComment(Callback<EntityBase> callBack, long dynamicId, String comment, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setDynamicId(dynamicId);
        param.setComment(comment);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DYNAMIC_RELEASE_COMMENT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 删除动态评论
     */
    public void reqDelDynamicComment(Callback<EntityBase> callBack, long commentId, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setCommentId(commentId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DYNAMIC_DEL_COMMENT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取动态评论列表
     */
    public void getDynamicCommentList(Callback<RespGetDynamicCommentList> callBack, long dynamicId, int pageIndex, String tokenId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenId);
        param.setDynamicId(dynamicId);
        param.setPage(pageIndex);
        param.setRows(KEY_PAGE_SIZE);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_DYNAMIC_COMMENT_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 用户登陆
     */
    public void reqUserLogin(Callback<RespUserLogin> callBack, String phone, String password, String regId) {
        ServiceParam param = new ServiceParam();
        param.setPhone(phone);
        param.setPassword(password);
        param.setRegId(regId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.USER_LOGIN))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 验证tokenid
     */
    public void reqUserLoginByTokenId(Callback<RespUserLogin> callBack, String tokenid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.TOKEN_VALIDATE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 用户登出
     */
    public void reqUserLoginOut(Callback<EntityBase> callBack, String tokenid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.USER_LOGIN_OUT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 用户反馈
     */
    public void reqFeedback(Callback<EntityBase> callBack, String tokenid, String content) {
        ServiceParam param = new ServiceParam();
        param.setContent(content);
        param.setTokenId(tokenid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.FEEDBACK))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 患者查询
     */
    public void reqPatientSearch(Callback<RespPatientDetails> callBack, String tokenid, String cardNo) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        param.setCardNo(cardNo);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_SEARCH))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 患者添加
     */
    public void reqPatientAdd(Callback<EntityBase> callBack, String tokenid, String cardNo, String uid,
                String name, Integer gender, Integer age, String phone, String address, int isQr) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        param.setCardNo(cardNo);
        param.setUid(uid);
        if(TextUtils.isEmpty(uid)){
            param.setName(name);
            param.setGender(gender);
            param.setAge(age);
            param.setPhone(phone);
            param.setAddress(address);
        }
        param.setIsQr(isQr);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_ADD))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 患者详情
     */
    public void reqPatientDetail(Callback<RespPatientDetails> callBack, String tokenid, String uid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 患者删除
     */
    public void reqPatientDelete(Callback<EntityBase> callBack, String tokenid, String uid, int mPatientType) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        if(mPatientType > 0){
            param.setIsQr(mPatientType);
        }
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_DELETE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取患者病历
     */
    public void reqPatientMedicalRecord(Callback<RespGetPatientMedicalRecord> callBack, String tokenid, String uid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_MEDICAL_RECORD))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取病历详情
     */
    public void reqMedicalRecordDetails(Callback<RespGetPatientMedicalRecord> callBack, String tokenid, String historyId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(tokenid);
        param.setHistoryId(historyId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.MEDICAL_RECORD_DETAILS))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 创建/修改患者病历
     */
    public void reqModifyMedicalRecord(Callback<RespGetPatientMedicalRecord> callBack, String token, String uid, EntityRequestPatientMedicalRecord medicalRecord) {
        ServiceParam param = new ServiceParam();
        param.setHistory(medicalRecord);
        param.setTokenId(token);
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_MODIFY_MEDICAL_RECORD))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取患者病历更新历史列表
     */
    public void reqGetMedicalRecordHistoryList(Callback<RespGetMedicalRecordHistoryList> callBack, String token, String uid) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setUid(uid);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.PATIENT_MEDICAL_RECORD_HISTORY_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 提交订单
     */
    public void reqOrderCommit(Callback<RespOrderCommit> callBack, String token, long patientId, long expertId, long doctorId, String orderDate,
                               Integer orderTime, String orderPhone, String remark, Integer payType, Integer type ) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        if(patientId > 0) param.setPatientId(patientId);
        if(expertId > 0) param.setExpertId(expertId);
        if(doctorId > 0) param.setDoctorId(doctorId);
        param.setOrderDate(orderDate);
        param.setOrderTime(orderTime + "");
        param.setOrderPhone(orderPhone);
        param.setRemark(remark);
        param.setPayType(payType);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_COMMIT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 订单详情
     */
    public void reqOrderDetail(Callback<RespOrderCommit> callBack, String token, String orderNO ) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNO);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 获取订单列表接口
     */
    public void reqOrderInfoList(Callback<RespOrderInfoList> callBack, String token, Integer orderStateId, int page) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        if (orderStateId != null) {
            param.setOrderStateId(orderStateId);
        }
        param.setPage(page);
        param.setRows(KEY_PAGE_SIZE);
//        param.setRows(1);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 医生认证
     */
    public void reqCertificationDoctor(Callback<EntityBase> callBack, String token, String[] images) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setImages(images);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_CERTIFICATION))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 取消订单（下单者在医生应答前取消）
     * 下单者在医生应答前取消，全额退款
     */
    public void reqOrderCancel1TO24(Callback<EntityBase> callBack, String token, String orderNo) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_CANCEL_1TO24))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 取消订单（会诊开始前取消）
     * 等待会诊开始前24小时前取消订单,全额退款
     * 等待会诊开始前24小时内取消订单,扣除1%会诊费用
     */
    public void reqOrderCancel3TO22(Callback<EntityBase> callBack, String token, String orderNo) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_CANCEL_3TO22))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 专家处理订单（接单或拒绝）
     * 已付款的订单，专家处理改订单，可以接单或者拒绝
     */
    public void reqOrderExperReply(Callback<EntityBase> callBack, String token, String orderNo,
                                   Integer type, Integer orderTimeHour, Integer orderTimeMinute, String expertRemark) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        param.setType(type);
        param.setOrderTimeHour(orderTimeHour);
        param.setOrderTimeMinute(orderTimeMinute);
        param.setExpertRemark(expertRemark);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_EXPERT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 评价订单
     */
    public void reqOrderComment(Callback<EntityBase> callBack, String token, String orderNo,
                                   Double star, String comment) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        param.setStar(star);
        param.setComment(comment);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_EVALUATE))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 订单支付方式修改
     */
    public void reqOrderPayTypeModify(Callback<RespOrderCommit> callBack, String token, String orderNo, Integer payType) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        param.setPayType(payType);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.ORDER_PAYTYPE_MODIFY))
                .content(param)
                .build()
                .execute(callBack);
    }


    /**
     * 获取消息列表接口
     */
    public void reqMessageList(Callback<RespGetMessageList> callBack, int page, int pageSize, String token) {
        ServiceParam param = new ServiceParam();
        param.setRows(pageSize);
        param.setPage(page);
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.GET_MESSAGE_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 专家进入视频直播
     */
    public void reqConsultExpertResult(Callback<RespStartOrEndConsult> callBack, String token, long roomId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setConsultId(roomId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.CONSULT_EXPERT))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 结束转诊
     */
    public void reqOrderReferralEnd(Callback<EntityBase> callBack, String token, String orderNo) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setOrderNo(orderNo);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.REFERRAL_END))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 名医讲堂类型
     */
    public void reqDoctorForumTypes(Callback<RespDoctorForumTypes> callBack, String token) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_TYPES))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 名医讲堂列表
     */
    public void reqDoctorForumList(Callback<RespDoctorForumList> callBack, int pageIndex, String token, long typeId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setTypeId(typeId);
        param.setRows(KEY_PAGE_SIZE);
        param.setPage(pageIndex);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_LIST))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 名医讲堂评论列表
     */
    public void reqDoctorForumCommentList(Callback<RespDoctorForumComments> callBack, int page, String token, long lectureId) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setLectureId(lectureId);
        param.setRows(KEY_PAGE_SIZE);
        param.setPage(page);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_COMMENTS))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 名医讲堂详情
     */
    public void reqDoctorForumDetail(Callback<RespDoctorForumDetail> callBack, long lectureId, String token) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setLectureId(lectureId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_DETAIL))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 发表名医讲堂评论
     */
    public void reqDoctorForumReleaseComment(Callback<EntityBase> callBack, long lectureId, String comment, String token) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setComment(comment);
        param.setLectureId(lectureId);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_RELEASE_COMMENTS))
                .content(param)
                .build()
                .execute(callBack);
    }

    /**
     * 名医讲堂点赞和取消赞
     */
    public void reqDoctorForumPraise(Callback<EntityBase> callBack, long lectureId, int type, String token) {
        ServiceParam param = new ServiceParam();
        param.setTokenId(token);
        param.setLectureId(lectureId);
        param.setType(type);
        OkHttpUtils
                .postJson()
                .url(NetConstant.getReqUrl(NetConstant.DOCTOR_FORUM_PRAISE))
                .content(param)
                .build()
                .execute(callBack);
    }
}
