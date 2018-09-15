package cn.lonsun.rbac.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.rbac.internal.entity.PersonEO;

public interface IPersonUserService extends IMockService<PersonEO>{
	/**
	 * 根据userId获取Person对象
	 * @param userId
	 * @return
	 */
	public PersonEO getPerson(Long userId);
}
