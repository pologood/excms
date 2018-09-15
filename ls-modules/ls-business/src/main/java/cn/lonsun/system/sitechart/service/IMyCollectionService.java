package cn.lonsun.system.sitechart.service;


import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;

/**
 * Created by hu on 2016/8/15.
 */
public interface IMyCollectionService extends IBaseService<MyCollectionEO> {
    Pagination getPage(MyCollectionVO query);
}
