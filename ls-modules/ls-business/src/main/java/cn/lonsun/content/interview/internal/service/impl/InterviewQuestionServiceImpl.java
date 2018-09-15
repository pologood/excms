package cn.lonsun.content.interview.internal.service.impl;

import java.text.NumberFormat;
import java.util.List;


import cn.lonsun.content.interview.vo.InterviewStatusVO;
import cn.lonsun.content.interview.vo.ParticipationAnalyseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.interview.internal.dao.IInterviewQuestionDao;
import cn.lonsun.content.interview.internal.entity.InterviewQuestionEO;
import cn.lonsun.content.interview.internal.service.IInterviewQuestionService;
import cn.lonsun.content.interview.vo.InterviewQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class InterviewQuestionServiceImpl extends BaseService<InterviewQuestionEO> implements IInterviewQuestionService {

    @Autowired
    private IInterviewQuestionDao interviewQuestionDao;

    @Override
    public void deleteByInterviewId(Long[] ids) {
        interviewQuestionDao.deleteByInterviewId(ids);
    }

    @Override
    public List<InterviewQuestionEO> getListByInterviewId(Long interviewId) {
        return interviewQuestionDao.getListByInterviewId(interviewId);
    }

    @Override
    public void delete(Long[] ids) {
        interviewQuestionDao.delete(InterviewQuestionEO.class, ids);
    }

    @Override
    public Pagination getPage(InterviewQueryVO query) {
        return interviewQuestionDao.getPage(query);
    }

    @Override
    public ParticipationAnalyseVO getParticipationAnalyseById(InterviewStatusVO vo) {
        ParticipationAnalyseVO paVo = new ParticipationAnalyseVO();
        Long participationNum=0L;
        if (vo.isParticipationNum()){
            participationNum = interviewQuestionDao.getParticipationNum(vo.getId());
            paVo.setParticipationNum(participationNum);
        }

        Long answerNum = 0L;
        if (vo.isAnswerNum()){
            answerNum= interviewQuestionDao.getAnswerNum(vo.getId());
            paVo.setAnswerNum(answerNum);
        }
        if (vo.isQtNetFriendNum()){
            paVo.setQtNetFriendNum(interviewQuestionDao.getQtNetFriendNum(vo.getId()));
        }

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);

        if (answerNum != 0 && vo.isAnswerRate()) {
            String result = numberFormat.format((float) answerNum / (float) participationNum * 100);
            paVo.setAnswerRate(result + "%");
        }else{
            paVo.setAnswerRate("0%");
        }

        return paVo;
    }


    @Override
    public List<InterviewQuestionEO> getListByInterviewId(Long interviewId,String sortOrder) {
        return interviewQuestionDao.getListByInterviewId(interviewId,sortOrder);
    }
}
