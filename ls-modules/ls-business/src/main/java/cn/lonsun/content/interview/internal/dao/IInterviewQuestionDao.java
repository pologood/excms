package cn.lonsun.content.interview.internal.dao;

import java.util.List;

import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface IInterviewQuestionDao extends IBaseDao<InterviewQuestionEO>{

	void deleteByInterviewId(Long[] ids);

	List<InterviewQuestionEO> getListByInterviewId(Long interviewId);

	Pagination getPage(InterviewQueryVO query);

	//总参与数
	Long getParticipationNum(Long id);

	//答复数
	Long getAnswerNum(Long id);

	//提问网友
	Long getQtNetFriendNum(Long id);

	List<InterviewQuestionEO> getListByInterviewId(Long interviewId,String sortOrder);

}
