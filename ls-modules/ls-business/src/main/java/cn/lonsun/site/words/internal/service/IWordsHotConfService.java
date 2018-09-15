package cn.lonsun.site.words.internal.service;

import java.util.Map;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;

/**
 * @author gu.fei
 * @version 2015-9-1 9:00
 */
public interface IWordsHotConfService extends IBaseService<WordsHotConfEO> {

    public Object getEOList();

    public Object getPageListByTplId(ParamDto paramDto);

    public Object getEOById(Long id);

    public WordsHotConfEO getEOByWords(String words);

    public Object getEOByTypeId(Long id);

    public void addEO(WordsHotConfEO eo);

    public void delEO(Long id);

    public void editEO(WordsHotConfEO eo);

    public Map<String,Object> getMaps();

    public WordsHotConfEO getCurSiteHas(Long siteId,String words);

}
