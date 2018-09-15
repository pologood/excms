package cn.lonsun.site.words.internal.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.words.internal.dao.IWordsHotConfDao;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.service.IWordsHotConfService;

/**
 * @author gu.fei
 * @version 2015-9-1 9:00
 */
@Service("wordsHotConfService")
public class WordsHotConfService extends BaseService<WordsHotConfEO> implements IWordsHotConfService {

    @Autowired
    private IWordsHotConfDao wordsHotConfDao;

    @Override
    public Object getEOList() {
        return wordsHotConfDao.getEOList();
    }

    @Override
    public Object getPageListByTplId(ParamDto paramDto) {
        return wordsHotConfDao.getPageListByTplId(paramDto);
    }

    @Override
    public Object getEOById(Long id) {
        return wordsHotConfDao.getEOById(id);
    }

    @Override
    public WordsHotConfEO getEOByWords(String words) {
        return wordsHotConfDao.getEOByWords(words);
    }

    @Override
    public Object getEOByTypeId(Long id) {
        return wordsHotConfDao.getEOByTypeId(id);
    }

    @Override
    public void addEO(WordsHotConfEO eo) {
        wordsHotConfDao.addEO(eo);
    }

    @Override
    public void delEO(Long id) {
        wordsHotConfDao.delEO(id);
    }

    @Override
    public void editEO(WordsHotConfEO eo) {
        wordsHotConfDao.editEO(eo);
    }

    @Override
    public Map<String, Object> getMaps() {
        return wordsHotConfDao.getMaps();
    }

    @Override
    public WordsHotConfEO getCurSiteHas(Long siteId, String words) {
        return wordsHotConfDao.getCurSiteHas(siteId,words);
    }
}
