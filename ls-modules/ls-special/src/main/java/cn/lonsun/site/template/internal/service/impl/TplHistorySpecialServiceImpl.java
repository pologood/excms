package cn.lonsun.site.template.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;
import cn.lonsun.site.template.internal.service.ITplHistorySpecialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class TplHistorySpecialServiceImpl extends BaseService<TemplateHistoryEO> implements ITplHistorySpecialService {

    @Resource
    private ContentMongoServiceImpl contentMongoService;

    @Override
    public TemplateHistoryEO saveTplContent(ContentMongoEO eo) {
        TemplateHistoryEO thEO = new TemplateHistoryEO();
        thEO.setTempId(eo.getId());
        thEO.setTempContent(eo.getContent());
        thEO.setTypeCode(eo.getType());
        this.saveEntity(thEO);
        contentMongoService.save(eo);
        return thEO;
    }
}
