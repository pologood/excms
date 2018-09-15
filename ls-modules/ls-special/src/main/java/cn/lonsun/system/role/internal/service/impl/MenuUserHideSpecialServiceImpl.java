package cn.lonsun.system.role.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.system.role.internal.dao.IMenuUserHideSpecialDao;
import cn.lonsun.system.role.internal.entity.RbacMenuUserHideEO;
import cn.lonsun.system.role.internal.service.IMenuUserHideSpecialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class MenuUserHideSpecialServiceImpl extends MockService<RbacMenuUserHideEO> implements IMenuUserHideSpecialService  {

    @Resource
    private IMenuUserHideSpecialDao menuUserHideSpecialDaoDao;

    @Override
    public void phyDelete(Long menuId) {
        menuUserHideSpecialDaoDao.phyDelete(menuId);
    }
}
