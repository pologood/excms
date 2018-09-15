package cn.lonsun.staticcenter.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.commentMgr.internal.entity.CommentEO;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.core.base.controller.BaseController;

@Controller
@RequestMapping(value = "/comment", produces = { "application/json;charset=UTF-8" })
public class CommentController extends BaseController {

	@Autowired
	private ICommentService commentService;
	
	@RequestMapping("saveComment")
	@ResponseBody
	private Object saveComment(HttpServletRequest request,CommentEO commentEo,String checkCode){
		if(StringUtils.isEmpty(checkCode)){
			return ajaxErr("验证码不能为空！");
		}
		Object obj = request.getSession().getAttribute("webCode");
		if(obj == null){
			return ajaxErr("验证码已经失效！");
		}
		String webCode = (String)obj;
		if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
			return ajaxErr("验证码不正确，请重新输入");
		}
		commentService.saveEntity(commentEo);
		return ajaxOk("提交成功");
	}
}
