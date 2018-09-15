
package cn.lonsun.process.dao;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.vo.ProcessAgentQueryVO;

import java.util.List;

/**
 *数据持久层接口
 *@date 2015-1-7 9:31  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public interface IProcessAgentDao extends IBaseDao<ProcessAgentEO> {

    /**
     * 获取代填分页列表
     * @param queryVO
     * @return
     */
    Pagination getAgentPagination(ProcessAgentQueryVO queryVO);

    /**
     * 判断被代填人是否已设置代填
     * @param beAgentUserId
     * @param beAgentOrganId
     * @param agentId
     * @param moduleCode
     * @return
     */
    boolean existBeAgent(Long beAgentUserId, Long beAgentOrganId, Long agentId, String moduleCode);

    List<ProcessAgentEO> getAgents(ReceiverVO[] receivers, String moduleCode);
}
