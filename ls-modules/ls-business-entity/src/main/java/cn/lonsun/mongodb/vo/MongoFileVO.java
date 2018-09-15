package cn.lonsun.mongodb.vo;

import com.mongodb.DBObject;

import java.util.Date;

public class MongoFileVO implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * mongoDb 生成的主键
     */
    private String mongoId;

    /**
     * 外键
     */
    private Long caseId;

    /**
     * 附件名称
     */
    private String fileName;

    /**
     * 上传时间
     */
    private Date uploadDate;

    /**
     * 附件后缀名
     */
    private String contentType;

    /**
     * MD5
     */

    private String md5;

    /*附加属性*/
    private DBObject metaData;

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public DBObject getMetaData() {
        return metaData;
    }

    public void setMetaData(DBObject metaData) {
        this.metaData = metaData;
    }
}
