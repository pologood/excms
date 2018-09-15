package cn.lonsun.govbbs.internal.vo;

/**
 * Created by lonsun on 2016-12-24.
 */
public class MemberStaticVO {

    private String name;

    private Integer  postCount=0;

    private Integer replyCount = 0;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
}
