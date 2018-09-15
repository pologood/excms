package cn.lonsun.special.internal.vo;

import cn.lonsun.special.internal.entity.SpecialThemeEO;

/**
 * 云模板数据
 * @author zhongjun
 */
public class SpecialCloudThemeVO extends SpecialThemeEO {

    /**
     * 下载量
     */
    private Long downloadNum;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 下载状态
     */
    private Long downloadStatus;

    /**
     * 是否为热门专题
     */
    private boolean IsHot = false;

    /**
     * 本地下载的id
     */
    private Long localId = 0l;

    /**
     * 本地版本
     */
    private Integer localVersion = 1;


    public Long getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(Long downloadNum) {
        this.downloadNum = downloadNum;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public Integer getLocalVersion() {
        return localVersion;
    }

    public void setLocalVersion(Integer localVersion) {
        this.localVersion = localVersion;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isHot() {
        return IsHot;
    }

    public void setHot(boolean hot) {
        IsHot = hot;
    }

    public Long getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Long downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
