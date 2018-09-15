package cn.lonsun.govbbs.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsPlateUnitEO;
import cn.lonsun.govbbs.internal.service.IBbsPlateService;
import cn.lonsun.govbbs.internal.service.IBbsPlateUnitService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.vo.BbsPlateVO;
import cn.lonsun.govbbs.util.PlateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "bbsPlate", produces = { "application/json;charset=UTF-8" })
public class BbsPlateController extends BaseController {

	@Autowired
	private IBbsPlateService bbsPlateService;

	@Autowired
	private IBbsPlateUnitService bbsPlateUnitService;

	@Autowired
	private IBbsPostService bbsPostService;

	@RequestMapping("select")
	public String select(Model m,String postIds) {
		m.addAttribute("postIds",postIds);
		return "/bbs/plate_select";
	}

	@RequestMapping("tree")
	public String tree(Model m){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("siteId", LoginPersonUtil.getSiteId());
		map.put("recordStatus", BbsPlateEO.RecordStatus.Normal.toString());
		List<BbsPlateEO> plates = bbsPlateService.getEntities(BbsPlateEO.class,map);
		m.addAttribute("plates", plates);
		return "/bbs/plate_tree";
	}

	@RequestMapping("getUnits")
	public String getUnits(){
		return "/bbs/plate_getUnits";
	}


	@RequestMapping("getTrees")
	@ResponseBody
	public Object getTrees(Long parentId,Long siteId){
		List<BbsPlateEO> plates = bbsPlateService.getPlates(parentId,siteId);
		List<BbsPlateVO> plateVos = null;
		if(plates != null && plates.size()>0){
			plateVos = new ArrayList<BbsPlateVO>();
			for(BbsPlateEO plate:plates){
				BbsPlateVO plateVo = getPlateVO(plate);
				plateVo.setHasPost(bbsPostService.hasPost(plate.getPlateId()));
				plateVos.add(plateVo);
			}
		}
		return getObject(plateVos);
	}


	@RequestMapping("getPlateByTree")
	@ResponseBody
	public Object getPlateByTree(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("siteId", LoginPersonUtil.getSiteId());
		map.put("recordStatus", BbsPlateEO.RecordStatus.Normal.toString());
		List<BbsPlateEO> plates = bbsPlateService.getEntities(BbsPlateEO.class,map);
		return getObject(plates);
	}


	/**
	 * h获取所有版块
	 * @return
	 */
	@RequestMapping("getPlates")
	@ResponseBody
	public Object getPlates(){
		Long siteId =  LoginPersonUtil.getSiteId();
		if(siteId == null){
			return null;
		}
		return getObject(bbsPlateService.getPlatesByPids(PlateUtil.topPlate,siteId));
	}

	public static BbsPlateVO getPlateVO(BbsPlateEO plate) {
		BbsPlateVO plateVO = new BbsPlateVO();
		BeanUtils.copyProperties(plate, plateVO);
		if(plate.getHasChild() == 1){
			plateVO.setIsParent(true);
		}
		return plateVO;
	}

	@RequestMapping("getPlate")
	@ResponseBody
	public Object getPlate(Long plateId,Long parentId,Long siteId) {
		BbsPlateEO bbsPlate = null;
		// 当organId为null时，表示前端发起请求返回空对象
		if (plateId != null) {
			bbsPlate= bbsPlateService.getEntity(BbsPlateEO.class, plateId);
		} else {
			bbsPlate = new BbsPlateEO();
			// 排序号默认取最大排序号+2
			Long sortNum = bbsPlateService.getMaxSortNum(parentId,siteId);
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			bbsPlate.setSortNum(sortNum);
		}
		return getObject(bbsPlate);
	}


	@RequestMapping("getPlateUnits")
	@ResponseBody
	public Object getPlateUnits(Long plateId) {
		List<BbsPlateUnitEO> units = bbsPlateUnitService.getUnits(plateId);
		return getObject(units);
	}

	@RequestMapping("save")
	@ResponseBody
	public Object save(BbsPlateEO bbsPlate) {
		if(bbsPlate.getPlateId() !=null){
			bbsPlateService.updateEntity(bbsPlate);
		}else{
			//所有父节点串
			bbsPlate.setParentIds(bbsPlateService.getParentIds(bbsPlate.getParentId()));
			bbsPlateService.saveEntity(bbsPlate);
		}
		//放进m缓存
		CacheHandler.saveOrUpdate(BbsPlateEO.class,bbsPlate);
		//更新单位关联表
		if (bbsPlate.getParentId() != null) {
			try{
				bbsPlateService.updateHasChildren(bbsPlate.getParentId());
			}catch(Exception e){

			}
		}
		return getObject(bbsPlate);
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(Long plateId) {
		BbsPlateEO bbsPlate= bbsPlateService.getEntity(BbsPlateEO.class, plateId);
		Long parentId = bbsPlate.getParentId();
		bbsPlateService.delete(plateId);
		//删除缓存
		CacheHandler.delete(BbsPlateEO.class,bbsPlate);
		if (parentId != null) {
			try{
				bbsPlateService.updateHasChildren(parentId);
			}catch(Exception e){
			}
		}
		return getObject();
	}

	@RequestMapping("move")
	@ResponseBody
	public Object move(BbsPlateVO bbsPlate) {
		BbsPlateEO bbsPlateEO = new BbsPlateEO();
		AppUtil.copyProperties(bbsPlateEO,bbsPlate);
		if(bbsPlate.getPlateId() !=null) {
			bbsPlateService.updateEntity(bbsPlateEO);
		}
		//更新单位关联表
		if (bbsPlate.getParentId() != null) {
			try{
				bbsPlateService.updateHasChildren(bbsPlate.getOldParentId());
			}catch(Exception e){

			}
		}
		return getObject(bbsPlate);
	}

	@RequestMapping("movePlate")
	public String movePlate(){
		return "/bbs/move_plate";
	}
}
