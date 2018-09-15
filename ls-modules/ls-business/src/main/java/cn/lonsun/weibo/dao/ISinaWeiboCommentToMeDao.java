package cn.lonsun.weibo.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.weibo.entity.SinaWeiboCommentToMeEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;

/**
 * @author gu.fei
 * @version 2015-12-24 13:52
 */
public interface ISinaWeiboCommentToMeDao extends IMockDao<SinaWeiboCommentToMeEO> {

    public WeiboPagination getPageCurComment(WeiboPageVO vo);

    public void delByCommentId(String commentId);
}
