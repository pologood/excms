package cn.lonsun.content.messageBoard.dao;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.sitechart.vo.KVL;
import cn.lonsun.system.sitechart.vo.KVP;

import java.util.Date;
import java.util.List;

public interface IMessageBoardDao extends IMockDao<MessageBoardEO> {
    //获取留言分页
    public Pagination getPage(MessageBoardPageVO pageVO);

    public Long countMessageBoard(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    /**
     * 按类别 获取满意度分组 个数
     * @return
     */
    public List<KVP> getSatisfactoryTypeCount(MessageBoardPageVO pageVO);

    /**
     * 按栏目 获取满意度分组 个数
     * @return
     */
    public List<KVL> getSatisfactoryUnitCount(MessageBoardPageVO pageVO);

    public Long getCount(String type,Long columnId);

    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    List<ContentChartVO> getSatisfactoryList(ContentChartQueryVO queryVO);

    List<MessageBoardEditVO> getColumnIdByTypeCode();

    GuestBookNumVO getAnalys(Long[] columnIds, Long siteId);

    List<GuestBookTypeVO> getAnalysType(Long[] columnIds, Long siteId, Integer isReply);

    Pagination getMemberPage(MessageBoardPageVO vo);

    List<MessageBoardEditVO> listMessageBoard(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num);

    MessageBoardEditVO searchEO(String randomCode, String docNum, Long siteId);

    List<MessageBoardEditVO> getMessageBoardBySiteId(Long siteId);

    MessageBoardEO getMessageBoardByKnowledgeBaseId(Long knowledgeBaseId);

    public Pagination getMobilePage(MessageBoardPageVO pageVO);

    public Long getDateTotalCount(Long[] columnIds,String Date,Long siteId,String monthDate);

    public Long getDateReplyCount(Long[] columnIds,String Date,Long siteId,String monthDate);

    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId);

    Pagination getUnitPage(MessageBoardPageVO pageVO);

    Pagination getUnAuditCount(UnAuditContentsVO uaVO);

    public List<MessageBoardSearchVO> getAllPulishMessageBoard();

    Long getNoDealCount();

    Pagination getMobilePage2(ContentPageVO contentPageVO);

    public Object getCallbackPage(MessageBoardPageVO pageVO);

    public List<MessageBoardListVO> getMessageBoardUnitList(StatisticsQueryVO vo);

    public List<MessageBoardEditVO> getUnReply(Long columnId, int day);

    List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);

    Long getSelectNum(Long siteId, Long[] columnIds);

    MessageBoardEditVO queryRemoved(Long id);

    void batchCompletelyDelete(Long[] messageBoardIds);

    Pagination getPageByQuery(MessageBoardSearchVO searchVO);

    Long getSummaryCount(StatisticsQueryVO queryVO);
}
