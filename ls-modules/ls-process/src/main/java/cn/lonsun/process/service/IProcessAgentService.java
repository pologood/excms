
package cn.lonsun.process.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.vo.ProcessAgentQueryVO;

import java.util.Map;

/**
 *服务层接口
 *@date 2015-1-7 9:35  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public interface IProcessAgentService extends IBaseService<ProcessAgentEO> {

    /**
     * 获取代填分页列表
     * @param queryVO
     * @return
     */
    Pagination getAgentPagination(ProcessAgentQueryVO queryVO);

    int saveAgent(ProcessAgentEO processAgentEO, Long unitId);


    /**
     * 根据被代填人S获取代填对象S
     * @param receivers
     * @return
     */
    Map<String,ProcessAgentEO> getAgents(ReceiverVO[] receivers,String moduleCode);
}
