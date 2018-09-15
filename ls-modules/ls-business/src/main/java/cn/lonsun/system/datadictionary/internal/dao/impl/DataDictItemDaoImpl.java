package cn.lonsun.system.datadictionary.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.dao.IDataDictItemDao;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.vo.DataSortVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: DataDictItemDaoImpl
 * @Description: 数据字典项数据访问层
 * @date 2015年10月15日 上午11:31:18
 */
@Repository("dataDictItemDao")
public class DataDictItemDaoImpl extends BaseDao<DataDictItemEO> implements IDataDictItemDao {

    @Override
    public List<DataDictItemEO> getListByDictId(Long dictId) {
        String hql = "from DataDictItemEO where dataDicId=? order by sortNum asc";
        return getEntitiesByHql(hql, new Object[]{dictId});
    }

    @Override
    public DataDictItemEO getDataDictItemByCode(String code, Long dictId) {
        String hql = "from DataDictItemEO where code=? and dataDicId=? order by sortNum asc";
        return getEntityByHql(hql, new Object[]{code, dictId});
    }

    @Override
    public Pagination getPageByDictId(Long pageIndex, Integer pageSize, Long dictId, String name) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("from DataDictItemEO where dataDicId= " + dictId);
//		Object[] values=new Object[]{dictId};
//		if(name!=null&&name.trim()!=null){
//			String name1=name.trim();
//			hql+=" and name like ? ";
//			values=new Object[]{dictId,"%"+name1+"%"};
//		}
        if (null != LoginPersonUtil.getUserId()) {
            if (LoginPersonUtil.isRoot()) {
                hql.append(" and 1=1");
            } else if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
                hql.append(" and( siteId=:siteId or siteId is null)");
                map.put("siteId", LoginPersonUtil.getSiteId());
            } else {
                hql.append(" and 1!=1");
            }
        }
        hql.append(" order by sortNum asc");
        return getPagination(pageIndex, pageSize, hql.toString(), map);
    }

    @Override
    public void deleteItemByDictId(Long dictId) {
        String hql = "delete from DataDictItemEO where dataDicId=?";
        executeUpdateByHql(hql, new Object[]{dictId});
    }

    @Override
    public DataSortVO getMaxItem(Long dictId) {
        String hql = "select max(sortNum) as sort from DataDictItemEO where dataDicId=?";
        return (DataSortVO) getBean(hql, new Object[]{dictId}, DataSortVO.class);
    }

    @Override
    public void updateDefault(Long dictId, Long itemId, Integer flag, Long siteId) {
        String hql = "update DataDictItemEO set isDefault=? where itemId!=? and dataDicId =?";
        List<Object> obj = new ArrayList<Object>();
        obj.add(flag);
        obj.add(itemId);
        obj.add(dictId);
        if (siteId != null) {
            hql += " and siteId=?";
            obj.add(siteId);
        }
        executeUpdateByHql(hql, obj.toArray(new Object[obj.size()]));
    }

    @Override
    public void updateDefault(Long itemId, Integer flag) {
        String hql = "update DataDictItemEO set isDefault=? where itemId=?";
        executeUpdateByHql(hql, new Object[]{flag, itemId});
    }

    @Override
    public void updateHide(Long itemId,Integer flag){
        String hql = "update DataDictItemEO set isHide=? where itemId=?";
        executeUpdateByHql(hql, new Object[]{flag,itemId});
    }


    @Override
    public List<DataDictItemEO> getItemList(Long dictId) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("from DataDictItemEO where dataDicId= " + dictId);
        if (LoginPersonUtil.isRoot()) {
            hql.append(" and 1=1");
        } else {
            hql.append(" and( siteId=:siteId or siteId is null)");
            map.put("siteId", LoginPersonUtil.getSiteId());
        }
        hql.append(" order by sortNum asc");
        return getEntitiesByHql(hql.toString(), map);
    }

}
