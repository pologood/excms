package cn.lonsun.pushinfo.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pushinfo.dao.IPushInfoDao;
import cn.lonsun.pushinfo.entity.PushInfoEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

/**
 * @author gu.fei
 * @version 2016-1-21 14:27
 */
@Repository("pushInfoDao")
public class PushInfoDao extends MockDao<PushInfoEO> implements IPushInfoDao {

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        StringBuilder hql = new StringBuilder("from PushInfoEO where siteId = ?");
        return this.getPagination(dto.getPageIndex(), dto.getPageSize(), SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{LoginPersonUtil.getSiteId()});
    }

    @Override
    public PushInfoEO getByPath(String path) {
        return this.getEntityByHql("from PushInfoEO where path=?",new Object[]{path});
    }

    @Override
    public void deleteEOs(Long[] ids) {
        String _ids = null;
        for(Long id : ids) {
            if(_ids == null) {
                _ids = id + "";
            } else {
                _ids += "," + id;
            }
        }

        this.executeUpdateBySql("DELETE FROM CMS_PUSH_INFO WHERE ID IN (" + _ids + ")", new Object[]{});
    }
}
