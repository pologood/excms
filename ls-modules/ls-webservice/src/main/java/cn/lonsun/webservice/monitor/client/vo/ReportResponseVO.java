package cn.lonsun.webservice.monitor.client.vo;

import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-11-13 9:31
 */
public class ReportResponseVO implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Long reportId;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
}
