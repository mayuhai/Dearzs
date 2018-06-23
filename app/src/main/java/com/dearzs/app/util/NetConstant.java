package com.dearzs.app.util;

public class NetConstant {
    /**
     * 服务器切换开关;SerType.TEST表示测试服务器;SerType.FORMAL表示正式服务器
     **/
    private static SerType serSwitch = SerType.TEST;

    public static void initServerSwitch(SerType serverSwitch) {
        serSwitch = serverSwitch;
    }

    /**
     * 测试接口
     **/
    private static String TAOCHE_INTERFACE_TEST_URL = "https://dev.api.dearzs.com/service";
//    private static String TAOCHE_INTERFACE_TEST_URL = "http://101.201.79.180:18080/service";

    /**
     * 正式接口
     **/
    private static String TAOCHE_INTERFACE_FORMAL_URL = "https://api.dearzs.com/service";
    /**
     * 服务器地址
     **/
    public static String TAOCHE_INTERFACE_URL = getServerHost();
//    /**
//     * 服务器接口路径
//     **/
//    private static String SERVER_PATH = "/api/";

    /**
     * 获取服务器地址
     *
     * @return
     */
    public static String getServerHost() {
        switch (serSwitch) {
            case FORMAL:
                TAOCHE_INTERFACE_URL = TAOCHE_INTERFACE_FORMAL_URL;
                break;
            default:
                TAOCHE_INTERFACE_URL = TAOCHE_INTERFACE_TEST_URL;
                break;
        }
        return TAOCHE_INTERFACE_URL;
    }

//    /**
//     * 服务器路径
//     *
//     * @return
//     */
//    public static String getServerPath() {
//        return SERVER_PATH;
//    }

    public enum SerType {
        /**
         * 测试
         **/
        TEST,
        /**
         * 正式
         **/
        FORMAL
    }

    /**
     * 与服务端交互的密钥
     */
    public static final String secretKey = "2CB3147B-D93C-964B-47AE-EEE448C84E3C";

    /**
     * 获取接口的全路径
     *
     * @return
     */
    public static String getReqUrl(String interfaceAddress) {
        return NetConstant.getServerHost() + interfaceAddress;
    }

    /**
     * 订单列表以及搜索接口
     */
    public static final String GET_ORDER_LIST = "order/orderlist";

    /**
     * 线索列表以及搜索接口
     */
    public static final String GET_CLUE_LIST = "clues/getlist";

    /**
     * 获取订单筛选状态接口
     */
    public static final String GET_ORDER_SEARCH_STATUS = "order/searchstatus";

    /**
     * 获取未读线索/订单数量接口
     */
    public static final String GET_UN_READ_COUNT = "clues/unreadcount";

    /**
     * 线索/订单列表设置已读接口
     */
    public static final String GET_READ = "clues/read";

    public static final String GET_AGE = "carinfo/age";//车龄筛选
    public static final String GET_PRICE = "carinfo/price";//价格筛选
    public static final String GET_DRIVE_MILEAGE = "carinfo/drivemileage";//里程筛选
    public static final String GET_CITY = "carinfo/city";//城市筛选
    public static final String GET_DEALER = "carinfo/dealer";//商家搜索筛选
    public static final String GET_BRAND = "carinfo/brand";//获取品牌筛选
    public static final String GET_SERIAL = "carinfo/serial";//获取品牌筛选

    public static final int KEY_LOGIN = 1;
    public static final int KEY_REGISTER = 2;
    public static final int KEY_FORGET_PSWD = 3;

    /**
     * 获取客户信息及搜索
     */
    public static final String GET_CUSTOM_LIST = "/custom/customlist";

//    /**
//     * 获取客户订单列表信息及搜索
//     */
//    public static final String GET_CUSTOM_ORDER_LIST = "/custom/customorders";

    /**
     * 获取客户订单列表信息及搜索
     */
    public static final String GET_MY_WALLET_YUE = "--------";

    /**
     * 开启、关闭接收线索
     */
    public static final String GET_IS_RECEIVE_CLUE = "/my/clues";

    /**
     * 忘记密码（可修改密码）接口
     */
    public static final String USER_FORGET_PWD = "/account/uppasswd";

    /**
     * 注册接口
     */
    public static final String USER_REGISTER = "/account/reg";

    /**
     * 验证token
     */
    public static final String TOKEN_VALIDATE = "/account/tokenvalidate";

    /**
     * 登录接口
     */
    public static final String USER_LOGIN = "/account/login";

