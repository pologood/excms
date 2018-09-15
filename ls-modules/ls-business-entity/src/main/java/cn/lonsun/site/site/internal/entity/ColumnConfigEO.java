package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 栏目配置实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Entity
@Table(name = "cms_column_config")
public class ColumnConfigEO extends AMockEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COLUMN_CONFIG_ID")
    private Long columnConfigId;

    //主表ID
    @Column(name = "indicator_id")
    private Long indicatorId;

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

    //内容模型
    @Column(name = "CONTENT_MODEL_CODE")
    private String contentModelCode;

    //生成页面的栏目Id
    @Column(name = "gene_page_ids")
    private String genePageIds;

    //生成页面的栏目名称
    @Column(name = "gene_page_names")
    private String genePageNames;

    //跳转地址
    @Column(name = "trans_url")
    private String transUrl;

    //是否启用跳转地址
    @Column(name = "is_start_url")
    private Integer isStartUrl = Integer.valueOf(0);

    //跳转窗口
    @Column(name = "trans_window")
    private Integer transWindow = Integer.valueOf(0);

    //前台是否显示
    @Column(name = "is_show")
    private Integer isShow = 1;

    //keyWords
    @Column(name = "seo_keywords")
    private String keyWords;

    //description
    @Column(name = "seo_description")
    private String description;

    //栏目类型code
    @Column(name = "column_type_code")
    private String columnTypeCode;

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
    @Column(name="PROCESS_NAME")
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
    @Column(name = "is_tofile")
    private Integer isToFile = 0;//是否归档

    @Column(name = "to_filedate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toFileDate;//归档时间

    @Column(name = "to_fileid")
    private String toFileId;

    //栏目类别
    @Column(name="column_class_code")
    private String columnClassCode;

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

    public String getGenePageIds() {
        return genePageIds;
    }

    public void setGenePageId(String genePageIds) {
        this.genePageIds = genePageIds;
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

    public String getTransUrl() {
        return transUrl;
    }

    public void setTransUrl(String transUrl) {
        this.transUrl = transUrl;
    }

    public Integer getTransWindow() {
        return transWindow;
    }

    public void setTransWindow(Integer transWindow) {
        this.transWindow = transWindow;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
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

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
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

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
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

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
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
}
