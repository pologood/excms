package cn.lonsun.rbac.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.rbac.internal.entity.RelationshipEO;
import cn.lonsun.rbac.internal.service.IPersonRelationshipService;
import cn.lonsun.rbac.internal.service.IRelationshipService;

/**
 * 人员上下级关系接口实现类
 *  
 * @author xujh 
 * @date 2014年10月29日 下午2:41:25
 * @version V1.0
 */
@Service("personRelationshipService")
public class PersonRelationshipServiceImpl  extends BaseService<RelationshipEO> implements IPersonRelationshipService {
	
	@Autowired
	private IRelationshipService relationshipService;

	@Override
	public boolean hasSubordinates(Long leaderPersonId) {
		return relationshipService.hasSubordinates(leaderPersonId);
	}

	@Override
	public boolean isInRelationship(Long personId) {
		return relationshipService.isInRelationship(personId);
	}


}
