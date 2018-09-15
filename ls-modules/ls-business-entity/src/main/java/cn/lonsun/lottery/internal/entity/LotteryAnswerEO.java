package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lonsun on 2017-1-11.
 */
@Entity
@Table(name="LOTTERY_ANSWER")
public class LotteryAnswerEO extends AMockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ANSWER_ID")
    private Long answerId;

    @Column(name="ANSWER")
    private String answer;

    @Column(name="ANSWER_OPTION")
    private String answerOption;


    @Column(name="IS_TRUE")
    private Integer isTrue=0;
    @Column(name="LOTTERY_ID")
    private Long  lotteryId;

    @Column(name="LOTTERY_TITLE")
    private String  lotteryTitle;
    @Column(name="SITE_ID")
    private Long  siteId;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getIsTrue() {
        return isTrue;
    }

    public void setIsTrue(Integer isTrue) {
        this.isTrue = isTrue;
    }

    public Long getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(Long lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryTitle() {
        return lotteryTitle;
    }

    public void setLotteryTitle(String lotteryTitle) {
        this.lotteryTitle = lotteryTitle;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }
}
