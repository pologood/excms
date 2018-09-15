package cn.lonsun.weibo.service;

import weibo4j.model.Paging;
import weibo4j.model.WeiboException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.SinaWeiboUserInfoEO;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-9 16:12
 */
public interface ISinaWeiboService {

    /**
     * 同步微博数据
     * @param paging
     * @return
     */
    public void syncWeiboDataOnline(Paging paging,Long siteId) throws WeiboException;

    /**
     * 根据auth类型同步数据
     * @param paging
     * @param auth
     * @throws WeiboException
     */
    public void syncWeiboContentOnline(Paging paging,String auth) throws WeiboException;

    /**
     * 同步评论信息
     * @param paging
     * @throws WeiboException
     */
    public void syncWeibCommentOnline(Paging paging,String type) throws WeiboException;

    /**
     * 同步用户信息
     * @param paging
     * @throws WeiboException
     */
    public void syncWeiboUserOnline(Paging paging,String type) throws WeiboException;

    /**
     * 获取自己的微博信息
     * @return
     */
    public SinaWeiboUserInfoEO getSelfWeiboInfo();

    /**
     * 分页获取当前用户微博信息
     * 最多200条
     * @param vo
     * @return
     */
    public Pagination getPageCurWeibo(WeiboPageVO vo);

    /**
     * 根据微博ID移除一条微博记录
     * @param weiboId
     */
    public Object removeWeibo(String weiboId);

    /**
     * 添加微博
     * @param vo
     * @return
     */
    public void publishWeibo(SinaWeiboContentVO vo) throws Exception;

    /**
     * 转发一条新微博
     * @param weiboId
     */
    public Object repostWeibo(String weiboId);

    /**
     * 收藏一条微博
     * @param tagId 标签ID
     * @param weiboId 微博ID
     */
    public Object favoriteWeibo(String tagId,String weiboId);

    /**
     * 取消收藏一条微博
     * @param weiboId
     */
    public Object cancelFavoriteWeibo(String weiboId);

    /**
     * 获取微博评论列表
     * @param vo
     */
    public Object getPageComments(WeiboPageVO vo) throws WeiboException;

    /**
     * 评论微博
     * @param weiboId 微博ID
     * @param comment 评论内容（140汉字以内）
     * @return
     */
    public Object commentWeibo(String weiboId,String comment);

    /**
     * 评论微博
     * @param weiboId 微博ID
     * @param comment 评论内容（140汉字以内）
     * @param commentOrgin 当评论转发微博时，是否评论给原微博，false：否、true：是
     * @return
     */
    public Object commentWeibo(String weiboId,String comment,boolean commentOrgin);

    /**
     * 回复评论
     * @param cid
     * @param weiboId
     * @param comment
     * @return
     */
    public Object replyComment(String cid, String weiboId, String comment);

    /**
     * 删除评论
     * @param cid
     * @return
     */
    public Object removeReplyComment(String cid,String commentType);

    /**
     * 分页查询关注用户
     * @param vo
     * @return
     */
    public Object getPageFollows(WeiboPageVO vo);

    /**
     * 取消关注用户
     * @param uid
     * @return
     */
    public Object cancelFollow(String uid);
}
