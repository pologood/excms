package cn.lonsun.content.survey.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyQuestionEO;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.*;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.util.TimeOutUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("surveyThemeService")
public class SurveyThemeServiceImpl extends BaseService<SurveyThemeEO> implements ISurveyThemeService{

	@Autowired
	private ISurveyThemeDao surveyThemeDao;

	@Autowired
	private ISurveyQuestionService surveyQuestionService;

	@Autowired
	private ISurveyIpService surveyIpService;

	@Autowired
	private ISurveyOptionsService surveyOptionsService;

	@Autowired
	private ISurveyReplyService surveyReplyService;

	@Autowired
	private IBaseContentService baseContentService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public Pagination getPage(QueryVO query) {
		return surveyThemeDao.getPage(query);
	}

	@Override
	public void delete(Long[] ids,Long[] contentIds) {
		List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
		if (list != null && list.size() > 0) {
			for (BaseContentEO content : list) {
				CacheHandler.delete(BaseContentEO.class, content);
			}
		}
		baseContentService.delete(BaseContentEO.class, contentIds);
	}

	@Override
	public void deleteByContentIds(Long contentId) {
		if(contentId == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "contentId不能为空");
		}
		SurveyThemeEO theme = getSurveyThemeByContentId(contentId);
		if(theme !=null){
			this.delete(SurveyThemeEO.class,new Long[]{theme.getThemeId()});
			surveyQuestionService.deleteByThemeId(new Long[]{theme.getThemeId()});
			surveyOptionsService.deleteByThemeId(new Long[]{theme.getThemeId()});
			surveyIpService.deleteByThemeId(new Long[]{theme.getThemeId()});
			surveyReplyService.deleteByQuestionIds(new Long[]{theme.getThemeId()});
		}
	}

	@Override
	public Long saveOrupdate(SurveyThemeVO surveyThemeVO) {
		System.out.println("======================contentEO======================" + JSON.toJSONString(surveyThemeVO));
		BaseContentEO content = null;
		SurveyThemeEO st = null;
		Long contentId = null;
		if(surveyThemeVO.getIsPublish() == 1 && surveyThemeVO.getIssuedTime() == null){
			surveyThemeVO.setIssuedTime(new Date());
		}
		if(surveyThemeVO.getContentId() != null){
			contentId = surveyThemeVO.getContentId();
			content = baseContentService.getEntity(BaseContentEO.class, surveyThemeVO.getContentId());
			content.setTitle(surveyThemeVO.getTitle());
			content.setNum(surveyThemeVO.getSortNum());
			content.setIsPublish(surveyThemeVO.getIsPublish());
			content.setPublishDate(surveyThemeVO.getIssuedTime());
			baseContentService.updateEntity(content);
			st = getEntity(SurveyThemeEO.class, surveyThemeVO.getThemeId());
			BeanUtils.copyProperties(surveyThemeVO, st);
			updateEntity(st);
			SysLog.log("网上评议：修改内容（"+content.getTitle()+"）","SurveyThemeEO", CmsLogEO.Operation.Update.toString());
		}else{
			content = new BaseContentEO();
			content.setTitle(surveyThemeVO.getTitle());
			content.setColumnId(surveyThemeVO.getColumnId());
			content.setSiteId(surveyThemeVO.getSiteId());
			content.setTypeCode(BaseContentEO.TypeCode.reviewInfo.toString());
			content.setNum(surveyThemeVO.getSortNum());
			content.setIsPublish(surveyThemeVO.getIsPublish());
			content.setPublishDate(surveyThemeVO.getIssuedTime());
			baseContentService.saveEntity(content);
			st = new SurveyThemeEO();
			BeanUtils.copyProperties(surveyThemeVO, st);
			st.setContentId(content.getId());
			saveEntity(st);
			contentId = content.getId();
			SysLog.log("网上评议：添加内容（"+content.getTitle()+"）","SurveyThemeEO", CmsLogEO.Operation.Update.toString());
		}
		CacheHandler.saveOrUpdate(BaseContentEO.class,content);
		if(surveyThemeVO.getHasClild() == null){
			setQuestion(st);
		}
		//发布
		if(surveyThemeVO.getIsPublish() == 1){
			try{
				final Long siteIdR = surveyThemeVO.getSiteId();
				final Long columnIdR = surveyThemeVO.getColumnId();
				final Long contentIdR = surveyThemeVO.getContentId();
				taskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						MessageSender.sendMessage(new MessageStaticEO(siteIdR,columnIdR,new Long[]{contentIdR}).setType(MessageEnum.PUBLISH.value()));
					}
				});
				//创建索引
				SolrIndexVO vo = new SolrIndexVO();
				vo.setId(contentIdR + "");
				vo.setTitle(surveyThemeVO.getTitle());
				vo.setTypeCode(BaseContentEO.TypeCode.reviewInfo.toString());
				vo.setColumnId(columnIdR);
				vo.setSiteId(siteIdR);
				vo.setContent(HtmlUtil.getTextFromTHML(surveyThemeVO.getContent()));
				vo.setCreateDate(surveyThemeVO.getIssuedTime());
				try {
					SolrFactory.deleteIndex(contentIdR + "");
					SolrFactory.createIndex(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("信息发布失败");
			}
		}
		return contentId;
	}

	private void setQuestion(SurveyThemeEO surveyTheme) {
		deleteClildByThemIds(new Long[]{surveyTheme.getThemeId()});
		if(!StringUtils.isEmpty(surveyTheme.getObjectNames())){
			String[] objectNames = surveyTheme.getObjectNames().split(",");
			String[] typeNames = surveyTheme.getTypeNames().split(",");
			if(objectNames !=null && objectNames.length >0){
				for(String objectName:objectNames){
					SurveyQuestionEO sq = new SurveyQuestionEO();
					sq.setThemeId(surveyTheme.getThemeId());
					sq.setTitle(objectName);
					surveyQuestionService.saveEntity(sq);
					if(typeNames !=null && typeNames.length >0){
						for(String typeName:typeNames){
							SurveyOptionsEO so =new SurveyOptionsEO();
							so.setThemeId(surveyTheme.getThemeId());
							so.setQuestionId(sq.getQuestionId());
							so.setTitle(typeName);
							surveyOptionsService.saveEntity(so);
						}

					}
				}
			}
		}

	}

	public void deleteClildByThemIds(Long[] ids) {
		if(ids !=null && ids.length>0){
			surveyQuestionService.deleteByThemeId(ids);
			surveyOptionsService.deleteByThemeId(ids);
			surveyIpService.deleteByThemeId(ids);
			surveyReplyService.deleteByQuestionIds(ids);
		}

	}


	@Override
	public SurveyThemeEO getSurveyThemeByContentId(Long contentId) {
		return surveyThemeDao.getSurveyThemeByContentId(contentId);
	}

	@Override
	public SurveyThemeVO getSurveyThemeVO(BaseContentVO content) {
		SurveyThemeVO vo = new SurveyThemeVO();
		SurveyThemeEO surveyTheme  = getSurveyThemeByContentId(content.getId());
		if(surveyTheme != null){
			BeanUtils.copyProperties(surveyTheme, vo);
			if(surveyTheme.getStartTime() != null && surveyTheme.getEndTime() !=null){
				vo.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(), surveyTheme.getEndTime()));
			}
		}
		vo.setContentId(content.getId());
		vo.setColumnId(content.getColumnId());
		vo.setSiteId(content.getSiteId());
		vo.setTitle(content.getTitle());
		vo.setSortNum(content.getNum());
		vo.setIsPublish(content.getIsPublish());
		vo.setIssuedTime(content.getPublishDate());
		return vo;
	}

	@Override
	public SurveyThemeVO getSurveyThemeVO(Long id) {
		SurveyThemeVO vo = new SurveyThemeVO();
		SurveyThemeEO surveyTheme  = getEntity(SurveyThemeEO.class, id);
		if(surveyTheme != null){
			BeanUtils.copyProperties(surveyTheme, vo);
			if(surveyTheme.getStartTime() != null && surveyTheme.getEndTime() !=null){
				vo.setIsTimeOut(TimeOutUtil.getTimeOut(surveyTheme.getStartTime(), surveyTheme.getEndTime()));
			}
			BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, surveyTheme.getContentId());
			if(content !=null){
				vo.setContentId(content.getId());
				vo.setColumnId(content.getColumnId());
				vo.setSiteId(content.getSiteId());
				vo.setTitle(content.getTitle());
				vo.setSortNum(content.getNum());
				vo.setIsPublish(content.getIsPublish());
				vo.setIssuedTime(content.getPublishDate());
			}
		}

		return vo;
	}

	@Override
	public List<SurveyThemeVO> getSurveyThemeVOS(String code) {
		return surveyThemeDao.getSurveyThemeVOS(code);
	}

	@Override
	public BaseContentEO save(SurveyThemeVO surveyThemeVO) {
		BaseContentEO content = null;
		SurveyThemeEO st = null;
		if(surveyThemeVO.getIsPublish() == 1 && surveyThemeVO.getIssuedTime() == null){
			surveyThemeVO.setIssuedTime(new Date());
		}
		if(surveyThemeVO.getContentId() != null){
			content = baseContentService.getEntity(BaseContentEO.class, surveyThemeVO.getContentId());
			content.setTitle(surveyThemeVO.getTitle());
			content.setNum(surveyThemeVO.getSortNum());
			if(surveyThemeVO.getIsPublish()!=null&&surveyThemeVO.getIsPublish()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(surveyThemeVO.getIsPublish());
			}
			content.setPublishDate(surveyThemeVO.getIssuedTime());
			baseContentService.updateEntity(content);
			st = getEntity(SurveyThemeEO.class, surveyThemeVO.getThemeId());
			BeanUtils.copyProperties(surveyThemeVO, st);
			updateEntity(st);
			SysLog.log("在线调查：修改内容（"+content.getTitle()+"）","SurveyThemeEO", CmsLogEO.Operation.Update.toString());
		}else{
			content = new BaseContentEO();
			content.setTitle(surveyThemeVO.getTitle());
			content.setColumnId(surveyThemeVO.getColumnId());
			content.setSiteId(surveyThemeVO.getSiteId());
			content.setTypeCode(BaseContentEO.TypeCode.survey.toString());
			content.setNum(surveyThemeVO.getSortNum());
			if(surveyThemeVO.getIsPublish()!=null&&surveyThemeVO.getIsPublish()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(surveyThemeVO.getIsPublish());
			}
			content.setPublishDate(surveyThemeVO.getIssuedTime());
			baseContentService.saveEntity(content);
			st = new SurveyThemeEO();
			BeanUtils.copyProperties(surveyThemeVO, st);
			st.setContentId(content.getId());
			saveEntity(st);
			SysLog.log("在线调查：新增内容（"+content.getTitle()+"）","SurveyThemeEO", CmsLogEO.Operation.Add.toString());
		}
		CacheHandler.saveOrUpdate(BaseContentEO.class,content);
		//发布
		if(content.getIsPublish() == 2){
			try{
				final Long siteIdR = content.getSiteId();
				final Long columnIdR = content.getColumnId();
				final Long contentIdR = content.getId();
				//创建索引
				SolrIndexVO vo = new SolrIndexVO();
				vo.setId(contentIdR + "");
				vo.setTitle(content.getTitle());
				vo.setTypeCode(BaseContentEO.TypeCode.survey.toString());
				vo.setColumnId(columnIdR);
				vo.setSiteId(siteIdR);
				vo.setContent(HtmlUtil.getTextFromTHML(st.getContent()));
				vo.setCreateDate(content.getPublishDate());
				try {
					SolrFactory.deleteIndex(contentIdR + "");
					SolrFactory.createIndex(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("信息发布失败");
			}
		}
		return content;
	}
}
