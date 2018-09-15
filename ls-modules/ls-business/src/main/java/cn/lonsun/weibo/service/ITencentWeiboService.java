package cn.lonsun.weibo.service;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-9 16:12
 */
public interface ITencentWeiboService {

    /**
     * 分页获取粉丝
     * @param vo
     * @return
     */
    public Pagination getPageFans(WeiboPageVO vo);

    /**
     * 分页获取关注的人
     * @param vo
     * @return
     */
    public Pagination getPageFollows(WeiboPageVO vo);

    /**
     * 批量取消关注
     * @param openIDs
     */
    public void cancelIdol(String[] openIDs);

    /**
     * 批量删除微博
     * @param weiboIds
     */
    public void deleteWeibo(String[] weiboIds);

    /**
     * 添加微博
     * @return
     */
    public void publishWeibo(String content) throws Exception;

}
