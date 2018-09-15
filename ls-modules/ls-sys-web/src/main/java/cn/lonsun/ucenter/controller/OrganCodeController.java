package cn.lonsun.ucenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.service.IOrganCodeService;

/**
 * 单位编码
 *
 * @author xujh
 * @version 1.0
 * 2015年2月13日
 *
 */
@Controller
@RequestMapping("/organCode")
public class OrganCodeController extends BaseController {
	@Autowired
	private IOrganCodeService organCodeService;
	
	@RequestMapping("organCodesPage")
	public String organCodesPage(){
		return "";
	}
	
	@RequestMapping("editPage")
	public String editPage(){
		return "";
	}
	
	/**
	 * 获取分页信息
	 *
	 * @return
	 */
	@RequestMapping("getPagination")
	@ResponseBody
	public Object getPagination(PageQueryVO query){
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}
		return getObject(organCodeService.getPagination(query));
	}

}
