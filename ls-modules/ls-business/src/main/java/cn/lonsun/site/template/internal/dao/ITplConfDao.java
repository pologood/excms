package cn.lonsun.site.template.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-8-25 11:52
 */
public interface ITplConfDao extends IBaseDao<TemplateConfEO> {

    public Pagination getPageEOList(ParamDto paramDto);

    public Object getEOList(Long siteId, String type);

    public Object getByTemptype(Long siteId, String tempType);

    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType);

    public Object getVrtpls();

    public Object getEOById(Long id);

    public Object addEO(TemplateConfEO eo);

    public Object delEO(Long id);

    public Object editEO(TemplateConfEO eo);

    public void updateTempFile(String path, Long id);

    public Object getList(String stationId, String tempType);
}
