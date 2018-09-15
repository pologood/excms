package cn.lonsun.datacollect.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IHtmlCollectDataDao;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.site.template.util.SqlHelper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("htmlCollectDataDao")
public class HtmlCollectDataDao extends MockDao<HtmlCollectDataEO> implements IHtmlCollectDataDao {

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        StringBuilder hql = new StringBuilder("from HtmlCollectDataEO where 1=1 and taskId = ?");
        vo.setSortField("publishDate");
        vo.setSortOrder("desc");
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), new Object[]{vo.getTaskId()});
    }

    @Override
    public void saveData(Map<String, Object> map) {
        if(null != map) {
            String columns = "";
            String values = "";
            Object[] params = new Object[map.size()];
            int i = 0;
            for (String key : map.keySet()) {
                if("TITLE".equalsIgnoreCase(key)) {
                }
                columns += "," + key;
                values += ",?";
                params[i++] = map.get(key);
            }

            String sql = "INSERT INTO CMS_HTML_COLLECT_DATA(ID,RECORD_STATUS" + columns + ") VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'" + AMockEntity.RecordStatus.Normal.toString() + "'" + values + ")";
            this.executeUpdateBySql(sql, params);
        }
    }

    public List<HtmlCollectDataEO> getEntityByName(Long taskId,String title) {
        return this.getEntitiesByHql("from HtmlCollectDataEO where taskId=? and title=?",new Object[]{taskId,title});
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        this.executeUpdateByHql("delete from HtmlCollectDataEO where taskId = ?",new Object[]{taskId});
    }

    @Override
    public void deleteById(Long id) {
        this.executeUpdateByHql("delete from HtmlCollectDataEO where id = ?",new Object[]{id});
    }
}
