package cn.lonsun.staticcenter.generate.tag.impl.collectInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.ideacollect.internal.dao.ICollectIdeaDao;
import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.ideacollect.vo.CollectInfoWebVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.TimeOutUtil;

import com.alibaba.fastjson.JSONObject;


@Component
public class CollectInfoBeanService extends AbstractBeanService {
	
	@Autowired
	private ICollectInfoDao collectInfoDao;
	
	@Autowired
	private ICollectIdeaDao collectIdeaDao;

	@Override
	public Object getObject(JSONObject paramObj) {
		Context context = ContextHolder.getContext();
		Long columnId = context.getColumnId();
		Long contentId = context.getContentId();// 根据文章id查询文章
		// 此写法是为了使得在页面这样调用也能解析
		if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
			columnId = paramObj.getLong(GenerateConstant.ID);
		}
		//需要显示条数.
		Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount,s.result as result"
				+ " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.isPublish = 1 and b.recordStatus= ? and b.typeCode = ? and b.id = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(BaseContentEO.TypeCode.collectInfo.toString());
		values.add(contentId);
		hql.append(" order by s.createDate desc");
		CollectInfoVO infoVO = (CollectInfoVO) collectInfoDao.getBean(hql.toString(), values.toArray(), CollectInfoVO.class);
		CollectInfoWebVO infoWeb = null;
		if(infoVO != null){
			infoWeb = new CollectInfoWebVO();
			BeanUtils.copyProperties(infoVO, infoWeb);
			if(infoWeb.getStartTime() != null && infoWeb.getEndTime() !=null){
				infoWeb.setIsTimeOut(TimeOutUtil.getTimeOut(infoWeb.getStartTime(), infoWeb.getEndTime()));
			}
//			IdeaQueryVO query = new IdeaQueryVO();
//			query.setCollectInfoId(infoWeb.getCollectInfoId());
//			query.setPageSize(pageSize);
//			query.setIssued(CollectIdeaEO.Status.Yes.getStatus());
//			Pagination page = collectIdeaDao.getPage(query);
//			infoWeb.setIdeaPage(page);
			infoWeb.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(),HtmlEnum.ACRTILE.getValue(),infoWeb.getColumnId(),infoWeb.getContentId()));
		}
		return infoWeb;
	}

}
