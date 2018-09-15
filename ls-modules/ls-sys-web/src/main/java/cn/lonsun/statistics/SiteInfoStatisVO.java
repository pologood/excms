package cn.lonsun.statistics;

/**
 * @author gu.fei
 * @version 2018-02-08 15:39
 */
public class SiteInfoStatisVO {

    private String siteName;

    private Long pv;

    private Long uv;

    private Long newsCount;

    private Long publicCount;

    private Long wxCount;

    private Long messageCount;

    private Long replyedCount;

    private Long registerUserCount;

    private Long onlineDealCount;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getPv() {
        return pv;
    }

    public void setPv(Long pv) {
        this.pv = pv;
    }

    public Long getUv() {
        return uv;
    }

    public void setUv(Long uv) {
        this.uv = uv;
    }

    public Long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Long newsCount) {
        this.newsCount = newsCount;
    }

    public Long getPublicCount() {
        return publicCount;
    }

    public void setPublicCount(Long publicCount) {
        this.publicCount = publicCount;
    }

    public Long getWxCount() {
        return wxCount;
    }

    public void setWxCount(Long wxCount) {
        this.wxCount = wxCount;
    }

    public Long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Long messageCount) {
        this.messageCount = messageCount;
    }

    public Long getReplyedCount() {
        return replyedCount;
    }

    public void setReplyedCount(Long replyedCount) {
        this.replyedCount = replyedCount;
    }

    public Long getRegisterUserCount() {
        return registerUserCount;
    }

    public void setRegisterUserCount(Long registerUserCount) {
        this.registerUserCount = registerUserCount;
    }

    public Long getOnlineDealCount() {
        return onlineDealCount;
    }

    public void setOnlineDealCount(Long onlineDealCount) {
        this.onlineDealCount = onlineDealCount;
    }
}
