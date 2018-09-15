package cn.lonsun.weibo.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;

/**
 * @author gu.fei
 * @version 2015-12-9 13:56
 */
public interface IWeiboRadioContentDao extends IBaseDao<WeiboRadioContentEO> {

    public WeiboRadioContentEO getByType(String type);

    public Pagination getPageEOs(ParamDto dto);

    public void deleteRadioEO(String[] weiboIds, String type);
}
