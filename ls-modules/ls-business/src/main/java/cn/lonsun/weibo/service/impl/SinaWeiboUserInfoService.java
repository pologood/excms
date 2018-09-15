package cn.lonsun.weibo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.dao.ISinaWeiboUserInfoDao;
import cn.lonsun.weibo.entity.SinaWeiboUserInfoEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboUserInfoService;

/**
 * @author gu.fei
 * @version 2015-12-25 16:39
 */
@Service
public class SinaWeiboUserInfoService extends MockService<SinaWeiboUserInfoEO> implements ISinaWeiboUserInfoService {

    @Autowired
    private ISinaWeiboUserInfoDao sinaWeiboUserInfoDao;

    @Override
    public Pagination getPageCurUser(WeiboPageVO vo) {
        return sinaWeiboUserInfoDao.getPageCurUser(vo);
    }

    @Override
    public List<SinaWeiboUserInfoEO> getByAuth(String auth) {
        return sinaWeiboUserInfoDao.getByAuth(auth);
    }

    @Override
    public SinaWeiboUserInfoEO getByUserId(String userId) {
        return sinaWeiboUserInfoDao.getByUserId(userId);
    }
}
