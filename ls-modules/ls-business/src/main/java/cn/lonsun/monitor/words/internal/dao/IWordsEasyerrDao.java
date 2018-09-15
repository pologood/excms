package cn.lonsun.monitor.words.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;

import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
public interface IWordsEasyerrDao extends IBaseDao<WordsEasyerrEO> {


    Object getPageEOList(ParamDto paramDto);

    public void delEO(Long id);

    public List<WordsEasyerrEO> getEOs(ParamDto paramDto);

    Pagination getPage(ParamDto paramDto);

    public List<WordsEasyerrEO> getEOs();

    public Object getEOById(Long id);

    public WordsEasyerrEO getEOByWords(String words);

    public void addEO(WordsEasyerrEO eo);

    public void editEO(WordsEasyerrEO eo);

    public Map<String,Object> getMaps();

    public WordsEasyerrEO getCurSiteHas(Long siteId, String words);

    public void delEOByProvenance(String provenance, Long siteId);

    public int deleteByWords(String words, Long siteId);

    public int deleteByWords(List<WordsEasyerrEO> list, Long siteId);

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    List<MonitorSiteConfigVO> getSiteRegisterInfos(Long siteId);
}
