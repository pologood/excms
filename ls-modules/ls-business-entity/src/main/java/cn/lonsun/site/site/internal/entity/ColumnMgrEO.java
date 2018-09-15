package cn.lonsun.site.site.internal.entity;

/**
 * 栏目视图实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-8<br/>
 */

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cms_column_mgr")
public class ColumnMgrEO {

    //主表ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indicator_id")
    private Long indicatorId;

    // 名称
    @Column(name = "name")
    private String name;

    @Column(name = "SHORTNAME")
    private String shortName;

    // 父菜单的主键
    @Column(name = "parent_id")
    private Long parentId;

    // 生成静态的完整路径
    @Column(name = "url_path")
    private String urlPath;

    // 序号
    @Column(name = "sort_num")
    private Integer sortNum;

    // 是否是父亲
    @Column(name = "is_parent")
    private Integer isParent = Integer.valueOf(0);

    //创建日期
    @Column(name = "create_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate;

    //站点ID
    @Column(name = "site_id")
    private Long siteId;

    // 类型
    @Column(name = "type")
    private String type;

    //栏目配置ID
    @Column(name = "COLUMN_CONFIG_ID")
    private Long columnConfigId;

    //同步到栏目的ID
    @Column(name = "SYN_COLUMN_IDS")
    private String synColumnIds;

    //同步到栏目的名称
    @Column(name = "SYN_COLUMN_NAMES")
    private String synColumnNames;

    //引用到栏目的ID
    @Column(name = "REFER_COLUMN_IDS")
    private String referColumnIds;

    //引用到栏目的名称
    @Column(name = "REFER_COLUMN_NAMES")
    private String referColumnNames;

    //引用到信息公开目录的ID
    @Column(name = "REFER_ORGAN_CAT_IDS")
    private String referOrganCatIds;

    //引用到信息公开目录的名称
    @Column(name = "REFER_ORGAN_CAT_NAMES")
    private String referOrganCatNames;

    //生成页面的栏目Id
    @Column(name = "gene_page_ids")
    private String genePageIds;

    //内容模型
    @Column(name = "CONTENT_MODEL_CODE")
    private String contentModelCode;

    //生成页面的栏目名称
    @Column(name = "gene_page_names")
    private String genePageNames;

    @Column(name = "is_submit")
    private Integer isSubmit = 1;

    //是否启用跳转地址
    @Column(name = "is_start_url")
    private Integer isStartUrl = Integer.valueOf(0);

    //跳转地址
    @Column(name = "trans_url")
    private String transUrl;

    //跳转窗口
    @Column(name = "trans_window")
    private Integer transWindow = Integer.valueOf(0);

    //栏目类型code值
    @Column(name = "column_type_code")
    private String columnTypeCode;

    //内容模型配置字段
    @Column(name = "content")
    private String content;

    //记录字段
    @Column(name = "record_status")
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString();

    //栏目是否显示
    @Column(name = "is_show")
    private Integer isShow = 1;

    //keyWords
    @Column(name = "seo_keywords")
    private String keyWords;

    //description
    @Column(name = "seo_description")
    private String description;

    //链接
    @Column(name = "is_logo")
    private Integer isLogo = 0;//是否启用Logo

    @Column(name = "height")
    private Integer height;//图高度

    @Column(name = "width")
    private Integer width;//图宽度

    @Column(name = "num")
    private Long num = 0L;//条数

    @Column(name = "link_code")
    private String linkCode;//链接特殊码，用于指定生成的link.vm文件

    //绑定流程id
    @Column(name="process_id")
    private Long processId;

    //绑定流程name
    @Column(name="process_name")
    private String processName;

    @Column(name="title_count")
    private Integer titleCount;//标题字数

    @Column(name="remarks_count")
    private Integer remarksCount;//摘要字数

    @Column(name = "update_cycle")
    private Integer updateCycle = 0;//更新周期

    @Column(name = "yellow_card_warning")
    private Integer yellowCardWarning = 0;//黄牌警示天数

    @Column(name = "red_card_warning")
    private Integer redCardWarning = 0;//红牌警示天数

    //栏目类别
    @Column(name="column_class_code")
    private String columnClassCode;

    @Transient
    private List<FunctionEO> functions;

    @Transient
    private Integer lev;

    @Transient
    private List<ColumnMgrEO> childs;

    @Transient
    private Long commentNum;

    /*
    * 默认超管权限
    * */
    @Transient
    private String opt = "super";

    @Transient
    private boolean checked;

    @Transient
    private String active; //确定选项是否选中

    @Transient
    private String uri;

    @Transient
    private Long relId;

    @Transient
    private String columnStrId;

    @Transient
    private String parentStrId;

    @Transient
    private String unitIds;

    @Transient
    private String unitNames;

    @Transient
    private boolean isHave;

    @Transient
    private String contentModelName;

    @Transient
    private Long firstColumnId;//用于颍州网上办事

    //是否统计
    @Transient
    private Long count = 0L;

    @Transient
    private Integer isCount = 0;

    @Transient
    private Integer isReferColumn = 0;//0-否 1-是

    @Transient
    private Integer rowIndex=0;
    @Column(name = "is_tofile")
    private Integer isToFile = 0;//是否归档
    @Column(name = "to_filedate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toFileDate;//归档时间
    @Column(name = "to_fileid")
    private String toFileId;


    @Transient
    private String columnClassName;

    public Integer getIsToFile() {
        return isToFile;
    }

    public void setIsToFile(Integer isToFile) {
        this.isToFile = isToFile;
    }

    public Date getToFileDate() {
        return toFileDate;
    }

    public void setToFileDate(Date toFileDate) {
        this.toFileDate = toFileDate;
    }

    public String getToFileId() {
        return toFileId;
    }

    public void setToFileId(String toFileId) {
        this.toFileId = toFileId;
    }

    public Integer getIsSubmit() {
        return isSubmit;
    }

    public void setIsSubmit(Integer isSubmit) {
        this.isSubmit = isSubmit;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getGenePageIds() {
        return genePageIds;
    }

    public void setGenePageIds(String genePageIds) {
        this.genePageIds = genePageIds;
    }

    public String getContentModelCode() {
        return contentModelCode;
    }

    public void setContentModelCode(String contentModelCode) {
        this.contentModelCode = contentModelCode;
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

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTransUrl() {
        return transUrl;
    }

    public void setTransUrl(String transUrl) {
        this.transUrl = transUrl;
    }

    public List<FunctionEO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionEO> functions) {
        this.functions = functions;
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


    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Integer getLev() {
        return lev;
    }

    public void setLev(Integer lev) {
        this.lev = lev;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<ColumnMgrEO> getChilds() {
        return childs;
    }

    public void setChilds(List<ColumnMgrEO> childs) {
        this.childs = childs;
    }

    public Long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Long commentNum) {
        this.commentNum = commentNum;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public String getParentStrId() {
        return parentStrId;
    }

    public void setParentStrId(String parentStrId) {
        this.parentStrId = parentStrId;
    }

    public String getColumnStrId() {
        return columnStrId;
    }

    public void setColumnStrId(String columnStrId) {
        this.columnStrId = columnStrId;
    }

    public String getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(String unitIds) {
        this.unitIds = unitIds;
    }

    public String getUnitNames() {
        return unitNames;
    }

    public void setUnitNames(String unitNames) {
        this.unitNames = unitNames;
    }

    public boolean getIsHave() {
        return isHave;
    }

    public void setIsHave(boolean have) {
        isHave = have;
    }

    public String getContentModelName() {
        return contentModelName;
    }

    public void setContentModelName(String contentModelName) {
        this.contentModelName = contentModelName;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getIsLogo() {
        return isLogo;
    }

    public void setIsLogo(Integer isLogo) {
        this.isLogo = isLogo;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public Long getFirstColumnId() {
        return firstColumnId;
    }

    public void setFirstColumnId(Long firstColumnId) {
        this.firstColumnId = firstColumnId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getIsCount() {
        return isCount;
    }

    public void setIsCount(Integer isCount) {
        this.isCount = isCount;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getTitleCount() {
        return titleCount;
    }

    public void setTitleCount(Integer titleCount) {
        this.titleCount = titleCount;
    }

    public Integer getRemarksCount() {
        return remarksCount;
    }

    public void setRemarksCount(Integer remarksCount) {
        this.remarksCount = remarksCount;
    }

    public Integer getUpdateCycle() {
        return updateCycle;
    }

    public void setUpdateCycle(Integer updateCycle) {
        this.updateCycle = updateCycle;
    }

    public Integer getYellowCardWarning() {
        return yellowCardWarning;
    }

    public void setYellowCardWarning(Integer yellowCardWarning) {
        this.yellowCardWarning = yellowCardWarning;
    }

    public Integer getRedCardWarning() {
        return redCardWarning;
    }

    public void setRedCardWarning(Integer redCardWarning) {
        this.redCardWarning = redCardWarning;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getColumnClassCode() {
        return columnClassCode;
    }

    public void setColumnClassCode(String columnClassCode) {
        this.columnClassCode = columnClassCode;
    }

    public String getColumnClassName() {
        return columnClassName;
    }

    public void setColumnClassName(String columnClassName) {
        this.columnClassName = columnClassName;
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

    public Integer getIsReferColumn() {
        return isReferColumn;
    }

    public void setIsReferColumn(Integer isReferColumn) {
        this.isReferColumn = isReferColumn;
    }
}
