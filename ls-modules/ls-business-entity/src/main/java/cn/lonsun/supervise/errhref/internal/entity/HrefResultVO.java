package cn.lonsun.supervise.errhref.internal.entity;

/**
 * @author gu.fei
 * @version 2016-5-27 15:51
 */
public class HrefResultVO {

    private String urlName; //链接名称

    private String url; //链接地址

    private String parentUrl; //父页面

    private Integer repCode; //访问返回编码

    private String repDesc; //访问返回说明

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public Integer getRepCode() {
        return repCode;
    }

    public void setRepCode(Integer repCode) {
        this.repCode = repCode;
    }

    public String getRepDesc() {
        return repDesc;
    }

    public void setRepDesc(String repDesc) {
        this.repDesc = repDesc;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }
}
