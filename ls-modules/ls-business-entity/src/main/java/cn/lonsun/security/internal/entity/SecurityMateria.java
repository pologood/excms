package cn.lonsun.security.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2016-12-12.
 */
@Entity
@Table(name = "CMS_SECURITY_MATERIA")
public class SecurityMateria extends AMockEntity {

    @Id
    @Column(name = "MATERIA_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long materiaId;
    /**
     * 名称
     */

    @Column(name = "MATERIA_NAME")
    private String materiaName;
    /**
     * 年份
     */
    @Column(name = "YEAR")
    private Integer year;
    /**
     * 期刊号
     */
    @Column(name="PERIODICAL")
    private Integer periodical;

    @Column(name = "MONTH ")
    private Integer month ;
    /**
     * 文件路径
     */
    @Column(name = "FILE_PATH")
    private String filePath;
    /**
     * 文件类型
     */
    @Column(name = "FILE_TYPE")
    private String fileType;

    @Column(name = "SITE_ID")
    private Long siteId;

    //基础表主键
    @Column(name = "BASE_CONTENT_ID")
    private Long baseContentId;
    @Column(name = "AUTHOR")
    private String author;
    @Transient
    private Integer isPublish = 0;
    @Transient
    private String   imageLink;
    public Long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Long materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaName() {
        return materiaName;
    }

    public void setMateriaName(String materiaName) {
        this.materiaName = materiaName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPeriodical() {
        return periodical;
    }

    public void setPeriodical(Integer periodical) {
        this.periodical = periodical;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getBaseContentId() {
        return baseContentId;
    }

    public void setBaseContentId(Long baseContentId) {
        this.baseContentId = baseContentId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
