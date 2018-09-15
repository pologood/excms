package cn.lonsun.system.globalconfig.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.system.globalconfig.internal.dao.IGlobConfigCateDao;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigCateEO;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigCateService;

@Service("globConfigCateService")
public class GlobConfigCateServiceImpl extends BaseService<GlobConfigCateEO> implements
		IGlobConfigCateService {

	@Autowired
	private IGlobConfigCateDao globConfigCateDao;

	@Override
	public GlobConfigCateEO getGlobConfigCateByCode(String code) {
		return globConfigCateDao.getGlobConfigCateByCode(code);
	}

	@Override
	public GlobConfigCateEO getGlobConfigCateByKey(String key) {
		return globConfigCateDao.getGlobConfigCateByKey(key);
	}

}
