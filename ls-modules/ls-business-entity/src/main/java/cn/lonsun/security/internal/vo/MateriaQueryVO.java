package cn.lonsun.security.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

import java.util.Date;

/**
 * Created by lonsun on 2016-12-12.
 */
public class MateriaQueryVO extends PageQueryVO {

    private String keyWord;

    private Integer create;
    private Long siteId;


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
