package cn.lonsun.nlp.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsArticleRelEO;
import cn.lonsun.nlp.internal.vo.ContentVO;

import java.util.Date;
import java.util.List;

/**
 * 关键词与文章对应关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpKeyWordsArticleRelService extends IBaseService<NlpKeyWordsArticleRelEO> {

    /**
     * 分析文章关键词
     * @param contentId
     * @param content
     */
    List<Long> analyseKeyWords(Long contentId,Long siteId, String content);

    /**
     * 根据contentId删除关键词与文章对应关系
     * @param contentId
     */
    void delByContentId(Long contentId);

    /**
     * 根据contentId获取关键词与文章对应关系
     * @param contentId
     */
    List<NlpKeyWordsArticleRelEO> getByContentId(Long contentId);

    /**
     * 根据关键词查询相应的文章
     * @param keyWordId
     * @param st
     * @param ed
     * @param num
     * @return
     */
    List<ContentVO> queryContentsByKeyWordId(Long[] keyWordId,Long siteId, Date st, Date ed, Integer num);

    /**
     * 根据关键词查询相应的文章
     * @param keyWordId
     * @param siteId
     * @param st
     * @param ed
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pagination queryContentPage(Long[] keyWordId, Long siteId, Date st, Date ed, Long pageIndex,Integer pageSize);
}
