package cn.lonsun.wechatmgr.vo;

/**
 * Created by zhangchao on 2016/10/15.
 */
public class UnitTopStatisVO implements java.io.Serializable {

    private String name;

    private Long sendCount = 0L;

    private Long replyCount = 0L;

    private String replyTimeRate = "0.0";

    private Integer replyRate = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public String getReplyTimeRate() {
        return replyTimeRate;
    }

    public void setReplyTimeRate(String replyTimeRate) {
        this.replyTimeRate = replyTimeRate;
    }

    public Integer getReplyRate() {
        return replyRate;
    }

    public void setReplyRate(Integer replyRate) {
        this.replyRate = replyRate;
    }

    public Long getSendCount() {
        return sendCount;
    }

    public void setSendCount(Long sendCount) {
        this.sendCount = sendCount;
    }
}
