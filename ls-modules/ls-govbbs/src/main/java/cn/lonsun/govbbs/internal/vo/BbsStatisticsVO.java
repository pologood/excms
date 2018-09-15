package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2016/12/26.
 */
public class BbsStatisticsVO implements java.io.Serializable{



    private Long todaySum = 0L;

    private Long yesSum = 0L;

    private Long allSum = 0L;

    private Long memberSum = 0L;

    private String newMemberName;

    private Long newMemberId;


    public Long getAllSum() {
        return allSum;
    }

    public void setAllSum(Long allSum) {
        this.allSum = allSum;
    }

    public Long getYesSum() {
        return yesSum;
    }

    public void setYesSum(Long yesSum) {
        this.yesSum = yesSum;
    }

    public Long getTodaySum() {
        return todaySum;
    }

    public void setTodaySum(Long todaySum) {
        this.todaySum = todaySum;
    }

    public Long getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(Long newMemberId) {
        this.newMemberId = newMemberId;
    }

    public String getNewMemberName() {
        return newMemberName;
    }

    public void setNewMemberName(String newMemberName) {
        this.newMemberName = newMemberName;
    }

    public Long getMemberSum() {
        return memberSum;
    }

    public void setMemberSum(Long memberSum) {
        this.memberSum = memberSum;
    }
}
