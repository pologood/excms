package cn.lonsun.content.vo;

/**
 * Created by Administrator on 2016-6-21.
 */
public class GuestBookNumVO {

    //留言数
    private Long amount;
    //回复数
    private Long replyNum;
    //回复率
    private String replyRate;

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
}
