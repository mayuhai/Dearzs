package com.dearzs.app.entity;

import com.dearzs.app.entity.resp.RespGetPatientMedicalRecord;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;

import java.util.ArrayList;
import java.util.List;

public class ServiceParam {
    private String uid;
    private String phone;
    private Integer type;
    private String code;
    private String regId;
    private Integer optsType;
    //    private Integer page = 1;
    //    private Integer rows = 10;
    private Integer page;
    private Integer rows;
    private String tokenId;
    private String name;
    private String lng;
    private String lat;
    private String consigneeName;
    private String consigneeCountry;
    private String consigneeProvince;
    private String consigneeCity;
    private String consigneeAddress;
    private String consigneePhonePre;
    private String consigneePhone;
    private Integer state;
    private String waybillNumber;
    private String avatar;
    private String billNumbers;
    private Long transferId;
    private String longUrl;
    private String password;
    private Integer gender;
    private String email;
    private Integer medicalAge;
    private String job;
    private String area;
    private String[] images;
    private Long hid;
    private String cardNo;
    private String address;
    private Integer age;
    private String intro;
    private Long hospitalId;
    private Long departmentId;
    private Long dynamicId;
    private Long commentId;
    private String content;
    private String video;
    private List<PDynamicImg> dynamicImgs;
    private Double visitMoney;
    private Integer visitState;
    private Double referralMoney;
    private Integer referralState;
    private String visitUnit;
    private Long newsId;
    private Long fid;
    private String date;
    private String time;
    private String sortOrder;
    private String sortName;
    private EntityRequestPatientMedicalRecord history;
    private String historyId;
    private Long patientId;
    private Long expertId;
    private String orderDate;
    private String orderTime;
    private String orderPhone;
    private String remark;
    private Integer payType;
    private Integer orderStateId;
    private String orderNo;
    private Integer orderTimeHour;
    private Integer orderTimeMinute;
    private String expertRemark;
    private Integer week;
    private String caseAnalysis;
    private String diseaseDiagnosis;
    private String treatmentPrograms;
    private String result;
    private Long consultId;
    private Double star;
    private String comment;
    private String label;
    private String djob;
    private String bankAccount;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankAccountBranch;
    private int isQr;
    private long doctorId;

    private long typeId;
    private long lectureId;

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public int getIsQr() {
        return isQr;
    }

    public void setIsQr(int isQr) {
        this.isQr = isQr;
    }

    public void setLectureId(long lectureId) {
        this.lectureId = lectureId;
    }

