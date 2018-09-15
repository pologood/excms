package cn.lonsun.content.vo;

/**
 * 参与度分析
 * Created by zx on 2016-6-21.
 */
public class GuestBookAnalyseVO {

    //总留言数
    private Long amount=0L;
    //回复数
    private Long replyNum=0L;
    //回复率
    private String replyRate;

    //发布且公开数（选登数）
    private Long selectNum=0L;

    //举报数
    private Long reportNum=0L;
    //建议数
    private Long suggestNum=0L;
    //咨询数
    private Long consultNum=0L;
    //投诉数
    private Long complainNum=0L;

    //举报回复数
    private Long reportReplyNum=0L;
    //建议回复数
    private Long suggestReplyNum=0L;
    //咨询回复数
    private Long consultReplyNum=0L;
    //投诉回复数
    private Long complainReplyNum=0L;

    //今日留言
    private Long todayAmount=0L;
    //今日回复
    private Long todayReplyNum=0L;
    //今日在办
    private Long todayHandlingNum=0L;
    //本年度留言、受理
    private Long yearAmount=0L;
    //本年度回复、办结
    private Long yearReplyNum=0L;
    //本年度在办
    private Long yearHandlingNum=0L;
    //在办
    private Long handlingNum=0L;
    //上月受理
    private Long monthAmount=0L;
    //上月办结
    private Long monthReplyNum=0L;
    //上月在办
    private Long monthHandlingNum=0L;
    //本月受理
    private Long curMonthAmount=0L;
    //本月办结
    private Long curMonthReplyNum=0L;
    //本月在办
    private Long curMonthHandlingNum=0L;

    //本季度留言、受理
    private Long curQuarterAmount=0L;
    //本季度回复、办结
    private Long curQuarterReplyNum=0L;
    //本季度在办
    private Long curQuarterHandlingNum=0L;

    public Long getCurQuarterAmount() {
        return curQuarterAmount;
    }

    public void setCurQuarterAmount(Long curQuarterAmount) {
        this.curQuarterAmount = curQuarterAmount;
    }

    public Long getCurQuarterReplyNum() {
        return curQuarterReplyNum;
    }

    public void setCurQuarterReplyNum(Long curQuarterReplyNum) {
        this.curQuarterReplyNum = curQuarterReplyNum;
    }

    public Long getCurQuarterHandlingNum() {
        return curQuarterHandlingNum;
    }

    public void setCurQuarterHandlingNum(Long curQuarterHandlingNum) {
        this.curQuarterHandlingNum = curQuarterHandlingNum;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(Long replyNum) {
        this.replyNum = replyNum;
    }

    public String getReplyRate() {
        return replyRate;
    }

    public void setReplyRate(String replyRate) {
        this.replyRate = replyRate;
    }

    public Long getConsultNum() {
        return consultNum;
    }

    public void setConsultNum(Long consultNum) {
        this.consultNum = consultNum;
    }

    public Long getComplainNum() {
        return complainNum;
    }

    public void setComplainNum(Long complainNum) {
        this.complainNum = complainNum;
    }

    public Long getSuggestNum() {
        return suggestNum;
    }

    public void setSuggestNum(Long suggestNum) {
        this.suggestNum = suggestNum;
    }

    public Long getReportNum() {
        return reportNum;
    }

    public void setReportNum(Long reportNum) {
        this.reportNum = reportNum;
    }

    public Long getTodayAmount() {
        return todayAmount;
    }

    public void setTodayAmount(Long todayAmount) {
        this.todayAmount = todayAmount;
    }

    public Long getTodayReplyNum() {
        return todayReplyNum;
    }

    public void setTodayReplyNum(Long todayReplyNum) {
        this.todayReplyNum = todayReplyNum;
    }

    public Long getTodayHandlingNum() {
        return todayHandlingNum;
    }

    public void setTodayHandlingNum(Long todayHandlingNum) {
        this.todayHandlingNum = todayHandlingNum;
    }

    public Long getYearAmount() {
        return yearAmount;
    }

    public void setYearAmount(Long yearAmount) {
        this.yearAmount = yearAmount;
    }

    public Long getYearReplyNum() {
        return yearReplyNum;
    }

    public void setYearReplyNum(Long yearReplyNum) {
        this.yearReplyNum = yearReplyNum;
    }

    public Long getYearHandlingNum() {
        return yearHandlingNum;
    }

    public void setYearHandlingNum(Long yearHandlingNum) {
        this.yearHandlingNum = yearHandlingNum;
    }

    public Long getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(Long monthAmount) {
        this.monthAmount = monthAmount;
    }

    public Long getMonthReplyNum() {
        return monthReplyNum;
    }

    public void setMonthReplyNum(Long monthReplyNum) {
        this.monthReplyNum = monthReplyNum;
    }

    public Long getMonthHandlingNum() {
        return monthHandlingNum;
    }

    public void setMonthHandlingNum(Long monthHandlingNum) {
        this.monthHandlingNum = monthHandlingNum;
    }

    public Long getCurMonthAmount() {
        return curMonthAmount;
    }
    public Long setCurMonthAmount() {
        return curMonthAmount;
    }

    public void setCurMonthAmount(Long curMonthAmount) {
        this.curMonthAmount = curMonthAmount;
    }

    public Long getCurMonthReplyNum() {
        return curMonthReplyNum;
    }

    public void setCurMonthReplyNum(Long curMonthReplyNum) {
        this.curMonthReplyNum = curMonthReplyNum;
    }

    public Long getCurMonthHandlingNum() {
        return curMonthHandlingNum;
    }

    public void setCurMonthHandlingNum(Long curMonthHandlingNum) {
        this.curMonthHandlingNum = curMonthHandlingNum;
    }

    public Long getReportReplyNum() {
        return reportReplyNum;
    }

    public void setReportReplyNum(Long reportReplyNum) {
        this.reportReplyNum = reportReplyNum;
    }

    public Long getSuggestReplyNum() {
        return suggestReplyNum;
    }

    public void setSuggestReplyNum(Long suggestReplyNum) {
        this.suggestReplyNum = suggestReplyNum;
    }

    public Long getConsultReplyNum() {
        return consultReplyNum;
    }

    public void setConsultReplyNum(Long consultReplyNum) {
        this.consultReplyNum = consultReplyNum;
    }

    public Long getComplainReplyNum() {
        return complainReplyNum;
    }

    public void setComplainReplyNum(Long complainReplyNum) {
        this.complainReplyNum = complainReplyNum;
    }

    public Long getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(Long selectNum) {
        this.selectNum = selectNum;
    }

    public Long getHandlingNum() {
        return handlingNum;
    }

    public void setHandlingNum(Long handlingNum) {
        this.handlingNum = handlingNum;
    }
}
