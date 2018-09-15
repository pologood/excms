package cn.lonsun.pagestyle.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.pagestyle.internal.dao.IPageStyleDao;
import cn.lonsun.pagestyle.internal.dao.IPageStyleModelDao;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import cn.lonsun.pagestyle.internal.entity.PageStyleModelEO;
import org.springframework.stereotype.Repository;

/**
 * 界面样式和内容模型关联
 */
@Repository("pageStyleModelDao")
public class PageStyleModelDaoImpl extends BaseDao<PageStyleModelEO> implements IPageStyleModelDao {

    /**
     * 根据样式id删除关联内容
     * @param styleId
     */
    @Override
    public void deleteByStyle(Long styleId) {
        String hql  = "delete from PageStyleModelEO where styleId = ?";
        super.executeUpdateByHql(hql, new Object[]{styleId});
    }

}
