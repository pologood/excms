package cn.lonsun.site.template.internal.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.dao.ITplHistoryDao;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;

/**
 * @author gu.fei
 * @version 2015-8-25 11:53
 */
@Repository("tplHistoryDao")
public class TplHistoryDao extends BaseDao<TemplateHistoryEO> implements ITplHistoryDao {

    @Override
    public Pagination getPageEOs(ParamDto paramDto) {
        Long pageIndex = paramDto.getPageIndex();
        Integer pageSize = paramDto.getPageSize();

        return this.getPagination(pageIndex, pageSize, getOrderSql("from TemplateHistoryEO T where T.tempId = ?", paramDto), new Object[]{paramDto.getId()});
    }

    public String getOrderSql(String sql,ParamDto paramDto) {

        StringBuffer sb = new StringBuffer(sql + " order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortField()) ? "createDate" : paramDto.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortOrder()) ? "desc" : paramDto.getSortOrder());

        return sb.toString();

    }

    @Override
    public Object getEOList() {
        return this.getEntitiesByHql("from TemplateHistoryEO", new Object[]{});
    }

    @Override
    public Object getEOById(Long id) {
        List<TemplateHistoryEO> list = this.getEntitiesByHql("from TemplateHistoryEO T where T.id = ?", new Object[]{id});
        if(list.size() > 0)
            return list.get(0);
        else
            return new TemplateHistoryEO();
    }

    @Override
    public Object getEOByTplId(Long id) {
        return this.getEntitiesByHql("from TemplateHistoryEO T where T.tempId = ?", new Object[]{id});
    }

    @Override
    public Object addEO(TemplateHistoryEO eo) {
        return this.save(eo);
    }

    @Override
    public Object editEO(TemplateHistoryEO eo) {
        this.update(eo);
        return null;
    }

    @Override
    public Object delEO(Long id) {
        return this.executeUpdateBySql("DELETE FROM CMS_TEMPLATE_HISTORY WHERE ID = ?",new Object[]{id});
    }

    @Override
    public Long getLastVersion(Long tempId){
        String sql = "SELECT MAX(ID) FROM CMS_TEMPLATE_HISTORY WHERE TEMP_ID = " + tempId;
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        List list = sqlQuery.list();
        return list.get(0) == null ?0L : Long.parseLong(list.get(0).toString()) ;
    }
}
