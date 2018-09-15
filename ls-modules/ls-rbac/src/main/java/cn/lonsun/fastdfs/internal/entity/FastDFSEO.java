package cn.lonsun.fastdfs.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

@Entity
@Table(name="system_fastdfs")
public class FastDFSEO extends AMockEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name = "connect_timeout")
    private String connectTimeout;
    @Column(name = "network_timeout")
    private String networkTimeout;
    @Column(name = "tracker_server")
    private String trackerServer;
    @Column(name = "downLoad_ip")
    private String downLoadIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(String networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public String getTrackerServer() {
        return trackerServer;
    }

    public void setTrackerServer(String trackerServer) {
        this.trackerServer = trackerServer;
    }

    public String getDownLoadIp() {
        return downLoadIp;
    }

    public void setDownLoadIp(String downLoadIp) {
        this.downLoadIp = downLoadIp;
    }
}

