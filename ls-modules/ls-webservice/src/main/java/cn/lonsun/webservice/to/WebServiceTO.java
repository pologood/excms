package cn.lonsun.webservice.to;

import java.io.Serializable;

/**
 * WebService统一返回对象
 * 
 * @author xujh
 * @date 2014年11月4日 下午4:12:05
 * @version V1.0
 */
public class WebServiceTO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * WebService返回错误码
     * 
     * @author xujh
     * @date 2014年11月6日 下午2:28:34
     * @version V1.0
     */
    public enum ErrorCode {
        SystemError("000"), // 系统异常
        ArgumentsError("001"), // 入参错误
        LoginFailure("002");// 登录失败

        private String value;

        private ErrorCode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // 返回内容，需要在返回前将返回对象转码成json传输
    private String jsonData;
    // 请求状态，0：失败，1：成功
    private int status = 1;
    // 错误码
    private String errorCode;
    // 描述信息,如果失败，那么返回描述信息
    private String description;

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WebServiceTO success(String jsonData, String description) {
        this.status = 1;
        this.jsonData = jsonData;
        this.description = description;
        return this;
    }

    public WebServiceTO error(ErrorCode errorCode, Exception exception) {
        this.status = 0;
        this.errorCode = errorCode.toString();
        this.description = exception.getMessage();
        return this;
    }

    public WebServiceTO error(ErrorCode errorCode, String errorMessage) {
        this.status = 0;
        this.errorCode = errorCode.toString();
        this.description = errorMessage;
        return this;
    }
}