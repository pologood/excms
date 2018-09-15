package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.dao.ISinaWeiboContentFDao;
import cn.lonsun.weibo.entity.SinaWeiboContentFEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboContentFService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboContentFService extends MockService<SinaWeiboContentFEO> implements ISinaWeiboContentFService {

    @Autowired
    private ISinaWeiboContentFDao sinaWeiboContentFDao;

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        return sinaWeiboContentFDao.getPageCurWeibo(vo);
    }

    @Override
    public void delByWeiboId(String weiboId) {
        sinaWeiboContentFDao.delByWeiboId(weiboId);
    }

    @Override
    public SinaWeiboContentFEO getByWeiboId(String weiboId) {
        return sinaWeiboContentFDao.getByWeiboId(weiboId);
    }
}
