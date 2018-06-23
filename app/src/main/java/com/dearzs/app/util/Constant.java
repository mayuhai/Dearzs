package com.dearzs.app.util;

import android.os.Environment;

import com.tencent.TIMConversationType;

import java.io.File;

/**
 * 常量实体类
 */
public class Constant {
    /**
     * tokenid
     **/
    public static final String DEARZS_SP = "dearzs_sp";

    public static final String KEY_IS_FIRST_RUN = "app_is_first_run";
    public static final String KEY_APP_VIRSON_CODE = "app_virson_code";

    /**
     * 缓存应用主目录名称
     **/
    public final static String ROOT_APP_DIR = Environment.getExternalStorageDirectory() + File.separator + "Dearzs";
    /**
     * 缓存图片文件名称
     **/
    public final static String DEFAULT_CACHE_DIR = ROOT_APP_DIR + File.separator + "Photo";
    /**
     * 缓存文件名称
     **/
    public final static String DEFAULT_FILE_DIR = ROOT_APP_DIR + File.separator + "File";
//    /**用户上传图片缓存路径**/
//    public static final String DEFAULT_DIR = ROOT_APP_DIR + File.separator + "dearzs";

    /**
     * 　是否需要刷新数据的标识
     */
    public static final int REFRESH_DATA = 222;
    public static final int NO_REFRESH_DATA = REFRESH_DATA + 1;
    public static final int GET_MORE_DATA = NO_REFRESH_DATA + 1;

    /**
     * 更新页面的加载状态
     */
    public static final int PAGE_LOAD_ING = 0; // 正在加载
    public static final int PAGE_LOAD_RESULT = PAGE_LOAD_ING + 1; //显示加载结果及无数据
    public static final int PAGE_LOAD_COMPLET = PAGE_LOAD_RESULT + 1; //加载完成,隐藏界面
    public static final int PAGE_LOAD_ERROR = PAGE_LOAD_COMPLET + 1; //加载出错,网络错误
    public static final int PAGE_LOAD_DELAY_ING = PAGE_LOAD_ERROR + 1; // 延迟加载
    /**
     * 无效数据值
     */
    public static final int INVALID = -999;

    /**
     * 跳转到WebViewPage页面时链接的类型，用于标识链接的动作
     */
    public static final String WEB_REDIRECT_TYPE = "web_redirect_type";
    /**
     * 跳转到WebViewPage的类型为广告
     */
    public static final String WEB_REDIRECT_TYPE_AD = "web_redirect_ad";
    public static final String WEB_PAGE_TITLE = "web_page_title";
    public static final String WEB_PAGE_URL = "web_page_url";
    public static final String WEB_PAGE_PARAM2 = "web_page_param2";
    public static final String WEB_PAGE_PARAM3 = "web_page_param3";

    public static final String KEY_FROM = "key_from";       //区分从哪个界面过来的 通用Key
    public static final String KEY_IS_FROM_NOTIFY = "key_from_nofity";       //区分从通知栏过来的 通用Key

    public static final String KEY_IS_DOCTOR = "key_is_doctor";       //区分是不是认证过了

