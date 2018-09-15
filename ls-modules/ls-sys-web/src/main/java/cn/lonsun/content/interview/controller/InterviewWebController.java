package cn.lonsun.content.interview.controller;



import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.internal.service.IInterviewQuestionService;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
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
@RequestMapping(value = "interviewWeb", produces = { "application/json;charset=UTF-8" })
public class InterviewWebController  extends BaseController{

	@Autowired
	private IInterviewQuestionService interviewQuestionService;

	@Autowired
	private IInterviewInfoService interviewInfoService;


	@SuppressWarnings("unchecked")
	@RequestMapping("getQuestionPage")
	@ResponseBody
	public Object getQuestionPage(InterviewQueryVO query,String dateFormat){
		if(query.getInterviewId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "interviewId不能为空");
		}
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(10);
		}
		query.setIssued(InterviewInfoEO.Status.Yes.getStatus());
		Pagination page = interviewQuestionService.getPage(query);
		List<InterviewQuestionEO> list= (List<InterviewQuestionEO>)page.getData();
		dateFormat = StringUtils.isEmpty(dateFormat)?"yyyy/MM/dd HH:mm:ss":dateFormat;
		SimpleDateFormat simple = new SimpleDateFormat(dateFormat);
		if(list !=null && list.size()>0){
			for(InterviewQuestionEO question:list){
				question.setCreateWebTime(simple.format(question.getCreateDate()));
				question.setReplyWebTime(question.getReplyTime()!=null?simple.format(question.getReplyTime()):"");
			}
		}
		return getObject(list);
	}

	@RequestMapping("saveResult")
	@ResponseBody
	public Object saveResult(Long interviewId,String name,String content,String checkCode,HttpServletRequest request){
		if(interviewId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(content)){
			return ajaxErr("提交参数不能为空");
		}
		String webCode = (String) request.getSession().getAttribute("webCode");
		if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
			return ajaxErr("验证码不能为空！");
		}
		if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
			return ajaxErr("验证码不正确，请重新输入");
		}
		InterviewQuestionEO question = new InterviewQuestionEO();
		question.setInterviewId(interviewId);
		question.setName(name);
		question.setContent(content);
		question.setIp(RequestUtil.getIpAddr(request));
		interviewQuestionService.saveEntity(question);
		return ajaxOk("保存成功！");
	}
}
