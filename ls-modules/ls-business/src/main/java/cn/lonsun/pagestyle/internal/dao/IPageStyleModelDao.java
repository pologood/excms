package cn.lonsun.pagestyle.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import cn.lonsun.pagestyle.internal.entity.PageStyleModelEO;

public interface IPageStyleModelDao extends IBaseDao<PageStyleModelEO> {

    public void deleteByStyle(Long styleId);
}