    public static final String KEY_IS_FROM_ORDER = "key_from_order";       //区分从订单跳转过来 通用Key
    public static final String KEY_CONSULT_TYPE = "key_consult_type";           //会诊类型：1 正常会诊，2转诊
    public static final int KEY_CONSULT_NORMAL = 1;           //会诊类型：1 正常会诊，2转诊
    public static final int KEY_CONSULT_ZHUAN = 2;           //会诊类型：1 正常会诊，2转诊
    public static final String KEY_EXPERT_ID = "key_expert_id";
    public static final String KEY_DYNAMIC_ID = "key_dynamic_id";
    public static final String KEY_RELEASE_COMMET_TYPE = "release_comment_type";
    public static final int KEY_RELEASE_COMMET_TYPE_DYNAMIC = 0;
    public static final int KEY_RELEASE_COMMET_TYPE_LECTURE = 1;
    public static final String KEY_DYNAMIC_INFO = "key_dynamic_info";
    public static final String KEY_EXPERT_INFO = "key_expert_info";
    public static final String KEY_USER_INFO = "key_user_info";
    public static final String KEY_USER_VISITS = "key_user_visits";
    public static final String KEY_FROM_ORDER_CONFIRM = "key_from_order_confirm";
    public static final String KEY_PATIENT_INFO = "key_patient_info";
    public static final String KEY_CONSULTION_INFO = "key_consultion_info";
    public static final String KEY_CONSULTION_ID = "key_consultion_id";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_PATIENT_ID = "key_patient_id";
    public static final String KEY_PATIENT_TYPE = "key_patient_type";
    public static final String KEY_IDENTITY_NUM = "key_identity_num";
    public static final String KEY_MEDICAL_RECORD_INFO = "key_medical_record_info";
    public static final String KEY_FROM_MEDICAL_RECORD_HISTORY = "key_from_medical_record_history";
    public static final String KEY_FROM_PATIENT_DETAILS = "key_from_patient_details";
    public static final String KEY_FROM_CONSULT_RESULT = "key_from_consult_result";
    public static final String KEY_MEDICAL_RECORD_HISTORY_INFO = "key_medical_record_history_info";
    public static final String KEY_ORDER_COMMIT = "key_order_commit";
    public static final String KEY_ORDER_INFO = "key_order_info";
    public static final String KEY_ORDER_NO = "key_order_no";
    public static final String KEY_CERTIFICATION = "certification";
    public static final String KEY_DISEASE_STR = "disease";     //疾病
    public static final String KEY_CURRENT_PAGE = "current_page";     //首页当前页的index
    public static final String KEY_SEARCH_CODE = "search_code";
    public static final String KEY_IS_SELECT_DOCTOR = "key_is_select_doctor";
    public static final String EVENT_UPDATE_USER_INFO = "event_update_user_info";     //首页当前页的index

    public static final String EVENT_UPDATE_CONSUL_STATE = "event_update_room_state";     //更新会诊室状态

    public static final int REQUEST_CODE_HOME_ACTIVITY = 10000;      //主界面
    public static final int REQUEST_CODE_PERSIONAL_ACTIVITY = REQUEST_CODE_HOME_ACTIVITY + 1;      //个人资料界面
    public static final int REQUEST_CODE_ADD_PATIENT_ACTIVITY = REQUEST_CODE_PERSIONAL_ACTIVITY + 1;      //添加患者界面
    public static final int REQUEST_CODE_SET_VISIT_TIME_ACTIVITY = REQUEST_CODE_ADD_PATIENT_ACTIVITY + 1;      //添加预约时间界面
    public static final int REQUEST_CODE_PATIENT_LIBRARY_ACTIVITY = REQUEST_CODE_SET_VISIT_TIME_ACTIVITY + 1;      //患者库界面
    public static final int REQUEST_CODE_PATIENT_MEDICAL_RECORD = REQUEST_CODE_PATIENT_LIBRARY_ACTIVITY + 1;      //患者病历界面

    public static final int HAS_NOT_COLLECTED = 0;      //未收藏
    public static final int HAS_COLLECTED = 1;          //已收藏
    public static final int HAS_NOT_PRAISED = 0;        //未赞
    public static final int HAS_PRAISED = 1;            //已赞
    public static final int COLLECTION_EXPERT = 1;        //专家 1医生专家 2医学资讯
    public static final int COLLECTION_CONSULTATION = 2;            //咨询 1医生专家 2医学资讯

    public static final int MALE = 1;        //男性
    public static final int FEMALE = 2;      //女性

    public static final int EXPERT_AGREE = 1;       //同意
    public static final int EXPERT_REFUSE = 2;      //拒绝

    public static final String KEY_MAX_PIC_COUNT = "key_max_pic_count";

    /**
     * 用户信息相关
     **/
    public static final String USER_CONFIG = "user_config";
    /**
     * 用户名
     **/
    public static final String KEY_USERNAME = "username";
    /**
     * 密码
     **/
    public static final String KEY_PASSWORD = "password";

    /**
     * tokenid
     **/
    public static final String KEY_TOKENID = "tokenid";

    /**
     * userId
     **/
    public static final String KEY_USER_ID = "user_id";

    /**
     * userType
     **/
    public static final String KEY_USER_TYPE = "user_type";

    /**
     * 客服电话
     **/
    public static final String KEY_HOT_LINE = "hot_line";

    /**
     * 二维码Url
     **/
    public static final String KEY_QR_CODE_URL = "qr_code_url";