    /**
     * 登出接口
     */
    public static final String USER_LOGIN_OUT = "/account/logout";

    /**
     * 忘记密码
     */
    public static final String USER_FOGET_PASSWORD = "/account/uppasswd";

    /**
     * 修改密码
     */
    public static final String USER_MODIFY_PASSWORD = "/account/modpasswd";

    /**
     * 反馈接口
     */
    public static final String FEEDBACK = "/feedback";

    /**
     * 获取疾病分类列表接口
     */
    public static final String GET_DISEASE_CATEGORY_LIST = "/disease";

    /**
     * 获取城市列表接口
     */
    public static final String GET_HOSPITAL_CITY_LIST = "/hospital/city/list";

    /**
     * 获取医院列表接口
     */
    public static final String GET_HOSPITAL_LIST = "/hospital/list?";

    /**
     * 获取医院科室列表接口
     */
    public static final String GET_HOSPITAL_DPMT_LIST = "/hospital/dpmt/list";

    /**
     * 获取验证手机验证码接口
     */
    public static final String GET_VALID_PHONE_CODE = "/code/phone";

    /**
     * 获取短信验证码接口
     */
    public static final String GET_MOBILE_CODE = "/login/getmobilecode";

    /**
     * 获取修改密码接口
     */
    public static final String GET_MODIFY_PASSWORD = "/login/modifypassword";

    /**
     * 获取收藏列表接口
     */
    public static final String GET_COLLECTION_LIST = "/account/fav/list";

    /**
     * 获取Banner列表接口
     */
    public static final String GET_BANNER_LIST = "/banner";

    /**
     * 获取咨询列表接口
     */
    public static final String GET_CONSULTATATION_LIST = "/news/list";

    /**
     * 获取咨询详情接口
     */
    public static final String GET_CONSULTATATION_DETAIL = "/news/detail";

    /**
     * 获取会诊室列表接口
     */
    public static final String GET_CONSULTA_LIST = "/consult/list";

    /**
     * 开始会诊接口
     */
    public static final String START_CONSULTA = "/consult/start";

    /**
     * 结束会诊接口
     */
    public static final String END_CONSULTA = "/consult/end";

    /**
     * 获取会诊详情接口
     */
    public static final String GET_CONSULTA_DETAIL = "/consult/detail";

    /**
     * 获取消息列表接口
     */
    public static final String GET_MESSAGE_LIST = "/msg/list";

    /**
     * 获取收支明细列表接口
     */
    public static final String GET_PAYMENTS_BALANCE_LIST = "/account/balance/detail";

    /**
     * 图片上传接口
     */
    public static final String GET_UPLOAD_PIC = "/upload";

    /**
     * 检查app更新接口
     */
    public static final String GET_APP_UPDATA = "/v/android";

    /**
     * 获取专家列表
     */
    public static final String GET_EXPERT_LIST = "/expert";

    /**
     * 获取主治医生列表
     */
    public static final String GET_EXPERT_LIST_2 = "/expert2";

    /**
     * 专家详情
     */
    public static final String GET_EXPERT_DETAIL = "/expert/detail";

    /**
     * 社区--获取动态列表
     */
    public static final String GET_COMMUNTITY_DYNAMIC_DETAIL = "/dynamic/list";

    /**
     * 社区--动态赞和取消赞
     */
    public static final String DEAL_DYNAMIC_PRAISE = "/dynamic/praise";

    /**
     * 社区--获取动态评论列表
     */
    public static final String GET_DYNAMIC_COMMENT_LIST = "/dynamic/c/list";

    /**
     * 社区--动态发布评论
     */
    public static final String DYNAMIC_RELEASE_COMMENT = "/dynamic/c/pub";

    /**
     * 社区--删除动态评论
     */
    public static final String DYNAMIC_DEL_COMMENT = "/dynamic/c/del";

    /**
     * 资讯赞和取消赞
     */
    public static final String DEAL_MEDICAL_CONSULTATION_PRAISE = "/news/praise";

    /**
     * 社区--删除动态
     */
    public static final String DELETE_DYNAMIC = "/dynamic/delete";

    /**
     * 社区--发布动态
     */
    public static final String GET_RELEASE_DYNAMIC = "/dynamic/add";

    /**
     * 社区--动态评论
     */
    public static final String GET_RELEASE_DYNAMIC_COMMENT = "/dynamic/comment";

