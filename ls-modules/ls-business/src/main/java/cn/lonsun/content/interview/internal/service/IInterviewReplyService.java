package cn.lonsun.content.interview.internal.service;

import java.util.List;
import java.util.Map;

import cn.lonsun.content.interview.internal.entity.InterviewReplyEO;
import cn.lonsun.core.base.service.IBaseService;

public interface IInterviewReplyService extends IBaseService<InterviewReplyEO>{

	void deleteByInterviewId(Long[] ids);

	Map<Long, List<InterviewReplyEO>> getMapByInterviewId(Long interviewId);

	List<InterviewReplyEO> getListByInterviewId(Long interviewId);

	void deleteByQuestionId(Long[] ids);

}
