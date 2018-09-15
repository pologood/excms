package cn.lonsun.rbac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.service.IRtxFailureService;
import cn.lonsun.rbac.internal.vo.RtxQueryVO;

/**
 * RTX同步失败记录控制器
 *
 * @author xujh
 * @version 1.0
 * 2015年4月22日
 *
 */
@Controller
@RequestMapping(value="rtxFailure",produces = {"application/json;charset=UTF-8"})
public class RtxFailureController  extends BaseController{
	@Autowired
	IRtxFailureService rtxFailureService;
	
	
	@RequestMapping("list")
	public Object list(){
		return "/app/mgr/rtxrecord/rtx_record";		
	}
	
	/**
	 * 分页查询记录
	 *
	 * @param query
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(RtxQueryVO rfvo){
		return getObject(rtxFailureService.getPage(rfvo));
	}

	/**
	 * 数据同步
	 */
	@RequestMapping("synchronous")
	@ResponseBody
	public Object synchronous(Long[] ids){
		rtxFailureService.synchronous();
		return getObject();
	}
}
