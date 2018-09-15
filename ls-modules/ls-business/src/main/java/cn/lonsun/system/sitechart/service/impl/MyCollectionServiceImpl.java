package cn.lonsun.system.sitechart.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.sitechart.dao.IMyCollectionDao;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.service.IMyCollectionService;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hu on 2016/8/15.
 */
@Service
public class MyCollectionServiceImpl extends BaseService<MyCollectionEO> implements IMyCollectionService{
   @Autowired
   private IMyCollectionDao myCollectionDao;
    @Override
    public Pagination getPage(MyCollectionVO query) {
        return myCollectionDao.getPage(query);
    }

}