    public long getLectureId() {
        return lectureId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public long getTypeId() {
        return typeId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountBranch() {
        return bankAccountBranch;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccountBranch(String bankAccountBranch) {
        this.bankAccountBranch = bankAccountBranch;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getDjob() {
        return djob;
    }

    public void setDjob(String djob) {
        this.djob = djob;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getConsultId() {
        return consultId;
    }

    public void setConsultId(Long consultId) {
        this.consultId = consultId;
    }

    public void setCaseAnalysis(String caseAnalysis) {
        this.caseAnalysis = caseAnalysis;
    }

    public String getCaseAnalysis() {
        return caseAnalysis;
    }

    public void setOptsType(Integer optsType) {
        this.optsType = optsType;
    }

    public String getDiseaseDiagnosis() {
        return diseaseDiagnosis;
    }

    public void setDiseaseDiagnosis(String diseaseDiagnosis) {
        this.diseaseDiagnosis = diseaseDiagnosis;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setTreatmentPrograms(String treatmentPrograms) {
        this.treatmentPrograms = treatmentPrograms;
    }

    public String getResult() {
        return result;
    }

    public String getTreatmentPrograms() {
        return treatmentPrograms;
    }

    private List<EntityUserVisits> userVisits;

    public List<EntityUserVisits> getUserVisits() {
        return userVisits;
    }

    public void setUserVisits(List<EntityUserVisits> userVisits) {
        this.userVisits = userVisits;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistory(EntityRequestPatientMedicalRecord history) {
        this.history = history;
    }

    public EntityRequestPatientMedicalRecord getHistory() {
        return history;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOptsType(int optsType) {
        this.optsType = optsType;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Long getFid() {
        return fid;
    }

    public int getOptsType() {
        return optsType;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Double getVisitMoney() {
        return visitMoney;
    }

    public void setVisitMoney(Double visitMoney) {
        this.visitMoney = visitMoney;
    }

    public String getVisitUnit() {
        return visitUnit;
    }

    public void setVisitUnit(String visitUnit) {
        this.visitUnit = visitUnit;
    }

    public List<PDynamicImg> getDynamicImgs() {
        return dynamicImgs;
    }

    public void setDynamicImgs(List<PDynamicImg> dynamicImgs) {
        this.dynamicImgs = dynamicImgs;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Long getHid() {
        return hid;
    }

    public void setHid(Long hid) {
        this.hid = hid;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public Integer getMedicalAge() {
        return medicalAge;
    }

    public void setMedicalAge(Integer medicalAge) {
        this.medicalAge = medicalAge;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getBillNumbers() {
        return billNumbers;
    }

    public void setBillNumbers(String billNumbers) {
        this.billNumbers = billNumbers;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneeCountry() {
        return consigneeCountry;
    }

    public void setConsigneeCountry(String consigneeCountry) {
        this.consigneeCountry = consigneeCountry;
    }

    public String getConsigneeProvince() {
        return consigneeProvince;
    }

    public void setConsigneeProvince(String consigneeProvince) {
        this.consigneeProvince = consigneeProvince;
    }

    public String getConsigneeCity() {
        return consigneeCity;
    }

    public void setConsigneeCity(String consigneeCity) {
        this.consigneeCity = consigneeCity;
    }

    public String getConsigneeAddress() {
        return consigneeAddress;
    }

    public void setConsigneeAddress(String consigneeAddress) {
        this.consigneeAddress = consigneeAddress;
    }

    public String getConsigneePhonePre() {
        return consigneePhonePre;
    }

    public void setConsigneePhonePre(String consigneePhonePre) {
        this.consigneePhonePre = consigneePhonePre;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }


    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public Integer getVisitState() {
        return visitState;
    }

    public void setVisitState(Integer visitState) {
        this.visitState = visitState;
    }

    public Double getReferralMoney() {
        return referralMoney;
    }

    public void setReferralMoney(Double referralMoney) {
        this.referralMoney = referralMoney;
    }

    public Integer getReferralState() {
        return referralState;
    }

    public void setReferralState(Integer referralState) {
        this.referralState = referralState;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getExpertId() {
        return expertId;
    }

    public void setExpertId(Long expertId) {
        this.expertId = expertId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getOrderStateId() {
        return orderStateId;
    }

    public void setOrderStateId(Integer orderStateId) {
        this.orderStateId = orderStateId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderTimeHour() {
        return orderTimeHour;
    }

    public void setOrderTimeHour(Integer orderTimeHour) {
        this.orderTimeHour = orderTimeHour;
    }

    public Integer getOrderTimeMinute() {
        return orderTimeMinute;
    }

    public void setOrderTimeMinute(Integer orderTimeMinute) {
        this.orderTimeMinute = orderTimeMinute;
    }

    public String getExpertRemark() {
        return expertRemark;
    }

    public void setExpertRemark(String expertRemark) {
        this.expertRemark = expertRemark;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static void main(String[] args) {
        ServiceParam param = new ServiceParam();
        param.setTokenId("851bfe888ccb1f77277a4a51e6e7a409a4156cd3");
        param.setContent("动态动态");

        List<PDynamicImg> list = new ArrayList<>();
        PDynamicImg img = new PDynamicImg();
        img.setImg("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
        img.setSeq(1);
        list.add(img);
        PDynamicImg img2 = new PDynamicImg();
        img2.setImg("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
        img2.setSeq(2);
        list.add(img2);
        param.setDynamicImgs(list);
        System.out.println(new Gson().toJson(param));
    }
}
