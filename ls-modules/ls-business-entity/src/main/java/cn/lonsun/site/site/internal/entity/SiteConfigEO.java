package cn.lonsun.site.site.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 站点配置实体类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015年8月24日 <br/>
 */
@Entity
@Table(name = "cms_site_config")
public class SiteConfigEO extends AMockEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "site_config_id")
    private Long siteConfigId;

    //主表ID
    @Column(name = "indicator_id")
    private Long indicatorId;

    //站点全称
    @Column(name = "site_title")
    private String siteTitle;

    //Logo文件（暂未用到）
    @Column(name = "logo_file")
    private String logoFile;

    //关键词
    @Column(name = "seo_keywords")
    private String keyWords;

    //描述
    @Column(name = "seo_description")
    private String description;

    //视频转换地址
    @Column(name = "video_trans_url")
    private String videoTransUrl;

    //是否启动美图
    @Column(name = "is_start_beauty")
    private Integer isStartBeauty = 0;

    //正文图片限宽
    @Column(name = "content_pic_width")
    private Integer contentPicWidth;

    //视频默认图片
    @Column(name = "video_pic_path")
    private String videoPicPath;

    //是否启用视频转换
    @Column(name = "is_video_trans")
    private Integer isVideoTrans = 0;

    //是否绑定信息公开模块
    @Column(name = "is_band_public_module")
    private Integer isBandPublicModule = 0;

    //采编后是否直接发布
    @Column(name = "is_direct_publish_edited")
    private Integer isDirectPublishEdited = 0;

    //是否启用页面触发器
    @Column(name = "is_trigger_page")
    private Integer isTriggerPage = 0;

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

    //公共栏目
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

    //论坛管理模板
    @Column(name = "bbs_temp_id")
    private Long BBSTempId;

    //扩展字段区分站点类型，现有TYPE涉及到其它业务逻辑，故不在上面增加类型参数
    @Column(name = "SITE_TYPE")
    private Integer siteType = 0;

    //对应子站专题ID，应用于专题子站
    @Column(name = "SPECIAL_ID")
    private Long specialId;

    //站点标识符
    @Column(name = "site_id_code")
    private String siteIDCode;

    //对应该模板主题ID
    @Transient
    private Long themeId;

    public Long getSiteConfigId() {
        return siteConfigId;
    }

    public void setSiteConfigId(Long siteConfigId) {
        this.siteConfigId = siteConfigId;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getLogoFile() {
        return logoFile;
    }

    public void setLogoFile(String logoFile) {
        this.logoFile = logoFile;
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

    public Integer getIsStartBeauty() {
        return isStartBeauty;
    }

    public void setIsStartBeauty(Integer isStartBeauty) {
        this.isStartBeauty = isStartBeauty;
    }

    public Integer getContentPicWidth() {
        return contentPicWidth;
    }

    public void setContentPicWidth(Integer contentPicWidth) {
        this.contentPicWidth = contentPicWidth;
    }

    public String getVideoPicPath() {
        return videoPicPath;
    }

    public void setVideoPicPath(String videoPicPath) {
        this.videoPicPath = videoPicPath;
    }

    public Integer getIsVideoTrans() {
        return isVideoTrans;
    }

    public void setIsVideoTrans(Integer isVideoTrans) {
        this.isVideoTrans = isVideoTrans;
    }

    public Integer getIsBandPublicModule() {
        return isBandPublicModule;
    }

    public void setIsBandPublicModule(Integer isBandPublicModule) {
        this.isBandPublicModule = isBandPublicModule;
    }

    public Integer getIsDirectPublishEdited() {
        return isDirectPublishEdited;
    }

    public void setIsDirectPublishEdited(Integer isDirectPublishEdited) {
        this.isDirectPublishEdited = isDirectPublishEdited;
    }

    public Integer getIsTriggerPage() {
        return isTriggerPage;
    }

    public void setIsTriggerPage(Integer isTriggerPage) {
        this.isTriggerPage = isTriggerPage;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
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

    public Long getBBSTempId() {
        return BBSTempId;
    }

    public void setBBSTempId(Long BBSTempId) {
        this.BBSTempId = BBSTempId;
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
