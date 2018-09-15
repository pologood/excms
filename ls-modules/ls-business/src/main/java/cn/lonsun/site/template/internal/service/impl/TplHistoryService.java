package cn.lonsun.site.template.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.template.internal.dao.ITplHistoryDao;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.site.template.internal.service.ITplHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author gu.fei
 * @version 2015-8-25 11:54
 */
@Transactional
@Service("tplHistoryService")
public class TplHistoryService extends BaseService<TemplateHistoryEO> implements ITplHistoryService {

    @Autowired
    private ITplHistoryDao tplHistoryDao;

    @Autowired
    private ITplConfService tplConfService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Override
    public Object getEOList() {
        return tplHistoryDao.getEOList();
    }

    @Override
    public Object getEOById(Long id) {
        return tplHistoryDao.getEOById(id);
    }

    @Override
    public Object getEOByTplId(ParamDto paramDto) {
        Pagination page = tplHistoryDao.getPageEOs(paramDto);
        return page;
    }

    @Override
    public Object addEO(TemplateHistoryEO eo) {
        return tplHistoryDao.addEO(eo);
    }

    @Override
    public Object delEO(Long id) {
        return tplHistoryDao.delEO(id);
    }

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

    @Override
    public Long getLastVersion(Long tempId){
        return tplHistoryDao.getLastVersion(tempId);
    }
}
