package cn.lonsun.content.interview.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.interview.internal.dao.IInterviewReplyDao;
import cn.lonsun.content.interview.internal.entity.InterviewReplyEO;
import cn.lonsun.content.interview.internal.service.IInterviewReplyService;
import cn.lonsun.core.base.service.impl.BaseService;

@Service
public class InterviewReplyServiceImpl extends BaseService<InterviewReplyEO> implements IInterviewReplyService{

	@Autowired
	private IInterviewReplyDao interviewReplyDao;

	@Override
	public void deleteByInterviewId(Long[] ids) {
		interviewReplyDao.deleteByInterviewId(ids);
	}

	@Override
	public Map<Long, List<InterviewReplyEO>> getMapByInterviewId(
			Long interviewId) {
		Map<Long,List<InterviewReplyEO>> map = null;
		List<InterviewReplyEO> replys = getListByInterviewId(interviewId);
		if(replys !=null && replys.size()>0){
			map = new HashMap<Long,List<InterviewReplyEO>>();
			List<InterviewReplyEO> replyList = null;
			for(InterviewReplyEO reply:replys){
				Long questionId = reply.getQuestionId();
				replyList = map.get(questionId);
				if(!(replyList != null && replyList.size() > 0)){
					replyList = new ArrayList<InterviewReplyEO>();
				}
				replyList.add(reply);
				map.put(questionId, replyList);
			}
		}
		return map;
	}

	@Override
	public List<InterviewReplyEO> getListByInterviewId(Long interviewId) {
		return interviewReplyDao.getListByInterviewId(interviewId);
	}

	@Override
	public void deleteByQuestionId(Long[] ids) {
		interviewReplyDao.deleteByQuestionId(ids);
	}



}
