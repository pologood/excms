package cn.lonsun.content.onlinereview.controller;



import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.onlinereview.internal.service.IReviewInfoService;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.service.ISurveyOptionsService;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.QuestionOptionsVO;
import cn.lonsun.content.survey.vo.SurveyQueryVO;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.VideoToMp4ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "reviewInfo", produces = { "application/json;charset=UTF-8" })
public class ReviewInfoController extends BaseController{

	@Autowired
	private IReviewInfoService reviewInfoService;

	@Autowired
	private ISurveyThemeService surveyThemeService;

	@Autowired
	private ISurveyQuestionService surveyQuestionService;

	@Autowired
	private ISurveyOptionsService surveyOptionsService;

	@Autowired
	private IBaseContentService baseContentService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Value("${ffmpeg.path}")
	private String path;

	@RequestMapping("index")
	public String index() {
		return "/content/onlinereview/reviewInfo_list";
	}

	@RequestMapping("edit")
	public String edit(Long themeId,Model m) {
		m.addAttribute("themeId", themeId);
		return "/content/onlinereview/reviewInfo_edit";
	}

	@RequestMapping("replyList")
	public String replyList(Long optionId,Model m) {
		m.addAttribute("optionId", optionId);
		return "/content/onlinereview/question_replyList";
	}

	@RequestMapping("replyEdit")
	public String replyEdit(Long optionId,Model m) {
		m.addAttribute("optionId", optionId);
		return "/content/survey/question_replyEdit";
	}

	@RequestMapping("editQuestion")
	public String editQuestion(Long questionId,Long themeId,Model m) {
		m.addAttribute("themeId", themeId);
		m.addAttribute("questionId", questionId);
		return "/content/onlinereview/question_edit";
	}

	@RequestMapping("editOptions")
	public String editOptions(Long optionId,Long questionId,Long themeId,Model m) {
		m.addAttribute("optionId", optionId);
		m.addAttribute("themeId", themeId);
		m.addAttribute("questionId", questionId);
		return "/content/onlinereview/option_edit";
	}

	@RequestMapping("questionList")
	public String list(Long themeId,Integer options,Model m) {
		m.addAttribute("themeId", themeId);
		return "/content/onlinereview/reviewInfo_question";
	}

	@RequestMapping("saveReview")
	@ResponseBody
	public Object saveReview(SurveyThemeVO surveyThemeVO){
		if (surveyThemeVO.getVideoStatus().equals(0) && surveyThemeVO.getIsPublish() == 1) {//
			return ajaxErr("文章中有视频未转换，不能进行发布操作！");
		}
		Long contentId = surveyThemeService.saveOrupdate(surveyThemeVO);
		if (surveyThemeVO.getVideoStatus().equals(0)) {
			// 转换视频
			VideoToMp4ConvertUtil.transfer(path, surveyThemeVO.getColumnId(), contentId);
		}
		return getObject();
	}


	@RequestMapping("getSurveyTheme")
	@ResponseBody
	public Object getSurveyTheme(Long id,Long siteId,Long columnId,String typeCode){
		SurveyThemeVO surveyTheme = null;
		if(id == null){
			if(siteId == null || columnId == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
			}
			surveyTheme = new SurveyThemeVO();
			Long sortNum = baseContentService.getMaxSortNum(siteId,columnId,BaseContentEO.TypeCode.reviewInfo.toString());
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			surveyTheme.setSortNum(sortNum);
		}else{
			surveyTheme = surveyThemeService.getSurveyThemeVO(id);
			Long count = surveyOptionsService.getVotesCount(id);
			if(count > 0){
				surveyTheme.setHasClild(1);
			}
		}
		return getObject(surveyTheme);
	}

	@RequestMapping("getQuestionList")
	@ResponseBody
	public Object getQuestionList(SurveyQueryVO query){
		if(query.getThemeId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "themeId不能为空");
		}
		List<QuestionOptionsVO> qoList = new ArrayList<QuestionOptionsVO>();
		//获取所有问题
		List<SurveyQuestionEO> questions = surveyQuestionService.getListByThemeId(query.getThemeId());
		if(questions != null && questions.size() > 0){
			QuestionOptionsVO question = null;
			QuestionOptionsVO option = null;
			//获取所有选项
			List<SurveyOptionsEO> options = surveyOptionsService.getListByThemeId(query.getThemeId());
			//处理选项
			Map<Long,List<SurveyOptionsEO>> map = getMap(options);
			for(SurveyQuestionEO sq:questions){
				question = new QuestionOptionsVO();
				question.setId(sq.getQuestionId());
				question.setTitle(sq.getTitle());
				question.setPid(0L);
				question.setType(QuestionOptionsVO.Type.Question.toString());
				List<SurveyOptionsEO> soList = (map == null?null:map.get(sq.getQuestionId()));
				if(soList !=null && soList.size()>0){
					Long count = 0L;
					for(int i=0;i<soList.size();i++){
						option= new QuestionOptionsVO();
						option.setId(soList.get(i).getOptionId());
						option.setPid(sq.getQuestionId());
						option.setTitle(soList.get(i).getTitle());
						option.setVotesCount(soList.get(i).getVotesCount());
						option.setType(QuestionOptionsVO.Type.Option.toString());
						count += soList.get(i).getVotesCount();
						qoList.add(option);
					}
					question.setVotesCount(count);
				}
				qoList.add(question);
			}
		}
		return getObject(qoList);
	}

	private Map<Long, List<SurveyOptionsEO>> getMap(List<SurveyOptionsEO> options) {
		Map<Long,List<SurveyOptionsEO>> map = null;
		if(options !=null && options.size()>0){
			map = new HashMap<Long,List<SurveyOptionsEO>>();
			List<SurveyOptionsEO> soList = null;
			for(SurveyOptionsEO so:options){
				Long questionId = so.getQuestionId();
				soList = map.get(questionId);
				if(!(soList != null && soList.size() > 0)){
					soList = new ArrayList<SurveyOptionsEO>();
				}
				soList.add(so);
				map.put(questionId, soList);
			}
		}
		return map;
	}

