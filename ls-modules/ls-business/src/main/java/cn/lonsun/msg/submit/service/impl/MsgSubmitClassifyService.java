package cn.lonsun.msg.submit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.dao.IMsgSubmitClassifyDao;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.service.IMsgSubmitClassifyService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.util.ColumnUtil;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class MsgSubmitClassifyService extends BaseService<CmsMsgSubmitClassifyEO> implements IMsgSubmitClassifyService {

    @Autowired
    private IMsgSubmitClassifyDao msgSubmitClassifyDao;

    @Override
    public List<CmsMsgSubmitClassifyEO> getEOs() {
        return msgSubmitClassifyDao.getEOs();
    }

    @Override
    public CmsMsgSubmitClassifyEO getEOById(Long id) {
        return msgSubmitClassifyDao.getEOById(id);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Pagination page = msgSubmitClassifyDao.getPageEOs(dto);
        List<CmsMsgSubmitClassifyEO> list = (List<CmsMsgSubmitClassifyEO>) page.getData();

        for(CmsMsgSubmitClassifyEO eo:list) {
            if(eo.getColumnId() != null) {
                eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),eo.getcSiteId()));
            }
        }

        return page;
    }

    @Override
    public void saveEO(CmsMsgSubmitClassifyEO eo) {
        this.saveEntity(eo);
        CacheHandler.reload(CmsMsgSubmitClassifyEO.class.getName());
    }

    @Override
    public void updateEO(CmsMsgSubmitClassifyEO eo) {
        this.updateEntity(eo);
        CacheHandler.reload(CmsMsgSubmitClassifyEO.class.getName());
    }

    @Override
    public void deleteEO(Long[] ids) {
        for(Long id : ids) {
            this.delete(CmsMsgSubmitClassifyEO.class, id);
        }
        CacheHandler.reload(CmsMsgSubmitClassifyEO.class.getName());
    }
}
