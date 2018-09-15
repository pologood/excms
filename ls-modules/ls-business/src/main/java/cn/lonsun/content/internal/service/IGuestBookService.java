package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;

import java.util.Date;
import java.util.List;

/**
 * @author hujun
 * @ClassName: IGuestBookService
 * @Description: 留言管理接口
 * @date 2015年10月26日
 */
public interface IGuestBookService extends IMockService<GuestBookEO> {

    //获取留言分页
    public Pagination getPage(GuestBookPageVO pageVO);

    /**
     *
     * @Description: 更改发布状态
     * @param @param ids
     * @param @param siteId
     * @param @param columnId
     * @param @param status
     * @return void
     * @throws
     * @author hujun
     * @date 2015年10月27日
     */
    /*public void changePublish(Long[] ids,Long siteId,Long columnId,Integer status);

	*/

    /**
     * @param @param id
     * @param @param content
     * @return void
     * @throws
     * @Description: 添加回复内容
     * @author hujun
     * @date 2015年10月27日
     *//*
    public void reply(Long id,String content);
	
	//查询单位没实现
	public Object queryOrgan();
	
	//转办更改接收单位ID
	public void changeOrganId(Long guestBookId,Long receiveOrganId);*/

    //保存前台留言到从表
    public GuestBookEO saveGuestBook(GuestBookEO eo, Long siteId, Long columnId);

    /*//会员登录保存留言
    public void vipSave(GuestBookEO eo);*/
    //回复留言
    public GuestBookEO reply(GuestBookEditVO eo);

    //删除从表留言
    public void remove(Long id);

    //批量删除（假删）
    public void batchDelete(Long[] ids);

    //发布&批量发布
    public void publish(Long[] ids, Integer status);

    //转办更改接收单位id同时保存备注
    public void forward(Long id, Long receiveId, String receiveUserCode, Integer recType, String localUnitId);

    //修改功能
    public void modifySave(GuestBookEO eo);

    //统计回复或未回复数量
    public Long count(Integer i);

    //返回当前栏目留言条数
    public Long countData(Long columnId);
	
	/*//获取未审核留言分页
	public Pagination getNoAuditPage(GuestBookPageVO pageVO);*/
	
	/*//获取已删除留言分页
	public Pagination getRecycleBinPage(GuestBookPageVO pageVO);*/

    //根据主键查询被删除的内容
    public Object queryRemoved(Long id);

    //恢复被删留言
    public void recovery(Long id);

    //物理删除留言
    public void completelyDelete(Long id);

    //批量恢复
    public void batchRecovery(Long[] ids);

    //批量物理删除
    public void batchCompletelyDelete(Long[] ids);

    /*public Pagination getRecycleContentPage(GuestBookPageVO pageVO);

    //获取待审内容分页
    public Pagination getNoAuditContentPage(UnAuditContentsVO uaVO);*/
    //获取待审留言
    public GuestBookEO noAuditGuestBook(Long id);

    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO);

    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO);

    public Pagination getGuestPage(StatisticsQueryVO vo);

    public List<GuestListVO> getGuestList(StatisticsQueryVO vo);


    //保存留言
    public GuestBookEO saveGusetBook(GuestBookEditVO vo);

    public Long getUnReplyCount(Long columnId, int day);

    public List<GuestBookEditVO> getUnReply(Long columnId, int day);

    public List<GuestBookEditVO> getGuestBookBySiteId(Long siteId);

    //根据ID返回EO
    public List<GuestBookEO> getGuestBookList(Long[] ids);

    public GuestBookEditVO searchEO(String randomCode, String docNum, Long siteId);

    public GuestBookEditVO getVO(Long id);

    public Long countGuestbook(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    public Long countGuestbook2(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse);

    public List<GuestListVO> replyOKRank(Long siteId, String columnIds, Integer isPublish);

    public Pagination getMobilePage(GuestBookPageVO pageVO);

    //留言分析
    public GuestBookAnalyseVO getAnalys(GuestBookStatusVO vo);


    public List<GuestBookSearchVO> getAllGuestBook();

    public List<GuestListVO> replyOrderByOrgan(String columnIds, Long siteId, String organIds, int num);

    public List<GuestBookEditVO> listGuestBook(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num);


    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId);

    public void setRead(Long contentId);

    public List<GuestBookEditVO> getFromSite(Long siteId, Long recUnitId, Integer num);

    public GuestBookEO getGuestBookByContentId(Long id);

    public Pagination getMobilePage2(ContentPageVO contentPageVO);
}
