package cn.lonsun.weibo.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.weibo.entity.SinaWeiboCommentToMeEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;

/**
 * @author gu.fei
 * @version 2015-12-24 13:52
 */
public interface ISinaWeiboCommentToMeService extends IMockService<SinaWeiboCommentToMeEO> {

    public WeiboPagination getPageCurComment(WeiboPageVO vo);

    public void delByCommentId(String commentId);
}
