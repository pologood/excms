package cn.lonsun.weibo.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.SinaWeiboContentMEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-24 13:55
 */
public interface ISinaWeiboContentMService extends IMockService<SinaWeiboContentMEO> {

    /**
     * 分页获取当前用户微博信息
     * 最多200条
     * @param vo
     * @return
     */
    public Pagination getPageCurWeibo(WeiboPageVO vo);

    /**
     * 根据微博ID和微博类型删除微博
     * @param weiboId
     */
    public void delByWeiboId(String weiboId);

    /**
     * 根据weiboId获取微博
     * @param weiboId
     * @return
     */
    public SinaWeiboContentMEO getByWeiboId(String weiboId);
}
