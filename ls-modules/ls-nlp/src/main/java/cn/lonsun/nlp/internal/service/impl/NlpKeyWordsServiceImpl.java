package cn.lonsun.nlp.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsEO;
import cn.lonsun.nlp.internal.service.INlpKeyWordsService;
import cn.lonsun.nlp.utils.NlpirUtil;
import cn.lonsun.nlp.utils.ReadConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关键词
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Service
public class NlpKeyWordsServiceImpl extends BaseService<NlpKeyWordsEO> implements INlpKeyWordsService {

    @Autowired
    private INlpKeyWordsDao nlpKeyWordsDao;

    /**
     * 分析并保存新的关键词
     * @param content
     * @return
     */
    @Override
    public List<Long> analyseAndSaveWord(String content) {
        int num = 0;
        try {
            num = Integer.parseInt(ReadConfigUtil.getValue("word_num"));//单篇文章的关键词数
        }catch (Exception e){
            e.printStackTrace();
        }
        List<Long> wordIds = new ArrayList<Long>();
        List<String> keyWordList = NlpirUtil.getKeyWords(content,num);
        if(keyWordList!=null&&keyWordList.size()>0){
            List<NlpKeyWordsEO> keyWordsEOS =  nlpKeyWordsDao.getEOsByKeyword(keyWordList);
            Map<String,Long> existWords = new HashMap<String,Long>();
            if(keyWordsEOS!=null&&keyWordsEOS.size()>0){
                for(NlpKeyWordsEO wordsEO:keyWordsEOS){
                    existWords.put(wordsEO.getName(),wordsEO.getId());
                }
            }
            for(String keyword:keyWordList){
                if(!AppUtil.isEmpty(keyword)){
                    if(existWords.containsKey(keyword)){//该关键词已存在
                        wordIds.add(existWords.get(keyword));//将id返回，以便做对应关系
                    }else{
                        NlpKeyWordsEO keyWordsEO = new NlpKeyWordsEO();
                        keyWordsEO.setName(keyword);
                        Long id = this.saveEntity(keyWordsEO);
                        wordIds.add(id);//将id返回，以便做对应关系;
                    }
                }
            }
        }
        return wordIds;
    }
}
