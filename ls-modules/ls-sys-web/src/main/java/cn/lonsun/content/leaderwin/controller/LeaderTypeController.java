package cn.lonsun.content.leaderwin.controller;


import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderTypeService;
import cn.lonsun.content.leaderwin.vo.LeaderQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "leaderType", produces = { "application/json;charset=UTF-8" })
public class LeaderTypeController extends BaseController{
	@Autowired
	private ILeaderTypeService leaderTypeService;

	@Autowired
	private ILeaderInfoService leaderInfoService;


	@RequestMapping("list")
	public String list() {
		return "/content/leaderwin/leaderType_list";
	}

	@RequestMapping("edit")
	public String edit(Long typeId,Model m) {
		m.addAttribute("typeId", typeId);
		return "/content/leaderwin/leaderType_edit";
	}
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(LeaderQueryVO query){

		if(query.getColumnId() == null || query.getSiteId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}

		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		Pagination page = leaderTypeService.getPage(query);

		return getObject(page);
	}


	@RequestMapping("save")
	@ResponseBody
	public Object save(LeaderTypeEO leaderInfo){
		if(leaderInfo.getLeaderTypeId() != null){
			leaderTypeService.updateEntity(leaderInfo);
		}else{
			leaderTypeService.saveEntity(leaderInfo);
		}
		return getObject();
	}


	@RequestMapping("getLeaderTypes")
	@ResponseBody
	public Object getLeaderTypes(Long siteId,Long columnId){
		if(siteId == null || columnId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}

		return getObject(leaderTypeService.getList(siteId,columnId));
	}

	@RequestMapping("getLeaderType")
	@ResponseBody
	public Object getLeaderType(Long typeId,Long siteId,Long columnId){
		LeaderTypeEO leaderType = null;
		if(typeId == null){
			leaderType = new LeaderTypeEO();
			Long sortNum = leaderTypeService.getMaxSortNum(siteId,columnId);
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			leaderType.setSortNum(sortNum);
		}else{
			leaderType = leaderTypeService.getEntity(LeaderTypeEO.class,typeId);
		}
		return getObject(leaderType);
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids) {
		try{
			if(ids != null && ids.length >0){
				leaderTypeService.delete(LeaderTypeEO.class, ids);
			}
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}

	@RequestMapping("del")
	@ResponseBody
	public Object del(Long id) {
		if(id != null){
//			Map<String,Object> map = new HashMap<String,Object>();
//			map.put("leaderTypeId",id);
			List<LeaderInfoVO> objects = leaderInfoService.getLeaderInfoVOSByTypeId(id);
			if(objects != null && objects.size() > 0){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "此分类下存在领导，不可以删除!");
			}
			leaderTypeService.delete(LeaderTypeEO.class, id);
		}
		return getObject();
	}

}
