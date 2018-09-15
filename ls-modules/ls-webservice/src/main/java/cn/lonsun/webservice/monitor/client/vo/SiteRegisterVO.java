package cn.lonsun.webservice.monitor.client.vo;

/**
 * webservice接口参数类
 * @author gu.fei
 * @version 2017-11-23 9:02
 */
public class SiteRegisterVO {


    //注册码
    private String registerCode;

    //站点ID
    private Long siteId;

    //站点名称
    private String siteName;

    //站点域名
    private String url;

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
