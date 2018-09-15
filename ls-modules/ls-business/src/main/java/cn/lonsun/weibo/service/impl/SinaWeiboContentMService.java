package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.dao.ISinaWeiboContentMDao;
import cn.lonsun.weibo.entity.SinaWeiboContentMEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboContentMService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboContentMService extends MockService<SinaWeiboContentMEO> implements ISinaWeiboContentMService {

    @Autowired
    private ISinaWeiboContentMDao sinaWeiboContentMDao;

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        return sinaWeiboContentMDao.getPageCurWeibo(vo);
    }

    @Override
    public void delByWeiboId(String weiboId) {
        sinaWeiboContentMDao.delByWeiboId(weiboId);
    }

    @Override
    public SinaWeiboContentMEO getByWeiboId(String weiboId) {
        return sinaWeiboContentMDao.getByWeiboId(weiboId);
    }
}
