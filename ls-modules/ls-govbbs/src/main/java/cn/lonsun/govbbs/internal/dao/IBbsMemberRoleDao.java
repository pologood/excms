package cn.lonsun.govbbs.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;

import java.util.List;

/**
 * 论坛附件Service层<br/>
 *
 */

public interface IBbsMemberRoleDao extends IMockDao<BbsMemberRoleEO> {

    BbsMemberRoleEO getMemberRoleByPoints(Integer memberPoints, Long siteId);

    List<BbsMemberRoleEO> BbsMemberRoleList(Long siteId);
}

