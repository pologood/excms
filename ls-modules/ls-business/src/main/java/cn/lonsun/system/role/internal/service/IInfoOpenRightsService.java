package cn.lonsun.system.role.internal.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.system.role.internal.entity.RbacInfoOpenRightsEO;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IInfoOpenRightsService extends IMockService<RbacInfoOpenRightsEO> {

    /**
     * 查询信息公开权限
     * @param organId
     * @return
     */
    public Object getInfoOpenRights(Long organId,Long roleId);

    /**
     * 查询信息公开权限
     * @param organId
     * @return
     */
    public Object getInfoOpenRights(Long organId,Long roleId,Long siteId);

    /**
     * 获取权限
     * @param list
     * @return
     */
    public List<OrganCatalogVO> getInfoOpenRights(List<OrganCatalogVO> list);

    /**
     * 获取当前用户拥有的权限
     * @return
     */
    public Map<String,Set<FunctionEO>> getCurUserHasRights();

    /**
     * 保存权限
     * @param rights
     * @param roleId
     * @param organIds
     */
    public void saveRights(String rights, Long roleId,String organIds);

    /**
     * 获取权限
     * @param roleIds
     * @return
     */
    public List<RbacInfoOpenRightsEO> getEOsByRoleIds(Long[] roleIds);

    /**
     * 删除权限
     * @param roleId
     * @param organIds
     */
    public void delByRoleIdAndOrganId(Long roleId,String organIds);

}
