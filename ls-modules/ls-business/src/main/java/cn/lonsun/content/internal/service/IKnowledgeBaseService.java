package cn.lonsun.content.internal.service;

import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

/**
 * @author zhangmf
 * @version 2018-05-23 17:26
 */
public interface IKnowledgeBaseService extends IBaseService<KnowledgeBaseEO> {
    Pagination getPage(ContentPageVO query);

    KnowledgeBaseEO saveOrupdate(KnowledgeBaseVO knowledgeBaseVO);

    void delete(Long[] ids, Long[] contentIds);

    void batchCompletelyDelete(Long[] contentIds);
}
