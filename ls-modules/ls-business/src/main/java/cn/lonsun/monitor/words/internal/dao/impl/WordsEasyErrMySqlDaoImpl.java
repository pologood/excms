package cn.lonsun.monitor.words.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.words.internal.dao.IWordsEasyerrDao;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.util.SqlHelper;
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
@Repository("wordsEasyErrMySqlDao")
public class WordsEasyErrMySqlDaoImpl extends WordsEasyErrDaoImpl {


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
        if(paramDto.getSiteId() != null && paramDto.getSiteId() != 0l){
            hql.append(" and (t.siteId = ? or t.siteId = -1 )");
            values.add(paramDto.getSiteId());
        }
        if(StringUtils.isNotEmpty(paramDto.getKeyValue())){
            hql.append(" and (t.words like ? or t.replaceWords like ? )");
            String content = "%"+ paramDto.getKeyValue() + "%";
            values.add(content);
            values.add(content);
        }
        //如果是0则增加到查询条件，否则做为空兼容性处理
        if(StringUtils.isNotEmpty(paramDto.getSeriousErr())){
            if(paramDto.getSeriousErr().equals("0")){
                hql.append(" and t.seriousErr = 0 ");
            }else{
                hql.append(" and (t.seriousErr = 1 or t.seriousErr is null) ");
            }
        }
        if(StringUtils.isNotEmpty(paramDto.getProvenance())){
            hql.append(" and t.provenance = ?");
            values.add(paramDto.getProvenance());
        }
        if(StringUtils.isNotEmpty(paramDto.getSortField())){
            if("createDate".equals(paramDto.getSortField())){
                hql.append(" order by createDate ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder())?paramDto.getSortOrder():" desc")
                        .append(", t.words asc");
            }else {
                hql.append(" order by t.").append(paramDto.getSortField()).append(" ")
                        .append(StringUtils.isNotEmpty(paramDto.getSortOrder()) ? paramDto.getSortOrder() : " desc");
            }
        }else{
            hql.append(" order by t.words asc, t.createDate desc");
        }
        return getPagination(pageIndex,pageSize,hql.toString(),values.toArray());
        // return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql, paramDto), new Object[]{});
    }

    @Override
    public Pagination getPage(ParamDto paramDto){
        StringBuffer hql = new StringBuffer();
        List<Object> values = new ArrayList<Object>();
        hql.append("from WordsEasyerrEO t where 1=1");
        //站点私有词库和云平台词库
        if(paramDto.getSiteId() != null && paramDto.getSiteId() != 0l){
            hql.append(" and (t.siteId = ? or t.siteId = -1 )");
            values.add(paramDto.getSiteId());
        }
        //如果是0则增加到查询条件，否则做为空兼容性处理
        if(StringUtils.isNotEmpty(paramDto.getSeriousErr())){
            if(paramDto.getSeriousErr().equals("0")){
                hql.append(" and t.seriousErr = 0 ");
            }else{
                hql.append(" and (t.seriousErr = 1 or t.seriousErr is null) ");
            }
        }
        if(StringUtils.isNotEmpty(paramDto.getSortField())){
            hql.append(" order by t.").append(paramDto.getSortField()).append(" ")
                    .append(StringUtils.isNotEmpty(paramDto.getSortOrder())?paramDto.getSortOrder():" desc");
        }else{
            hql.append(" order by t.words asc, t.createDate desc");
        }
        return getPagination(paramDto.getPageIndex(), paramDto.getPageSize(), hql.toString(), values.toArray()) ;
    }

    @Override
    public Map<String, Object> getMaps() {

        List<WordsEasyerrEO> list = this.getEntitiesByHql("from WordsEasyerrEO", new Object[]{});
        Map<String, Object> map = new HashMap<String, Object>(list.size());

        for(WordsEasyerrEO eo : list)
            map.put(eo.getSiteId() + "_" + eo.getWords(),eo);

        return map;
    }

    @Override
    public int deleteByWords(List<WordsEasyerrEO> list, Long siteId) {
        StringBuilder sql = new StringBuilder("delete from WordsEasyerrEO t where t.siteId = ? and t.words in ");
        StringBuilder option = new StringBuilder();
        int num = 0;
        for(int i = 0, size = list.size(); i < size; i++){
            WordsEasyerrEO item = list.get(i);
            option.append("'").append(item.getWords()).append("'").append(",");
            if(i != 0 && i % 100 == 0){
                num += executeUpdateByHql(sql.toString() +" ( "+ option.substring(0,option.length() -1) + " ) ", new Object[]{siteId});
                option = new StringBuilder();
            }
        }
        if(option.length() > 0){
            num += executeUpdateByHql(sql.toString() +" ( "+ option.substring(0,option.length() -1) + " ) ", new Object[]{siteId});
        }
        return num;
    }
}
