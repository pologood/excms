package cn.lonsun.source.dataexport.impl.ex7.content;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.content.IInterviewInfoExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.InterviewInfoVO;
import cn.lonsun.target.datamodel.content.InterviewQuestionVO;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author caohaitao
 * @Title: Ex7InterviewInfoExportService
 * @Package cn.lonsun.source.dataexport.impl.ex7.content
 * @Description: 在线访谈
 * @date 2018/3/9 8:37
 */
@Service("ex7InterviewInfoExportService")
public class Ex7InterviewInfoExportService extends JdbcAble<InterviewInfoVO> implements IInterviewInfoExportService {

    private static final String QUERY_INTERVIEW_SQL = "SELECT i.IVS_ID as IVS_ID, i.IVS_Title as title,i.IVS_GuestSynopsis as userNames,i.IVS_LinkImage as picUrl,i.IVS_Moderator as presenter,i.IVS_Date as time,i.IVS_Synopsis as desc,i.IVS_Open as isOpen,i.IVS_HtmlUrl as linkUrl,i.IVS_zy as content FROM InterViewSort i WHERE i.IVS_SS_ID = ?";
    private static final String QUERY_QUESTION_SQL = "SELECT  i.IVI_Name as name,i.IVI_Content as content,i.IVI_RemoteIp as ip,i.IVI_RevertDate as replyDate,i.IVI_RevertTime as replyTime,i.IVI_Date as createDate,i.IVI_Time as createTime,i.IVI_ShowIs as issued,i.IVI_Date as issuedTime,i.IVI_RevertIS as isReply,i.IVI_Revert as replyContent,i.IVI_RevertGuests as replyName FROM InterViewInfo i WHERE i.IVI_IVS_ID = ?";

    @Override
    public List<InterviewInfoVO> getDataList(ContentQueryVO queryVO) {

        List<InterviewInfoVO> infoVOS = queryList(QUERY_INTERVIEW_SQL, queryVO.getColumnId());
        for (InterviewInfoVO infoVO : infoVOS) {
            List<InterviewQuestionVO> questionVOS = queryBeanList(QUERY_QUESTION_SQL, InterviewQuestionVO.class, infoVO.getIVS_ID());
            for (InterviewQuestionVO questionVO : questionVOS) {
                //查询时将访谈时间映射到两个字段，将日期和时间合并至一个字段返回，保存时以createDate为准
                Date ivi_Time = questionVO.getCreateTime();
                Date ivi_Date = questionVO.getCreateDate();
                Date createDate = dateAddTime(ivi_Date, ivi_Time);
                questionVO.setCreateDate(createDate);
                //查询时将回复时间映射到两个字段，将日期和时间合并至一个字段返回，保存时以replyTime为准
                Date revertTime = questionVO.getReplyTime();
                Date revertDate = questionVO.getReplyDate();
                Date replyTime = dateAddTime(revertDate, revertTime);
                questionVO.setReplyTime(replyTime);
            }
            //将questionList放入InterviewInfoVO
            infoVO.setQuestionList(questionVOS);
        }
        return infoVOS;
    }

    @Override
    public List<InterviewInfoVO> getDataByIds(ContentQueryVO queryVO, Object... ids) {
        return null;
    }

    private Date dateAddTime(Date date1, Date date2) {
        Date date3 = new Date();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            DateFormat dateFormat = DateFormat.getTimeInstance();
            if (!AppUtil.isEmpty(date1) && !AppUtil.isEmpty(date2)) {
                date3 = simpleDateFormat2.parse(simpleDateFormat1.format(date1) + " " + dateFormat.format(date2));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date3;
    }
}
