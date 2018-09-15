package cn.lonsun.site.template.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;

/**
 * @author gu.fei
 * @version 2015-8-25 11:52
 */
public interface ITplHistoryDao extends IBaseDao<TemplateHistoryEO> {

    public Pagination getPageEOs(ParamDto paramDto);

    public Object getEOList();

    public Object getEOById(Long id);

    public Object getEOByTplId(Long id);

    public Object addEO(TemplateHistoryEO eo);

    public Object editEO(TemplateHistoryEO eo);

    public Object delEO(Long id);

    public Long getLastVersion(Long tempId);
}
