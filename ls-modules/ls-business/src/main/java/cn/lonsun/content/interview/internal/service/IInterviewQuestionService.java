package cn.lonsun.content.interview.internal.service;

import java.util.List;

import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
import cn.lonsun.content.interview.vo.InterviewStatusVO;
import cn.lonsun.content.interview.vo.ParticipationAnalyseVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface IInterviewQuestionService extends IBaseService<InterviewQuestionEO>{

	void deleteByInterviewId(Long[] ids);

	List<InterviewQuestionEO> getListByInterviewId(Long interviewId);

	void delete(Long[] ids);

	Pagination getPage(InterviewQueryVO query);

	//公共参与分析
	ParticipationAnalyseVO getParticipationAnalyseById(InterviewStatusVO vo);
	List<InterviewQuestionEO> getListByInterviewId(Long interviewId,String sortOrder);

}
