package cn.lonsun.site.site.vo;

import cn.lonsun.indicator.internal.entity.FunctionEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-27 <br/>
 */
public class ColumnVO  {
    private Long columnConfigId;//栏目配置表的主键

    private String synColumnIds;//同步到栏目的ID

    private String synColumnNames;//同步到栏目的名称

    //引用到栏目的ID
    private String referColumnIds;

    //引用到栏目的名称
    private String referColumnNames;

    //引用到信息公开目录的ID
    private String referOrganCatIds;

    //引用到信息公开目录的名称
    private String referOrganCatNames;

    private String columnTypeCode;//栏目类型ID

    private String columnTypeName;//栏目类型名称

    private Integer tempType=0;//模板类型

    private String contentModelCode;//内容模型

    private String genePageIds;//生成页面栏目Id

    private String  genePageNames;//生成页面栏目名称

    private Long indicatorId;//主表ID

    private String name;//栏目名称

    private String urlPath;

    private String uri;//链接地址

    private Long parentId;//父节点

    private Integer sortNum;//排序

    private Integer isParent = Integer.valueOf(0);//是否为父节点

    private String recordStatus="Normal";//记录状态

    private Long siteId;//站点Id

    private Boolean open= Boolean.FALSE;

    private String transUrl;//跳转地址

    private Integer isStartUrl=Integer.valueOf(0); //是否启用跳转地址

    private Integer transWindow=Integer.valueOf(0);//跳转窗口

    private Boolean drag=Boolean.TRUE;

    private String type;//类型

    private Boolean isLeaf;

    private List<FunctionEO> functions;

    /*
    * 默认超管权限
    * */
    private String opt = "super";

    private boolean checked;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate = new Date();//创建时间

    private String keyWords;//关键词

    private String description;//描述


    public Long getColumnConfigId() {
        return columnConfigId;
    }

    public void setColumnConfigId(Long columnConfigId) {
        this.columnConfigId = columnConfigId;
    }

    public String getSynColumnIds() {
        return synColumnIds;
    }

    public void setSynColumnIds(String synColumnIds) {
        this.synColumnIds = synColumnIds;
    }

    public String getSynColumnNames() {
        return synColumnNames;
    }

    public void setSynColumnNames(String synColumnNames) {
        this.synColumnNames = synColumnNames;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public Integer getTempType() {
        return tempType;
    }

    public void setTempType(Integer tempType) {
        this.tempType = tempType;
    }

    public String getContentModelCode() {
        return contentModelCode;
    }

    public void setContentModelCode(String contentModelCode) {
        this.contentModelCode = contentModelCode;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenePageIds() {
        return genePageIds;
    }

    public void setGenePageIds(String genePageIds) {
        this.genePageIds = genePageIds;
    }

    public String getGenePageNames() {
        return genePageNames;
    }

    public void setGenePageNames(String genePageNames) {
        this.genePageNames = genePageNames;
    }

    public Integer getIsStartUrl() {
        return isStartUrl;
    }

    public void setIsStartUrl(Integer isStartUrl) {
        this.isStartUrl = isStartUrl;
    }

    public Integer getTransWindow() {
        return transWindow;
    }

    public void setTransWindow(Integer transWindow) {
        this.transWindow = transWindow;
    }

    public String getTransUrl() {
        return transUrl;
    }

    public void setTransUrl(String transUrl) {
        this.transUrl = transUrl;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getDrag() {
        return drag;
    }

    public void setDrag(Boolean drag) {
        this.drag = drag;
    }

    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public List<FunctionEO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionEO> functions) {
        this.functions = functions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getReferColumnIds() {
        return referColumnIds;
    }

    public void setReferColumnIds(String referColumnIds) {
        this.referColumnIds = referColumnIds;
    }

    public String getReferColumnNames() {
        return referColumnNames;
    }

    public void setReferColumnNames(String referColumnNames) {
        this.referColumnNames = referColumnNames;
    }

    public String getReferOrganCatIds() {
        return referOrganCatIds;
    }

    public void setReferOrganCatIds(String referOrganCatIds) {
        this.referOrganCatIds = referOrganCatIds;
    }

    public String getReferOrganCatNames() {
        return referOrganCatNames;
    }

    public void setReferOrganCatNames(String referOrganCatNames) {
        this.referOrganCatNames = referOrganCatNames;
    }
}
