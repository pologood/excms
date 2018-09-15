
package cn.lonsun.process.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 *@date 2015-1-8 9:42  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class ProcessAgentVO {

    /** 主键 */
    private Long agentId;

    /** 被代填人姓名 */
    private String beAgentPersonName;

    /** 被代填人部门名称 */
    private String beAgentOrganName;

    /** 代填人姓名S */
    private String agentPersonNames;

    /** 代填人部门名称S */
    private String agentOrganNames;

    /** 创建日期 */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+08:00")
    private Date createDate;

    private String agents;


    public String getBeAgentPersonName() {
        return beAgentPersonName;
    }

    public void setBeAgentPersonName(String beAgentPersonName) {
        this.beAgentPersonName = beAgentPersonName;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getBeAgentOrganName() {
        return beAgentOrganName;
    }

    public void setBeAgentOrganName(String beAgentOrganName) {
        this.beAgentOrganName = beAgentOrganName;
    }

    public String getAgentPersonNames() {
        return agentPersonNames;
    }

    public void setAgentPersonNames(String agentPersonNames) {
        this.agentPersonNames = agentPersonNames;
    }

    public String getAgentOrganNames() {
        return agentOrganNames;
    }

    public void setAgentOrganNames(String agentOrganNames) {
        this.agentOrganNames = agentOrganNames;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAgents() {
        return agents;
    }

    public void setAgents(String agents) {
        this.agents = agents;
    }
}
