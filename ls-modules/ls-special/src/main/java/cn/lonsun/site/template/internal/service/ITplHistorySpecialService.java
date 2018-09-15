package cn.lonsun.site.template.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface ITplHistorySpecialService  extends IBaseService<TemplateHistoryEO> {

    public TemplateHistoryEO saveTplContent(ContentMongoEO eo);

}
