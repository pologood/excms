package cn.lonsun.system.systemlog.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 
 * @ClassName: CmsLoginHistoryEO
 * @Description: 登入日志实体类
 * @author Hewbing
 * @date 2015年8月25日 上午10:57:37
 *
 */
@Entity
@Table(name="cms_login_history")
public class CmsLoginHistoryEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3922115842442265283L;
    
    /**
     * 登录状态
     *  
     */
    public enum LoginStatus{
    	Success,Failure
    }
    
    /**
     * 登录类型
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
    //站点ID
    @Column(name="site_id")
    private Long siteId;
    //栏目ID
    @Column(name="column_id")
    private Long columnId;
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
/*    @Column(name="organ_id")
    private Long organId;*/
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

    public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
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

/*	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}*/

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

}
