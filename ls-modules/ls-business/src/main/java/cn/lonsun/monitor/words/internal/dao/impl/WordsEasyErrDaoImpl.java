package cn.lonsun.monitor.words.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.words.internal.dao.IWordsEasyerrDao;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.util.SqlHelper;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
@Repository("wordsEasyErrDao")
public class WordsEasyErrDaoImpl extends BaseDao<WordsEasyerrEO> implements IWordsEasyerrDao {


    @Override
    public Object getPageEOList(ParamDto paramDto) {
        List<Object> values = new ArrayList<Object>();
        Long pageIndex = paramDto.getPageIndex();
        Integer pageSize = paramDto.getPageSize();
//        Long siteId = LoginPersonUtil.getSiteId();
        //String hql = "from WordsEasyerrEO where 1=1 and siteId in (" + siteId + ",-1)";
        StringBuffer hql = new StringBuffer("from WordsEasyerrEO t where 1=1");
        // hql.append("from WordsEasyerrEO t where 1=1");
//        hql.append(" and t.siteId in (" + siteId + ",-1)");
       /* if(paramDto.getIsInto()!=null){

        }*/
        //站点私有词库和云平台词库
        if (paramDto.getSiteId() != null && paramDto.getSiteId() != 0l) {
            hql.append(" and (t.siteId = ? or t.siteId = -1 )");
            values.add(paramDto.getSiteId());
        }
        if (StringUtils.isNotEmpty(paramDto.getKeyValue())) {
            hql.append(" and (t.words like ? or t.replaceWords like ? )");
            String content = "%" + paramDto.getKeyValue() + "%";
            values.add(content);
            values.add(content);
        }
        //如果是0则增加到查询条件，否则做为空兼容性处理
        if (StringUtils.isNotEmpty(paramDto.getSeriousErr())) {
            if (paramDto.getSeriousErr().equals("0")) {
                hql.append(" and t.seriousErr = 0 ");
            } else {
                hql.append(" and (t.seriousErr = 1 or t.seriousErr is null) ");
            }
        }
        if (StringUtils.isNotEmpty(paramDto.getProvenance())) {
            hql.append(" and t.provenance = ?");
            values.add(paramDto.getProvenance());
        }
        if (StringUtils.isNotEmpty(paramDto.getSortField())) {
            if ("words".equals(paramDto.getSortField())) {
                hql.append(" order by NLSSORT(t.words,'NLS_SORT = SCHINESE_PINYIN_M') ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc");
            }else if("createDate".equals(paramDto.getSortField())) {
                hql.append(" order by createDate ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc")
                        .append(", NLSSORT(t.words,'NLS_SORT = SCHINESE_PINYIN_M') asc ");
            }else{
                hql.append(" order by t.").append(paramDto.getSortField()).append(" ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc");
            }
        } else {
            hql.append(" order by NLSSORT(t.words,'NLS_SORT = SCHINESE_PINYIN_M') asc, t.createDate desc");
        }
        return getPagination(pageIndex, pageSize, hql.toString(), values.toArray());
        // return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql, paramDto), new Object[]{});
    }

    @Override
    public void delEO(Long id) {
        this.executeUpdateBySql("DELETE FROM MONITOR_WORDS_EASYERR_CONF WHERE ID = ?", new Object[]{id});
    }

    /**
     * 根据来源删除
     *
     * @param provenance
     */
    @Override
    public void delEOByProvenance(String provenance, Long siteId) {
        StringBuilder sql = new StringBuilder();
        List values = new ArrayList();
        sql.append("DELETE FROM MONITOR_WORDS_EASYERR_CONF WHERE PROVENANCE = ?");
        values.add(provenance);
        if(siteId != null && siteId != -1l){
            sql.append(" and site_id = ?");
            values.add(siteId);
        //如果不是删除云平台词库，则必须要传入站点id
        }else if(!WordsEasyerrEO.Provenance.Cloud.name().equals(provenance)){
            throw new BaseRunTimeException("参数缺失：siteId ");
        }
        this.executeUpdateBySql(sql.toString(), values.toArray());
    }

    @Override
    public List<WordsEasyerrEO> getEOs(ParamDto paramDto) {
        Long siteId = LoginPersonUtil.getSiteId();
        // String hql = "from WordsEasyerrEO where 1=1 and siteId in (" + siteId + ",-1)";
        StringBuilder hql = new StringBuilder("from WordsEasyerrEO where 1=1 and t.siteId = ? or (t.siteId = 0 and t.provenance = ?) ");
        return this.getEntitiesByHql(SqlHelper.getSearchAndOrderSql(hql.toString(), paramDto),
                new Object[]{paramDto.getSiteId(), WordsEasyerrEO.Provenance.Cloud.name()});
    }

