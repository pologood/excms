package cn.lonsun.staticcenter.generate.tag.impl.leaderwin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;

import com.alibaba.fastjson.JSONObject;

@Component
public class LeaderInfoListBeanService extends AbstractBeanService {

	@Autowired
	private ILeaderInfoDao leaderInfoDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getObject(JSONObject paramObj) {
		Context context = ContextHolder.getContext();
		List<Long> ids = new ArrayList<Long>();
		String id = paramObj.getString(GenerateConstant.ID);
		if(!StringUtils.isEmpty(id)){
			// 默认查询本栏目
			ids.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(
					id.split(","), Long.class))));

		}else{
			Long columnId = context.getColumnId();
			// 默认查询本栏目
			ids.add(columnId);
		}
		String idsStr = StringUtils.join(ids.toArray(),",");
		//需要显示条数.
		Integer num = paramObj.getInteger(GenerateConstant.NUM);
		List<Object> values = new ArrayList<Object>();StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
				+ " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId in ("+idsStr+") and b.isPublish = 1");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(BaseContentEO.TypeCode.leaderInfo.toString());
		hql.append(" order by b.num desc");
		List<LeaderInfoVO> list = (List<LeaderInfoVO>) leaderInfoDao.getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class, num);
		if (null != list && !list.isEmpty()) {
			// 处理文章链接
			for (LeaderInfoVO liVo : list) {
				liVo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(),HtmlEnum.ACRTILE.getValue(),liVo.getColumnId(),liVo.getContentId()));
			}
		}
		return list;
	}
}