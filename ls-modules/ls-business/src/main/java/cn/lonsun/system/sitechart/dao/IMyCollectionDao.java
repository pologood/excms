package cn.lonsun.system.sitechart.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;

/**
 * Created by hu on 2016/8/15.
 */
public interface IMyCollectionDao extends IBaseDao<MyCollectionEO> {
    Pagination getPage(MyCollectionVO query);
}
