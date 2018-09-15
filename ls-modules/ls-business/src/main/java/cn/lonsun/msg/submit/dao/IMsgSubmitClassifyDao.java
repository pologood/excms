package cn.lonsun.msg.submit.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgSubmitClassifyDao extends IBaseDao<CmsMsgSubmitClassifyEO> {

    public List<CmsMsgSubmitClassifyEO> getEOs();

    public CmsMsgSubmitClassifyEO getEOById(Long id);

    public Pagination getPageEOs(ParamDto vo);

}
