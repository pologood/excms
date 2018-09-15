package cn.lonsun.lsrobot.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2016-07-07 15:12
 */
public class RobotPageVO extends PageQueryVO {

    private String keys;

    private String keyValue;

    private String ifActive;

    private String ifShow;

    private Long siteId;

    private boolean ifExact = false;//是否完全匹配查询

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

    public String getIfActive() {
        return ifActive;
    }

    public void setIfActive(String ifActive) {
        this.ifActive = ifActive;
    }

    public String getIfShow() {
        return ifShow;
    }

    public void setIfShow(String ifShow) {
        this.ifShow = ifShow;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public boolean getIfExact() {
        return ifExact;
    }

    public void setIfExact(boolean ifExact) {
        this.ifExact = ifExact;
    }
}
