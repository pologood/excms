package cn.lonsun.content.ideacollect.controller;

import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectIdeaService;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.core.util.TipsMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping(value = "collectWeb", produces = { "application/json;charset=UTF-8" })
public class CollectWebController  extends BaseController{

	@Autowired
	private ICollectIdeaService collectIdeaService;
	
	@Autowired
	private ICollectInfoService collectInfoService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping("getIdeaPage")
	@ResponseBody
	public Object getIdeaPage(IdeaQueryVO query,String dateFormat){
		if(query.getCollectInfoId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "CollectInfoId不能为空");
		}
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(10);
		}
		query.setIssued(CollectIdeaEO.Status.Yes.getStatus());
		Pagination page = collectIdeaService.getPage(query);
		List<CollectIdeaEO> list= (List<CollectIdeaEO>)page.getData();
		dateFormat = StringUtils.isEmpty(dateFormat)?"yyyy/MM/dd HH:mm:ss":dateFormat;
		SimpleDateFormat simple = new SimpleDateFormat(dateFormat);
		if(list !=null && list.size()>0){
			for(CollectIdeaEO idea:list){
				idea.setCreateWebTime(idea.getCreateDate()!=null?simple.format(idea.getCreateDate()):"");
			}
		}
		return getObject(list);
	}

	@RequestMapping("saveResult")
	@ResponseBody
	public Object saveResult(Long collectInfoId,String name,String phone,String content,String checkCode,HttpServletRequest request){
		if(collectInfoId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)|| StringUtils.isEmpty(content)){
			return ajaxErr("提交参数不能为空");
		}
		String webCode = (String) request.getSession().getAttribute("webCode");
		if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
			return ajaxErr("验证码不能为空！");
		}
		if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
			return ajaxErr("验证码不正确，请重新输入");
		}
		CollectIdeaEO idea = new CollectIdeaEO();
		idea.setCollectInfoId(collectInfoId);
		idea.setName(name);
		idea.setContent(content);
		idea.setIp(RequestUtil.getIpAddr(request));
		collectIdeaService.saveEntity(idea);
		return ajaxOk("保存成功！");
	}
	
}