    @Override
    public Pagination getPage(ParamDto paramDto) {
        StringBuffer hql = new StringBuffer();
        List<Object> values = new ArrayList<Object>();
        hql.append("from WordsEasyerrEO t where 1=1");
        //站点私有词库和云平台词库
        if (paramDto.getSiteId() != null && paramDto.getSiteId() != 0l) {
            hql.append(" and (t.siteId = ? or t.siteId = -1 )");
            values.add(paramDto.getSiteId());
        }
        //如果是0则增加到查询条件，否则做为空兼容性处理
        if (StringUtils.isNotEmpty(paramDto.getSeriousErr())) {
            if (paramDto.getSeriousErr().equals("0")) {
                hql.append(" and t.seriousErr = 0 ");
            } else {
                hql.append(" and (t.seriousErr = 1 or t.seriousErr is null) ");
            }
        }
        if (StringUtils.isNotEmpty(paramDto.getSortField())) {
            if ("words".equals(paramDto.getSortField())) {
                hql.append(" order by NLSSORT(t.words, 'NLS_SORT = SCHINESE_PINYIN_M') ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc");
            }else if("createDate".equals(paramDto.getSortField())){
                hql.append(" order by createDate ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder())?paramDto.getSortOrder():" desc")
                        .append(", NLSSORT(t.words,'NLS_SORT = SCHINESE_PINYIN_M') asc ");
            }else{
                hql.append(" order by t.").append(paramDto.getSortField()).append(" ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc");
            }
        } else {
            hql.append(" order by NLSSORT(t.words,'NLS_SORT = SCHINESE_PINYIN_M') asc, t.createDate desc");
        }
        return getPagination(paramDto.getPageIndex(), paramDto.getPageSize(), hql.toString(), values.toArray());
    }

    @Override
    public List<WordsEasyerrEO> getEOs() {
        return this.getEntitiesByHql("from WordsEasyerrEO", new Object[]{});
    }

    @Override
    public Object getEOById(Long id) {
        return this.getEntityByHql("from WordsEasyerrEO T where T.id = ?", new Object[]{id});
    }

    @Override
    public WordsEasyerrEO getEOByWords(String words) {
        return this.getEntityByHql("from WordsEasyerrEO T where T.words = ?", new Object[]{words});
    }

    @Override
    public void addEO(WordsEasyerrEO eo) {
        this.save(eo);
    }


    @Override
    public void editEO(WordsEasyerrEO eo) {
        WordsEasyerrEO nEo = (WordsEasyerrEO) this.getEOById(eo.getId());
        nEo.setWords(eo.getWords());
        nEo.setReplaceWords(eo.getReplaceWords());
        this.update(nEo);
    }

    @Override
    public Map<String, Object> getMaps() {

        List<WordsEasyerrEO> list = this.getEntitiesByHql("from WordsEasyerrEO", new Object[]{});
        Map<String, Object> map = new HashMap<String, Object>(list.size());

        for (WordsEasyerrEO eo : list)
            map.put(eo.getSiteId() + "_" + eo.getWords(), eo);

        return map;
    }

    @Override
    public WordsEasyerrEO getCurSiteHas(Long siteId, String words) {
        String hql = "from WordsEasyerrEO t where (t.siteId = ? or t.siteId = -1 ) and t.words = ?";
        return this.getEntityByHql(hql, new Object[]{siteId, words});
    }


    @Override
    public int deleteByWords(String words, Long siteId) {
        String sql = "delete from WordsEasyerrEO t where t.words = ? ";
        List<WordsEasyerrEO> list = this.getEntitiesByHql("from WordsEasyerrEO", new Object[]{});
        return executeUpdateByHql(sql, new Object[]{words});
    }

    @Override
    public int deleteByWords(List<WordsEasyerrEO> list, Long siteId) {
        StringBuilder sql = new StringBuilder("delete from WordsEasyerrEO t where t.siteId = ? and t.words in ");
        StringBuilder option = new StringBuilder();
        int num = 0;
        for (int i = 0, size = list.size(); i < size; i++) {
            WordsEasyerrEO item = list.get(i);
            option.append("'").append(item.getWords()).append("'").append(",");
            if (i != 0 && i % 100 == 0) {
                num += executeUpdateByHql(sql.toString() + " ( " + option.substring(0, option.length() - 1) + " ) ", new Object[]{siteId});
                option = new StringBuilder();
            }
        }
        if (option.length() > 0) {
            num += executeUpdateByHql(sql.toString() + " ( " + option.substring(0, option.length() - 1) + " ) ", new Object[]{siteId});
        }
        return num;
    }

    /**
     * 查询站点以及其日常监测开通信息
     *
     * @return
     */
    @Override
    public List<MonitorSiteConfigVO> getSiteRegisterInfos(Long siteId) {
        String sql = "select r.indicator_Id as siteId,r.name as siteName," +
                "m.is_registered as isRegistered,m.registered_code as registeredCode,m.registered_time as registeredTime "
                + " from cms_site_mgr r left join monitor_site_register m on r.indicator_Id = m.site_Id where 1=1 ";
        List<Object> values = new ArrayList<Object>();
        if(siteId!=null&&siteId>0){
            sql += " and r.indicator_id = ? ";
            values.add(siteId);
        }

        List<String> queryField = new ArrayList<String>();
        queryField.add("siteId");
        queryField.add("siteName");
        queryField.add("isRegistered");
        queryField.add("registeredCode");
        queryField.add("registeredTime");

        return (List<MonitorSiteConfigVO>)getBeansBySql(sql,values.toArray(),MonitorSiteConfigVO.class,queryField.toArray(new String[]{}));
    }
}