package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.weibo.dao.ISinaWeiboCommentToMeDao;
import cn.lonsun.weibo.entity.SinaWeiboCommentToMeEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;
import cn.lonsun.weibo.service.ISinaWeiboCommentToMeService;

/**
 * @author gu.fei
 * @version 2015-12-24 13:56
 */
@Service
public class SinaWeiboCommentToMeService extends MockService<SinaWeiboCommentToMeEO> implements ISinaWeiboCommentToMeService {

    @Autowired
    private ISinaWeiboCommentToMeDao sinaWeiboCommentToMeDao;

    @Override
    public WeiboPagination getPageCurComment(WeiboPageVO vo) {
        return sinaWeiboCommentToMeDao.getPageCurComment(vo);
    }

    @Override
    public void delByCommentId(String commentId) {
        sinaWeiboCommentToMeDao.delByCommentId(commentId);
    }
}
