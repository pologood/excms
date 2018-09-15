package cn.lonsun.solr.vo;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-1-13 9:46
 */
public class SolrIndexVO {

    @Field
    private String id;      //唯一标识主键 ：BaseContentEO 的主键 id

    @Field
    private Long columnId;

    @Field
    private String title; //索引标题 ：文章标题

    @Field
    private String remark; //摘要 ：文章摘要

    @Field
    private String content; //内容 ：文章内容

    @Field
    private String typeCode;//类型

    @Field
    private Integer typeSortNum;//类型排序

    @Field
    private Long siteId; //BaseContentEO 的站点id

    @Field
    private Date createDate;//BaseContentEO 的站点createDate

    @Field
    private String url;

    /******网上办事字段-begin******/

    @Field
    private String setAccord; //网上办事设置依据字段

    @Field
    private String applyCondition;//网上办事依赖条件字段

    @Field
    private String handleData;//网上办事办事资料

    @Field
    private String handleProcess;//网上办事办事流程

    /****** 网上办事字段-end ******/

    @Field
    private String type;//留言类型-政务论坛

    /******信息公开字段-begin******/

    @Field
    private String indexNum;// 索引号

    @Field
    private String fileNum;// 文号

    /****** 信息公开字段-end ******/

    @Field
    private String author;// 作者

    private String uri;

    //分组标记
    private Integer groupIndex;

    //分组个数
    private Integer count;

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

    public Integer getTypeSortNum() {
        return typeSortNum;
    }

    public void setTypeSortNum(Integer typeSortNum) {
        this.typeSortNum = typeSortNum;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSetAccord() {
        return setAccord;
    }

    public void setSetAccord(String setAccord) {
        this.setAccord = setAccord;
    }

    public String getApplyCondition() {
        return applyCondition;
    }

    public void setApplyCondition(String applyCondition) {
        this.applyCondition = applyCondition;
    }

    public String getHandleData() {
        return handleData;
    }

    public void setHandleData(String handleData) {
        this.handleData = handleData;
    }

    public String getHandleProcess() {
        return handleProcess;
    }

    public void setHandleProcess(String handleProcess) {
        this.handleProcess = handleProcess;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(String indexNum) {
        this.indexNum = indexNum;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
