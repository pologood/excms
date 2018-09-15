package cn.lonsun.content.onlinereview.controller;

import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyIpService;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.SurveyWebVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 网站提交数据处理
 * @author zhangchao
 *
 */
@Controller
@RequestMapping(value = "reviewWeb", produces = { "application/json;charset=UTF-8" })
public class ReviewWebController  extends BaseController{

	@Autowired
	private ISurveyThemeService surveyThemeService;

	@Autowired
	private ISurveyQuestionService surveyQuestionService;

	@Autowired
	private ISurveyOptionsService surveyOptionsService;

	@Autowired
	private ISurveyIpService surveyIpService;

	@RequestMapping("saveResult")
	@ResponseBody
	public Object saveResult(String resultList,String checkCode,HttpServletRequest request){
		String webCode = (String) request.getSession().getAttribute("webCode");
		if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
			return ajaxErr("验证码不能为空！");
		}
		if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
			return ajaxErr("验证码不正确，请重新输入");
		}
		if(!StringUtils.isEmpty(resultList)){
			SurveyWebVO[] resultLists = Jacksons.json().fromJsonToObject(resultList,SurveyWebVO[].class);
			if(resultLists!=null && resultLists.length>0){
				String ip = RequestUtil.getIpAddr(request);
				//获取主题id
				Long themeId = resultLists[0].getThemeId();
				SurveyThemeEO theme = surveyThemeService.getEntity(SurveyThemeEO.class, themeId);
				if(theme == null){return ajaxErr("评议主题不存在！");}
				//获取改ip已投票数
				Long ipCount = null;
				if(theme.getIpLimit() == 1){
					ipCount = surveyIpService.getSurveyIpCount(themeId, ip, null);
					if(ipCount >= theme.getIpDayCount()){
						return ajaxErr("此评议主题你已投票"+theme.getIpDayCount()+"次！");
					}
				}else{
					ipCount = surveyIpService.getSurveyIpCount(themeId, ip, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					if(ipCount >= theme.getIpDayCount()){
						return ajaxErr("此评议主题每天只能投票"+theme.getIpDayCount()+"次！");
					}
				}
				//验证完成后，保存投票结果
				surveyOptionsService.saveReviewWeb(resultLists,themeId,ip);
			}
		}
		return ajaxOk("保存成功！");
	}
}
