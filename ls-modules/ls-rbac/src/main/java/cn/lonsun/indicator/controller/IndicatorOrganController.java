package cn.lonsun.indicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorOrganEO;
import cn.lonsun.indicator.internal.service.IIndicatorOrganService;

/**
 * 部分应用只获取某几个单位的数据关系控制器
 *
 * @author xujh
 * @version 1.0
 * 2015年3月12日
 *
 */
@Controller
@RequestMapping(value = "indicatorOrgan")
public class IndicatorOrganController  extends BaseController{
	@Autowired
	private IIndicatorOrganService indicatorOrganService;
	
	/**
	 * 列表页面
	 *
	 * @return
	 */
	@RequestMapping("listPage")
	public ModelAndView listPage(){
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/app/mgr/developer/indicator_organ_list");
		return mav;
	}
	
	/**
	 * 编辑页面
	 *
	 * @param indicatorOrganId
	 * @return
	 */
	@RequestMapping("editPage")
	public ModelAndView editPage(Long indicatorOrganId){
		ModelAndView mav = new ModelAndView();
		mav.addObject("indicatorOrganId", indicatorOrganId);
		//如果是新建页面，那么获取默认的sortNum
		if(indicatorOrganId==null){
			mav.addObject("sortNum", indicatorOrganService.getMaxSortNum()+1);
		}
		mav.setViewName("/app/mgr/developer/indicator_organ_edit");
		return mav;
	}
	
	/**
	 * 获取分页内容
	 *
	 * @param query
	 * @return
	 */
	@RequestMapping("getPagination")
	@ResponseBody
	public Object getPagination(PageQueryVO query){
		if(query.getPageIndex()==null||query.getPageIndex()<0){
			query.setPageIndex(Long.valueOf(0));
		}
		if(query.getPageSize()==null||query.getPageSize()<0){
			query.setPageSize(Integer.valueOf(15));
		}
		return getObject(indicatorOrganService.getPagination(query));
	}
	
	/**
	 * 保存
	 *
	 * @param eo
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public Object save(IndicatorOrganEO eo){
		IndicatorOrganEO io = indicatorOrganService.getIndicatorOrgans(eo.getShortCutId(), eo.getMenuId(), eo.getUnitId());
		if(io!=null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "应用菜单与单位数据关系已存在");
		}
		indicatorOrganService.saveEntity(eo);
		return getObject();
	}
	
	/**
	 * 更新
	 *
	 * @param eo
	 * @return
	 */
	@RequestMapping("update")
	@ResponseBody
	public Object update(IndicatorOrganEO eo){
		indicatorOrganService.updateEntity(eo);
		return getObject();
	}
	
	/**
	 * 删除
	 *
	 * @param indicatorOrganIds
	 * @return
	 */
	@RequestMapping("deleteIOs")
	@ResponseBody
	public Object deleteIOs(@RequestParam(value="indicatorOrganIds[]")Long[] indicatorOrganIds){
		indicatorOrganService.delete(IndicatorOrganEO.class, indicatorOrganIds);
		return getObject();
	}
}
