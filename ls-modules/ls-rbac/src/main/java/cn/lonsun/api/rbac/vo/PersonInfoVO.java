package cn.lonsun.api.rbac.vo;


/**
 * 登录返回VO
 *  
 * @author xujh 
 * @date 2014年10月8日 下午2:05:27
 * @version V1.0
 */
public class PersonInfoVO {
	
	/**
	 * SUCCESS：登录失败，FAILURE：登录成功
	 * @author xujh
	 *
	 */
	public static enum Status{
		SUCCESS,
		FAILURE;
	}
	
	//登录状态, 0：登录失败，1：登录成功
	private String status = Status.FAILURE.toString();
	//单点登录生成的标识编码
	private String token;
	//帐号ID
	private Long userId;
	//帐号
	private String uid;
	//人员ID
	private Long personId;
	//人员姓名
	private String personName;
	//所属非兼职组织ID
	private Long organId;
	//所属非兼职组织名称
	private String organName;
	//所属单位ID
	private Long unitId;
	//所属单位名称
	private String unitName;
	//政务邮箱
	private String coreMail;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Long getPersonId() {
		return personId;
	}
	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public Long getOrganId() {
		return organId;
	}
	public void setOrganId(Long organId) {
		this.organId = organId;
	}
	public String getOrganName() {
		return organName;
	}
	public void setOrganName(String organName) {
		this.organName = organName;
	}
	public String getCoreMail() {
		return coreMail;
	}
	public void setCoreMail(String coreMail) {
		this.coreMail = coreMail;
	}
	public Long getUnitId() {
		return unitId;
	}
	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
}
