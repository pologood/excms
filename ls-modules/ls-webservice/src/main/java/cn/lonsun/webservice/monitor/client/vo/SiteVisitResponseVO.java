package cn.lonsun.webservice.monitor.client.vo;

import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-09-21 17:15
 */
public class SiteVisitResponseVO implements Serializable {

    private static final long serialVersionUID = 0L;

    //返回编码
    private int respCode;

    //是否连接
    private Boolean isConnected = false;

    //描述
    private String desc;

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public Boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Boolean connected) {
        isConnected = connected;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
