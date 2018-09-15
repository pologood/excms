package cn.lonsun.content.interview.internal.dao;

import java.util.List;

import cn.lonsun.content.interview.internal.entity.InterviewReplyEO;
import cn.lonsun.core.base.dao.IBaseDao;

public interface IInterviewReplyDao extends IBaseDao<InterviewReplyEO>{

	void deleteByInterviewId(Long[] ids);

	List<InterviewReplyEO> getListByInterviewId(Long interviewId);

	void deleteByQuestionId(Long[] ids);

}
