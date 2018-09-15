package cn.lonsun.webservice.vo.axis2;

import cn.lonsun.core.base.entity.ABaseEntity;

/**
 * WebService实体
 * 
 * @author xujh
 * @date 2014年10月23日 下午1:41:58
 * @version V1.0
 */
public class WebServiceVO extends ABaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    // 主键
    private Long webServiceId;
    // 系统编码
    private String systemCode;
    // 所属系统名称
    private String systemName;
    // 对象唯一标识符
    private String code;
    // 去除服务器地址和端口后的地址
    private String uri;
    // 服务命名空间
    private String nameSpace;
    // 方法
    private String method;
    // 描述
    private String description;

    public Long getWebServiceId() {
        return webServiceId;
    }

    public void setWebServiceId(Long webServiceId) {
        this.webServiceId = webServiceId;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}