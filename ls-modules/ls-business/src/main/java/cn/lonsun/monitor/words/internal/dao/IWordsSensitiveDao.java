package cn.lonsun.monitor.words.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;

import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
public interface IWordsSensitiveDao extends IBaseDao<WordsSensitiveEO>  {


    public Object getPageEOList(ParamDto paramDto);

    public List<WordsSensitiveEO> getEOs(ParamDto paramDto);

    public List<WordsSensitiveEO> getEOs();

    public Object getEOById(Long id);

    public WordsSensitiveEO getEOByWords(String words);

    public void addEO(WordsSensitiveEO eo);

    public void delEO(Long id);

    public void editEO(WordsSensitiveEO eo);

    public Map<String,Object> getMaps();

    public WordsSensitiveEO getCurSiteHas(Long siteId, String words);

    public void delEOByProvenance(String provenance, Long siteId);

    public int deleteByWords(String words);

    public int deleteByWords(List<WordsSensitiveEO> list, Long siteId);

    /**
     * 查询站点以及其日常监测开通信息
     * @return
     */
    List<MonitorSiteConfigVO> getSiteRegisterInfos(Long siteId);
}
