package cn.lonsun.site.site.internal.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.vo.ColumnVO;

import java.util.List;
import java.util.Map;


/**
 * 栏目配置Dao层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
public interface IColumnConfigDao extends IMockDao<ColumnConfigEO> {
    /**
     * 获取序号
     * @param parentId
     * @return
     */
    public Integer getNewSortNum(Long parentId,boolean isCom) ;

    /**
     * 根据栏目ID获取栏目配置信息
     * @param indicatorId
     * @return
     */
    public ColumnConfigEO getColumnConfigByIndicatorId(Long indicatorId);

    /**
     * 根据栏目类型获取指定站点下的栏目树
     * 1、为复制文章提供接口
     * @param siteIds
     * @param columnId
     * @param columnTypeCode
     * @param flag
     * @return
     */
    public List<ColumnMgrEO> getColumnTreeByType(Long[] siteIds,Long columnId,String columnTypeCode,Boolean flag);

    /**
     * 供权限管理查看树
     *
     * @param siteId
     * @return
     */
    public List<ColumnVO> getTree(Long siteId);

    /**
     * 权限管理：获取所有的站点和栏目
     * @return
     */
    public List<ColumnMgrEO> getTree();
    /**
     * 根据主表Id数组获取站点和栏目
     * 1、为角色管理提供接口
     * @param idStr
     * @return
     */
    public List<ColumnMgrEO> getColumnByIds(String idStr);

    /**
     * 根据站点ID获取站点信息
     * @param idStr
     * @return
     */
    public List<ColumnMgrEO> getSiteByIds(String idStr);
    /**
     * 根据站点ID查询站点下的栏目树（异步加载）
     * 1、为内容协同提供接口
     * @param indicatorId
     * @return
     */
    public List<ColumnVO> getColumnTreeBySite(Long indicatorId);
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
     * 根据栏目类型code值获取某站点下的栏目
     * @param siteId
     * @param code
     * @return
     */
    public List<ColumnMgrEO> getColumnByTypeCode(Long siteId, String code);

    /**
     * 根据内容模型code值获取某站点或栏目下的子栏目
     * @param indicatorId
     * @param codes
     * @return
     */
    public List<ColumnMgrEO> getColumnByModelCodes(Long indicatorId, String codes);

    /**
     * 获取层级的栏目树
     * @param indicatorId
     * @param level
     * @param condition
     * @param vo
     * @return
     */
    public List<ColumnMgrEO> getLevelColumnTree(Long indicatorId,  int[] level, String condition,PageQueryVO vo);

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
    public List<ColumnMgrEO> getChildColumn(Long indicatorId, int[] parentFlag);

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
     * 根据父栏目获取所有子栏目 flag:true（本站点及站点为空的）
     * @param indicatorId
     * @param flag
     * @param siteId
     * @return
     */
    public List<ColumnMgrEO> getColumnByParentId(Long indicatorId,boolean flag,Long siteId);

    /**
     * 根据站点数组  返回站点及站点下所有栏目
     * @param idStr
     * @return
     */
    public List<ColumnMgrEO> getColumnBySiteIds(String idStr);

    /**
     * 获取某个栏目下所有的栏目
     * @param columnId
     * @return
     */
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId);

    /**
     * 获取某个站点下内容模型code值为code的栏目
     * @param siteId
     * @param code
     * @return
     */
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code);

    /**
     * 获取栏目移动树
     * @param indicatorId
     * @param columnId
     * @return
     */
    public List<ColumnMgrEO> getMoveTree(Long indicatorId, Long columnId);

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
    public List<ColumnMgrEO> getByColumnTypeCodes(String[] codes, Long siteId,Boolean flag);

    /**
     * @param codes
     * @param siteId
     * @return
     */
    public List<ColumnMgrEO> getColumns(List<String> codes, Long siteId);

    public List<ColumnMgrEO> getSiteMap(String lev, Long siteId, Long columnId, String columnIds, Boolean link,Boolean isCom);

    public ColumnMgrEO getById(Long indicatorId);

    Long getSourceColumnCount(String referColumnId);

    /**
     * 查询引用目录的源栏目
     * @param referOrganCatId
     * @return
     */
    Long getSourceOrganCatCount(String referOrganCatId);
}
