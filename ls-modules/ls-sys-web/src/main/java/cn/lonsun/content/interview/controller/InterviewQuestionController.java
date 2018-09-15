package cn.lonsun.content.interview.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

@Controller
@RequestMapping(value = "interviewQuestion", produces = { "application/json;charset=UTF-8" })
public class InterviewQuestionController extends BaseController{

	@Autowired
	private IInterviewQuestionService interviewQuestionService;
	
	@Autowired
	private IInterviewInfoService interviewInfoService;

	@RequestMapping("list")
	public String list(Long interviewId,Model m,Long topPageIndex) {
		m.addAttribute("interviewId", interviewId);
		m.addAttribute("topPageIndex", topPageIndex);
		return "/content/interview/question_list";
	}
	
	@RequestMapping("edit")
	public String edit(Long questionId,Model m) {
		m.addAttribute("questionId", questionId);
		return "/content/interview/question_edit";
	}
	
	@RequestMapping("editReply")
	public String editReply(Long questionId,Model m) {
		m.addAttribute("questionId", questionId);
		return "/content/interview/question_editReply";
	}

	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(InterviewQueryVO query){
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
		Pagination page = interviewQuestionService.getPage(query);
		return getObject(page);
	}

	@RequestMapping("saveQuestion")
	@ResponseBody
	public Object saveQuestion(InterviewQuestionEO question,HttpServletRequest request){
		if(question.getQuestionId() != null){
			interviewQuestionService.updateEntity(question);
		}else{
			question.setIp(RequestUtil.getIpAddr(request));
			interviewQuestionService.saveEntity(question);
		}
		return getObject();
	}

	@RequestMapping("saveReply")
	@ResponseBody
	public Object saveReply(InterviewQuestionEO question){
		if(question.getQuestionId() != null){
			InterviewQuestionEO questionInfo = interviewQuestionService.getEntity(InterviewQuestionEO.class, question.getQuestionId());
			questionInfo.setIsReply(question.getIsReply());
			questionInfo.setReplyName(question.getReplyName());
			questionInfo.setReplyContent(question.getReplyContent());
			questionInfo.setReplyTime(new Date());
			questionInfo.setReplyPic(question.getReplyPic());
			interviewQuestionService.updateEntity(questionInfo);
		}
		return getObject();
	}

	@RequestMapping("updateIssued")
	@ResponseBody
	public Object updateIssued(@RequestParam("ids") Long[] ids,
			Integer status) {
		if(ids !=null && ids.length >0){
			for(Long id:ids){
				InterviewQuestionEO question = interviewQuestionService.getEntity(InterviewQuestionEO.class, id);
				if(question!= null){
					if(question.getIssued() != status){
						if(status == 1){
							question.setIssuedTime(new Date());
						}else{
							question.setIssuedTime(null);
						}
						question.setIssued(status);
						interviewQuestionService.updateEntity(question);
					}
				}
			}
		}
		return getObject();
	}

	@RequestMapping("getQuestion")
	@ResponseBody
	public Object getQuestion(Long questionId){
		InterviewQuestionEO question = null;
		if(questionId == null){
			question = new InterviewQuestionEO();
		}else{
			question = interviewQuestionService.getEntity(InterviewQuestionEO.class,questionId);
			if(question.getInterviewId() != null){
				InterviewInfoEO info = interviewInfoService.getEntity(InterviewInfoEO.class, question.getInterviewId());
				if(info != null){
					question.setNames(info.getUserNames());
				}
			}
		}
		return getObject(question);
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids) {
		try{
			if(ids != null && ids.length >0){
				interviewQuestionService.delete(ids);
			}
		}catch(Exception e){
			throw new BaseRunTimeException();
		}
		return getObject();
	}

}
