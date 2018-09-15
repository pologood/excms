package cn.lonsun.content.internal.service;

import java.util.List;

import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.vo.ContentReferRelationPageVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

public interface IContentReferRelationService extends IMockService<ContentReferRelationEO> {

    Pagination getPagination(ContentReferRelationPageVO pageVO);

    List<ContentReferRelationEO> getByCauseId(Long causeId, String modelCode, String type);

    List<ContentReferRelationEO> getByReferId(Long referId, String modelCode, String type);

    void deleteReferInfo(Long contentId);

    void delteByReferId(Long[] referIds);

    /**
     * 判断新闻是否是被引用新闻
     * @param baseContentId
     * @return
     */
    boolean checkIsRefered(Long baseContentId);

    /**
     * 判断新闻是否是引用新闻
     * @param baseContentId
     * @return
     */
    boolean checkIsRefer(Long baseContentId);

    /**
     * 查询通过上级栏目引用到当前栏目或者目录的数据
     * @param columnId
     * @param catId
     * @param pReferColumnId
     * @return
     */
    List<ContentReferRelationEO>  getByParentReferColumn(Long columnId,Long catId,Long pReferColumnId);

    /**
     * 查询通过上级目录引用到当前栏目或者目录的数据
     * @param columnId
     * @param catId
     * @param pReferColumnId
     * @return
     */
    List<ContentReferRelationEO>  getByParentReferOrganCat(Long columnId,Long catId,Long pReferColumnId,Long pReferCatId);

    /**
     * 恢复复制引用关系
     * @param ids
     */
    void recoveryByReferIds(Long[] ids);

    /**
     * 查询被引用的新闻id列表
     * @param causeId
     * @param modelCode
     * @return
     */
    List<Long> getReferedIds(Long[] causeId, String modelCode);

    /**
     * 查询被引用的新闻id列表
     * @param referId
     * @param modelCode
     * @return
     */
    List<Long> getReferIds(Long[] referId, String modelCode);
}
