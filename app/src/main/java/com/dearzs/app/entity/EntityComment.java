package com.dearzs.app.entity;

/**
 * 评论
     {
     "comment": "专业诊疗，值得信赖",
     "createTime": 1464776045000,
     "expertId": 1,
     "id": 1,
     "star": 4.8,
     "uid": 1,
     "user": {
     "avatar": "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
     "id": 0,
     "name": "json"
     }
     }

 */
public class EntityComment extends EntityBase {
    private String comment ;
    private long createTime ;
    private int expertId;
    private int id;
    private float star;
    private int uid ;
    private EntityUser user ;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public EntityUser getUser() {
        return user;
    }

    public void setUser(EntityUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "EntityComment{" +
                "comment='" + comment + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
