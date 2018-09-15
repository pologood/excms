package cn.lonsun.weibo.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.SinaWeiboContentSEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-24 13:52
 */
public interface ISinaWeiboContentSDao extends IMockDao<SinaWeiboContentSEO> {

    public Pagination getPageCurWeibo(WeiboPageVO vo);

    public void delByWeiboId(String weiboId);

    public SinaWeiboContentSEO getByWeiboId(String weiboId);
}