//	@RequestMapping("countData")
//	@ResponseBody
//	public Object countData(Long columnId){
//		return getObject(surveyThemeService.countData(columnId,SurveyThemeEO.Type.Review.toString()));
//	}

	//	@SuppressWarnings("unchecked")
	//	@RequestMapping("getPage")
	//	@ResponseBody
	//	public Object getPage(ReviewQueryVO query){
	//
	//		if(query.getColumnId() == null || query.getSiteId() == null){
	//			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
	//		}
	//		// 页码与查询最多查询数据量纠正
	//		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
	//			query.setPageIndex(0L);
	//		}
	//		Integer size = query.getPageSize();
	//		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
	//			query.setPageSize(15);
	//		}
	//		Pagination page = reviewInfoService.getPage(query);
	//
	//		if(page.getData() !=null && page.getData().size()>0){
	//			for(ReviewInfoEO reviewInfo:(List<ReviewInfoEO>) page.getData()){
	//				if(reviewInfo.getStartTime() != null && reviewInfo.getEndTime() !=null){
	//					reviewInfo.setIsTimeOut(TimeOutUtil.getTimeOut(reviewInfo.getStartTime(), reviewInfo.getEndTime()));
	//				}
	//			}
	//		}
	//		return getObject(page);
	//	}
	//
	//	@RequestMapping("save")
	//	@ResponseBody
	//	public Object save(ReviewInfoEO reviewInfo){
	//		if(!StringUtils.isEmpty(reviewInfo.getObjectIds()) && reviewInfo.getObjectIds().length()>3999){
	//			throw new BaseRunTimeException(TipsMode.Message.toString(), "评议对象过多,请重新选择");
	//		}
	//		if(!StringUtils.isEmpty(reviewInfo.getTypeIds()) && reviewInfo.getTypeIds().length()>3999){
	//			throw new BaseRunTimeException(TipsMode.Message.toString(), "评议类型过多,请重新选择");
	//		}
	//		if(reviewInfo.getIssued() == 1 && reviewInfo.getIssuedTime() == null){
	//			reviewInfo.setIssuedTime(new Date());
	//		}
	//		if(reviewInfo.getInfoId() != null){
	//			reviewInfoService.updateEntity(reviewInfo);
	//		}else{
	//			reviewInfoService.saveEntity(reviewInfo);
	//		}
	//		return getObject();
	//	}
	//
	//	@RequestMapping("updateIssued")
	//	@ResponseBody
	//	public Object updateIssued(@RequestParam("ids") Long[] ids,
	//			Integer status) {
	//		if(ids !=null && ids.length >0){
	//			for(Long id:ids){
	//				ReviewInfoEO reviewInfo = reviewInfoService.getEntity(ReviewInfoEO.class, id);
	//				if(reviewInfo!= null){
	//					if(reviewInfo.getIssued() != status){
	//						if(status == 1){
	//							reviewInfo.setIssuedTime(new Date());
	//						}else{
	//							reviewInfo.setIssuedTime(null);
	//						}
	//						reviewInfo.setIssued(status);
	//						reviewInfoService.updateEntity(reviewInfo);
	//					}
	//				}
	//			}
	//		}
	//		return getObject();
	//	}
	//
	//	@RequestMapping("getReviewInfo")
	//	@ResponseBody
	//	public Object getReviewInfo(Long infoId,Long siteId,Long columnId){
	//		ReviewInfoEO reviewInfo = null;
	//		if(infoId == null){
	//			reviewInfo = new ReviewInfoEO();
	//			Long sortNum = reviewInfoService.getMaxSortNum(siteId,columnId);
	//			if (sortNum == null) {
	//				sortNum = 2L;
	//			} else {
	//				sortNum = sortNum + 2;
	//			}
	//			reviewInfo.setSortNum(sortNum);
	//		}else{
	//			reviewInfo = reviewInfoService.getEntity(ReviewInfoEO.class,infoId);
	//		}
	//		return getObject(reviewInfo);
	//	}
	//
	//	@RequestMapping("updateSort")
	//	@ResponseBody
	//	public Object updateSort(Long id,Long sortNum,String type){
	//		if(StringUtils.isEmpty(type)){
	//			throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择排序类型");
	//		}
	//		if(id == null){
	//			throw new BaseRunTimeException(TipsMode.Message.toString(), "id 不能为空");
	//		}
	//		reviewInfoService.updateSort(id,sortNum,type);
	//		return getObject();
	//	}
	//
	//	@RequestMapping("delete")
	//	@ResponseBody
	//	public Object delete(@RequestParam("ids") Long[] ids) {
	//		try{
	//			if(ids != null && ids.length >0){
	//				reviewInfoService.delete(ids);
	//			}
	//		}catch(Exception e){
	//			throw new BaseRunTimeException();
	//		}
	//		return getObject();
	//	}
}
