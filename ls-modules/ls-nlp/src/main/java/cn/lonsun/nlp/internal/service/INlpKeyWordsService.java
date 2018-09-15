package cn.lonsun.nlp.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsEO;

import java.util.List;

/**
 * 关键词
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpKeyWordsService extends IBaseService<NlpKeyWordsEO> {
    /**
     * 分析并保存新的关键词
     * @param content
     * @return
     */
    List<Long> analyseAndSaveWord(String content);

}
