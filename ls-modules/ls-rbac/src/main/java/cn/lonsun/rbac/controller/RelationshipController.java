package cn.lonsun.rbac.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.RelationshipEO;
import cn.lonsun.rbac.internal.facade.ILdapFacadeService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IRelationshipService;
import cn.lonsun.rbac.internal.vo.RelationshipVO;
import cn.lonsun.rbac.vo.RelationshipQueryVO;

/**
 * 上下级关系管理控制器
 * 
 * @author xujh
 * @date 2014年10月29日 上午9:05:47
 * @version V1.0
 */
@Controller
@RequestMapping("relationship")
public class RelationshipController extends BaseController {

	Logger logger = LoggerFactory.getLogger(RelationshipController.class);

	@Autowired
	private IRelationshipService relationshipService;
	@Autowired
	private ILdapFacadeService ldapFacadeService;
	@Autowired
	private IOrganService organService;
	@Autowired
	private IPersonService personService;

	/**
	 * 上下级管理首页
	 * 
	 * @return
	 */
	@RequestMapping("index")
	public String index(HttpSession session,HttpServletRequest request,Long indicatorId) {
		//获取用户管理的单位主键串
		Long userId = SessionUtil.getLongProperty(session, "userId");
		List<OrganEO> units = organService.getFLUnits4UnitManager(userId,indicatorId);
		if(units!=null&&units.size()>0){
			String unitIds = null;
			for(OrganEO unit:units){
				if(StringUtils.isEmpty(unitIds)){
					unitIds = unit.getOrganId().toString();
				}else{
					unitIds = unitIds+","+unit.getOrganId().toString();
				}
			}
			request.setAttribute("unitIds", unitIds);
		}
		return "/app/mgr/relationship/index";
	}

	/**
	 * 上下级管理首页
	 * 
	 * @return
	 */
	/**
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("subordinatesPage")
	public ModelAndView subordinatesPage(HttpServletRequest request) {
		Long leaderPersonId = RequestUtil.getLongParameter(request,"leaderPersonId");
		ModelAndView view = new ModelAndView();
		view.addObject("leaderPersonId", leaderPersonId);
		Long unitId = SessionUtil.getLongProperty(request.getSession(), "unitId");
		view.addObject("unitId", unitId);
		view.setViewName("/app/mgr/relationship/subordinates");
		return view;
	}

	/**
	 * 选择下级的弹出页面
	 * 
	 * @return
	 */
	@RequestMapping("getSubordinatesPage")
	public String getSubordinatesPage() {
		return "/app/mgr/relationship/getSubordinates";
	}

	/**
	 * 为KO构建空对象
	 * 
	 * @return
	 */
	@ResponseBody
	public Object getNullRelationship() {
		return getObject(new RelationshipEO());
	}

	/**
	 * 根据主键获取上下级关系对象
	 * 
	 * @return
	 */
	@ResponseBody
	public Object getRelationship(Long relationshipId) {
		// 入参验证
		if (relationshipId == null || relationshipId <= 0) {
			logger.error("The argument relationshipId of the method getRelationship is illegal.");
			throw new NullPointerException();
		}
		return getObject(relationshipService.getEntity(RelationshipEO.class,
				relationshipId));
	}

