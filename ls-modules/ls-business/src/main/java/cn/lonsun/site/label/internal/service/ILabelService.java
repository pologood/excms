package cn.lonsun.site.label.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.label.internal.vo.LabelVO;

/**
 * @author DooCal
 * @ClassName: ILabelService
 * @Description:
 * @date 2015/9/6 16:37
 */
public interface ILabelService extends IMockService<LabelEO> {

  public List<LabelVO> getTree(Long pid);

  public LabelEO getById(Long id);

  public List<LabelVO> getByName(String name);

  public Object saveLabel(LabelEO eo);

  public Object updateLabel(LabelEO eo);

  public Object delLabel(Long id);

  public Object updateLabelConfig(Long id, String config);

  public Object copyLabel(Long id, String labelName, String labelNotes);

  public Long childCount(Long pid);

}
