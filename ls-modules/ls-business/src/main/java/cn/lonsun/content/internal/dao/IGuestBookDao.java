package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;

import java.util.Date;
import java.util.List;

public interface IGuestBookDao extends IMockDao<GuestBookEO> {
    //获取留言分页
    public Pagination getPage(GuestBookPageVO pageVO);

    //查询单位
    public Object queryOrgan();

    //更改接收单位

    public void changeOrganId(Long id, Long receiveId);

    public GuestBookEO getByContentId(Long contentId);

    //查询发布标志
    public GuestBookEO queryAuditMark(Long guestBookId);

    //保存前台留言
    public void saveGuestBook(GuestBookEO eo);

    //批量删除从表记录
    public void batchDelete(Long[] ids);

    //删除从表记录
    public void remove(Long id);

    //发布&批量发布
    public void publish(Long[] ids, Integer status);

    //统计回复和未回复的留言条数
    public Long count(Integer i);

    //返回当前栏目留言条数
    public Long checkDelete(Long columnId);

	/*//获取未审核留言分页
    public Pagination getNoAuditPage(GuestBookPageVO pageVO);
	
	//获取回收站分页
	public Pagination getRecycleBinPage(GuestBookPageVO pageVO);*/

    //根据主表主键查询被删除的详细内容
    public Object queryRemoved(Long id);

    //恢复被删除的留言
    public void recovery(Long id);

    //物理删除留言
    public void completelyDelete(Long id);

    //批量恢复
    public void batchRecovery(Long[] ids);

    //批量物理删除
    public void batchCompletelyDelete(Long[] ids);

    /*//得到回收站分页
    public Pagination getRecycleContentPage(GuestBookPageVO pageVO);
    
    //获取待审内容分页
  	public Pagination getNoAuditContentPage(UnAuditContentsVO uaVO);*/
    //获取待审留言
    public GuestBookEO noAuditGuestBook(Long id);

    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);

    public Pagination getGuestPage(StatisticsQueryVO vo);


    public List<GuestListVO> getGuestList(StatisticsQueryVO queryVO);

    public Long getUnReplyCount(Long columnId, int day);

    public List<GuestBookEditVO> getUnReply(Long columnId, int day);

    public List<GuestBookEditVO> getGuestBookBySiteId(Long siteId);

    //根据ID返回list
    public List<GuestBookEO> getGuestBookList(Long[] ids);

    public Long countGuestbook(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    public Long countGuestbook2(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    public List<GuestListVO> replyOKRank(Long siteId, String columnIds, Integer isPublish);

    public Pagination getMobilePage(GuestBookPageVO pageVO);

    public GuestBookNumVO getAnalys(GuestBookStatusVO vo);

    public List<GuestBookTypeVO> getAnalysType(Long[] columnIds, Long siteId, Integer isReply);

    public Long getDateTotalCount(Long[] columnIds, String Date, Long siteId, String monthDate);

    public Long getDateReplyCount(Long[] columnIds, String Date, Long siteId, String monthDate);

    public Long getDateHandingCount(Long[] columnIds, String Date, Long siteId, String monthDate);

    public List<GuestBookSearchVO> getAllGuestBook();

    public List<GuestListVO> replyOrderByOrgan(StatisticsQueryVO queryVO);

    public GuestBookEditVO searchEO(String randomCode, String docNum, Long siteId);

    public List<GuestBookEditVO> listGuestBook(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num);

    public Long getSelectNum(Long siteId, Long[] columnIds);

    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId);

    public List<GuestBookEditVO> getFromSite(Long siteId, Long recUnitId, Integer num);

    public GuestBookEO getGuestBookByContentId(Long contentId);

    public Pagination getMobilePage2(ContentPageVO contentPageVO);
}
