package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.RecordsException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IRelationshipDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RelationshipEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IRelationshipService;
import cn.lonsun.rbac.vo.RelationshipQueryVO;

/**
 * 人员上下级关系接口实现类
 *  
 * @author xujh 
 * @date 2014年10月29日 下午2:41:25
 * @version V1.0
 */
@Service("relationshipService")
public class RelationshipService extends BaseService<RelationshipEO> implements IRelationshipService {

	@Autowired
	private IRelationshipDao relationshipDao;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IOrganService organService;
	
	@Override
	public List<RelationshipEO> getSubordinates(Long leaderPersonId) {
		return relationshipDao.getSubordinates(leaderPersonId);
	}
	
	/**
	 * 获取下属
	 * 
	 * @param unitIds
	 * @return
	 */
	public List<RelationshipEO> getSubordinates(List<Long> unitIds){
		return relationshipDao.getSubordinates(unitIds);
	}

	@Override
	public List<RelationshipEO> saveRelationships(Long leaderPersonId, List<Long> personIds) {
		//获取领导
		Long leaderOrganId = null;
		String leaderName = null;
		PersonEO leader = null;
		if(leaderPersonId!=null&&leaderPersonId>0){
			//获取领导个人详细信息
			leader = personService.getEntity(PersonEO.class, leaderPersonId);
			if(leader==null){
				throw new RecordsException();
			}
			leaderOrganId = leader.getOrganId();
			leaderName = leader.getName();
		}
		//获取添加的下属
		Map<String,Object> params = new HashMap<String,Object>(1);
		params.put("personId", personIds);
		List<PersonEO> persons = personService.getEntities(PersonEO.class, params);
		List<RelationshipEO> ships = null;
		if(persons!=null&&persons.size()>0){
			//构造上下级关系
			ships = new ArrayList<RelationshipEO>(persons.size()+1);
			for(PersonEO p:persons){
				RelationshipEO ship = new RelationshipEO();
				ship.setPersonId(p.getPersonId());
				ship.setOrganId(p.getOrganId());
				ship.setUnitId(p.getUnitId());
				ship.setName(p.getName());
				ship.setUserId(p.getUserId());
				ship.setLeaderOrganId(leaderOrganId);
				ship.setOrganName(p.getOrganName());
				ship.setLeaderPersonId(leaderPersonId);
				ship.setLeaderName(leaderName);
				ship.setSubordinateCount(relationshipDao.getSubordinateCount(p.getPersonId()));
				ships.add(ship);
			}
			//保存到数据库
			saveEntities(ships);
			//更新领导的下属数量
			if(leader!=null){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("personId", leaderPersonId);
				List<RelationshipEO> leaderShips = getEntities(RelationshipEO.class, map);
				if(leaderShips!=null&&leaderShips.size()>0){
					for(RelationshipEO ship:leaderShips){
						ship.setSubordinateCount(ship.getSubordinateCount()+ships.size());
					}
				}
				updateEntities(leaderShips);
				ships.addAll(leaderShips);
			}
		}
		return ships;
	}

	@Override
	public Integer updateSubordinateCount(Long leaderPersonId) {
		if(leaderPersonId==null){
			throw new NullPointerException();
		}
		//更新下属数量
		RelationshipEO ship = getShipByPersonId(leaderPersonId);
		Integer count = Integer.valueOf(0);
		if(ship!=null){
			//获取下属数量
			count = relationshipDao.getSubordinateCount(leaderPersonId);
			if(ship.getSubordinateCount().intValue()!=count.intValue()){
				ship.setSubordinateCount(count);
				updateEntity(ship);
			}
		}
		return count;
	}

	@Override
	public RelationshipEO getShipByPersonId(Long personId) {
		return relationshipDao.getShipByPersonId(personId);
	}

	@Override
	public List<RelationshipEO> getShipsByPersonIds(Long[] personIds) {
		return relationshipDao.getShipsByPersonIds(personIds);
	}

	@Override
	public Pagination getPage(RelationshipQueryVO vo) {
		Pagination page = relationshipDao.getPage(vo);
		List<?> data = page.getData();
		if(data!=null&&data.size()>0){
			//获取不重复的部门、单位主键集合
			List<Long> organIds = new ArrayList<Long>();
			for(Object obj:data){
				RelationshipEO ship = (RelationshipEO)obj;
				Long organId = ship.getOrganId();
				Long unitId = ship.getUnitId();
				if(!organIds.contains(organId)){
					organIds.add(organId);
				}
				if(!organIds.contains(unitId)){
					organIds.add(unitId);
				}
			}
			//查询部门和单位
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("organId", organIds);
			params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
			List<OrganEO> organs = organService.getEntities(OrganEO.class, params);
			//构造map，key-organId,value-organName
			Map<Long, String> map = new HashMap<Long, String>(organs.size());
			for(OrganEO organ:organs){
				map.put(organ.getOrganId(), organ.getName());
			}
			//设置单位名称和部门名称
			for(Object obj:data){
				RelationshipEO ship = (RelationshipEO)obj;
				ship.setOrganName(map.get(ship.getOrganId()));
				ship.setUnitName(map.get(ship.getUnitId()));
			}
		}
		return page;
	}

	@Override
	public boolean hasSubordinates(Long leaderPersonId) {
		if(leaderPersonId==null){
			throw new NullPointerException();
		}
		return relationshipDao.hasSubordinates(leaderPersonId);
	}
	@Override
	public boolean isInRelationship(Long personId){
		if(personId==null){
			throw new NullPointerException();
		}
		return relationshipDao.hasSubordinates(personId);
	}

	@Override
	public List<RelationshipEO> getAllSubordinates(Long leaderPersonId) {
		List<RelationshipEO> relations = relationshipDao.getAllSubordinates(leaderPersonId);
		if(relations!=null&&relations.size()>0){
			List<Long> organIds = new ArrayList<Long>();
			//key-organId
			Map<String,List<RelationshipEO>> map = new HashMap<String, List<RelationshipEO>>();
			for(RelationshipEO r:relations){
				Long organId = r.getOrganId();
				List<RelationshipEO> rs = map.get(organId.toString());
				if(rs==null){
					rs = new ArrayList<RelationshipEO>();
					rs.add(r);
					organIds.add(organId);
					map.put(organId.toString(), rs);
				}else{
					rs.add(r);
				}
			}
			if(organIds!=null&&organIds.size()>0){
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("organId", organIds);
				params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
				List<OrganEO> organs = organService.getEntities(OrganEO.class, params);
				if(organs!=null&&organs.size()>0){
					for(OrganEO organ:organs){
						Long organId = organ.getOrganId();
						List<RelationshipEO> rs = map.get(organId.toString());
						if(rs!=null&&rs.size()>0){
							for(RelationshipEO r:rs){
								r.setOrganName(organ.getName());
							}
						}
					}
				}
			}
		}
		return relations;
	}


}
