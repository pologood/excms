package cn.lonsun.content.survey.controller;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyQuestionService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;

import static cn.lonsun.cache.client.CacheHandler.getEntity;


@Controller
@RequestMapping(value = "/survey/theme", produces = { "application/json;charset=UTF-8" })
public class SurveyThemeController extends BaseController{

	@Autowired
	private ISurveyThemeService surveyThemeService;

	@Autowired
	private ISurveyQuestionService surveyQuestionService;

	@Autowired
	private IBaseContentService baseContentService;


	@RequestMapping("list")
	public String list() {
		return "/content/survey/theme_list";
	}
	@RequestMapping("edit")
	public String edit(Long themeId,Model m) {
		m.addAttribute("themeId", themeId);
		return "/content/survey/theme_edit";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(QueryVO query){
		if(query.getColumnId() == null || query.getSiteId() == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
		}
		if (StringUtils.isEmpty(query.getTypeCode())) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "typeCode不能为空");
		}
		// 页码与查询最多查询数据量纠正
		if (query.getPageIndex()==null||query.getPageIndex() < 0) {
			query.setPageIndex(0L);
		}
		Integer size = query.getPageSize();
		if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
			query.setPageSize(15);
		}

		Pagination page = surveyThemeService.getPage(query);

		if(page.getData() !=null && page.getData().size()>0){
			for(SurveyThemeVO surveyTheme:(List<SurveyThemeVO>) page.getData()){
				if(surveyTheme.getStartTime() != null && surveyTheme.getEndTime() !=null){
					surveyTheme.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(), surveyTheme.getEndTime()));
				}
			}
		}
		//		Pagination page = baseContentService.getPageBySortNum(query);
		//		List<SurveyThemeVO> vos = null;
		//		if(page.getData() !=null && page.getData().size()>0){
		//			vos = new ArrayList<SurveyThemeVO>();
		//			for(BaseContentVO content:(List<BaseContentVO>) page.getData()){
		//				vos.add(surveyThemeService.getSurveyThemeVO(content));
		//			}
		//		}
		//		page.setData(vos);
		return getObject(page);
	}

	@RequestMapping("saveSurvey")
	@ResponseBody
	public Object saveSurvey(SurveyThemeVO surveyThemeVO){
		BaseContentEO contentEO=surveyThemeService.save(surveyThemeVO);
		if(surveyThemeVO.getIsPublish()!=null&&surveyThemeVO.getIsPublish()==1){
			//发布
			MessageSender.sendMessage(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()));
		}
		return getObject();
	}


	@RequestMapping("getSurveyTheme")
	@ResponseBody
	public Object getSurveyTheme(Long id,Long siteId,Long columnId){
		SurveyThemeVO surveyTheme = null;
		if(id == null){
			if(siteId == null || columnId == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
			}
			surveyTheme = new SurveyThemeVO();
			Long sortNum = baseContentService.getMaxSortNum(siteId,columnId,BaseContentEO.TypeCode.survey.toString());
			if (sortNum == null) {
				sortNum = 2L;
			} else {
				sortNum = sortNum + 2;
			}
			surveyTheme.setSortNum(sortNum);
		}else{
			surveyTheme = surveyThemeService.getSurveyThemeVO(id);
			List<SurveyQuestionEO> list = surveyQuestionService.getListByThemeId(id);
			if(list != null && list.size() > 0){
				surveyTheme.setHasClild(1);
			}
		}
		return getObject(surveyTheme);
	}

	@RequestMapping("delete")
	@ResponseBody
	public Object delete(@RequestParam("ids") Long[] ids,@RequestParam("contentIds") Long[] contentIds) {
		if (ids == null || ids.length < 1) {
			throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项！");
		}
		List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
		if (list != null && list.size() > 0) {
			Integer isPublish = 0;
			for (BaseContentEO contentEO : list) {
				if (contentEO != null && contentEO.getIsPublish() != null && contentEO.getIsPublish().intValue() == 1) {
					isPublish = 1;
					break;
				}
				String newsType = "";
				if(BaseContentEO.TypeCode.survey.toString().equals(contentEO.getTypeCode())){
					newsType = "在线调查";
				}else if(BaseContentEO.TypeCode.reviewInfo.toString().equals(contentEO.getTypeCode())){
					newsType = "网上评议";
				}

				//添加操作日志
				if(list.size()>1){
					SysLog.log(newsType+"：批量删除内容（" + contentEO.getTitle()+"）", "SurveyThemeEO",
							CmsLogEO.Operation.Update.toString());
				}else{
					SysLog.log(newsType+"：删除内容（" + contentEO.getTitle()+"）", "SurveyThemeEO",
							CmsLogEO.Operation.Update.toString());
				}
			}
			BaseContentEO baseContentEO = getEntity(BaseContentEO.class, contentIds[0]);
			surveyThemeService.delete(ids,contentIds);
			if(isPublish!=null&&isPublish.intValue()==1){
				MessageSenderUtil.publishContent(
						new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(),ids).setType(MessageEnum.UNPUBLISH.value()), 2);
			}
		}
		return getObject();
	}

	/**
	 * KinderEdit上传附件的方法
	 * 
	 * @Time 2014年9月17日 下午2:45:53
	 * @param request
	 * @param response
	 * @return
	 * @throws FileUploadException
	 */
	@RequestMapping(params = "action=uploadFiles")
	public void uploadFiles(HttpServletRequest request,
			HttpServletResponse response){
		EditorUploadUtil.uploadByKindEditor(SystemCodes.contentMgr, request, response);
	}

	//	@RequestMapping("countData")
	//	@ResponseBody
	//	public Object countData(Long columnId){
	//		return getObject(surveyThemeService.countData(columnId,SurveyThemeEO.Type.Survey.toString()));
	//	}
}
