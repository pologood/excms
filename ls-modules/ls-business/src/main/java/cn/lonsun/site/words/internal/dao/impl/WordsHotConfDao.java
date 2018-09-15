package cn.lonsun.site.words.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.site.words.internal.dao.IWordsHotConfDao;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2015-9-1 8:59
 */
@Repository("wordsHotConfDao")
public class WordsHotConfDao extends BaseDao<WordsHotConfEO> implements IWordsHotConfDao {

    @Override
    public Object getEOList() {
        return this.getEntitiesByHql("from WordsHotConfEO", new Object[]{});
    }

    @Override
    public Object getPageListByTplId(ParamDto paramDto) {

        Long pageIndex = paramDto.getPageIndex();
        Integer pageSize = paramDto.getPageSize();
        Long siteId = LoginPersonUtil.getSiteId();
        String hql = "from WordsHotConfEO where 1=1 and siteId in (" + siteId + ",-1)";
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql, paramDto), new Object[]{});
    }

    @Override
    public Object getEOById(Long id) {
        return this.getEntityByHql("from WordsHotConfEO T where T.id = ?", new Object[]{id});
    }

    @Override
    public WordsHotConfEO getEOByWords(String words) {
        return this.getEntityByHql("from WordsHotConfEO T where T.hotName = ?", new Object[]{words});
    }

    @Override
    public Object getEOByTypeId(Long id) {
        return this.getEntitiesByHql("from WordsHotConfEO T where T.hotTypeId = ?", new Object[]{id});
    }

    @Override
    public void addEO(WordsHotConfEO eo) {
        this.save(eo);
    }

    @Override
    public void delEO(Long id) {
        this.executeUpdateBySql("DELETE FROM CMS_WORDS_HOT_CONF WHERE ID = ?", new Object[]{id});
    }

    @Override
    public void delEOByHotId(Long id) {
        this.executeUpdateBySql("DELETE FROM CMS_WORDS_HOT_CONF WHERE HOT = ?", new Object[]{id});
    }

    @Override
    public void editEO(WordsHotConfEO eo) {
        WordsHotConfEO nEo = (WordsHotConfEO) this.getEOById(eo.getId());

        nEo.setHotName(eo.getHotName());
        nEo.setHotUrl(eo.getHotUrl());
        nEo.setHotTypeId(eo.getHotTypeId());
        nEo.setOpenType(eo.getOpenType());
        nEo.setUrlDesc(eo.getUrlDesc());

       this.update(nEo);
    }

    @Override
    public Map<String, Object> getMaps() {

        List<WordsHotConfEO> list = this.getEntitiesByHql("from WordsHotConfEO", new Object[]{});
        Map<String, Object> map = new HashMap<String, Object>(list.size());

        for(WordsHotConfEO eo : list)
            map.put(eo.getSiteId() + "_" + eo.getHotName(),eo);

        return map;
    }

    @Override
    public WordsHotConfEO getCurSiteHas(Long siteId, String words) {
        String hql = "from WordsHotConfEO T where T.siteId = ? and T.hotName = ?";
        return this.getEntityByHql(hql, new Object[]{siteId,words});
    }
}