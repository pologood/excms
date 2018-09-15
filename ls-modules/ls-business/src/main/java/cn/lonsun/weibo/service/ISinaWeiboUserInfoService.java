package cn.lonsun.weibo.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.SinaWeiboUserInfoEO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-12-25 16:39
 */
public interface ISinaWeiboUserInfoService extends IMockService<SinaWeiboUserInfoEO> {

    /**
     * 分页获取用户信息
     * @param vo
     * @return
     */
    public Pagination getPageCurUser(WeiboPageVO vo);

    /**
     * 根据类型查询用户
     * @param auth
     * @return
     */
    public List<SinaWeiboUserInfoEO> getByAuth(String auth);

    /**
     * 根据ID查询
     * @param userId
     * @return
     */
    public SinaWeiboUserInfoEO getByUserId(String userId);
}
