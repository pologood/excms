package cn.lonsun.system.role.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.system.role.internal.entity.RbacUserSiteRightsEO;
import cn.lonsun.system.role.internal.entity.vo.IdsVO;
import cn.lonsun.system.role.internal.entity.vo.SiteRightsVO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-9-18 16:25
 */
public interface IUserSiteRightsDao extends IBaseDao<RbacUserSiteRightsEO> {

    /**
     * 根据用户ID删除
     * @param userId
     */
    void deleteByUserId(Long userId);

    /**
     * 获取栏目权限的ID
     * @param userId
     * @param siteId
     * @return
     */
    List<Long> getInicatorIdList(Long userId,Long siteId);

    /**
     * 查询站点权限
     * @param userId
     * @param type
     * @param siteId
     * @return
     */
    List<SiteRightsVO> getSiteRights(Long userId,String type,Long siteId);

    /**
     * 查询站点权限
     * @param userId
     * @param type
     * @return
     */
    List<IdsVO> getSites(Long userId, String type);
}
