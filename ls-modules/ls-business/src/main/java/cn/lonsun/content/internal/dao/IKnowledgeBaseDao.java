package cn.lonsun.content.internal.dao;

import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * @author zhangmf
 * @version 2018-05-23 17:27
 */
public interface IKnowledgeBaseDao extends IBaseDao<KnowledgeBaseEO> {
    Pagination getPage(ContentPageVO query);

    KnowledgeBaseEO getKnowledgeBaseByContentId(Long contentId);

    List<KnowledgeBaseVO> getKnowledgeBaseVOS(String code);

    void batchCompletelyDelete(Long[] contentIds);
}
