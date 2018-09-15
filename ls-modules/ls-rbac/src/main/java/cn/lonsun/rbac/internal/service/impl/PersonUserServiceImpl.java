package cn.lonsun.rbac.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IPersonUserService;
import cn.lonsun.rbac.internal.service.IUserService;

@Service("personUserServcie")
public class PersonUserServiceImpl extends MockService<PersonEO> implements IPersonUserService
{
	
	@Autowired
	private IPersonService personService;
	@Autowired
	private IUserService userService;
	@Override
	public PersonEO getPerson(Long userId) {
		return personService.getUnpluralisticPersons(userId);
	}


}
