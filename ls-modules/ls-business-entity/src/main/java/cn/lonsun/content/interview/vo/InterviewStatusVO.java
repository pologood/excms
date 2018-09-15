package cn.lonsun.content.interview.vo;

/**
 * 参与度分析
 * Created by ZX on 2016-6-17.
 */
public class InterviewStatusVO {
    //id
    private Long id;

    //公共参与数
    private boolean participationNum=true;

    //答复数
    private boolean answerNum=false;

    //答复率
    private boolean answerRate=false;

    //提问网友数
    private boolean qtNetFriendNum=false;

    //参与网友数
    //private Long ptNetFriendNum;

    //网友参与度
    //private String participationRate;


    public boolean isParticipationNum() {
        return participationNum;
    }

    public void setParticipationNum(boolean participationNum) {
        this.participationNum = participationNum;
    }

    public boolean isAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(boolean answerNum) {
        this.answerNum = answerNum;
    }

    public boolean isAnswerRate() {
        return answerRate;
    }

    public void setAnswerRate(boolean answerRate) {
        this.answerRate = answerRate;
    }

    public boolean isQtNetFriendNum() {
        return qtNetFriendNum;
    }

    public void setQtNetFriendNum(boolean qtNetFriendNum) {
        this.qtNetFriendNum = qtNetFriendNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
