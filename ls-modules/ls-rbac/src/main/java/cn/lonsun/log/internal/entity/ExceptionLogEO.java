package cn.lonsun.log.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.ABaseEntity;

@Entity
@Table(name="system_exception_log")
public class ExceptionLogEO extends ABaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;
    /**
     * 业务对象操作类型
     * @author xujh
     *
     */
    public enum Operation{
        Add,//新增操作
        Delete,//删除操作
        Update//更新操作
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="exceprion_log_id")
    private Long exceptionLogId;
    //业务对象类型,例如：UserEO
    @Column(name="case_type")
    private String caseType;
    //操作
    @Column(name="operation")
    private String operation;

    //存放对象修改的前后的对比
    @Column(name="description")
    private String description;
    //操作的IP地址
    @Column(name="operation_ip")
    private String operationIp;
    // 异常信息
    @Column(name="exception")
    private String exception;
    // 操作的URI
    @Column(name="requestUri")
    private String requestUri;
    // 操作的方法
    @Column(name="method")
    private String method;
    // 操作提交的数据
    @Column(name="params")
    private String params;
    // 操作用户代理信息
    @Column(name="user_agent")
    private String userAgent;
    // 操作的浏览器
    @Column(name="broswer")
    private String broswer;

    public Long getExceptionLogId() {
        return exceptionLogId;
    }
    public void setExceptionLogId(Long exceptionLogId) {
        this.exceptionLogId = exceptionLogId;
    }
    public String getCaseType() {
        return caseType;
    }
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOperationIp() {
        return operationIp;
    }
    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }
    public String getException() {
        return exception;
    }
    public void setException(String exception) {
        this.exception = exception;
    }
    public String getRequestUri() {
        return requestUri;
    }
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public String getBroswer() {
        return broswer;
    }
    public void setBroswer(String broswer) {
        this.broswer = broswer;
    }
}

