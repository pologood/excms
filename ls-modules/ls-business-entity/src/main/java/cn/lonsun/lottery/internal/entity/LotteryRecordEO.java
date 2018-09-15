package cn.lonsun.lottery.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by lizhi on 2017-1-15.
 */
@Entity
@Table(name="LOTTERY_RECORD")
public class LotteryRecordEO extends AMockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="RECORD_ID")
    private Long recordId;
    @Column(name="NAME")
    private String name;

    @Column(name="PHONE")
    private String phone;
    @Column(name="RESULTS")
    private Integer results=0;
    @Column(name="ANSWER")
    private String answer;

    @Column(name="TRUE_ANSWER")
    private String trueAnswer;
    @Column(name="LOTTERY_ID")
    private Long lotteryId;
    @Column(name="LOTTERY_TITLE")
    private String lotteryTitle;
    @Column(name="SITE_ID")
    private Long siteId;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getResults() {
        return results;
    }

    public void setResults(Integer results) {
        this.results = results;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
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

}
