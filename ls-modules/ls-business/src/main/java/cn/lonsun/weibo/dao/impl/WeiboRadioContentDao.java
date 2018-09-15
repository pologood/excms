package cn.lonsun.weibo.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.SqlHelper;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.dao.IWeiboRadioContentDao;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;

/**
 * @author gu.fei
 * @version 2015-12-8 17:12
 */
@Repository
public class WeiboRadioContentDao extends BaseDao<WeiboRadioContentEO> implements IWeiboRadioContentDao {

    @Override
    public WeiboRadioContentEO getByType(String type) {
        return this.getEntityByHql("from WeiboRadioContentEO where type = ?",new Object[]{type});
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Long pageIndex = dto.getPageIndex();
        Integer pageSize = dto.getPageSize();
        StringBuffer hql = new StringBuffer(" from WeiboRadioContentEO where 1=1 and type = ? and siteId = ? ");

        return this.getPagination(pageIndex, pageSize, SqlHelper.getSearchAndOrderSql(hql.toString(), dto), new Object[]{dto.getType(), LoginPersonUtil.getSiteId()});
    }

    @Override
    public void deleteRadioEO(String[] weiboIds, String type) {
        String weiboIdsn = null;
        for(String weiboId : weiboIds) {
            if(null == weiboIdsn) {
                weiboIdsn = weiboId;
            } else {
                weiboIdsn += "," + weiboId;
            }
        }
        this.executeUpdateBySql("DELETE FROM CMS_WB_RADIO_CONTENT WHERE WEIBO_ID in ("+weiboIdsn+") AND TYPE = ?",new Object[]{type});
    }
}
