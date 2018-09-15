package cn.lonsun.msg.submit.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IMsgSubmitClassifyService extends IBaseService<CmsMsgSubmitClassifyEO> {

    public List<CmsMsgSubmitClassifyEO> getEOs();

    public CmsMsgSubmitClassifyEO getEOById(Long id);

    public Pagination getPageEOs(ParamDto dto);

    public void saveEO(CmsMsgSubmitClassifyEO eo);

    public void updateEO(CmsMsgSubmitClassifyEO eo);

    public void deleteEO(Long[] ids);

}
