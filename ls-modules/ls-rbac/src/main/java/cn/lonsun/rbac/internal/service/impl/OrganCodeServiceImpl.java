package cn.lonsun.rbac.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IOrganCodeDao;
import cn.lonsun.rbac.internal.entity.OrganCodeEO;
import cn.lonsun.rbac.internal.service.IOrganCodeService;

@Service
public class OrganCodeServiceImpl extends BaseService<OrganCodeEO> implements IOrganCodeService{
	
	@Autowired
	private IOrganCodeDao organCodeDao;

	@Override
	public Pagination getPagination(PageQueryVO query) {
		return organCodeDao.getPagination(query);
	}

}
