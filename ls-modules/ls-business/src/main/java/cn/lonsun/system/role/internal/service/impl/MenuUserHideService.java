package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.system.role.internal.dao.IMenuUserHideDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.service.IMenuUserHideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gu.fei
 * @version 2015-10-30 10:58
 */
@Service
public class MenuUserHideService extends MockService<RbacMenuUserHideEO> implements IMenuUserHideService {

    @Autowired
    private IMenuUserHideDao menuUserHideDao;

    @Override
    public void phyDelete(Long menuId) {
        menuUserHideDao.phyDelete(menuId);
    }
}
