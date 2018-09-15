package cn.lonsun.rbac.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.rbac.internal.dao.IOrganPersonDao;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganPersonService;
import cn.lonsun.rbac.internal.service.IPersonService;

@Service("organPersonService")
public class OrganPersonServiceImpl extends MockService<OrganPersonEO> implements
		IOrganPersonService {

	@Autowired
	private IOrganPersonDao organPersonDao;
	@Autowired
	private IPersonService personService;
	
	
	@Override
	public void deleteByPersonId(Long personId) {
		if(personId==null||personId<=0){
			throw new NullPointerException();
		}
		List<OrganPersonEO> ous = organPersonDao.getOrganPersonsByPersonId(personId);
		delete(ous);
	}
	
	/**
	 * 根据单位ID集合删除人员和单位的关系记录
	 *
	 * @param organIds
	 */
	public void deleteByOrganIds(List<Long> organIds){
		if(organIds==null||organIds.size()<=0){
			return;
		}
		organPersonDao.deleteByOrganIds(organIds);
	}

	@Override
	public void deleteByOrganId(Long organId) {
		if(organId==null||organId<=0){
			throw new NullPointerException();
		}
		List<OrganPersonEO> ous = organPersonDao.getOrganPersonsByOrganId(organId);
		delete(ous);
	}

	@Override
	public List<OrganPersonEO> getOrganPersonsByPersonId(Long personId) {
		if(personId==null||personId<=0){
			throw new NullPointerException();
		}
		return organPersonDao.getOrganPersonsByPersonId(personId);
	}

	@Override
	public List<OrganPersonEO> getOrganPersonsByOrganId(Long organId) {
		if(organId==null||organId<=0){
			throw new NullPointerException();
		}
		return organPersonDao.getOrganPersonsByOrganId(organId);
	}

	@Override
	public void updateOrganName(Long organId, String organName,String type) {
		if(organId==null||StringUtils.isEmpty(organName)){
			throw new NullPointerException();
		}
		//是否单位名称被修改
		boolean isUnitNameChaged = false;
		Map<String,Object> params = new HashMap<String,Object>();
		//单位
		if(type.endsWith(TreeNodeVO.Type.Organ.toString())){
			isUnitNameChaged = true;
			params.put("unitId", organId);
		}else{//部门
			params.put("organId", organId);
		}
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<PersonEO> persons = personService.getEntities(PersonEO.class, params);
		if(persons!=null&&persons.size()>0){
			for(PersonEO p:persons){
				if(isUnitNameChaged){
					p.setUnitName(organName);
				}else{
					p.setOrganName(organName);
				}
			}
			personService.updateEntities(persons);
		}
		organPersonDao.updateOrganName(organId, organName);
	}

}
