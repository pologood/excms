package cn.lonsun.solr.vo;

/**
 * @author gu.fei
 * @version 2016-1-13 9:46
 */
public class QueryResultVO {

    private String id;      //唯一标识主键 ：BaseContentEO 的主键 id

    private Long columnId;

    private String columnName;

    private String title; //索引标题 ：文章标题

    private String remark;//摘要

    private String content; //内容 ：文章内容

    private String typeCode;//类型

    private Long siteId; //BaseContentEO 的站点id

    private String link; //链接地址

    private String createDate;//BaseContentEO 的站点createDate

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
