package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.dao.ISinaWeiboContentDao;
import cn.lonsun.weibo.entity.SinaWeiboContentEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboContentService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboContentService extends MockService<SinaWeiboContentEO> implements ISinaWeiboContentService {

    @Autowired
    private ISinaWeiboContentDao sinaWeiboContentDao;

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        return sinaWeiboContentDao.getPageCurWeibo(vo);
    }

    @Override
    public void delByWeiboId(String weiboId) {
        sinaWeiboContentDao.delByWeiboId(weiboId);
    }

    @Override
    public SinaWeiboContentEO getByWeiboId(String weiboId) {
        return sinaWeiboContentDao.getByWeiboId(weiboId);
    }
}