	/**
	 * 保存上下级关系
	 * 
	 * @param ships
	 * @return
	 */
	@RequestMapping("saveRelationships")
	@ResponseBody
	public Object saveRelationships(Long leaderPersonId,
			@RequestParam("personIds[]") Long[] personIds) {
		List<RelationshipEO> ships = null;
		if (personIds == null || personIds.length <= 0) {
			throw new NullPointerException();
		}
		List<RelationshipEO> existedShips = relationshipService.getShipsByPersonIds(personIds);
		// 已经有直接领导的将被直接过滤掉
		List<Long> list = new ArrayList<Long>(personIds.length);
		Collections.addAll(list, personIds);
		//已经有直接领导的用户名称，用户最后提示
		String existedName = null;
		if (existedShips != null && existedShips.size() > 0) {
			for (RelationshipEO ship : existedShips) {
				if (list.contains(ship.getPersonId())) {
					if(existedName==null){
						existedName = ship.getName();
					}else{
						existedName = existedName.concat("、").concat(ship.getName());
					}
					list.remove(ship.getPersonId());
				}
			}
		}
		// 保存上下级关系
		List<RelationshipVO> vos = null;
		if (list.size() > 0) {
			ships = relationshipService.saveRelationships(leaderPersonId, list);
			vos = new ArrayList<RelationshipVO>(ships.size());
			for (RelationshipEO ship : ships) {
				RelationshipVO vo = new RelationshipVO();
				vo.setId(ship.getPersonId());
				vo.setName(ship.getName());
				vo.setPid(ship.getLeaderPersonId());
				vo.setIsParent(ship.getSubordinateCount() > 0 ? true : false);
				vo.setSubordinateCount(ship.getSubordinateCount());
				vo.setRelationshipId(ship.getRelationshipId());
				vo.setIcon(Icon.Male.getValue());
				vos.add(vo);
			}
		}
		ResultVO result = (ResultVO)getObject(vos);
		//设置提示信息
		if(existedName!=null){
			result.setDesc(existedName+"等人员存在直接领导，已忽略");
		}
		return result;
	}
	

	/**
	 * 获取领导下属
	 * 
	 * @param session
	 * @param leaderPersonId
	 * @param indicatorId
	 * @return
	 */
	@RequestMapping("getSubordinates")
	@ResponseBody
	public Object getSubordinates(HttpSession session,Long leaderPersonId,Long indicatorId) {
		if(indicatorId==null){
			throw new NullPointerException();
		}
		List<RelationshipEO> ships = null;
		if(leaderPersonId==null||leaderPersonId<=0){
			//获取当前用户所在的单位ID，可能又多个-兼职
			Long userId = SessionUtil.getLongProperty(session, "userId");
			List<OrganEO> units = organService.getFLUnits4UnitManager(userId,indicatorId);
			if(units!=null&&units.size()>0){
				List<Long> unitIds = new ArrayList<Long>(units.size());
				for(OrganEO unit:units){
					if(unit!=null){
						unitIds.add(unit.getOrganId());
					}
				}
				ships = relationshipService.getSubordinates(unitIds);
			}	
		}else{
			ships = relationshipService.getSubordinates(leaderPersonId);
		}
		List<RelationshipVO> vos = new ArrayList<RelationshipVO>(ships.size());
		for (RelationshipEO ship : ships) {
			RelationshipVO vo = new RelationshipVO();
			vo.setId(ship.getPersonId());
			vo.setName(ship.getName());
			vo.setPid(ship.getLeaderPersonId());
			vo.setIsParent(ship.getSubordinateCount() > 0 ? true : false);
			vo.setSubordinateCount(ship.getSubordinateCount());
			vo.setRelationshipId(ship.getRelationshipId());
			vo.setIcon(Icon.Male.getValue());
			vos.add(vo);
		}
		return getObject(vos);
	}
	
	/**
	 * 删除上下级关系
	 * 
	 * @param relationshipId
	 * @return 返回父节是否含有子节点的标记isParent
	 */
	@RequestMapping("deleteRelationship")
	@ResponseBody
	public Object deleteRelationship(Long relationshipId, Long leaderPersonId) {
		// 入参验证
		if (relationshipId == null || relationshipId <= 0) {
			throw new NullPointerException();
		}
		// 物理删除
		relationshipService.delete(RelationshipEO.class, relationshipId);
		// 更新父节点包含的子节点的数量
		Boolean isParent = Boolean.TRUE;
		if (leaderPersonId != null && leaderPersonId > 0) {
			Integer count = relationshipService
					.updateSubordinateCount(leaderPersonId);
			if (count <= 0) {
				isParent = Boolean.FALSE;
			}
		}
		return getObject(isParent);
	}

	/**
	 * 获取分页
	 * 
	 * @param leaderPersonId
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(RelationshipQueryVO vo) {
		if (vo == null || vo.getLeaderPersonId() == null) {
			throw new NullPointerException();
		}
		Pagination page = relationshipService.getPage(vo);
		return getObject(page);
	}
}
