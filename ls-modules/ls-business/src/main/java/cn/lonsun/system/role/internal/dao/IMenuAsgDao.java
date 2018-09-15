package cn.lonsun.system.role.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.rbac.internal.entity.IndicatorPermissionEO;

/**
 * @author gu.fei
 * @version 2015-10-30 10:46
 */
public interface IMenuAsgDao extends IMockDao<IndicatorPermissionEO> {

    /**
     * 删除角色的权限
     *
     * @author yy
     * @param roleId
     * @return
     */
    public void deleteByRoleAndIndicator(Long roleId);


    /**
     * 根据roleId查询
     *
     * @author yy
     * @param roleId
     * @return
     */
    public List<IndicatorPermissionEO> getByRoleId(Long roleId);

    /**
     * 根据角色和权限查询
     *
     * @author yy
     * @param roleId
     * @param right
     */
    public IndicatorPermissionEO getByRoleAndIndicator(Long roleId, Long right);

    /**
     * 删除权限
     *
     * @param roleId
     * @param indicatorIds
     */
    public void deletePermissions(Long roleId,List<Long> indicatorIds);
    /**
     * 根据角色ID获取所有的正常使用的IndicatorId集合
     * @param roleId
     * @return
     */
    public List<Long> getIndicatorIds(Long roleId);

}
