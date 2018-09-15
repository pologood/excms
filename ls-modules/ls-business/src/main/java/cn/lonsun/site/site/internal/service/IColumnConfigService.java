package cn.lonsun.site.site.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.vo.ColumnVO;

import java.util.List;
import java.util.Map;

/**
 *栏目配置service层 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */

public interface IColumnConfigService extends IMockService<ColumnConfigEO>{
   /**
    * 校验一个站点下栏目名称是否重复
    * @param name
    * @param parentId
    * @param indicatorId
    * @return
    */
   public  boolean checkColumnNameExist(String name,Long parentId,Long indicatorId);

   /**
    * 保存
    * @param columnVO
    */
   public Long saveEO(ColumnMgrEO columnVO);

   /**
    * 删除
    * @param indicatorId
    */
   public void deleteEO(Long indicatorId);

   /**
    * 获取排序号
    * @param parentId
    * @return
    */
   public Integer getNewSortNum(Long parentId,boolean isCom);


   /**
    * 获取栏目树的结构（异步加载）
    * 1、给栏目管理提供栏目树
    * @param
    * @return
    */
   public List<ColumnMgrEO> getColumnTree(Long indicatorId,String name);


   /**
    *  根据栏目类型获取该站点下的栏目树
    * 1、为复制文章提供接口
    * @param siteIds
    * @param columnId
    * @param columnTypeCode
    * @return
    */
   public List<ColumnMgrEO> getColumnTreeByType(Long[] siteIds,Long columnId, String columnTypeCode,Boolean flag) ;

   /**
    * 供权限管理查看树:siteId为空:获取所有的站点栏目树
    * @param siteId
    * @return
     */
   public List<ColumnMgrEO> getTree(Long siteId);

   /**
    * 根据主表Id数组获取站点和栏目
    * @param ids
    * @return
    */
   public List<ColumnMgrEO> getTreeByIds(Long[] ids);

   /**
    * 根据站点ID查询站点下的栏目树（异步加载）
    * 1、为内容协同提供接口
    * @param indicatorId
    * @return
    */
   public List<ColumnMgrEO> getColumnTreeBySite(Long indicatorId);

   /**
    * 得到一个站点下的所有栏目
    * 1、为栏目管理的"同步到栏目"提供栏目树
    * @param name
    * @param siteId
    * @return
    */
   public List<ColumnMgrEO> getAllColumnTree(Long siteId, String name);
   /**
    * 得到一个站点下的所有栏目和该站点
    * 1、为栏目管理的"生成页面"提供栏目树
    * @param name
    * @param siteId
    * @return
    */
   public List<ColumnMgrEO> searchColumnTree(Long siteId, String name);

   /**
    * 根据站点数组  返回站点及站点下所有栏目
    * @param siteIdArr
    * @return
    */
   public List<ColumnMgrEO> getColumnBySiteIds(Long[] siteIdArr);


   /**
    * 根据站点Id和内容模型的code值查找栏目树(给网上办事提供)
    * @param siteId
    * @param code
    * @return
    */
   public  List<ColumnMgrEO> getColumnByTypeCode(Long siteId,String code);

   /**
    *根据站点Id和内容模型的code值查找栏目树(异步加载，给评论管理提供)
    * @param indicatorId
    * @param code
    * @return
    */
   public List<ColumnMgrEO> getColumnByModelCodes(Long indicatorId, String code);

   /**
    * 根据站点Id，查找允许评论的栏目树
    * @param indicatorId
    * @return
    */
   public List<ColumnMgrEO> getColumnByIsComment(Long indicatorId);

   /**
    * 获取文章类型的栏目站点树
    * @return
    */
    public List<ColumnMgrEO> getArticleTree();

   /**
    * 获取层级的栏目树
    * @param indicatorId
    * @param level
    * @param condition
    * @param vo
     * @return
     */
    public List<ColumnMgrEO> getLevelColumnTree(Long indicatorId,int[] level,String condition,PageQueryVO vo);

   /**
    * 获取父栏目下所有的栏目
    * @param indicatorId
    * @return
    */
   public List<ColumnMgrEO> getParentColumns(Long indicatorId);

   /**
    * 根据该栏目ID获取所有父级栏目ID
    * @param indicatorId
    * @return
     */
    public Map<Long,Boolean> getParentIndicatorIds(Long indicatorId);

