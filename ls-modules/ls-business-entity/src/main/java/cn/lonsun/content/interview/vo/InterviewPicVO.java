package cn.lonsun.content.interview.vo;

/**
 * Created by admin on 2016/8/15.
 */
public class InterviewPicVO implements  java.io.Serializable{

    private Long picId;
    //图片标题
    private String picTitle;
    //描述
    private String description;
    //排序
    private Integer sortNum;

    public Long getPicId() {
        return picId;
    }

    public void setPicId(Long picId) {
        this.picId = picId;
    }

    public String getPicTitle() {
        return picTitle;
    }

    public void setPicTitle(String picTitle) {
        this.picTitle = picTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }
}
