package cn.lonsun.journals.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by lonsun on 2017-1-3.
 */
public class JournalsQueryVO extends PageQueryVO {

    private String keyWord;

    private Integer create;
    private Long siteId;
    private Long columnId;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Integer getCreate() {
        return create;
    }

    public void setCreate(Integer create) {
        this.create = create;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
