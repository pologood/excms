package cn.lonsun.system.role.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.system.role.internal.entity.RbacUserInfoOpenRightsEO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
public interface IUserInfoOpenRightsService extends IMockService<RbacUserInfoOpenRightsEO> {
    /**
     * 获取信息公开权限
     * @param userId
     * @return
     */
    List<OrganCatalogVO> getInfoOpenRights(Long userId,Long organId);

    /**
     * 保存信息公开权限
     * @param userId
     * @param organIds
     * @param rights
     */
    void saveInfoOpenRights(Long userId,String organIds,String rights);

    /**
     * 添加用户权限到map中
     * @param map
     */
    Map<String, Set<FunctionEO>> setInfoOpenFunction(Map<String, Set<FunctionEO>> map,Long siteId);
}
