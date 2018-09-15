package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBaseContentDao extends IMockDao<BaseContentEO> {

    /**
     * @param pageVO
     * @return
     * @Description Paging by condition
     * @author Hewbing
     * @date 2015年10月9日 下午3:47:44
     */
    public Pagination getPage(ContentPageVO pageVO);

    /**
     * @param pageVO
     * @Description change status
     * @author Hewbing
     * @date 2015年10月9日 下午3:48:06
     */
    public void changePublish(ContentPageVO pageVO);

    /**
     * @param columnId
     * @param status
     * @Description 按栏目改变发布状态 change publish status by column
     * @author Hewbing
     * @date 2015年10月9日 下午3:48:35
     */
//    public void publishByColumn(Long columnId, Integer status);

    /**
     * @param siteId
     * @param status
     * @Description 按站点改变发布状态 change publish status by site
     * @author Hewbing
     * @date 2015年10月9日 下午3:49:03
     */
//    public void publishBySite(Long siteId, Integer status);

    /**
     * @param columnId
     * @return
     * @Description 按栏目获取记录条数 get count by column
     * @author Hewbing
     * @date 2015年10月9日 下午3:49:32
     */
    public Long getCountByColumnId(Long columnId);

    /**
     * @param siteId
     * @return
     * @Description 按站点获取记录条数 get count by site
     * @author Hewbing
     * @date 2015年10月9日 下午3:50:13
     */
    public Long getCountBySiteId(Long siteId);

    /**
     * @param ids
     * @param status
     * @Description 修改置顶状态 change stickes status
     * @author Hewbing
     * @date 2015年10月9日 下午3:50:37
     */
    public void changeTopStatus(Long[] ids, Integer status);

    /**
     * @param ids
     * @param status
     * @Description 改变加热状态 change Heating status
     * @author Hewbing
     * @date 2015年10月9日 下午3:50:59
     */
    public void changeHotStatus(Long[] ids, Integer status);

    /**
     * @param id
     * @param imgPath
     * @return
     * @Description 改变缩略图 change the thumb
     * @author Hewbing
     * @date 2015年10月9日 下午3:51:21
     */
    public int changeImg(Long id, String imgPath);

    /**
     * @param ids
     * @param status 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeTitltStatus
     * @Description: 标题新闻状态 change the Headline news status
     */
    public void changeTitltStatus(Long[] ids, Integer status);

    /**
     * @param @param ids
     * @param @param status 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeNewStatus
     * @Description: 加新状态 change add new status
     */
    public void changeNewStatus(Long[] ids, Integer status);

    /**
     * @param num 设定文件
     * @return void 返回类型
     * @throws
     * @Title: setNum
     * @Description: 设置序号 set sort NO.
     */
    public void setNum(Long id, Long num);

    /**
     * @param columnId
     * @return SortVO 返回类型
     * @throws
     * @Title: getMaxNumByColumn
     * @Description: 根据栏目查找最大排序 get the max sort number
     */
    public SortVO getMaxNumByColumn(Long columnId);

    /**
     * @param siteId
     * @return SortVO 返回类型
     * @throws
     * @Title: getMaxNumBySite
     * @Description: 根据站点查找最大排序 get the max sort number
     */
    public SortVO getMaxNumBySite(Long siteId);

    /**
     * @param opr
     * @param sortNum
     * @param pageVO
     * @return SortVO 返回类型
     * @throws
     * @Title: getNextSort
     * @Description:
     */
    public List<SortVO> getNextSort(String opr, Long sortNum, ContentPageVO pageVO);

    /**
     * @param id Parameter
     * @return void return Type
     * @throws
     * @Title: markQuote
     * @Description: 标记引用状态
     */
    public void markQuote(Long id, Integer status);

    public void setHit(Long id);

    /**
     */
    public List<BaseContentEO> getPageLink(Long columnId, Long sortNum, String opr);

    /**
     * @return Pagination return type
     * @throws
     * @Title: getUnAuditContents
     * @Description: 获取未审核内容
     */
    public Pagination getUnAuditContents(UnAuditContentsVO contentVO);

    public List<ColumnTypeVO> getUnAuditColumnIds(UnAuditContentsVO contentVO);

    public Pagination getPageBySortNum(QueryVO query);

    public BaseContentEO getSort(SortUpdateVO sortVo);

    public Long getMaxSortNum(Long siteId, Long columnId, String typeCode);

    public Pagination getOpenCommentContent(ContentPageVO pageVO);

    // 查询被假删除的文章信息
    public BaseContentEO getRemoved(Long id);

    // 查询回收站列表
    public Pagination getRecycleContentPage(UnAuditContentsVO contentVO);

    /**
     * @param pageSize
     * @param pageIndex
     * @param columnId
     * @return Pagination return type
     * @throws
     * @Title: getStaticPage
     * @Description: 生成静态分页
     */
    Pagination getStaticPage(Long pageIndex, Integer pageSize, Long columnId, Long siteId);

    // 未审核的条数
    public Long noAuditCount(Long siteId, String typeCode, List<Long> columnIds);

    /**
     * 查询上一条和下一条记录，已发布的或者所有的
     *
     * @param columnId
     * @param siteId
     * @param typeCode
     * @param contentId
     * @param allOrPublish
     * @return
     * @author fangtinghua
     */
    BaseContentVO getLastNextVO(Long columnId, Long siteId, String typeCode, Long contentId, boolean allOrPublish);

    public void recovery(Long[] ids);

    /**
     * @param typeCode
     * @param siteId
     * @return List<BaseContentEO> return type
     * @throws
     * @Title: getContents
     * @Description: 获取新闻列表
     */
//    List<BaseContentEO> getContents(String typeCode, Long siteId);

    /**
     * @param typeCode
     * @return List<BaseContentEO> return type
     * @throws
     * @Title: getContents
     * @Description: 获取新闻
     */
//    List<BaseContentEO> getContents(String typeCode);

    public List<String> getExistTypeCode(Long siteId);

    public BaseContentEO getContent(Long id, String status);

    public List<BaseContentEO> getContentsByIds(Long[] ids);

    /**
     * @param title
     * @param typeCode
     * @param siteId
     * @param pageIndex
     * @param pageSize
     * @return Pagination return type
     * @throws
     * @Title: getContentForWeChat
     * @Description: 提供微信接口获取新闻
     */
    public Pagination getContentForWeChat(String title, String typeCode, Long siteId, Long pageIndex, Integer pageSize);

    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO, String ids);

    public Long getCountChart(ContentChartQueryVO queryVO);

    public List<WordListVO> getWordList(StatisticsQueryVO queryVO);

    public List<WordListVO> getWordListByColumn(StatisticsQueryVO queryVO);

    public List<WordListVO> getWordList1(StatisticsQueryVO queryVO);

    public Pagination getWordPage(StatisticsQueryVO vo);

    /**
     * @param columnId
     * @param isPublish
     * @param st
     * @param ed
     * @return Long return type
     * @throws
     * @Title: getCountByCondition
     * @Description:根据条件统计新闻数量
     */
    Long getCountByCondition(Long columnId, Integer isPublish, Date st, Date ed, String recordStatus);

    List<ContentTjVO> getCountByCondition(Long siteId, String typeCode, Integer isPublish, Date st, Date ed);

    Pagination getQueryPage(ContentPageVO pageVO, Long[] optColumns);

    List<BaseContentEO> deleteList(Long columnId);

    List<BaseContentVO> getCounts(ContentPageVO vo, Integer limit);

    /**
     * @param pageVO
     * @return Pagination
     * @throws
     * @Title: getPageByMobile
     * @Description:手机APP新闻列表
     */
    Pagination getPageByMobile(ContentPageVO pageVO);

    /**
     * 根据单位id获取新闻列表
     *
     * @param organIds
     * @param siteId
     * @param typeCode
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Pagination getPageByOrganIds(List<Long> organIds, Long siteId, String typeCode, Long pageIndex, Integer pageSize);

    List<Object> getStatisticCounts(List<Long> ids);

    List<BaseContentEO> getList(ContentPageVO pageVO);

    List<BaseContentEO> getBaseContents(Long siteId, Long columnId);

    /**
     * 根据类型获取条数
     *
     * @param siteId
     * @param columnId
     * @param type
     * @param isPublish
     * @return
     */
    Long getCountByTypeAndStatus(Long siteId, Long columnId, String type, Integer isPublish);

    /**
     * 获取内容
     *
     * @param siteId
     * @param title
     * @return
     */
    List<BaseContentEO> getBaseContents(Long siteId, String title);

    void deleteNewsByColumnId(Long columnId);

    List<BaseContentEO> getWorkGuidXYContent(Long siteId, Long columnId);

    /**
     * 获取文字，图片，视频，在规定时间的新增，发布，修改的数量
     * @param vo
     * @return
     */
    Long getSummaryCount(StatisticsQueryVO vo);


    /*获取栏目下的创建单位*/
    List<OrganEO> getCreateOrganByColumnIds(List<Long> columnIds, Long siteId);

    /*统计栏目下创建单位的发文数量*/
    List<Map<String,Object>> statisticsByColumnIdsAndOrganId(StatisticsQueryVO queryVO);
}