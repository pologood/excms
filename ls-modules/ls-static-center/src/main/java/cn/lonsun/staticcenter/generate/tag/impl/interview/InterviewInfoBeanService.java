package cn.lonsun.staticcenter.generate.tag.impl.interview;

import java.util.ArrayList;
import java.util.List;

import cn.lonsun.content.internal.dao.IContentPicDao;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.core.base.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.internal.dao.IInterviewQuestionDao;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;

import com.alibaba.fastjson.JSONObject;

@Component
public class InterviewInfoBeanService extends AbstractBeanService {

	@Autowired
	private IInterviewInfoDao interviewInfoDao;

	@Autowired
	private IInterviewQuestionDao interviewQuestionDao;

	@Autowired
	private IContentPicDao contentPicDao;

	@Override
	public Object getObject(JSONObject paramObj) {
		Context context = ContextHolder.getContext();
		//是否查询统计数
		Boolean queryCount= false;
		try {
			Boolean qc = paramObj.getBoolean("queryCount");
			queryCount = (qc==null?false:qc);
		}catch (Exception e){}
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
				+ "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
				+ "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
				+ "s.outLink as outLink,s.handleOrgan as handleOrgan,s.organizer as organizer,b.contentPath as contentPath,b.quoteStatus as quoteStatus,s.content as content,s.desc as desc,s.organDesc as organDesc,s.summary as summary,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.address as address"
				+ " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.isPublish = 1 and b.recordStatus= ? and b.typeCode = ? and b.id = ?");
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(BaseContentEO.TypeCode.interviewInfo.toString());
		values.add(contentId);
		hql.append(" order by b.num desc");
		InterviewInfoVO infoVO = (InterviewInfoVO) interviewInfoDao.getBean(hql.toString(), values.toArray(), InterviewInfoVO.class);
//		InterviewInfoWebVO infoWebVO  = null;
		if(infoVO != null){
//			infoWebVO = new InterviewInfoWebVO();
//			BeanUtils.copyProperties(infoVO, infoWebVO);
//			InterviewQueryVO query = new InterviewQueryVO();
//			query.setPageSize(pageSize);
//			query.setInterviewId(infoWebVO.getInterviewId());
//			query.setIssued(InterviewInfoEO.Status.Yes.getStatus());
//			Pagination pagination = interviewQuestionDao.getPage(query);
//			infoWebVO.setQuestionPage(new Pagination());
			infoVO.setPics(contentPicDao.getPicsList(infoVO.getContentId()));
			if(queryCount != null && queryCount){
				infoVO.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(),HtmlEnum.ACRTILE.getValue(),infoVO.getColumnId(),infoVO.getContentId()));
				infoVO.setQuestionCount(interviewQuestionDao.getParticipationNum(infoVO.getInterviewId()));
				infoVO.setReplyCount(interviewQuestionDao.getAnswerNum(infoVO.getInterviewId()));
				infoVO.setMemberCount(interviewQuestionDao.getQtNetFriendNum(infoVO.getInterviewId()));
			}

		}
		return infoVO;
	}

}