    /**
     * 查询子节点
     * @param indicatorId
     * @param parentFlag {0,1} 查询所有 {0} 查询 isparent = 0
     * @return
     */
    public List<ColumnMgrEO> getChildColumn(Long indicatorId,int[] parentFlag);

   /**公共栏目
    * 保存
    * @param columnVO
    */
   public Long saveComEO(ColumnMgrEO columnVO);

    /**
     * 获取公共栏目树
     * @param indicatorId
     * @return
     */
   public List<ColumnVO> getComColumnTree(Long indicatorId);

   /**
    * 获取某个站点下的层级公共栏目树
    * @param siteId
    * @param i
    * @return
     */
   public List<ColumnMgrEO> getLevComColumn(Long siteId, int[] i);

    /**
     * 获取在线办事、政民互动的栏目树
     * @param indicatorId
     * @param isShow
     * @param contentModelCode
     * @return
     */
   public List<ColumnMgrEO> getVirtualColumn(Long indicatorId,boolean isShow,String[] contentModelCode);

   /**
    * 根据父栏目获取所有子栏目
    * @param indicatorId
    * @return
    */
   public List<ColumnMgrEO> getColumnByPId(Long indicatorId);

   /**
    * 删除公共栏目
    * @param indicatorId
     */
   public void deleteComEO(Long indicatorId) ;

   /**
    *根据父栏目获取所有子栏目 flag:true（本站点及站点为空的）
    * @param indicatorId
    * @param flag
    * @return
     */
   public List<ColumnMgrEO> getColumnByParentId(Long indicatorId,boolean flag);

   /**
    * 获取某个栏目下所有的栏目
    * @param columnId
    * @return
     */
   public List<ColumnMgrEO> getAllColumnBySite(Long columnId);

    /**
     * 查询某个站点下内容模型code值为code的所有栏目
     * @param siteId
     * @param code
     * @return
     */
   public  List<ColumnMgrEO> getColumnByContentModelCode(Long siteId,String code);

   /**
    * 获取栏目类型为columnTypeCode的所有栏目
    * @param columnTypeCode
    * @return
    */
   public List<ColumnMgrEO> getAllTree(String columnTypeCode);

   /**
    * 给栏目采集提供接口：获取栏目名称
    * @param columnId
    * @param siteId
    * @return
    */
   public String getNamesByKeyId(Long columnId,Long siteId);

   /**
    * 获取栏目移动树
    * @param indicatorId
    * @param columnId
    * @return
    */
   public List<ColumnMgrEO> getMoveTree(Long indicatorId,Long columnId);

   /**
    * 检查栏目下是否有内容
    * @param columnId
    * @return
    */
   public boolean isHaveContent(Long columnId);


    /**
     * 判断站点下是否有栏目
     * @param siteId
     * @return
     */
   public Boolean getIsHaveColumn(Long siteId);

   /**
    * 根据栏目类型code值获取某站点下的栏目
    * @param codes
    * @param siteId
    * @return
     */
   public  List<ColumnMgrEO> getByColumnTypeCodes(String[] codes,Long siteId,Boolean flag);

   /**根据栏目类型code值获取某站点下的栏目
    * @param codes
    * @param siteId
    * @return
    */
   public List<ColumnMgrEO> getColumns(List<String> codes, Long siteId);

   /**
    * 获取网站地图结构
    * @param lev
    * @param siteId
    * @param columnId
    * @param columnIds
    * @param link
    * @param isCom
     * @return
     */
   public List<ColumnMgrEO> getSiteMap(String lev,Long siteId,Long columnId,String columnIds,Boolean link,Boolean isCom);

   /**
    * 根据栏目ID获取栏目信息
    * @param indicatorId
    * @return
     */
   public ColumnMgrEO getById(Long indicatorId);

   /**
    * 保存导入的栏目
    * @param list 解析excel后得到的列表信息
    * @param startRow 开始行号
    * @param siteId  站点ID
    * @param columnId 要导入的栏目ID
     * @return
     */
   public  List<ColumnMgrEO> saveUploadColumn(List<ColumnMgrEO> list, int startRow,Long siteId,Long columnId);

   /**
    * 查询引用栏目的源栏目
    * @param referColumnId
    * @return
    */
   Long getSourceColumnCount(String referColumnId);

   /**
    * 查询引用目录的源栏目
    * @param referOrganCatId
    * @return
    */
   Long getSourceOrganCatCount(String referOrganCatId);

}
