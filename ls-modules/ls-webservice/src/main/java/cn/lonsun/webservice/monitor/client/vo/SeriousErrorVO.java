package cn.lonsun.webservice.monitor.client.vo;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-11-23 11:45
 */
public class SeriousErrorVO {

    //报告ID
    private Long reportId;

    //检测时间
    private Date monitorDate;

    //文章ID
    private Long contentId;

    //标题
    private String title;

    //检测到词汇
    private String word;

    //文章类型
    private String typeCode;

    //严重错误来源
    private String fromCode;

    //词汇类型
    private String checkType;

    //结果
    private String result;

    //域名
    private String domain;

    //栏目ID
    private Long columnId;

    public Long getReportId() {

        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Date getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(Date monitorDate) {
        this.monitorDate = monitorDate;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }
}
