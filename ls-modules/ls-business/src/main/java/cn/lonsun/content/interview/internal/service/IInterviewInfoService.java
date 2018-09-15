package cn.lonsun.content.interview.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface IInterviewInfoService extends IBaseService<InterviewInfoEO>{

	Pagination getPage(QueryVO query);

	void delete(Long[] ids,Long[] contentIds);

	InterviewInfoVO getInterviewInfoVO(Long interviewId);

	InterviewInfoEO getInterviewInfoByContentId(Long id);
	/**
	 * 彻底删除接口
	 * @param contents
	 */
	void deleteByContentIds(BaseContentEO content);

	List<InterviewInfoVO> getInterviewInfoVOS(String code);

	BaseContentEO save(InterviewInfoVO interviewInfoVO, String picList);
}
