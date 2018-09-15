package cn.lonsun.weibo.entity.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author gu.fei
 * @version 2015-12-17 14:48
 */
public class WeiboPageVO extends PageQueryVO{

    public enum Type {
        self,    //自己发布的微博 || 自己微博账户信息
        all,     //所有关注用户的微博
        mention, //@我的微博
        follow,  //关注用户信息
        fans     //粉丝信息
    }

    public enum CommentType {
        byWeiboId, //根据微博ID获取评论
        byMe,      //我发出的评论
        toMe       //评论我的
    }

    private String weiboId; //微博ID

    private String auth; //获取微博类型  self：自己发布的微博 all:所有关注用户的微博 || self:用户自己信息 follow：关注用户信息 fans：粉丝信息

    private Integer filterByAuthor = 0; //作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。

    private String commentType = "byWeiboId"; //默认根据微博ID获取评论列表

    private String nickName; //微博用户昵称

    private String keys; //关键字

    private String keyValue; //查询值

    private Long siteId; //站点

    public String getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(String weiboId) {
        this.weiboId = weiboId;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Integer getFilterByAuthor() {
        return filterByAuthor;
    }

    public void setFilterByAuthor(Integer filterByAuthor) {
        this.filterByAuthor = filterByAuthor;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
