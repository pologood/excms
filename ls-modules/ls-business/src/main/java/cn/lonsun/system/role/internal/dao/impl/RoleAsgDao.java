package cn.lonsun.system.role.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.system.role.internal.dao.IRoleAsgDao;

/**
 * @author gu.fei
 * @version 2015-9-18 16:24
 */
@Repository
public class RoleAsgDao extends MockDao<RoleAssignmentEO> implements IRoleAsgDao {

}
