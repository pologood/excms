package cn.lonsun.net.service.entity.dto;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-20 11:25
 */
public class ParamVO {

    private Long pageIndex;

    private Integer pageSize;

    private String sortField;

    private String sortOrder;

    private String keys;

    private String keyValue;

    private List<Long> longs;

    private List<MapVO> mapVOs;

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
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

    public List<Long> getLongs() {
        return longs;
    }

    public void setLongs(List<Long> longs) {
        this.longs = longs;
    }

    public List<MapVO> getMapVOs() {
        return mapVOs;
    }

    public void setMapVOs(List<MapVO> mapVOs) {
        this.mapVOs = mapVOs;
    }
}