    /**
     * 邀请码
     **/
    public static final String KEY_QR_INVITE_CODE = "qr_invite_code";

    /**
     * 名医讲堂Id
     **/
    public static final String KEY_LECTURE_ID = "lecture_id";

    /**
     * versionName 服务器上最新的版本
     **/
    public static final String KEY_VERSIONNAME = "versionname";

    /**
     * 微信App秘钥
     */
    public static final String KEY_WEIXIN_APPSKEY = "eb07352fa3fa703adf5904ede9cf9ba4";

    /**
     * QQappid
     */
    public static final String KEY_QQ_APPID = "1105570700";

    /**
     * QQ App KEY
     */
    public static final String KEY_QQ_APPKEY = "z55fvZjTtYxcmVr1";

    /**
     * 微信appid
     */
    public static final String KEY_WEIXIN_APPID = "wxfcb112dc306c5d21";

    /**
     * 微信AppSecret
     */
    public static final String KEY_WEIXIN_APPSECRET = "ddb237130a2d196c84142392972a2b37";

    /**
     * 微信appid
     */
    public static final String KEY_SINA_APPID = "3039615785";

    /**
     * 微信AppSecret
     */
    public static final String KEY_SINA_APPSECRET = "fb4d4a06ace8bd039409b21187ee5bd5";

    /**
     * 微信支付分配的商户号partnerId
     */
    public static final String KEY_WEIXIN_PARTNERID = "1356536102";

    /**
     * 微信支付的最早版本
     */
    public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    /**
     * push_switch
     **/
    public static final String KEY_PUSH_SWITCH = "push_switch";

    //支付宝相关 start
    // 商户PID
    public static final String PARTNER = "2088221960270786";
    // 商户收款账号
    public static final String SELLER = "liuchengying@dearzs.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ+sovsmhebxbZhZq3UiCFKndcLaaywVUX5syERWNeDb28SJCVWd7ZJNvO+QmwVi8vMpusUBxGCCL7oZ+KUqnmjVY8Ujpp0BePjYmoae8hhToMSTsX/M5voZNtF17zctVIvYZKlKTKXACcGS36qmtLBpLljdHc74XGPMPp70RhA/AgMBAAECgYEAiMFRLY2G7Y8lcHCMWtv1COWIBIUReUYg8Ai3VWAI8TcceK1toziJut59zSiqvOhMcFrzLbwftxCJU/jeboeb949Agk/1/eyEZGNSnLBTI4wEu3qzJ/mpIFRQPDucpuqOXDBSrul24u8shjm+TuQBdzrJtsH5X2oipgbW1dnTxWECQQDM+H/33+bi2Rrjt5V3ampYzCe3Iiw7ciJQqgwQPJ+SUztWzexm2HwGB513YPfZr+Iab9axULyVrfTvcNoZrRfHAkEAx20+Enr71ZXyl1R4bLf/bAnXDSXuc1vlKIjmp9W8UukhJwZYWUCITx0k2qopDYs+pil9WN7l+74venDViNBzyQJADSEl4VTgT0uk401RL7MLWODK8non5y7qb8xUtX2MoyzdsjPCntvjJee6+Hinp7QSgasET5tiWRwpgmTyM47Y5QJAS/e0TovkxN8C81ytJxUMSpaRAiQx6mXWLJPB8as8uwStqVUEcLNheWmrsVRioRMrAVcKtcDrMPOo2GaFIUHu6QJAVzv0cqv1mh9CsjYCxuiBJebI6Qdi2VGT8yli/Jxx1AfbuFkT2ODtDZKWPsfLhJDPJrNz6XuSTf1pCIzJzPLt5Q==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    //支付宝相关 end

    //通知消息聊天发送者的身份
    public static String NOTIFY_IDENTITY_KEY;
    //通知的聊天类型
    public static TIMConversationType NOTIFY_TYPE_KEY;

    public static String WEIXINPACKAGENAME = "com.tencent.mm";

    //是否弹过引导设置出诊和转诊设置的弹框
    public static String KEY_IS_SHOW_GUIDE_SETTING_ALERT = "is_show_guide_setting_alert";

    //是否弹过引导设置昵称的弹框
    public static String KEY_IS_SHOW_NICKNAME_SETTING_ALERT = "is_show_nickname_setting_alert";
}
