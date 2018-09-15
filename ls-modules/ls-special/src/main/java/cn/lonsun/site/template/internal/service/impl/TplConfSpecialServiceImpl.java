package cn.lonsun.site.template.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.site.template.internal.dao.ITplConfSpecialDao;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.service.ITplConfSpecialService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushouyong on 2017-9-26.
 */
@Service
public class TplConfSpecialServiceImpl extends BaseService<TemplateConfEO> implements ITplConfSpecialService {

    @Resource
    private ITplConfSpecialDao tplConfSpecialDao;

    @Override
    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType) {
        return tplConfSpecialDao.getSpecialById(siteId, specialId, tempType);
    }

    @Override
    public List<TemplateConfEO> saveEO(TemplateConfEO eo) {
        List<TemplateConfEO> list = new ArrayList<TemplateConfEO>();
        Long siteId = LoginPersonUtil.getSiteId();
        if (!AppUtil.isEmpty(eo.getSiteId())) {
            siteId = eo.getSiteId();
        }
        eo.setSiteId(siteId);
        Long tid = this.saveEntity(eo);
        list.add(eo);
        if (eo.getType().equals(TemplateConfEO.Type.Virtual.toString())) {
            eo.setLeaf(1);
            //首页，栏目页，详细页
            TemplateConfEO index = new TemplateConfEO();
            index.setPid(tid);
            index.setName("首页");
            index.setTempType("index");
            index.setType(eo.getType());
            index.setSiteId(siteId);
            TemplateConfEO column = new TemplateConfEO();
            column.setPid(tid);
            column.setName("栏目页");
            index.setTempType("column");
            column.setType(eo.getType());
            column.setSiteId(siteId);
            TemplateConfEO detail = new TemplateConfEO();
            detail.setPid(tid);
            detail.setName("详细页");
            index.setTempType("content");
            detail.setType(eo.getType());
            detail.setSiteId(siteId);
            this.saveEntity(index);
            this.saveEntity(column);
            this.saveEntity(detail);
            list.add(index);
            list.add(column);
            list.add(detail);
        }

        return list;
    }

    @Override
    public Object delEO(Long id) {
        return tplConfSpecialDao.delEO(id);
    }
}
