package cn.lonsun.content.optrecord.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2018-01-18 9:12
 */
@Entity
@Table(name = "CMS_CONTENT_OPT_RECORD")
public class ContentOptRecordEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1300742296285582640L;

    /**
     * 操作类型
     */
    public enum Type {
        add, //添加
        edit, //编辑
        delete, //删除
        publish, //发布
        move, //移动
        copy, //复制
        push, //推送
        submit, //报送
        setTop,//置顶
        setTitle,//设置标题新闻
        setNew,//加新
        refer //引用
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    //文章内容ID
    @Column(name = "CONTENT_ID")
    private Long contentId;

    //操作类型
    @Column(name = "OPT_TYPE")
    private String optType;

    //操作状态
    @Column(name = "STATUS")
    private Integer status;

    //文章标题
    @Column(name = "TITLE")
    private String title;

    //操作人ID
    @Column(name = "OPT_USER_ID")
    private Long optUserId;

    //操作人名称
    @Column(name = "OPT_USER_NAME")
    private String optUserName;

    //操作人单位ID
    @Column(name = "OPT_ORGAN_ID")
    private Long optOrganId;

    //操作人单位名称
    @Column(name = "OPT_ORGAN_NAME")
    private String optOrganName;

    //操作栏目ID
    @Column(name = "COLUMN_ID")
    private Long columnId;

    //操作栏目名称
    @Column(name = "COLUMN_NAME")
    private String columnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getOptUserId() {
        return optUserId;
    }

    public void setOptUserId(Long optUserId) {
        this.optUserId = optUserId;
    }

    public String getOptUserName() {
        return optUserName;
    }

    public void setOptUserName(String optUserName) {
        this.optUserName = optUserName;
    }

    public Long getOptOrganId() {
        return optOrganId;
    }

    public void setOptOrganId(Long optOrganId) {
        this.optOrganId = optOrganId;
    }

    public String getOptOrganName() {
        return optOrganName;
    }

    public void setOptOrganName(String optOrganName) {
        this.optOrganName = optOrganName;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
