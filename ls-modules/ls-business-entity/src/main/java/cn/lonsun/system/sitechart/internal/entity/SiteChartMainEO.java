package cn.lonsun.system.sitechart.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;







import org.springframework.web.bind.annotation.RequestMapping;

import cn.lonsun.core.base.entity.ABaseEntity;
@Entity
@Table(name="CMS_SITE_CHART_MAIN")
public class SiteChartMainEO extends ABaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 121212l;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="SITE_ID")
	private Long siteId;

	@Column(name="MEMBER_ID")
	private Long memberId;
	//来访IP
	@Column(name="IP")
	private String ip;
	//来源路径
	@Column(name="REFERER")
	private String referer;
	//访问路径
	@Column(name="URL")
	private String url = "-";
	//访问页面标题
	@Column(name="TITLE")
	private String title;
	//访问时间戳，为cookie形式
	@Column(name="COOKIE")
	private String cookie = "-";
	//
	@Column(name="S_COOKIE")
	private String sCookie;
	//国
	@Column(name="COUNTRY")
	private String country;
	//省
	@Column(name="PROVINCE")
	private String province;
	//市
	@Column(name="CITY")
	private String city;
	//搜索引擎
	@Column(name="SEARCH_ENGINE")
	private String searchEngine = "-";
	//搜索词
	@Column(name="SEARCH_KEY")
	private String searchKey;
	//来源类型
	@Column(name="SOURCE_TYPE")
	private String sourceType = "-";
	//来源uri
	@Column(name="SOURCE_HOST")
	private String sourceHost = "-";
	//操作系统
	@Column(name="OS")
	private String os;
	//客户端（浏览器）
	@Column(name="CLIENT")
	private String client;
	//移动与分移动设备
	@Column(name="IS_PC")
	private String isPc;
	//分辨率
	@Column(name="RESOLUTION")
	private String resolution;
	//颜色深度
	@Column(name="COLOR_DEPTH")
	private String colorDepth;
	//语言
	@Column(name="LANGUAGE")
	private String language;

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

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
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

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getColorDepth() {
		return colorDepth;
	}

	public void setColorDepth(String colorDepth) {
		this.colorDepth = colorDepth;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}
