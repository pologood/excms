package cn.lonsun.net.service.entity.vo;

import java.util.List;

/**
 * Created by lonsun on 2017-7-4.
 */
public class MobileInteractVO {

    private String deptName;
    private String theme;
    private  String  status;
    private String sunmitDate;
    private List<MobileInteractVO> MobileInteractVO;
    //总邮件
    private Integer totalRefer;
    //待处理
    private Integer dealingRefer;

    private Integer dealRefer;
    private String url;
    private Integer isTimeOut=0;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSunmitDate() {
        return sunmitDate;
    }

    public void setSunmitDate(String sunmitDate) {
        this.sunmitDate = sunmitDate;
    }

    public List<MobileInteractVO> getMobileInteractVO() {
        return MobileInteractVO;
    }

    public void setMobileInteractVO(List<MobileInteractVO> mobileInteractVO) {
        MobileInteractVO = mobileInteractVO;
    }

    public Integer getTotalRefer() {
        return totalRefer;
    }

    public void setTotalRefer(Integer totalRefer) {
        this.totalRefer = totalRefer;
    }

    public Integer getDealingRefer() {
        return dealingRefer;
    }

    public void setDealingRefer(Integer dealingRefer) {
        this.dealingRefer = dealingRefer;
    }

    public Integer getDealRefer() {
        return dealRefer;
    }

    public void setDealRefer(Integer dealRefer) {
        this.dealRefer = dealRefer;
    }

    public Integer getIsTimeOut() {
        return isTimeOut;
    }

    public void setIsTimeOut(Integer isTimeOut) {
        this.isTimeOut = isTimeOut;
    }
}
