package cn.lonsun.govbbs.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.service.IBbsMemberRoleService;
import cn.lonsun.govbbs.internal.service.IBbsSettingService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping(value = "bbsSetting", produces = { "application/json;charset=UTF-8" })
public class BbsSettingController extends BaseController {

	@Autowired
	private IBbsSettingService bbsSettingService;

	@Autowired
	private IBbsMemberRoleService bbsMemberRoleService;

	@RequestMapping("edit")
	public String list(Model m) {
		List<BbsMemberRoleEO> list = bbsMemberRoleService.getBbsMemberRoleMap(LoginPersonUtil.getSiteId());
		m.addAttribute("roleList", list);
		return "/bbs/setting_edit";
	}


	@RequestMapping("getBbsSetting")
	@ResponseBody
	public Object getBbsSetting(Long siteId){
		if(siteId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "所属站点不能为空");
		}
//		BbsSettingEO bbsSetting = bbsSettingService.getBbsSeting(siteId);
		BbsSettingEO bbsSetting = CacheHandler.getEntity(BbsSettingEO.class,siteId);
		if(bbsSetting == null){
			bbsSetting = new BbsSettingEO();
			bbsSetting.setSiteId(siteId);
			SimpleDateFormat simple = new SimpleDateFormat("yyyy");
			try{
				Integer year = Integer.parseInt(simple.format(new Date()));
				bbsSetting.setYear(year);
			}catch(Exception e){}
		}
		return getObject(bbsSetting);
	}

	@RequestMapping("save")
	@ResponseBody
	public Object save(BbsSettingEO bbsSetting){
		if(bbsSetting.getSiteId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "站点id不能为空");
		}
		if(bbsSetting.getSettingId() != null){
			bbsSettingService.updateEntity(bbsSetting);
		}else{
			bbsSettingService.saveEntity(bbsSetting);
		}
		CacheHandler.saveOrUpdate(BbsSettingEO.class,bbsSetting);
//		BbsSettingUtil.put(bbsSetting.getSiteId(), bbsSetting);
		return getObject();
	}


	@RequestMapping("getBbsSet")
	@ResponseBody
	public Object getBbsSet(){
		Long siteId = LoginPersonUtil.getSiteId();
		if(siteId == null){
			return null;
		}
		return getObject(CacheHandler.getEntity(BbsSettingEO.class,siteId));
	}
}
