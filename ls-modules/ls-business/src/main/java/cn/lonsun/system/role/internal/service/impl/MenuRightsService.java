package cn.lonsun.system.role.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.system.role.internal.dao.IMenuRightsDao;
import cn.lonsun.system.role.internal.entity.RbacMenuRightsEO;
import cn.lonsun.system.role.internal.service.IMenuRightsService;

/**
 * @author gu.fei
 * @version 2015-9-18 16:26
 */
@Service
public class MenuRightsService extends BaseService<RbacMenuRightsEO> implements IMenuRightsService {

    @Autowired
    private IMenuRightsDao menuRightsDao;

    @Override
    public List<RbacMenuRightsEO> getEOsByRoleIds(Long[] roleIds) {
        return menuRightsDao.getEOsByRoleIds(roleIds);
    }

    @Override
    public void delByRoleId(Long roleId) {
        menuRightsDao.delByRoleId(roleId);
    }

    @Override
    public void delByRoleIdAndSiteId(Long roleId, Long siteId) {
        menuRightsDao.delByRoleIdAndSiteId(roleId,siteId);
    }
}
