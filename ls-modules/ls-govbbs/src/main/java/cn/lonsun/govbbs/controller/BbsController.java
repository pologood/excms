package cn.lonsun.govbbs.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "bbs", produces = { "application/json;charset=UTF-8" })
public class BbsController extends BaseController {

	@RequestMapping("index")
	public String list(Model m) {
		Integer isUnit = 0;
		if(!LoginPersonUtil.isRoot()){
			if(!LoginPersonUtil.isSuperAdmin()){
				isUnit = 1;
			}
		}
		m.addAttribute("isUnit",isUnit);
		return "/bbs/post_index";
	}

}
