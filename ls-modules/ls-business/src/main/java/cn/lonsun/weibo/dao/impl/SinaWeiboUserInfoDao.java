package cn.lonsun.weibo.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.weibo.dao.ISinaWeiboUserInfoDao;
import cn.lonsun.weibo.entity.SinaWeiboUserInfoEO;
import cn.lonsun.weibo.entity.WeiboType;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-25 16:38
 */
@Repository
public class SinaWeiboUserInfoDao extends MockDao<SinaWeiboUserInfoEO> implements ISinaWeiboUserInfoDao {

    @Override
    public Pagination getPageCurUser(WeiboPageVO vo) {
        Long pageIndex = vo.getPageIndex();
        Integer pageSize = vo.getPageSize();
        Map<String,Object> param = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer(" from SinaWeiboUserInfoEO where auth != :auth");
        param.put("auth",WeiboType.self.toString());
        vo.setSortField("createdAtUser");
        if(!AppUtil.isEmpty(vo.getAuth())) {
            hql.append(" and auth = :auth");
            param.put("auth",vo.getAuth());
        }
        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), vo), param);
    }

    @Override
    public List<SinaWeiboUserInfoEO> getByAuth(String auth) {
        return this.getEntitiesByHql("from SinaWeiboUserInfoEO where auth = ?", new Object[]{auth});
    }

    @Override
    public SinaWeiboUserInfoEO getByUserId(String userId) {
        return this.getEntityByHql("from SinaWeiboUserInfoEO where userId = ?",new Object[]{userId});
    }
}
