package cn.lonsun.statistics;

public class MessageBoardListVO{

    private Long organId;

    private Long count=0L;

    private Integer rate=0;

    private Long replyCount=0L;
    private Long receiveCount=0L;
    private Long noReplyCount=0L;
    private Integer replyRate=0;
    private Integer onTimeRate=0;
    private Long onTimeCount=0L;
    private Integer satisfactoryRate=0;

    private String organName;

    private String type;

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getReceiveCount() {
        return receiveCount;
    }

    public void setReceiveCount(Long receiveCount) {
        this.receiveCount = receiveCount;
    }

    public Long getNoReplyCount() {
        return noReplyCount;
    }

    public void setNoReplyCount(Long noReplyCount) {
        this.noReplyCount = noReplyCount;
    }

    public Integer getReplyRate() {
        return replyRate;
    }

    public void setReplyRate(Integer replyRate) {
        this.replyRate = replyRate;
    }

    public Integer getOnTimeRate() {
        return onTimeRate;
    }

    public void setOnTimeRate(Integer onTimeRate) {
        this.onTimeRate = onTimeRate;
    }

    public Long getOnTimeCount() {
        return onTimeCount;
    }

    public void setOnTimeCount(Long onTimeCount) {
        this.onTimeCount = onTimeCount;
    }

    public Integer getSatisfactoryRate() {
        return satisfactoryRate;
    }

    public void setSatisfactoryRate(Integer satisfactoryRate) {
        this.satisfactoryRate = satisfactoryRate;
    }
}
