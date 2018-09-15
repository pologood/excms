package cn.lonsun.net.service.entity.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by huangxx on 2017/2/24.
 */
public class OfficePublicityQueryVO extends PageQueryVO {


    private String acceptanceItem;
    private String officeStatus;

    public String getAcceptanceItem() {
        return acceptanceItem;
    }

    public void setAcceptanceItem(String acceptanceItem) {
        this.acceptanceItem = acceptanceItem;
    }

    public String getOfficeStatus() {
        return officeStatus;
    }

    public void setOfficeStatus(String officeStatus) {
        this.officeStatus = officeStatus;
    }
}
