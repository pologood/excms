package cn.lonsun.pagestyle.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.pagestyle.internal.dao.IPageStyleDao;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("pageStyleDao")
public class PageStyleDaoImpl extends MockDao<PageStyleEO>  implements IPageStyleDao {

    /**
     * 获取模型启用的样式
     * @param modelCode
     * @return
     */
    @Override
    public PageStyleEO getStyleByModel(String modelCode) {
        String hql = "select t.id as id, name as name, width as width, style as style,isBase as isBase,useAble as useAble" +
                "from PageStyleEO t, PageStyleModelEO m where t.recordStatus = ? and t.id = m.styleId and m.modelCode = ? and t.useAble = ?";
        List<PageStyleEO> list = super.getEntitiesByHql(hql,
                new Object[]{PageStyleEO.RecordStatus.Normal.toString(), modelCode, 1});
        if(list == null || list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
}
