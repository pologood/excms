package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBaseContentService extends IMockService<BaseContentEO> {
    /**
     * 图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
     *
     * @param eo
     * @return
     */
    public BaseContentEO getImageLinkFromContent(BaseContentEO eo);

    /**
     * @param pageVO
     * @return
     * @Description 获取新闻分页
     * @author Hewbing
     * @date 2015年10月20日 上午8:25:51
     */
    public Pagination getPage(ContentPageVO pageVO);

    /**
     * @param pageVO
     * @return
     * @Description 获取新闻分页通过父节点
     * @author Hewbing
     * @date 2015年10月20日 上午8:25:51
     */
    public Pagination getPageByParentId(String columnIds,ContentPageVO pageVO);

    /**
     * @param files
     * @param rootPath
     * @return
     * @Description 上传
     * @author Hewbing
     * @date 2015年10月20日 上午8:26:22
     */
    String uploadFile(MultipartFile[] files, String rootPath);

    /**
     * @param pageVO
     * @Description 更改发布状态
     * @author Hewbing
     * @date 2015年10月20日 上午8:26:36
     */
    public void changePublish(ContentPageVO pageVO);

    /**
     * @param columnId
     * @return
     * @Description 根据栏目iD查询记录条数
     * @author Hewbing
     * @date 2015年9月22日 下午2:01:01
     */
    public Long getCountByColumnId(Long columnId);

    /**
     * @param siteId
     * @return
     * @Description 根据站点ID查询记录条数
     * @author Hewbing
     * @date 2015年9月22日 下午2:01:46
     */
    public Long getCountBySiteId(Long siteId);

    /**
     * @param ids
     * @param status
     * @Description 更改置顶状态
     * @author Hewbing
     * @date 2015年10月20日 上午8:26:56
     */
    public void changeTopStatus(Long[] ids, Integer status);

    /**
     * @param ids
     * @param status
     * @Description 更改加热状态
     * @author Hewbing
     * @date 2015年10月20日 上午8:27:16
     */
    public void changeHotStatus(Long[] ids, Integer status);

    /**
     * @param id
     * @param imgPath
     * @Description 修改缩略图
     * @author Hewbing
     * @date 2015年10月9日 下午3:39:42
     */
    public int changeImg(Long id, String imgPath);

    /**
     * @param columnIds
     * @param contentId
     * @return
     * @Description 保存新闻复制
     * @author Hewbing
     * @date 2015年10月15日 下午2:10:16
     */
    public String saveCopy(String[] columnIds, Long contentId, BaseContentEO baseEO, String modelCode, String synColumnIsPublishs,Integer isColumnOpt);

    /**
     * @param @param ids
     * @param @param status 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeTitltStatus
     * @Description: 标题新闻状态
     */
    public void changeTitltStatus(Long[] ids, Integer status);

    /**
     * @param @param ids
     * @param @param status 设定文件
     * @return void 返回类型
     * @throws
     * @Title: changeNewStatus
     * @Description: 加新状态
     */
    public void changeNewStatus(Long[] ids, Integer status);

    /**
     * @param columnId
     * @return SortVO 返回类型
     * @throws
     * @Title: getMaxNumByColumn
     * @Description: 根据栏目查找最大排序
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
     * @Description: 获取下一排序
     */
    public SortVO getNextSort(String opr, Long sortNum, ContentPageVO pageVO);

    /**
     * @param opr
     * @param sortNum
     * @param oprSort
     * @param pageVO  设定文件
     * @return void 返回类型
     * @throws
     * @Title: contentSort
     * @Description:排序
     */
    public Long contentSort(String opr, Long sortNum, Long oprSort, ContentPageVO pageVO);

    /**
     * @param pageVO
     * @return Pagination 返回类型
     * @throws
     * @Title: getContentPage
     * @Description: 获取列表及内容
     */
    public Pagination getPageAndContent(ContentPageVO pageVO);

    public Long saveArticleNews(BaseContentEO contentEO, String content, Long[] synCloumnIds, Long synMsgCatIds, String publicSynOrganCatIds,
                                String publicSynOrganCatNames);


    /**
     * 同步新闻信息
     *
     * @param columnId
     * @param copyReferVO
     */
    public String synCloumnInfos(Long columnId, CopyReferVO copyReferVO);


    /**
     * 同步修改引用新闻信息
     * @param contentEO
     * @param isPublish
     * @return
     */
    String synEditReferNews(BaseContentEO contentEO,String content, Integer isPublish);


    /**
     * @param ids 设定文件
     * @return void 返回类型
     * @throws
     * @Title: removeArticle
     * @Description: 删除文章新闻
     */
    public String delContent(Long[] ids);

    /**
     * 批量修改发布状态
     * @param ids
     * @param isPublish
     * @return
     */
    public String publishs(Long[] ids,Integer isPublish);

    public Long setHit(Long id);

    public Map<String, BaseContentEO> getPageLink(Long id);

    /**
     * @param contentVO
     * @return Pagination return type
     * @throws
     * @Title: getUnAuditContents
     * @Description: 获取未审核内容
     */
    public Pagination getUnAuditContents(UnAuditContentsVO contentVO);

    /**
     * 获取所有未办理的栏目类型ID
     *
     * @param contentVO
     * @return
     */
    public List<ColumnTypeVO> getUnAuditColumnIds(UnAuditContentsVO contentVO);

    /**
     * for zc
     *
     * @param query
     * @return
     */
    public Pagination getPageBySortNum(QueryVO query);

    public void updateSort(SortUpdateVO sortVo);

    public Long getMaxSortNum(Long siteId, Long columnId, String typeCode);

    public Pagination getOpenCommentContent(ContentPageVO pageVO);

    // 查询被假删除的文章信息
    public BaseContentEO getRemoved(Long id);

    // 查询回收站列表
    public Pagination getRecycleContentPage(UnAuditContentsVO contentVO);

    // 查找待审核条数
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

    /**
     * 恢复数据
     *
     * @param ids
     */
    public void recovery(Long[] ids);

    /**
     * @param typeCode
     * @param siteId
     * @return List<BaseContentEO> return type
     * @throws
     * @Title: getContents
     * @Description:
     */
    List<BaseContentEO> getContents(String typeCode, Long siteId);

    List<BaseContentEO> getContents(String typeCode);

    /**
     * @param ids Parameter
     * @return void return type
     * @throws
     * @Title: removeArticleNews
     * @Description: 物理删除文章新闻
     */
    void removeArticleNews(Long[] ids);

    /**
     * @param ids Parameter
     * @return void return type
     * @throws
     * @Title: removePictrueNews
     * @Description: 物理删除图片新闻
     */
    void removePictrueNews(Long[] ids);

    /**
     * 物理删除视频信息
     *
     * @param ids
     */
    void removeVideoNews(Long[] ids);

    /**
     * 物理删除基础表数据
     *
     * @param ids
     */
    void removeBaseContent(Long[] ids);

    /**
     * 获取存在的编码
     *
     * @return
     */
    public List<String> getExistTypeCode(Long siteId);

    public BaseContentEO getContent(Long id, String status);

    public Pagination getContentForWeChat(String title, String typeCode, Long siteId, Long pageIndex, Integer pageSize);

    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO);

    public Long getCountChart(ContentChartQueryVO queryVO);

    public Pagination getWordPage(StatisticsQueryVO vo);

    public Pagination getWordPageByColumn(StatisticsQueryVO vo);

    public List<WordListVO> getWordList(StatisticsQueryVO vo);

    public List<WordListVO> getWordListByColumn(StatisticsQueryVO vo);

    /**
     * @param columnIds
     * @param contentId
     * @param modelCode
     * @return boolean return type
     * @throws
     * @Title: saveReferNews
     * @Description: 保存关联关系
     */
    String saveReferNews(String[] columnIds, Long contentId, String modelCode, String synColumnIsPublishs,Integer isColumnOpt);

    /**
     * @param columnId     栏目ID
     * @param isPublish    是否发布
     * @param st           查询开始时间
     * @param ed           查询结束时间
     * @param recordStatus 记录状态
     * @return Long return type
     * @throws
     * @Title: getCountByCondition
     * @Description: 根据条件统计新闻数目
     */
    Long getCountByCondition(Long columnId, Integer isPublish, Date st, Date ed, String recordStatus);

    List<ContentTjVO> getCountByCondition(Long siteId, String typeCode, Integer isPublish, Date st, Date ed);

    /**
     * @param pageVO
     * @return Pagination return type
     * @throws
     * @Title: getQueryPage
     * @Description: 根据条件查询内容
     */
    Pagination getQueryPage(ContentPageVO pageVO);

    /**
     * 新闻复制引用
     *
     * @param copyReferVO
     * @author fangtinghua
     */
    String copyRefer(CopyReferVO copyReferVO);

    /**
     * 新闻移动
     *
     * @param copyReferVO
     * @author fangtinghua
     */
    String moveNews(CopyReferVO copyReferVO);

    public List<BaseContentEO> deleteList(Long columnId);

    List<BaseContentVO> getCounts(ContentPageVO vo, Integer limit);

    /**
     * @param pageVO
     * @return Pagination
     * @throws
     * @Title: getPageByMobile
     * @Description:手机APP新闻列表
     */
    public Pagination getPageByMobile(ContentPageVO pageVO);

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

    /**
     * 更新排序号
     *
     * @param ids
     * @param nums
     */
    void updateNums(Long[] ids, Long[] nums);

    /**
     * 栏目id 统计所有文字
     *
     * @param ids
     * @return
     */
    Map<Long, Long> getStatisticCounts(List<Long> ids);

    /**
     * 统计栏目设置
     *
     * @param list
     */
    List<ColumnMgrEO> getStatisticsCount(List<ColumnMgrEO> list);

    List<BaseContentEO> getList(ContentPageVO pageVO);

    /**
     * 获取所有   时间排序
     *
     * @param siteId
     * @param columnId
     * @return
     */
    List<BaseContentEO> getBaseContents(Long siteId, Long columnId);

    /**
     * 根据类型获取条数
     *
     * @param type
     * @param isPublish
     * @return
     */
    Long getCountByTypeAndStatus(Long siteId, String type, Integer isPublish);

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

    List<WordListVO> getEmptyColumnList(StatisticsQueryVO vo);

    int getEmptyColumnCount(StatisticsQueryVO vo);

    /**
     * 获取文字，图片，视频，在规定时间的新增，发布，修改的数量
     * @param vo
     * @return
     */
    Long getSummaryCount(StatisticsQueryVO vo);


    /**
     * 查询引用栏目
     * @param referColumnIds
     * @param referOrganCatIds
     * @param sourceColumnIdStr  用于判断是否存在循环引用
     * @return
     */
    Map<String,String> getReferColumnCats(String referColumnIds,String referOrganCatIds,String sourceColumnIdStr);

    /*获取栏目下的创建单位*/
    List<OrganEO> getCreateOrganByColumnIds(List<Long> columnIds, Long siteId);

    /*统计栏目下创建单位的发文数量*/
    List<Map<String,Object>> statisticsByColumnIdsAndOrganId(StatisticsQueryVO queryVO);
}