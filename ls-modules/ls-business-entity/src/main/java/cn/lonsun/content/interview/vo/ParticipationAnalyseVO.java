package cn.lonsun.content.interview.vo;

/**
 * 参与度分析
 * Created by ZX on 2016-6-17.
 */
public class ParticipationAnalyseVO {

    //访谈Id
    //private Long interviewId;

    //公共参与数
    private Long participationNum=0L;

    //答复数
    private Long answerNum=0L;

    //答复率
    private String answerRate;

    //提问网友数
    private Long qtNetFriendNum=0L;

    //参与网友数
    //private Long ptNetFriendNum;

    //网友参与度
    //private String participationRate;


    public String getAnswerRate() {
        return answerRate;
    }

    public void setAnswerRate(String answerRate) {
        this.answerRate = answerRate;
    }

    public Long getParticipationNum() {
        return participationNum;
    }

    public void setParticipationNum(Long participationNum) {
        this.participationNum = participationNum;
    }

    public Long getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(Long answerNum) {
        this.answerNum = answerNum;
    }

    public Long getQtNetFriendNum() {
        return qtNetFriendNum;
    }

    public void setQtNetFriendNum(Long qtNetFriendNum) {
        this.qtNetFriendNum = qtNetFriendNum;
    }
}
