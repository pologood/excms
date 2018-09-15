package cn.lonsun.govbbs.internal.vo;

/**
 * Created by lonsun on 2016-12-24.
 */
public class UnitPlateStaticVO {
    private String name;
    private String plateName;
    private Integer replyCount=0;
    private Integer unReplyCount=0;
    private Integer outCount=0;
    private Integer yellowCount=0;
    private Integer readCount=0;
    private Integer count=0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Integer getUnReplyCount() {
        return unReplyCount;
    }

    public void setUnReplyCount(Integer unReplyCount) {
        this.unReplyCount = unReplyCount;
    }

    public Integer getOutCount() {
        return outCount;
    }

    public void setOutCount(Integer outCount) {
        this.outCount = outCount;
    }

    public Integer getYellowCount() {
        return yellowCount;
    }

    public void setYellowCount(Integer yellowCount) {
        this.yellowCount = yellowCount;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
