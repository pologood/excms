package cn.lonsun.datasourcemanage.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by lonsun on 2018-2-5.

 */
public class DataSourceQueryVo extends PageQueryVO {
    private Long manufacturerid;
    private Long siteId;

    public Long getManufacturerid() {
        return manufacturerid;
    }

    public void setManufacturerid(Long manufacturerid) {
        this.manufacturerid = manufacturerid;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}


