package cn.lonsun.site.thirdLoginManage.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * @author liuk
 * @version 2018-4-23 15:43:02
 */
public class ThirdLoginMgrPageVO extends PageQueryVO {


    private Long siteId;

    private String type;//第三方登录分类  QQ，微信，微博

    private Integer status = 0; //0:不启用 1：启用 默认不启用

    private String appId;

    private String appSecret;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}