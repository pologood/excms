package cn.lonsun.content.interview.internal.dao;

import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface IInterviewInfoDao extends IBaseDao<InterviewInfoEO>{

	Pagination getPage(QueryVO query);

	InterviewInfoEO getInterviewInfoByContentId(Long contentId);

	List<InterviewInfoVO> getInterviewInfoVOS(String code);
}
