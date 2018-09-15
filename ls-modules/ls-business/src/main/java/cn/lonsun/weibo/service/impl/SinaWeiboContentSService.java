package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.dao.ISinaWeiboContentSDao;
import cn.lonsun.weibo.entity.SinaWeiboContentSEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboContentSService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboContentSService extends MockService<SinaWeiboContentSEO> implements ISinaWeiboContentSService {

    @Autowired
    private ISinaWeiboContentSDao sinaWeiboContentSDao;

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        return sinaWeiboContentSDao.getPageCurWeibo(vo);
    }

    @Override
    public void delByWeiboId(String weiboId) {
        sinaWeiboContentSDao.delByWeiboId(weiboId);
    }

    @Override
    public SinaWeiboContentSEO getByWeiboId(String weiboId) {
        return sinaWeiboContentSDao.getByWeiboId(weiboId);
    }
}
