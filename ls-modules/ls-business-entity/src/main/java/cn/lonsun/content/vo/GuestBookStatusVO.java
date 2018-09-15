package cn.lonsun.content.vo;

/**
 * 参与度分析
 * Created by zx on 2016-6-21.
 */
public class GuestBookStatusVO {

    //栏目ids
    private Long[] columnIds;
    //站点Id
    private Long siteId;

    //单位Id
    private Long organId;

    //总留言数
    private boolean amount=true;
    //回复数
    private boolean replyNum=false;

    //回复率
    private boolean replyRate=false;

    //发布且公开数（选登数）
    private boolean selectNum=false;

    //举报数
    private boolean reportNum=false;
    //建议数
    private boolean suggestNum=false;
    //咨询数
    private boolean consultNum=false;
    //投诉数
    private boolean complainNum=false;

    //举报回复数
    private boolean reportReplyNum=false;
    //建议回复数
    private boolean suggestReplyNum=false;
    //咨询回复数
    private boolean consultReplyNum=false;
    //投诉回复数
    private boolean complainReplyNum=false;

    //今日留言
    private boolean todayAmount=false;
    //今日回复
    private boolean todayReplyNum=false;
    //今日在办
    private boolean todayHandlingNum=false;
    //在办
    private boolean handlingNum=true;

    //本年度留言、受理
    private boolean yearAmount=false;
    //本年度回复数、办结
    private boolean yearReplyNum=false;
    //本年度在办
    private boolean yearHandlingNum=false;

    //本月受理
    private boolean curMonthAmount=false;
    //本月办结
    private boolean curMonthReplyNum=false;
    //本月在办
    private boolean curMonthHandlingNum=false;

    //上月受理
    private boolean monthAmount=false;
    //上月办结
    private boolean monthReplyNum=false;
    //上月在办
    private boolean monthHandlingNum=false;

    //本季度受理
    private boolean curQuarterAmount=false;
    //本季度办结
    private boolean curQuarterReplyNum=false;
    //本季度在办
    private boolean curQuarterHandlingNum=false;

    public boolean isCurQuarterAmount() {
        return curQuarterAmount;
    }

    public void setCurQuarterAmount(boolean curQuarterAmount) {
        this.curQuarterAmount = curQuarterAmount;
    }

    public boolean isCurQuarterReplyNum() {
        return curQuarterReplyNum;
    }

    public void setCurQuarterReplyNum(boolean curQuarterReplyNum) {
        this.curQuarterReplyNum = curQuarterReplyNum;
    }

    public boolean isCurQuarterHandlingNum() {
        return curQuarterHandlingNum;
    }

    public void setCurQuarterHandlingNum(boolean curQuarterHandlingNum) {
        this.curQuarterHandlingNum = curQuarterHandlingNum;
    }

    public boolean isAmount() {
        return amount;
    }

    public void setAmount(boolean amount) {
        this.amount = amount;
    }

    public boolean isReplyNum() {
        return replyNum;
    }

    public void setReplyNum(boolean replyNum) {
        this.replyNum = replyNum;
    }

    public boolean isHandlingNum() {
        return handlingNum;
    }

    public void setHandlingNum(boolean handlingNum) {
        this.handlingNum = handlingNum;
    }

    public boolean isReplyRate() {
        return replyRate;
    }

    public void setReplyRate(boolean replyRate) {
        this.replyRate = replyRate;
    }

    public boolean isReportNum() {
        return reportNum;
    }

    public void setReportNum(boolean reportNum) {
        this.reportNum = reportNum;
    }

    public boolean isSuggestNum() {
        return suggestNum;
    }

    public void setSuggestNum(boolean suggestNum) {
        this.suggestNum = suggestNum;
    }

    public boolean isConsultNum() {
        return consultNum;
    }

    public void setConsultNum(boolean consultNum) {
        this.consultNum = consultNum;
    }

    public boolean isComplainNum() {
        return complainNum;
    }

    public void setComplainNum(boolean complainNum) {
        this.complainNum = complainNum;
    }

    public boolean isTodayAmount() {
        return todayAmount;
    }

    public void setTodayAmount(boolean todayAmount) {
        this.todayAmount = todayAmount;
    }

    public boolean isTodayReplyNum() {
        return todayReplyNum;
    }

    public void setTodayReplyNum(boolean tadayReplyNum) {
        this.todayReplyNum = tadayReplyNum;
    }

    public boolean isTodayHandlingNum() {
        return todayHandlingNum;
    }

    public void setTodayHandlingNum(boolean todayHandlingNum) {
        this.todayHandlingNum = todayHandlingNum;
    }

    public boolean isYearAmount() {
        return yearAmount;
    }

    public void setYearAmount(boolean yearAmount) {
        this.yearAmount = yearAmount;
    }

    public boolean isYearReplyNum() {
        return yearReplyNum;
    }

    public void setYearReplyNum(boolean yearReplyNum) {
        this.yearReplyNum = yearReplyNum;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long[] getColumnIds() {
        return columnIds;
    }

    public void setColumnIds(Long[] columnIds) {
        this.columnIds = columnIds;
    }

    public boolean isYearHandlingNum() {
        return yearHandlingNum;
    }

    public void setYearHandlingNum(boolean yearHandlingNum) {
        this.yearHandlingNum = yearHandlingNum;
    }

    public boolean isMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(boolean monthAmount) {
        this.monthAmount = monthAmount;
    }

    public boolean isMonthReplyNum() {
        return monthReplyNum;
    }

    public void setMonthReplyNum(boolean monthReplyNum) {
        this.monthReplyNum = monthReplyNum;
    }

    public boolean isMonthHandlingNum() {
        return monthHandlingNum;
    }

    public void setMonthHandlingNum(boolean monthHandlingNum) {
        this.monthHandlingNum = monthHandlingNum;
    }

    public boolean isCurMonthReplyNum() {
        return curMonthReplyNum;
    }

    public void setCurMonthReplyNum(boolean curMonthReplyNum) {
        this.curMonthReplyNum = curMonthReplyNum;
    }

    public boolean isCurMonthAmount() {
        return curMonthAmount;
    }

    public void setCurMonthAmount(boolean curMonthAmount) {
        this.curMonthAmount = curMonthAmount;
    }

    public boolean isCurMonthHandlingNum() {
        return curMonthHandlingNum;
    }

    public void setCurMonthHandlingNum(boolean curMonthHandlingNum) {
        this.curMonthHandlingNum = curMonthHandlingNum;
    }

    public boolean isReportReplyNum() {
        return reportReplyNum;
    }

    public void setReportReplyNum(boolean reportReplyNum) {
        this.reportReplyNum = reportReplyNum;
    }

    public boolean isSuggestReplyNum() {
        return suggestReplyNum;
    }

    public void setSuggestReplyNum(boolean suggestReplyNum) {
        this.suggestReplyNum = suggestReplyNum;
    }

    public boolean isConsultReplyNum() {
        return consultReplyNum;
    }

    public void setConsultReplyNum(boolean consultReplyNum) {
        this.consultReplyNum = consultReplyNum;
    }

    public boolean isComplainReplyNum() {
        return complainReplyNum;
    }

    public void setComplainReplyNum(boolean complainReplyNum) {
        this.complainReplyNum = complainReplyNum;
    }

    public boolean isSelectNum() {
        return selectNum;
    }

    public void setSelectNum(boolean selectNum) {
        this.selectNum = selectNum;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }
}
