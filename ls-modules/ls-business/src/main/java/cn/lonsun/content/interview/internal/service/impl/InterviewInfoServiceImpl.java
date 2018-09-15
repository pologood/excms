package cn.lonsun.content.interview.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.vo.ConvertMsg;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.internal.service.IInterviewQuestionService;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("interviewInfoService")
public class InterviewInfoServiceImpl extends BaseService<InterviewInfoEO> implements IInterviewInfoService{

	@Autowired
	private IInterviewInfoDao interviewInfoDao;

	@Autowired
	private IInterviewQuestionService interviewQuestionService;

	@Autowired
	private IBaseContentService baseContentService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private IContentPicService contentPicService;

	@Override
	public Pagination getPage(QueryVO query) {
		return interviewInfoDao.getPage(query);
	}

	@Override
	public void delete(Long[] ids,Long[] contentIds) {
		List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
		List<String> strs = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (BaseContentEO content : list) {
				if (content != null && !StringUtils.isEmpty(content.getImageLink())) {
					strs.add(content.getImageLink());
				}
				//删除图片
				List<ContentPicEO> pics = contentPicService.getPicsList(content.getId());
				if(pics != null && pics.size() >0 ){
					for(ContentPicEO pic:pics){
						FileUploadUtil.setStatus(new String[]{pic.getPath(),pic.getThumbPath()},0);
						contentPicService.delete(ContentPicEO.class, pic.getPicId());
					}
				}
				CacheHandler.delete(BaseContentEO.class, content);
			}
		}
		baseContentService.delete(BaseContentEO.class, contentIds);
		//		interviewInfoDao.delete(InterviewInfoEO.class,ids);
		//		interviewQuestionService.deleteByInterviewId(ids);
	}

	@Override
	public void deleteByContentIds(BaseContentEO content) {
		if(content == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "content不能为空");
		}
		if(!StringUtils.isEmpty(content.getImageLink())){
			FileUploadUtil.deleteFileCenterEO(content.getImageLink());
		}
		InterviewInfoEO interview = getInterviewInfoByContentId(content.getId());
		if(interview != null){
			interviewInfoDao.delete(InterviewInfoEO.class,new Long[]{interview.getInterviewId()});
			interviewQuestionService.deleteByInterviewId(new Long[]{interview.getInterviewId()});
		}
	}

	public InterviewInfoEO getInterviewInfoByContentId(Long contentId) {
		return interviewInfoDao.getInterviewInfoByContentId(contentId);
	}

	@Override
	public InterviewInfoVO getInterviewInfoVO(Long interviewId) {
		InterviewInfoVO interviewInfoVO = new InterviewInfoVO();
		InterviewInfoEO interviewInfo  = getEntity(InterviewInfoEO.class, interviewId);
		if(interviewInfo != null){
			BeanUtils.copyProperties(interviewInfo, interviewInfoVO);
			BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, interviewInfo.getContentId());
			if(content !=null){
				interviewInfoVO.setContentId(content.getId());
				interviewInfoVO.setContentPath(content.getContentPath());
				interviewInfoVO.setQuoteStatus(content.getQuoteStatus());
				interviewInfoVO.setColumnId(content.getColumnId());
				interviewInfoVO.setSortNum(content.getNum());
				interviewInfoVO.setSiteId(content.getSiteId());
				interviewInfoVO.setTitle(content.getTitle());
				interviewInfoVO.setPicUrl(content.getImageLink());
				interviewInfoVO.setIssued(content.getIsPublish());
				interviewInfoVO.setIssuedTime(content.getPublishDate());
			}
		}
		return interviewInfoVO;
	}

	@Override
	public List<InterviewInfoVO> getInterviewInfoVOS(String code) {
		return interviewInfoDao.getInterviewInfoVOS(code);
	}

	@Override
	public BaseContentEO save(InterviewInfoVO interviewInfoVO, String picList) {
		BaseContentEO content = null;
		InterviewInfoEO interviewInfo = null;
		if(interviewInfoVO.getIssued() == 1 && interviewInfoVO.getIssuedTime() == null){
			interviewInfoVO.setIssuedTime(new Date());
		}
		String prePath="";

		if(interviewInfoVO.getContentId() != null){
			content = baseContentService.getEntity(BaseContentEO.class, interviewInfoVO.getContentId());
			if(interviewInfoVO.getQuoteStatus().equals(100)){
				content.setQuoteStatus(interviewInfoVO.getQuoteStatus());
			}
			content.setVideoStatus(interviewInfoVO.getVideoStatus());
			prePath = content.getContentPath();
			content.setTitle(interviewInfoVO.getTitle());
			if(interviewInfoVO.getIssued()!=null&&interviewInfoVO.getIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(interviewInfoVO.getIssued());
			}
			content.setPublishDate(interviewInfoVO.getIssuedTime());
			content.setNum(interviewInfoVO.getSortNum());
			content.setImageLink(interviewInfoVO.getPicUrl());
			content.setContentPath(interviewInfoVO.getContentPath());
			if(StringUtils.isEmpty(interviewInfoVO.getContentPath())){
				content.setQuoteStatus(0);
			}
			baseContentService.updateEntity(content);
			interviewInfo = getEntity(InterviewInfoEO.class, interviewInfoVO.getInterviewId());
			BeanUtils.copyProperties(interviewInfoVO, interviewInfo);
			updateEntity(interviewInfo);
			SysLog.log("在线访谈：修改内容（"+content.getTitle()+"）","InterviewInfoEO", CmsLogEO.Operation.Update.toString());
		}else{
			content = new BaseContentEO();
			if(interviewInfoVO.getQuoteStatus().equals(100)){
				content.setQuoteStatus(interviewInfoVO.getQuoteStatus());
			}
			content.setTitle(interviewInfoVO.getTitle());
			content.setColumnId(interviewInfoVO.getColumnId());
			content.setSiteId(interviewInfoVO.getSiteId());
			content.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
			content.setNum(interviewInfoVO.getSortNum());
			if(interviewInfoVO.getIssued()!=null&&interviewInfoVO.getIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(interviewInfoVO.getIssued());
			}
			content.setVideoStatus(interviewInfoVO.getVideoStatus());

			content.setPublishDate(interviewInfoVO.getIssuedTime());
			content.setImageLink(interviewInfoVO.getPicUrl());
			content.setContentPath(interviewInfoVO.getContentPath());
			baseContentService.saveEntity(content);
			interviewInfo = new InterviewInfoEO();
			BeanUtils.copyProperties(interviewInfoVO, interviewInfo);
			interviewInfo.setContentId(content.getId());
			saveEntity(interviewInfo);
			SysLog.log("在线访谈：新增内容（"+content.getTitle()+"）","InterviewInfoEO", CmsLogEO.Operation.Add.toString());
		}
		if(!StringUtils.isEmpty(content.getImageLink())){
			FileUploadUtil.saveFileCenterEO(content.getImageLink());
		}
		if(!StringUtils.isEmpty(picList)){
			List<ContentPicEO> pics= (List<ContentPicEO>) Jacksons.json().fromJsonToList(picList,ContentPicEO.class);
			if(pics!=null&&pics.size()>0){
				List<String> mongoIds=new ArrayList<String>();
				for(ContentPicEO l:pics){
					contentPicService.updatePic(l,content.getId());
					ContentPicEO _eo=contentPicService.getEntity(ContentPicEO.class, l.getPicId());
					if(!AppUtil.isEmpty(_eo)){
						mongoIds.add(_eo.getPath());
						mongoIds.add(_eo.getThumbPath());
					}
				}
				int size =  pics.size();
				String[] ids=(String[]) mongoIds.toArray(new String[size]);
				FileUploadUtil.setStatus(ids, 1, content.getId(),content.getColumnId(),content.getSiteId());;
			}
		}

		//更新缓存
		CacheHandler.saveOrUpdate(BaseContentEO.class,content);
		//发布

		return content;
	}


}
