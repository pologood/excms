package cn.lonsun.wechatmgr.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 
 * @ClassName: WeChatAccountsInfoEO
 * @Description: 公众号配置表
 * @author Hewbing
 * @date 2015年12月22日 下午8:18:24
 *
 */
@Entity
@Table(name="CMS_WECHAT_ACCOUNTS_INFO")
public class WeChatAccountsInfoEO extends AMockEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	//站点ID
	@Column(name="SITE_ID")
	private Long siteId;
	
	//公众号
	@Column(name="ACCOUNTS_NAME")
	private String accountsName;
	
	//头像
	@Column(name="HEAD_IMG")
	private String headImg;
	
	//微信帐号
	@Column(name="ACCOUNTS")
	private String accounts;
	
	//原始ID
	@Column(name="PRIMITIVE_ID")
	private String primitiveId;
	
	//类型 :1：订阅号  2：服务号
	@Column(name="ACCOUNTS_TYPE")
	private Integer accountsType=1;
	
	//是否认证
	@Column(name="IS_AUTHENTICATION")
	private Integer isAuth=0;
	
	//二维码
	@Column(name="QR_CODE")
	private String qrCode;
	
	//apiurl
	@Column(name="API_URL")
	private String apiUrl;
	
	//token
	@Column(name="TOKEN")
	private String token;
	
	//
	@Column(name="APPID")
	private String appId;
	
	@Column(name="APPSECRET")
	private String appSecret;
	
	//是否启动审核
	@Column(name="IS_OPEN_EXAMINE")
	private Integer isOpenExamine=0;

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

	public String getAccountsName() {
		return accountsName;
	}

	public void setAccountsName(String accountsName) {
		this.accountsName = accountsName;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public String getPrimitiveId() {
		return primitiveId;
	}

	public void setPrimitiveId(String primitiveId) {
		this.primitiveId = primitiveId;
	}

	public Integer getAccountsType() {
		return accountsType;
	}

	public void setAccountsType(Integer accountsType) {
		this.accountsType = accountsType;
	}

	public Integer getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public Integer getIsOpenExamine() {
		return isOpenExamine;
	}

	public void setIsOpenExamine(Integer isOpenExamine) {
		this.isOpenExamine = isOpenExamine;
	}
	
	
}
