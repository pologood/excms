package cn.lonsun.system.sitechart.vo;

import javax.persistence.Column;

/**
 * Created by zhangchao on 2016/8/17.
 */
public class SiteCharWebVO  implements  java.io.Serializable{

    private Long id;

    private Long siteId;

    private Long memberId;
    //来访IP
    private String ip;
    //来源路径
    private String referer;
    //访问路径
    private String url;
    //访问页面标题
    private String title;
    //访问时间戳，为cookie形式
    private String cookie;
    //
    private String sCookie;
    //国
    private String country;
    //省
    private String province;
    //市
    private String city;
    //搜索引擎
    private String searchEngine;
    //搜索词
    private String searchKey;
    //来源类型
    private String sourceType;
    //来源uri
    private String sourceHost;

    //客户端（浏览器）
    private String client;
    //移动与分移动设备
    private String isPc;

    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getsCookie() {
        return sCookie;
    }

    public void setsCookie(String sCookie) {
        this.sCookie = sCookie;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getIsPc() {
        return isPc;
    }

    public void setIsPc(String isPc) {
        this.isPc = isPc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

