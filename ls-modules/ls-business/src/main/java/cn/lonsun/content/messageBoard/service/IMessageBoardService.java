package cn.lonsun.content.messageBoard.service;

import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.vo.*;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.sitechart.vo.KVL;
import cn.lonsun.system.sitechart.vo.KVP;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: IGuestBookService
 * @Description: 多回复留言管理接口
 */
public interface IMessageBoardService extends IMockService<MessageBoardEO> {

    //获取留言分页
    public Pagination getPage(MessageBoardPageVO pageVO);

    //保存前台留言到从表
    public MessageBoardEO saveMessageBoard(MessageBoardEO eo, Long siteId, Long columnId);

    //导入老平台数据
    public MessageBoardEO exportOldMessageBoard(MessageBoardEditVO editVO, MessageBoardReplyVO replyVO);

    //导入安庆老平台数据
    public MessageBoardEO exportAQOldMessageBoard(MessageBoardEditVO editVO, List<MessageBoardForwardVO> forwardListVO, List<MessageBoardReplyVO> replyListVO);

    //导入Excl
    public MessageBoardEO exportExcl(MessageBoardEditVO editVO, MessageBoardReplyVO replyVO);

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

    public MessageBoardEditVO getVO(Long id);

    public Long getCount(String type,Long columnId);

    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    public Pagination getUnitMessageBoardPage(StatisticsQueryVO vo);

    List<ContentChartVO> getSatisfactoryList(ContentChartQueryVO queryVO);

    public List<MessageBoardListVO> getMessageBoardList(StatisticsQueryVO queryVO);

    Object getAnalys(GuestBookStatusVO vo);

    Pagination getMemberPage(MessageBoardPageVO vo);

    List<MessageBoardEditVO> listMessageBoard(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num);

    public MessageBoardEditVO searchEO(String randomCode, String docNum, Long siteId);

    void saveComment(MessageBoardEO messageBoardEO);

    List<MessageBoardEditVO> getMessageBoardBySiteId(Long siteId);

    MessageBoardEO getMessageBoardByKnowledgeBaseId(Long knowledgeBaseId);

    public Pagination getMobilePage(MessageBoardPageVO pageVO);

    public Long getUnReadNum(Long siteId, Long  columnId, Long createUserId);

    Pagination getUnAuditCount(UnAuditContentsVO uaVO);

    MessageBoardEO saveMessageBoardVO(MessageBoardEditVO vo);

    List<MessageBoardSearchVO> getAllPulishMessageBoard();

    void setRead(Long id);

    Long getNoDealCount();

    Pagination getMobilePage2(ContentPageVO contentPageVO);

    Object getCallbackPage(  MessageBoardPageVO pageVO);

    List<MessageBoardListVO> getMessageBoardUnitList(StatisticsQueryVO vo);

    void exportOldAjjMessageBoard(MessageBoardEditVO vo, List<MessageBoardReplyVO> replyVOList);

    public  List<MessageBoardEditVO> getUnReply(Long columnId, int day);

    List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);

    MessageBoardEditVO queryRemoved(Long id);

    void batchCompletelyDelete(Long[] messageBoardIds);

    List<TreeNodeVO> getMessageTree(Long[] organIds , Long columnId);

    public Pagination getPageByQuery(MessageBoardSearchVO searchVO);

    void deleteReply(Long id, Long replyId);

    Long getSummaryCount(StatisticsQueryVO queryVO);
}
