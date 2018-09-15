package cn.lonsun.site.template.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.dao.ITplConfDao;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.site.template.util.DocFileManage;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-8-25 11:54
 */
@Transactional
@Service("tplConfService")
public class TplConfService extends BaseService<TemplateConfEO> implements ITplConfService {

    @Autowired
    private ITplConfDao tplConfDao;

    @Override
    public Pagination getPageEOList(ParamDto paramDto) {
        return tplConfDao.getPageEOList(paramDto);
    }

    @Override
    public Object getEOList(Long siteId, String type) {
        return tplConfDao.getEOList(siteId, type);
    }

    @Override
    public Object getByTemptype(Long siteId, String tempType) {
        return tplConfDao.getByTemptype(siteId, tempType);
    }

    @Override
    public List<TemplateConfEO> getSpecialById(Long siteId, Long specialId, String tempType) {
        return tplConfDao.getSpecialById(siteId, specialId, tempType);
    }

    @Override
    public Object getVrtpls() {
        return tplConfDao.getVrtpls();
    }

    @Override
    public Object getEOById(Long id) {
        return tplConfDao.getEOById(id);
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
    public Object addEO(TemplateConfEO eo) {
        return tplConfDao.addEO(eo);
    }

    @Override
    public Object delEO(Long id) {
        return tplConfDao.delEO(id);
    }

    @Override
    public Object editEO(TemplateConfEO eo) {
        return tplConfDao.editEO(eo);
    }

    public String readFile(String file) {
        String msg = null;
        try {
            msg = DocFileManage.readFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void updateTempFile(String path, Long id) {
        tplConfDao.updateTempFile(path, id);
    }

    @Override
    public Object getList(String stationId, String tempType) {
        return tplConfDao.getList(stationId, tempType);
    }


}
