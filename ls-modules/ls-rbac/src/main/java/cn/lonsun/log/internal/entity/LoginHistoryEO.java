package cn.lonsun.log.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 用户登录记录
 * @author xujh
 *
 */
@Entity
@Table(name="system_login_history")
public class LoginHistoryEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3922115842442265283L;
    
    /**
     * 登录状态
     *  
     * @author xujh 
     * @date 2014年10月6日 下午4:40:21
     * @version V1.0
     */
    public enum LoginStatus{
    	Success,Failure
    }
    
    /**
     * 登录类型
     *
     * @author xujh
     * @version 1.0
     * 2015年6月23日
     *
     */
    public enum Type{
    	PC(0),WEBVIEW(1),ANDROID(2);
    	
    	private Type(int value){
    		this.value = value;
    	}
    	
    	private int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
    	
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="login_history_id")
    private Long loginHistoryId;
    //登录类型
    @Column(name="type")
    private Integer type = Type.PC.getValue();
    //登录状态
    @Column(name="login_status")
    private String loginStatus;
    //描述
    @Column(name="description")
    private String description;
    // 登录ip
    @Column(name="login_ip")
    private String loginIp;
    // 用户名
    @Column(name="uid_")
    private String uid;
    // 姓名
    @Column(name="create_user")
    private String createUser;
    //部门ID
    @Column(name="organ_id")
    private Long organId;
    // 部门名称
    @Column(name="organ_name")
    private String organName;
    // 单位id
    @Column(name="unit_id")
    private Long unitId;
    // 单位名称
    @Column(name="unit_name")
    private String unitName;
    //操作系统
    @Column(name="OS")
    private String os;
    //浏览器
    @Column(name="BROWSER")
    private String browser;
    //屏幕分辨率
    @Column(name="RESOLUTION_RATIO")
    private String resolutionRatio;
    //客户端版本号
    @Column(name="CLIENT_VERSION")
    private String clientVersion;
    
    public Long getLoginHistoryId() {
        return loginHistoryId;
    }

    public void setLoginHistoryId(Long loginHistoryId) {
        this.loginHistoryId = loginHistoryId;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
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

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getResolutionRatio() {
		return resolutionRatio;
	}

	public void setResolutionRatio(String resolutionRatio) {
		this.resolutionRatio = resolutionRatio;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
}
