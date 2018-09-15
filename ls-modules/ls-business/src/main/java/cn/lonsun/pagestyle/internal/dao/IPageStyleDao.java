package cn.lonsun.pagestyle.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;

public interface IPageStyleDao extends IMockDao<PageStyleEO> {


    public PageStyleEO getStyleByModel(String modelCode);
}
