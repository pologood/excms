package cn.lonsun.rbac.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IRtxFailureDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RtxFailureEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IRtxFailureService;
import cn.lonsun.rbac.internal.vo.RtxQueryVO;

@Service("rtxFailureService")
public class RtxFailureServiceImpl extends BaseService<RtxFailureEO> implements
IRtxFailureService {
	@Autowired
	private IRtxFailureDao rtxFailureDao;

	@Autowired
	private IPersonService personService;

	@Autowired
	private IOrganService organService;


	@Override
	public Pagination getPage(RtxQueryVO rqvo) {
		return rtxFailureDao.getPage(rqvo);
	}

	@Override
	public void synchronous() {
		List<RtxFailureEO> listAll=rtxFailureDao.getEntities(RtxFailureEO.class, new HashMap<String,Object>());
		if(listAll!=null && listAll.size()>0){
			for(RtxFailureEO r:listAll){
				if(r.getType().equals(RtxFailureEO.Type.Organ.toString())){
					syncOrgan(r);
				}else if(r.getType().equals(RtxFailureEO.Type.Person.toString())){
					syncPerson(r);
				}
			}
		}

	}

	/**
	 * 人员处理
	 * @param r
	 */
	private void syncPerson(RtxFailureEO r) {
		String status=r.getOperation();
		if(status.equals(RtxFailureEO.Operation.Delete.toString())){
			deletePerson(r.getPersonId());
		}else{
			saveOrupdatePerson(r.getPersonId(),r.getPassword(),status);
		}
	}
	
	/**
	 * 单位处理
	 *
	 * @param r
	 */
	private void syncOrgan(RtxFailureEO r) {
		String operation=r.getOperation();
		if(operation.equals(RtxFailureEO.Operation.Delete.toString())){
			deleteOrgan(r.getOrganId());
		}else{
			saveOrupdateOrgan(r.getOrganId(),operation);
		}
	}

	/**
	 * 
	 *
	 * @param personId
	 * @param passWord
	 * @param op
	 */
	private void saveOrupdatePerson(Long personId,String passWord, String op) {
		boolean isExecuteSuccess = false;
		PersonEO person = personService.getEntity(PersonEO.class, personId);
		if(person!=null){
			String organId = person.getOrganId().toString();
			try {
//				isExecuteSuccess= rtxService.syncUser4Contact(person.getUid(), person.getName(), "0",passWord, organId, person.getOfficePhone(), person.getMobile(), person.getMail());
				if(isExecuteSuccess){
					deleteRtxFailure(personId,RtxFailureEO.Type.Person.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				//新增RTX同步失败记录
				String operation =op;
				String description = "RTX新增失败";
				if(op.equals(RtxFailureEO.Operation.Update.toString())){
					description="RTX更新失败";
				}
				RtxFailureEO eo = getRtxFailure(person,operation,description,passWord, e);
				rtxFailureDao.saveOrUpdate(eo);
			}
		}
	}

	/**
	 * 删除人员
	 *
	 * @param personId
	 */
	private void deletePerson(Long personId) {
		boolean isExecuteSuccess = false;
		List<String> uids =new ArrayList<String>();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("personId", personId);
		List<PersonEO> persons= personService.getEntities(PersonEO.class, map);
		if(persons!=null && persons.size()>0){
			for(PersonEO p:persons){
				uids.add(p.getUid());
			}
		}
		try {
			if(uids!=null&&uids.size()>0){
				for(String uid:uids){
//					isExecuteSuccess = rtxService.deleteUser(uid);
					if(isExecuteSuccess){
						deleteRtxFailure(personId,RtxFailureEO.Type.Person.toString());
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			//获取uid对应主单位部门的Person集合
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("uid", uids);
			params.put("isPluralistic", false);
			List<PersonEO> personList = personService.getEntities(PersonEO.class, params);
			String operation = RtxFailureEO.Operation.Delete.toString();
			String description = "RTX删除失败";
			if(personList!=null&&personList.size()>0){
				for(PersonEO person:persons){
					RtxFailureEO eo = getRtxFailure(person,  operation, description,"",e);
					rtxFailureDao.saveOrUpdate(eo);
				}
			}
		}
	}

	/**
	 * 保存或更新组织
	 *
	 * @param orId
	 * @param op
	 */
	private void saveOrupdateOrgan(Long orId,String op) {
		boolean isExecuteSuccess = false;
		OrganEO organ = organService.getEntity(OrganEO.class, orId);
		if(organ!=null){
			String organParentId = organ.getParentId()==null?null:organ.getParentId().toString();
			String organId = organ.getOrganId().toString();
			String organName = organ.getName();
			try {
//				isExecuteSuccess=rtxService.saveDept(organId, organParentId, organName);
				if(isExecuteSuccess){
					deleteRtxFailure(orId,RtxFailureEO.Type.Organ.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				//新增RTX同步失败记录
				String operation = op;
				String description = "RTX新增失败";
				if(op.equals(RtxFailureEO.Operation.Update.toString())){
					description="RTX更新失败";
				}
				RtxFailureEO eo = getRtxFailure(organ, e, operation, description);
				rtxFailureDao.saveOrUpdate(eo);
			}
		}

	}

	/**
	 * 删除组织
	 *
	 * @param orId
	 */
	private void deleteOrgan(Long orId) {
		boolean isExecuteSuccess = false;
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("organId", orId);
		List<OrganEO> organs = organService.getEntities(OrganEO.class, map);
		OrganEO organ=null;
		if(organs!=null && organs.size()>0){
			organ=organs.get(0);
		}
		if(organ!=null){
			String organId = organ.getOrganId().toString();
			try {
//				isExecuteSuccess=rtxService.deleteDept(organId);
				if(isExecuteSuccess){
					deleteRtxFailure(orId,RtxFailureEO.Type.Organ.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				//先判断该单位/部门是否有同步失败记录，如果有，那么直接操作该记录，否则新建记录
				String operation = RtxFailureEO.Operation.Delete.toString();
				String description = "RTX删除失败";
				RtxFailureEO eo = getRtxFailure(organ, e, operation, description);
				rtxFailureDao.saveOrUpdate(eo);
			}
		}
	}

	/**
	 * 删除rtx 记录
	 * @param caseId
	 * @param type
	 */
	private void deleteRtxFailure(Long caseId, String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(type.equals(RtxFailureEO.Type.Organ.toString())){
			params.put("organId", caseId);
		}else{
			params.put("personId", caseId);
		}
		params.put("type",type);
		List<RtxFailureEO> entities = rtxFailureDao.getEntities(RtxFailureEO.class, params);
		if(entities!=null&&entities.size()>0){
			rtxFailureDao.delete(entities);
		}
	}
	/**
	 * 获取RtxFailureEO，如果单位/部门已存在记录，那么直接获取并返回更新后的记录，否则新建记录
	 *
	 * @param organ
	 * @param e
	 * @param operation
	 * @param description
	 * @return
	 */
	private RtxFailureEO getRtxFailure(OrganEO organ,Exception e,String operation,String description){
		//先判断该单位/部门是否有同步失败记录，如果有，那么直接操作该记录，否则新建记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("organId", organ.getOrganId());
		params.put("type", RtxFailureEO.Type.Organ.toString());
		List<RtxFailureEO> entities = rtxFailureDao.getEntities(RtxFailureEO.class, params);
		RtxFailureEO eo = null;
		if(entities!=null&&entities.size()>0){
			eo = entities.get(0);
			eo.setOperation(operation);
		}else{
			eo = getNewRtxFailure(organ,operation,description, e);
		}
		//描述信息
		if(e instanceof BaseRunTimeException){
			BaseRunTimeException be = (BaseRunTimeException)e;
			//如果RTX给出了原因，那么直接赋给描述信息
			if(!StringUtils.isEmpty(be.getTipsMessage())){
				description = be.getTipsMessage();
			}
		}
		eo.setDescription(description);
		return eo;
	}

	/**
	 * 构造RTX同步失败记录
	 *
	 * @param organ
	 * @param e
	 * @return
	 */
	private RtxFailureEO getNewRtxFailure(OrganEO organ,String operation,String description,Exception e){
		//构造RTX同步失败记录
		RtxFailureEO eo = new RtxFailureEO();
		eo.setType(RtxFailureEO.Type.Organ.toString());
		eo.setOrganId(organ.getOrganId());
		eo.setOperation(operation);
		//描述信息
		if(e instanceof BaseRunTimeException){
			BaseRunTimeException be = (BaseRunTimeException)e;
			//如果RTX给出了原因，那么直接赋给描述信息
			if(!StringUtils.isEmpty(be.getTipsMessage())){
				description = be.getTipsMessage();
			}
		}
		eo.setDescription(description);
		return eo;
	}

	/**
	 * 构造RTX同步失败记录
	 *
	 * @param organ
	 * @param e
	 * @return
	 */
	private RtxFailureEO getNewRtxFailure(PersonEO person,String operation,String description,String password,Exception e){
		//构造RTX同步失败记录
		RtxFailureEO eo = new RtxFailureEO();
		eo.setType(RtxFailureEO.Type.Person.toString());
		//personId与organId设置互斥，因为可能存在人员调动到其他单位的情况，即organId有可能产生变化，所以需要即时查询
		eo.setPersonId(person.getPersonId());
		eo.setOperation(operation);
		eo.setPassword(password);
		//描述信息
		if(e instanceof BaseRunTimeException){
			BaseRunTimeException be = (BaseRunTimeException)e;
			//如果RTX给出了原因，那么直接赋给描述信息
			if(!StringUtils.isEmpty(be.getTipsMessage())){
				description = be.getTipsMessage();
			}
		}
		eo.setDescription(description);
		return eo;
	}

	/**
	 * 获取RtxFailureEO，如果单位/部门已存在记录，那么直接获取并返回更新后的记录，否则新建记录
	 *
	 * @param organ
	 * @param e
	 * @param operation
	 * @param description
	 * @return
	 */
	private RtxFailureEO getRtxFailure(PersonEO person,String operation,String description,String password,Exception e){
		//先判断该单位/部门是否有同步失败记录，如果有，那么直接操作该记录，否则新建记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("personId", person.getPersonId());
		params.put("type", RtxFailureEO.Type.Person.toString());
		List<RtxFailureEO> entities = rtxFailureDao.getEntities(RtxFailureEO.class, params);
		RtxFailureEO eo = null;
		if(entities!=null&&entities.size()>0){
			eo = entities.get(0);
			eo.setOperation(operation);
		}else{
			eo = getNewRtxFailure(person,operation,description,password, e);
		}
		eo.setPassword(password);
		//描述信息
		if(e instanceof BaseRunTimeException){
			BaseRunTimeException be = (BaseRunTimeException)e;
			//如果RTX给出了原因，那么直接赋给描述信息
			if(!StringUtils.isEmpty(be.getTipsMessage())){
				description = be.getTipsMessage();
			}
		}
		eo.setDescription(description);
		return eo;
	}

}
