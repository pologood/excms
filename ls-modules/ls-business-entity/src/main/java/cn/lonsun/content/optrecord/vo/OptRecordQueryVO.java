package cn.lonsun.content.optrecord.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2018-01-18 15:54
 */
public class OptRecordQueryVO extends PageQueryVO {

    private String type;

    private Long contentId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
}
