package cn.lonsun.special.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public class SpecialMaterialQueryVO extends PageQueryVO {

    private String name;

    private Long siteId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
