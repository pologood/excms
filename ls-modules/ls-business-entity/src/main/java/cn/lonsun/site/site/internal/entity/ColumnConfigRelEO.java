package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 栏目配置关联实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-7<br/>
 */

@Entity
@Table(name="cms_column_config_rel")
public class ColumnConfigRelEO extends AMockEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    //主表ID
    @Column(name="indicator_id")
    private Long indicatorId;

    //是否为父节点
    @Column(name="is_parent")
    private Integer isParent = Integer.valueOf(0);

    //栏目名称
    @Column(name="name")
    private String name;

    //排序
    @Column(name="sort_num")
    private Integer sortNum;

    //内容模型
    @Column(name="content_model_code")
    private String contentModelCode;

    //内容模型
    @Column(name="column_type_code")
    private String columnTypeCode;

    //跳转地址
    @Column(name="trans_url")
    private String transUrl;

    //是否启用跳转地址
    @Column(name="is_start_url")
    private Integer isStartUrl=Integer.valueOf(0);

    //跳转窗口
    @Column(name="trans_window")
    private Integer transWindow=Integer.valueOf(0);

    @Column(name="is_show")
    private Integer isShow=1;

    //关键词
    @Column(name="seo_keywords")
    private String keyWords;

    //描述
    @Column(name="seo_description")
    private String description;

    // 是否显示
    @Column(name = "IS_Hide")
    private Boolean isHide = Boolean.FALSE;

    //同步到栏目的ID
    @Column(name="SYN_COLUMN_IDS")
    private String synColumnIds;

    //同步到栏目的名称
    @Column(name="SYN_COLUMN_NAMES")
    private String synColumnNames;


    //生成页面的栏目Id
    @Column(name="gene_page_ids")
    private String genePageIds;

    //生成页面的栏目名称
    @Column(name="gene_page_names")
    private String genePageNames;

    //链接
    @Column(name="is_logo")
    private Integer isLogo=0;//是否启用Logo

    @Column(name="height")
    private Integer height;//图高度

    @Column(name="width")
    private Integer width;//图宽度

    @Column(name="num")
    private Long num=0L;//条数
    @Column(name = "is_tofile")
    private Integer isToFile = 0;//是否归档
    @Column(name = "to_filedate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toFileDate;//归档时间
    @Column(name = "to_fileid")
    private String toFileId;
    //站点ID
    private Long siteId;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
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

    public Integer getIsStartUrl() {
        return isStartUrl;
    }

    public void setIsStartUrl(Integer isStartUrl) {
        this.isStartUrl = isStartUrl;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean hide) {
        isHide = hide;
    }

    public String getContentModelCode() {
        return contentModelCode;
    }

    public void setContentModelCode(String contentModelCode) {
        this.contentModelCode = contentModelCode;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
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

    public String getGenePageNames() {
        return genePageNames;
    }

    public void setGenePageNames(String genePageNames) {
        this.genePageNames = genePageNames;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    public Boolean getHide() {
        return isHide;
    }

    public void setHide(Boolean hide) {
        isHide = hide;
    }

    public Integer getIsLogo() {
        return isLogo;
    }

    public void setIsLogo(Integer isLogo) {
        this.isLogo = isLogo;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
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
}
