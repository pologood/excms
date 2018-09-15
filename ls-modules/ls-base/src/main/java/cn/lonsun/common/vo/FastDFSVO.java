package cn.lonsun.common.vo;

import java.io.Serializable;

/**
 * Created by yjjun_pc on 2015-3-17.
 */
public class FastDFSVO implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1l;

    private Long id;
    private String connectTimeout;
    private String networkTimeout;
    private String trackerServer;
    private String downLoadIp;

    public String getTrackerServer() {
        return trackerServer;
    }

    public void setTrackerServer(String trackerServer) {
        this.trackerServer = trackerServer;
    }

    public String getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(String networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDownLoadIp() {
        return downLoadIp;
    }

    public void setDownLoadIp(String downLoadIp) {
        this.downLoadIp = downLoadIp;
    }
}
