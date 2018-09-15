package cn.lonsun.source.dataexport.impl.ex7.content;

import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.content.ISurveyExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.SurveyOptionsVO;
import cn.lonsun.target.datamodel.content.SurveyQuestionVO;
import cn.lonsun.target.datamodel.content.SurveyVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author caohaitao
 * @Title: Ex7SurveyExportService
 * @Package cn.lonsun.source.dataexport.impl.ex7.content
 * @Description: 网上调查
 * @date 2018/3/9 8:39
 */
@Service("ex7SurveyExportService")
public class Ex7SurveyExportService extends JdbcAble<SurveyVO> implements ISurveyExportService {

    private static final String QUERY_SURVEY_SQL = "SELECT v.VoteID as voteID, v.VoteName as title,v.VoteDate as publicDate FROM VoteName v where l.SS_ID = ?";
    private static final String QUERY_QUESTION_SQL = "SELECT t.VoteTitle as title FROM VoteTitle t where t.VoteID = ?";
    private static final String QUERY_OPTION_SQL = "SELECT s.VoteItem as title,s.VotePic as picUrl,s.VoteCount as votesCount FROM VoteStat s where s.VoteID = ?";

    @Override
    public List<SurveyVO> getDataList(ContentQueryVO queryVO) {
        List<SurveyVO> surveyVOS = queryList(QUERY_SURVEY_SQL, queryVO.getOldColumnId());
        for (SurveyVO surveyVO : surveyVOS) {
            List<SurveyQuestionVO> questionVOList = queryBeanList(QUERY_QUESTION_SQL, SurveyQuestionVO.class, surveyVO.getVoteID());
            for (SurveyQuestionVO questionVO : questionVOList) {
                List<SurveyOptionsVO> optionsVOList = queryBeanList(QUERY_OPTION_SQL, SurveyOptionsVO.class, questionVO.getSid());
                questionVO.setOptionsList(optionsVOList);
            }
            surveyVO.setQuestionList(questionVOList);
        }
        return surveyVOS;
    }

    @Override
    public List<SurveyVO> getDataByIds(ContentQueryVO queryVO, Object... ids) {
        return null;
    }
}
