package cn.lonsun.monitor.words.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
public class WordsPageVO extends PageQueryVO {

    private String sortField;

    private String sortOrder;

    private String keys;

    private String keyValue;

    private Long checkId;

    private String checkType;

    private String type; //检测类型 标题 副标题 内容

    @Override
    public String getSortField() {
        return sortField;
    }

    @Override
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    @Override
    public String getSortOrder() {
        return sortOrder;
    }

    @Override
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

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
