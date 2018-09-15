package cn.lonsun.site.label.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.label.internal.entity.LabelEO;

/**
 * @author DooCal
 * @ClassName: ILabelDao
 * @Description:
 * @date 2015/9/6 17:36
 */
public interface ILabelDao extends IMockDao<LabelEO> {

  public List<LabelEO> getTree(Long pid);

  public LabelEO getById(Long id);

  public List<LabelEO> getByName(String name);

  public List<LabelEO> getByName(String name, Long pid);

  public LabelEO getOneByName(String name, Long id);

  public Object saveLabel(LabelEO eo);

  public Object updateLabel(LabelEO eo);

  public Object delLabel(Long id);

  public Object updateLabelConfig(Long id, String config);

  public Long childCount(Long pid);

  public Object updateLabel(Long isParent, Long id);
}
