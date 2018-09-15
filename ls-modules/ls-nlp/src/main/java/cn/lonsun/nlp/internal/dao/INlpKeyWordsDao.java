package cn.lonsun.nlp.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsEO;

import java.util.List;

/**
 * 关键词
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpKeyWordsDao extends IBaseDao<NlpKeyWordsEO>{
    /**
     * 根据关键词查询记录
     * @param keyword
     * @return
     */
    NlpKeyWordsEO getEOByKeyword(String keyword);

    /**
     * 根据关键词查询记录
     * @param keyword
     * @return
     */
    List<NlpKeyWordsEO> getEOsByKeyword(List<String> keyword);
}
