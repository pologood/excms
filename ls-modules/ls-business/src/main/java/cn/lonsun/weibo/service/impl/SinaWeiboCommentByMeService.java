package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.weibo.dao.ISinaWeiboCommentByMeDao;
import cn.lonsun.weibo.entity.SinaWeiboCommentByMeEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;
import cn.lonsun.weibo.service.ISinaWeiboCommentByMeService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboCommentByMeService extends MockService<SinaWeiboCommentByMeEO> implements ISinaWeiboCommentByMeService {

    @Autowired
    private ISinaWeiboCommentByMeDao sinaWeiboCommentByMeDao;

    @Override
    public WeiboPagination getPageCurComment(WeiboPageVO vo) {
        return sinaWeiboCommentByMeDao.getPageCurComment(vo);
    }

    @Override
    public void delByCommentId(String commentId) {
        sinaWeiboCommentByMeDao.delByCommentId(commentId);
    }
}
