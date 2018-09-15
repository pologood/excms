package cn.lonsun.site.template.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-8-25 11:54
 */
public interface ITplConfService extends IBaseService<TemplateConfEO> {

    public Pagination getPageEOList(ParamDto paramDto);

    public Object getEOList(Long siteId, String type);

    public Object getByTemptype(Long siteId, String tempType);

    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType);

    /**
     * 获取公共模板
     *
     * @return
     */
    public Object getVrtpls();

    public Object getEOById(Long id);

    public List<TemplateConfEO> saveEO(TemplateConfEO eo);

    public Object addEO(TemplateConfEO eo);

    public Object delEO(Long id);

    public Object editEO(TemplateConfEO eo);

    public String readFile(String file);

    public void updateTempFile(String path, Long id);

    public Object getList(String stationId, String tempType);
}
