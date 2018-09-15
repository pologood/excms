package cn.lonsun.site.template.internal.entity;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.net.service.entity.dto.MapVO;

import java.util.List;
import java.util.Set;

/**
 * @author gu.fei
 * @version 2015-5-28 11:27
 */
public class ParamDto extends PageQueryVO {

    private Long id;

    private Long siteId;

    private String keys;

    private String keyValue;

    private String tempType;

    private String tempName;

    private Long stationId;

    private Long organId;

    private Long columnId;

    private Long classifyId;

    private String startDate;

    private String endDate;

    private List<MapVO> mapVOs;

    private String type;

    private String weiboName;

    private Long[] contentIds;

    private String tplType;

    private String resIds;

    private Integer publish;

    private boolean byOrgan = false;

    private Integer status;

    private String title;

    private Set<Long> siteIds;

    private Set<Long> columns;

    private Set<String> codes;

    private String optCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getTempType() {
        return tempType;
    }

    public void setTempType(String tempType) {
        this.tempType = tempType;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<MapVO> getMapVOs() {
        return mapVOs;
    }

    public void setMapVOs(List<MapVO> mapVOs) {
        this.mapVOs = mapVOs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeiboName() {
        return weiboName;
    }

    public void setWeiboName(String weiboName) {
        this.weiboName = weiboName;
    }

    public Long[] getContentIds() {
        return contentIds;
    }

    public void setContentIds(Long[] contentIds) {
        this.contentIds = contentIds;
    }

    public String getTplType() {
        return tplType;
    }

    public void setTplType(String tplType) {
        this.tplType = tplType;
    }

    public String getResIds() {
        return resIds;
    }

    public void setResIds(String resIds) {
        this.resIds = resIds;
    }

    public Integer getPublish() {
        return publish;
    }

    public void setPublish(Integer publish) {
        this.publish = publish;
    }

    public boolean getByOrgan() {
        return byOrgan;
    }

    public void setByOrgan(boolean byOrgan) {
        this.byOrgan = byOrgan;
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

    public boolean isByOrgan() {
        return byOrgan;
    }

    public Set<Long> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(Set<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public Set<Long> getColumns() {
        return columns;
    }

    public void setColumns(Set<Long> columns) {
        this.columns = columns;
    }

    public Set<String> getCodes() {
        return codes;
    }

    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }
}
