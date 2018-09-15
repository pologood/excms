package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 站点视图实体类 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-19<br/>
 */
@Entity
@Table(name = "cms_site_mgr")
public class SiteMgrEO {
    //主表ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indicator_id")
    private Long indicatorId;

    // 名称
    @Column(name = "name")
    private String name;

    //站点全称
    @Column(name = "site_title")
    private String siteTitle;

    //站点标识符
    @Column(name="site_id_code")
    private String siteIDCode;

    // 父菜单的主键
    @Column(name = "parent_id")
    private Long parentId;

    //视频转换地址
    @Column(name = "uri")
    private String uri;

    // 序号
    @Column(name = "sort_num")
    private Integer sortNum;

    // 是否是父亲
    @Column(name = "is_parent")
    private Integer isParent = Integer.valueOf(0);

    //创建时间
    @Column(name = "create_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createDate;

    // 类型
    @Column(name = "type")
    private String type;

    //站点配置ID
    @Column(name = "site_config_id")
    private Long siteConfigId;

    //关键词
    @Column(name = "seo_keywords")
    private String keyWords;

    //描述
    @Column(name = "seo_description")
    private String description;

    //视频转换地址
    @Column(name = "video_trans_url")
    private String videoTransUrl;

    //是否启用视频转换
    @Column(name = "is_video_trans")
    private Integer isVideoTrans = 0;

    //记录状态
    @Column(name = "record_status")
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString();

    //单位Id
    @Column(name = "unit_ids")
    private String unitIds;

    //单位名称
    @Column(name = "unit_names")
    private String unitNames;

    //首页模板
    @Column(name = "index_temp_id")
    private Long indexTempId;

    //评论模板
    @Column(name = "comment_temp_id")
    private Long commentTempId;

    //纠错模板
    @Column(name = "error_temp_id")
    private Long errorTempId;

    //信息公开模板
    @Column(name = "public_temp_id")
    private Long publicTempId;

    //站点搜索模板
    @Column(name = "search_temp_id")
    private Long searchTempId;

    //会员中心模板
    @Column(name = "member_id")
    private Long memberId;

    //站长管理的站点Id
    @Column(name = "station_id")
    private String stationId;

    //查询密码
    @Column(name = "station_pwd")
    private String stationPwd;

    //绑定公共栏目
    @Column(name = "com_column_id")
    private Long comColumnId;

    //站点模板
    @Column(name = "site_temp_id")
    private Long siteTempId;

    //是否启用wap站
    @Column(name = "is_wap")
    private Integer isWap = 0;

    //wap首页模板
    @Column(name = "wap_temp_id")
    private Long wapTempId;

    //wap信息公开首页模板
    @Column(name = "wap_public_temp_id")
    private Long wapPublicTempId;

    //手机搜索模板
    @Column(name = "phone_temp_id")
    private Long phoneTempId;

    //扩展字段区分站点类型，现有TYPE涉及到其它业务逻辑，故不在上面增加类型参数
    @Column(name = "SITE_TYPE")
    private Integer siteType = 0;

    //对应子站专题ID，应用于专题子站
    @Column(name = "SPECIAL_ID")
    private Long specialId;

    //对应该模板主题ID
    @Transient
    private Long themeId;

    @Transient
    private boolean isLeaf;

    @Transient
    private List<FunctionEO> functions;

    @Transient
    private String opt = "super";

    @Transient
    private boolean checked;

    @Transient
    private Boolean isHave;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSiteConfigId() {
        return siteConfigId;
    }

    public void setSiteConfigId(Long siteConfigId) {
        this.siteConfigId = siteConfigId;
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

    public String getVideoTransUrl() {
        return videoTransUrl;
    }

    public void setVideoTransUrl(String videoTransUrl) {
        this.videoTransUrl = videoTransUrl;
    }

    public Integer getIsVideoTrans() {
        return isVideoTrans;
    }

    public void setIsVideoTrans(Integer isVideoTrans) {
        this.isVideoTrans = isVideoTrans;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
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

    public String getUnitNames() {
        return unitNames;
    }

    public void setUnitNames(String unitNames) {
        this.unitNames = unitNames;
    }

    public String getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(String unitIds) {
        this.unitIds = unitIds;
    }

    public Long getIndexTempId() {
        return indexTempId;
    }

    public void setIndexTempId(Long indexTempId) {
        this.indexTempId = indexTempId;
    }

    public Long getCommentTempId() {
        return commentTempId;
    }

    public void setCommentTempId(Long commentTempId) {
        this.commentTempId = commentTempId;
    }

    public Long getErrorTempId() {
        return errorTempId;
    }

    public void setErrorTempId(Long errorTempId) {
        this.errorTempId = errorTempId;
    }

    public Long getPublicTempId() {
        return publicTempId;
    }

    public void setPublicTempId(Long publicTempId) {
        this.publicTempId = publicTempId;
    }

    public Long getSearchTempId() {
        return searchTempId;
    }

    public void setSearchTempId(Long searchTempId) {
        this.searchTempId = searchTempId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationPwd() {
        return stationPwd;
    }

    public void setStationPwd(String stationPwd) {
        this.stationPwd = stationPwd;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public Long getComColumnId() {
        return comColumnId;
    }

    public void setComColumnId(Long comColumnId) {
        this.comColumnId = comColumnId;
    }

    public Long getSiteTempId() {
        return siteTempId;
    }

    public void setSiteTempId(Long siteTempId) {
        this.siteTempId = siteTempId;
    }

    public Boolean getIsHave() {
        return isHave;
    }

    public void setIsHave(Boolean isHave) {
        this.isHave = isHave;
    }

    public Integer getIsWap() {
        return isWap;
    }

    public void setIsWap(Integer isWap) {
        this.isWap = isWap;
    }

    public Long getWapTempId() {
        return wapTempId;
    }

    public void setWapTempId(Long wapTempId) {
        this.wapTempId = wapTempId;
    }

    public Long getWapPublicTempId() {
        return wapPublicTempId;
    }

    public void setWapPublicTempId(Long wapPublicTempId) {
        this.wapPublicTempId = wapPublicTempId;
    }

    public Long getPhoneTempId() {
        return phoneTempId;
    }

    public void setPhoneTempId(Long phoneTempId) {
        this.phoneTempId = phoneTempId;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Long getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Long specialId) {
        this.specialId = specialId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getSiteIDCode() {
        return siteIDCode;
    }

    public void setSiteIDCode(String siteIDCode) {
        this.siteIDCode = siteIDCode;
    }
}
