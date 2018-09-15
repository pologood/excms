package cn.lonsun.content.ideacollect.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.codehaus.jackson.map.MapperConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectIdeaService;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.FileUploadUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("collectInfoService")
public class CollectInfoServiceImpl extends BaseService<CollectInfoEO> implements ICollectInfoService{

	@Autowired
	private ICollectInfoDao collectInfoDao;

	@Autowired
	private ICollectIdeaService collectIdeaService;

	@Autowired
	private IBaseContentService baseContentService;


	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public Pagination getPage(QueryVO query) {
		return collectInfoDao.getPage(query);
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
				CacheHandler.delete(BaseContentEO.class, content);
			}
		}
		if (strs != null && strs.size() > 0) {
			FileUploadUtil.deleteFileCenterEO(strs.toArray(new String[]{}));
		}
		baseContentService.delete(BaseContentEO.class, contentIds);
	}

	@Override
	public void deleteByContentIds(BaseContentEO content) {
		if(content == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "content不能为空");
		}
		if(!StringUtils.isEmpty(content.getImageLink())){
			FileUploadUtil.deleteFileCenterEO(content.getImageLink());
		}
		CollectInfoEO collect =  getCollectInfoByContentId(content.getId());
		if(collect != null){
			collectInfoDao.delete(CollectInfoEO.class, new Long[]{collect.getCollectInfoId()});
			collectIdeaService.deleteByCollectInfoId(new Long[]{collect.getCollectInfoId()});
		}
	}

	public CollectInfoEO getCollectInfoByContentId(Long contentId) {
		return collectInfoDao.getCollectInfoByContentId(contentId);
	}

	@Override
	public CollectInfoVO getCollectInfoVO(Long infoId) {
		CollectInfoVO collectInfoVO = new CollectInfoVO();
		CollectInfoEO collectInfo  = getEntity(CollectInfoEO.class, infoId);
		if(collectInfo != null){
			BeanUtils.copyProperties(collectInfo, collectInfoVO);
			BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, collectInfo.getContentId());
			if(content !=null){
				collectInfoVO.setContentId(content.getId());
				collectInfoVO.setColumnId(content.getColumnId());
				collectInfoVO.setSiteId(content.getSiteId());
				collectInfoVO.setSortNum(content.getNum());
				collectInfoVO.setTitle(content.getTitle());
				collectInfoVO.setPicUrl(content.getImageLink());
				collectInfoVO.setIsIssued(content.getIsPublish());
				collectInfoVO.setIssuedTime(content.getPublishDate());
			}
		}
		return collectInfoVO;
	}

	@Override
	public List<CollectInfoVO> getCollectInfoVOS(String code) {
		return collectInfoDao.getCollectInfoVOS(code);
	}

	@Override
	public BaseContentEO save(CollectInfoVO collectInfoVO) {
		BaseContentEO content = null;
		CollectInfoEO collectInfo = null;
		if(collectInfoVO.getIsIssued() == 1 && collectInfoVO.getIssuedTime() == null){
			collectInfoVO.setIssuedTime(new Date());
		}

		if(collectInfoVO.getContentId() != null){
			content = baseContentService.getEntity(BaseContentEO.class, collectInfoVO.getContentId());
			content.setTitle(collectInfoVO.getTitle());
			content.setNum(collectInfoVO.getSortNum());
			if(collectInfoVO.getIsIssued()!=null&&collectInfoVO.getIsIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(collectInfoVO.getIsIssued());
			}
			content.setPublishDate(collectInfoVO.getIssuedTime());
			content.setImageLink(collectInfoVO.getPicUrl());
			baseContentService.updateEntity(content);
			collectInfo = getEntity(CollectInfoEO.class, collectInfoVO.getCollectInfoId());
			BeanUtils.copyProperties(collectInfoVO, collectInfo);
			updateEntity(collectInfo);
			SysLog.log("民意征集：修改内容（"+content.getTitle()+"）","CollectInfoEO", CmsLogEO.Operation.Update.toString());
		}else{
			content = new BaseContentEO();
			content.setTitle(collectInfoVO.getTitle());
			content.setColumnId(collectInfoVO.getColumnId());
			content.setSiteId(collectInfoVO.getSiteId());
			content.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
			content.setNum(collectInfoVO.getSortNum());
			if(collectInfoVO.getIsIssued()!=null&&collectInfoVO.getIsIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(collectInfoVO.getIsIssued());
			}
			content.setPublishDate(collectInfoVO.getIssuedTime());
			content.setImageLink(collectInfoVO.getPicUrl());
			baseContentService.saveEntity(content);
			collectInfo = new CollectInfoEO();
			BeanUtils.copyProperties(collectInfoVO, collectInfo);
			collectInfo.setContentId(content.getId());
			saveEntity(collectInfo);
			SysLog.log("民意征集：新增内容（"+content.getTitle()+"）","CollectInfoEO", CmsLogEO.Operation.Add.toString());
		}
		if(!StringUtils.isEmpty(content.getImageLink())){
			FileUploadUtil.saveFileCenterEO(content.getImageLink());
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
				vo.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
				vo.setColumnId(columnIdR);
				vo.setSiteId(siteIdR);
				vo.setContent(HtmlUtil.getTextFromTHML(collectInfo.getContent()));
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
