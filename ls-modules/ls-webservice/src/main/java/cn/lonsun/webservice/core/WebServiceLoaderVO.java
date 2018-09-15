package cn.lonsun.webservice.core;

/**
 * webService加载VO
 * Created by zhusy on 2016-7-18.
 */
public class WebServiceLoaderVO {

    private  String url = null;
    private  String nameSpace = null;
    private  String method = null;
    private  String systemCodes = null;
    private  Object[] params = null;

    public WebServiceLoaderVO() {}

    public WebServiceLoaderVO(String url, String nameSpace, String method, String systemCodes, Object[] params) {
        this.url = url;
        this.nameSpace = nameSpace;
        this.method = method;
        this.systemCodes = systemCodes;
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getSystemCodes() {
        return systemCodes;
    }

    public void setSystemCodes(String systemCodes) {
        this.systemCodes = systemCodes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
