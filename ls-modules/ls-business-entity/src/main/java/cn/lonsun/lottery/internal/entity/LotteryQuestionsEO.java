package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by lonsun on 2017-1-9.
 */
@Entity
@Table(name="LOTTERY_QUESTIONS")
public class LotteryQuestionsEO extends AMockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="LOTTERY_ID")
    private Long   lotteryId;
    @Column(name="LOTTERY_TITLE")
    private String  lotteryTitle;

    @Column(name="SITE_ID")
    private Long siteId;

    @Column(name="ANSWER_ID")
    private Long answerId;

    @Transient
    List<LotteryAnswerEO> answerEOs;

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

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public List<LotteryAnswerEO> getAnswerEOs() {
        return answerEOs;
    }

    public void setAnswerEOs(List<LotteryAnswerEO> answerEOs) {
        this.answerEOs = answerEOs;
    }
}
