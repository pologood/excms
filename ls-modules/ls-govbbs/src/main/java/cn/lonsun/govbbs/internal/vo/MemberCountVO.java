package cn.lonsun.govbbs.internal.vo;

/**
 * Created by zhangchao on 2017/1/17.
 */
public class MemberCountVO implements java.io.Serializable{

    private Long memberId;

    private Long postCount = 0L;

    private Long replyCount = 0L;

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
