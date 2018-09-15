package cn.lonsun.ldap.internal.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * LDAP链接配置
 * @author xujh
 *
 */
@Entity
@Table(name="rbac_config")
public class ConfigEO extends AMockEntity implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2704413643608141625L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO) 
	@Column(name="CONFIG_ID")
	private Long configId;
	@Column(name="URL_")
	private String url;
	@Column(name="PASSWORD_")
	private String password;
	@Column(name="SORT_NUM")
	private int sortNum;// 优先级排序
	@Column(name="SERVER_NAME")
	private String serverName;//计算机名称

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
