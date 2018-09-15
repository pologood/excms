package cn.lonsun.content.leaderwin.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ArrayFormat;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.FileUploadUtil;

import java.util.*;

@Service("leaderInfoService")
public class LeaderInfoServiceImpl extends BaseService<LeaderInfoEO> implements ILeaderInfoService {

	@Autowired
	private ILeaderInfoDao leaderInfoDao;

	@Autowired
	private IBaseContentService baseContentService;


	@Autowired
	private TaskExecutor taskExecutor;


	@Override
	public Pagination getPage(QueryVO query) {
		return leaderInfoDao.getPage(query);
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
		if(strs != null && strs.size()>0){
			FileUploadUtil.deleteFileCenterEO(strs.toArray(new String[]{}));
		}
		baseContentService.delete(BaseContentEO.class, contentIds);
		//		leaderInfoDao.delete(LeaderInfoEO.class, ids);
	}

	@Override
	public void deleteByContentIds(BaseContentEO content) {
		if(content == null){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "content不能为空");
		}
		if(!StringUtils.isEmpty(content.getImageLink())){
			FileUploadUtil.deleteFileCenterEO(content.getImageLink());
		}
		LeaderInfoEO leader =  getLeaderInfoByContentId(content.getId());
		if(leader != null){
			leaderInfoDao.delete(LeaderInfoEO.class, new Long[]{leader.getLeaderInfoId()});
		}
	}

	private LeaderInfoEO getLeaderInfoByContentId(Long contentId) {
		return leaderInfoDao.getLeaderInfoByContentId(contentId);
	}

	@Override
	public void batchCompletelyDelete(Long[] contentIds) {
		leaderInfoDao.batchCompletelyDelete(contentIds);
		SysLog.log("彻底删除领导>> ID：" + ArrayFormat.ArrayToString(contentIds), "LeaderInfoEO", CmsLogEO.Operation.Update.toString());
	}

	@Override
	public LeaderInfoVO getLeaderInfoVO(Long infoId) {
		LeaderInfoVO leaderInfoVO = new LeaderInfoVO();
		LeaderInfoEO leaderInfo  = getEntity(LeaderInfoEO.class, infoId);
		if(leaderInfo != null){
			BeanUtils.copyProperties(leaderInfo, leaderInfoVO);
			//可以查询被删除的数据，供回收站查看使用 by liuk 2016-10-20
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id",leaderInfoVO.getContentId());
			BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, map);
			if(content !=null){
				leaderInfoVO.setContentId(content.getId());
				leaderInfoVO.setColumnId(content.getColumnId());
				leaderInfoVO.setSiteId(content.getSiteId());
				leaderInfoVO.setName(content.getTitle());
				leaderInfoVO.setSortNum(content.getNum());
				leaderInfoVO.setPicUrl(content.getImageLink());
				leaderInfoVO.setIssued(content.getIsPublish());
				leaderInfoVO.setIssuedTime(content.getPublishDate());
			}
		}
		return leaderInfoVO;
	}

	@Override
	public List<LeaderInfoVO> getLeaderInfoVOS(String code) {
		return leaderInfoDao.getLeaderInfoVOS(code);
	}

	@Override
	public List<LeaderInfoVO> getLeaderInfoVOSByTypeId(Long leaderTypeId) {
		return leaderInfoDao.getLeaderInfoVOSByTypeId(leaderTypeId);
	}

	@Override
	public BaseContentEO save(LeaderInfoVO leaderInfoVO) {
		BaseContentEO content = null;
		LeaderInfoEO leaderInfo = null;
		if(leaderInfoVO.getIssued() == 1 && leaderInfoVO.getIssuedTime() == null){
			leaderInfoVO.setIssuedTime(new Date());
		}

		if(leaderInfoVO.getContentId() != null){
			content = baseContentService.getEntity(BaseContentEO.class, leaderInfoVO.getContentId());
			content.setTitle(leaderInfoVO.getName());
			if(leaderInfoVO.getIssued()!=null&&leaderInfoVO.getIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(leaderInfoVO.getIssued());
			}
			content.setNum(leaderInfoVO.getSortNum());
			content.setPublishDate(leaderInfoVO.getIssuedTime());
			content.setImageLink(leaderInfoVO.getPicUrl());
			baseContentService.updateEntity(content);
			leaderInfo = getEntity(LeaderInfoEO.class, leaderInfoVO.getLeaderInfoId());
			BeanUtils.copyProperties(leaderInfoVO, leaderInfo);
			updateEntity(leaderInfo);
		}else{
			content = new BaseContentEO();
			content.setTitle(leaderInfoVO.getName());
			content.setColumnId(leaderInfoVO.getColumnId());
			content.setSiteId(leaderInfoVO.getSiteId());
			content.setTypeCode(BaseContentEO.TypeCode.leaderInfo.toString());
			content.setNum(leaderInfoVO.getSortNum());
			if(leaderInfoVO.getIssued()!=null&&leaderInfoVO.getIssued()==1){
				content.setIsPublish(2);
			}else{
				content.setIsPublish(leaderInfoVO.getIssued());
			}
			content.setPublishDate(leaderInfoVO.getIssuedTime());
			content.setImageLink(leaderInfoVO.getPicUrl());
			baseContentService.saveEntity(content);
			leaderInfo = new LeaderInfoEO();
			BeanUtils.copyProperties(leaderInfoVO, leaderInfo);
			leaderInfo.setContentId(content.getId());
			saveEntity(leaderInfo);
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
				vo.setTypeCode(BaseContentEO.TypeCode.leaderInfo.toString());
				vo.setColumnId(columnIdR);
				vo.setSiteId(siteIdR);
				vo.setContent(leaderInfo.getJobResume());
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

	@Override
	public List<LeaderInfoVO> getLeaderInfo(Long siteId) {
		return leaderInfoDao.getLeaderInfo(siteId);
	}

	@Override
	public List<LeaderInfoVO> getList(Long siteId, Long columnId) {
		return leaderInfoDao.getList(siteId,columnId);
	}
}
