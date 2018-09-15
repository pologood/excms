package cn.lonsun.site.label.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.site.label.internal.dao.ILabelDao;
import cn.lonsun.site.label.internal.entity.LabelEO;

/**
 * @author DooCal
 * @ClassName: LabelDaoImpl
 * @Description:
 * @date 2015/9/6 17:37
 */
@Repository("labelDao")
public class LabelDaoImpl extends MockDao<LabelEO> implements ILabelDao {

    @Override
    public List<LabelEO> getTree(Long pid) {
        List<Object> list = new ArrayList<Object>();
        String hql = "from LabelEO where record_status=?";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        if (pid != -1) {
            hql += " and parentId=? ";
            list.add(pid);
        }
        hql += " ORDER BY labelName";
        return this.getEntitiesByHql(hql, list.toArray());
    }

    @Override
    public LabelEO getById(Long id) {
        return this.getEntityByHql("from LabelEO where recordStatus=? and id = ?", new Object[]{AMockEntity.RecordStatus.Normal.toString(), id});
    }

    @Override
    public List<LabelEO> getByName(String name) {
        String hql = "from LabelEO";
        List<Object> list = new ArrayList<Object>();
        hql += " where recordStatus=? and labelName=?";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(name);
        hql += " ORDER BY labelName";
        return this.getEntitiesByHql(hql, list.toArray());
    }

    public List<LabelEO> getByName(String name, Long pid) {
        String hql = "from LabelEO";
        List<Object> list = new ArrayList<Object>();
        hql += " where recordStatus=? and labelName=? AND parentId=?";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(name);
        list.add(pid);
        hql += " ORDER BY labelName";
        return this.getEntitiesByHql(hql, list.toArray());
    }

    @Override
    public LabelEO getOneByName(String name, Long id) {
        String hql = "from LabelEO";
        List<Object> list = new ArrayList<Object>();
        hql += " where recordStatus=? and labelName=?";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(name);
        if (!AppUtil.isEmpty(id)) {
            hql += " AND Id<>?";
            list.add(id);
        }
        return this.getEntityByHql(hql, list.toArray());
    }

    @Override
    public Object saveLabel(LabelEO eo) {
        return this.save(eo);
    }

    @Override
    public Object updateLabel(LabelEO eo) {
        //this.update(eo);
        return this.executeUpdateBySql("UPDATE CMS_LABEL SET LABEL_NAME=?,LABEL_NOTES=? WHERE ID = ?", new Object[]{eo.getLabelName(), eo.getLabelNotes(), eo.getId()});
    }

    @Override
    public Object delLabel(Long id) {
        this.delete(LabelEO.class, new Long[]{id});
        return null;
    }

    @Override
    public Object updateLabelConfig(Long id, String config) {
        return this.executeUpdateBySql("UPDATE CMS_LABEL SET LABEL_CONFIG=? WHERE ID = ?", new Object[]{config, id});
    }

    @Override
    public Long childCount(Long pid) {
        List<Object> list = new ArrayList<Object>();
        String hql = "from LabelEO where record_status=? and parentId=? ORDER BY labelName";
        list.add(AMockEntity.RecordStatus.Normal.toString());
        list.add(pid);
        return this.getCount(hql, list.toArray());
    }

    @Override
    public Object updateLabel(Long isParent, Long id) {
        return this.executeUpdateBySql("UPDATE CMS_LABEL SET IS_PARENT=? WHERE ID = ?", new Object[]{isParent, id});
    }
}
