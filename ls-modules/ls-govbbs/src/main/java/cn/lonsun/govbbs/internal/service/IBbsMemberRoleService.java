package cn.lonsun.govbbs.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;

import java.util.List;

public interface IBbsMemberRoleService extends IMockService<BbsMemberRoleEO> {

	public List<BbsMemberRoleEO> getBbsMemberRoleMap(Long siteId);

	public BbsMemberRoleEO getMemberRoleByPoints(Integer memberPoints, Long siteId);

	List<BbsMemberRoleEO> BbsMemberRoleList(Long siteId);



}
