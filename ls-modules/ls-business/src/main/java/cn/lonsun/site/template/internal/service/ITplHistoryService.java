package cn.lonsun.site.template.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;

/**
 * @author gu.fei
 * @version 2015-8-25 11:54
 */
public interface ITplHistoryService extends IBaseService<TemplateHistoryEO> {

    public Object getEOList();

    public Object getEOById(Long id);

    public Object getEOByTplId(ParamDto paramDto);

    public Object addEO(TemplateHistoryEO eo);

    public Object delEO(Long id);

    public TemplateHistoryEO saveTplContent(ContentMongoEO eo);

    public Long getLastVersion(Long tempId);

}
