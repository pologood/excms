package cn.lonsun.msg.submit.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.msg.submit.entity.vo.EmployParamVo;

/**
 * @author gu.fei
 * @version 2015-11-18 13:44
 */
public interface IMsgToColumnService extends IBaseService<CmsMsgToColumnEO> {

    public List<CmsMsgToColumnEO> getEOs();

    public List<CmsMsgToColumnEO> getEOsByMsgId(Long msgId);

    /**
     * 批量采用
     * @param vos
     */
    public String batchEmploy(EmployParamVo vos);

}