    /**
     * 专家评论列表
     */
    public static final String GET_EXPERT_COMMENT_LIST = "/expert/comment";

    /**
     * 收藏和取消收藏
     */
    public static final String DEAL_COLLECTION = "/account/fav";

    /**
     * 获取患者库列表
     */
    public static final String GET_PATIENT_LIBRARY_LIST = "/account/patient";

    /**
     * 患者查询
     */
    public static final String PATIENT_SEARCH = "/account/patient/search";

    /**
     * 患者添加
     */
    public static final String PATIENT_ADD = "/account/patient/add";

    /**
     * 患者详情
     */
    public static final String PATIENT_DETAIL = "/account/patient/detail";

    /**
     * 患者删除
     */
    public static final String PATIENT_DELETE = "/account/patient/delete";

    /**
     * 获取患者病历
     */
    public static final String PATIENT_MEDICAL_RECORD = "/account/patient/history";

    /**
     * 获取病历详情
     */
    public static final String MEDICAL_RECORD_DETAILS = "/account/patient/history/detail";

    /**
     * 创建/修改患者病历
     */
    public static final String PATIENT_MODIFY_MEDICAL_RECORD = "/account/patient/history/add";

    /**
     * 获取患者病历更新列表
     */
    public static final String PATIENT_MEDICAL_RECORD_HISTORY_LIST = "/account/patient/history/list";

    /**
     * 更新用户信息
     */
    public static final String UPDATE_USER_INFO = "/account/update";

    /**
     * 提交订单
     */
    public static final String ORDER_COMMIT = "/order/commit";

    /**
     * 订单详情
     */
    public static final String ORDER_DETAIL = "/order/detail";

    /**
     * 我的订单列表
     */
    public static final String ORDER_LIST = "/order/myorder";

    /**
     * 医生认证
     */
    public static final String DOCTOR_CERTIFICATION = "/account/cert";

    /**
     * 取消订单（下单者在医生应答前取消）
     * 下单者在医生应答前取消，全额退款
     */
    public static final String ORDER_CANCEL_1TO24 = "/order/cancel/1to24";

    /**
     * 取消订单（会诊开始前取消）
     * 等待会诊开始前24小时前取消订单,全额退款
     * 等待会诊开始前24小时内取消订单,扣除1%会诊费用
     */
    public static final String ORDER_CANCEL_3TO22 = "/order/cancel/3to22";

    /**
     * 专家处理订单（接单或拒绝）
     */
    public static final String ORDER_EXPERT = "/order/expert";

    /**
     * /order/paytype 订单支付方式修改
     */
    public static final String ORDER_PAYTYPE_MODIFY = "/order/paytype";

    /**
     *  /order/evaluate 评价订单
     */
    public static final String ORDER_EVALUATE = "/order/evaluate";

    /**
     *  /consult/expert		专家进入视频直播
     */
    public static final String CONSULT_EXPERT = "/consult/expert";

    /**
     * 钱包助手 h5/bag
     */
    public static final String WALLET_HELP = "/h5/bag";

    /**
     * 安全协议   h5/security
     */
    public static final String SECURITY_LICENCE = "/h5/security";

    /**
     * 隐私协议
     */
    public static final String PRIVACY_LICENCE = "/h5/privacy";

    /**
     * 支付帮助
     */
    public static final String PAY_HELP_LICENCE = "/h5/payhelp";

    /**
     * 注册许可协议
     */
    public static final String REGISTER_LICENCE = "/h5/license";

    /**
     * 患者支付帮助文档
     */
    public static final String PATIENT_ORDER_HELP = "/h5/porder";

    /**
     * 结束转诊
     */
    public static final String REFERRAL_END = "/order/referral/end";

    /**
     * 专家讲堂类型
     */
    public static final String DOCTOR_FORUM_TYPES = "/lecture/types";

    /**
     * 专家讲堂列表
     */
    public static final String DOCTOR_FORUM_LIST = "/lecture/list";

    /**
     * 专家讲堂详情
     */
    public static final String DOCTOR_FORUM_DETAIL = "/lecture/detail";

    /**
     * 专家讲堂评论
     */
    public static final String DOCTOR_FORUM_COMMENTS = "/lecture/comments";

    /**
     * 专家讲堂发表评论
     */
    public static final String DOCTOR_FORUM_RELEASE_COMMENTS = "/lecture/c/pub";

    /**
     * 专家讲堂赞和取消赞
     */
    public static final String DOCTOR_FORUM_PRAISE = "/lecture/praise";

}
