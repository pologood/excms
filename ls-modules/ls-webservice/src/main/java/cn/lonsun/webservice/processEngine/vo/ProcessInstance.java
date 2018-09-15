/*
 * 2014-12-15 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.vo;

public class ProcessInstance {
    /** 流程实例主键 */
    private Long procInstId;
    /** 流程实例名称 */
    private String instanceName;
    public ProcessInstance(){}
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Long getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(Long procInstId) {
        this.procInstId = procInstId;
    }
}
