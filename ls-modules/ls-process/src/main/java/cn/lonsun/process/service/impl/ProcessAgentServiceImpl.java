
package cn.lonsun.process.service.impl;

import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;

import cn.lonsun.process.dao.IProcessAgentDao;
import cn.lonsun.process.entity.ProcessAgentEO;
import cn.lonsun.process.service.IProcessAgentService;
import cn.lonsun.process.vo.ProcessAgentQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *服务层接口实现
 *@date 2015-1-7 9:35  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
@Service("processAgentService")
public class ProcessAgentServiceImpl extends BaseService<ProcessAgentEO> implements IProcessAgentService {

    @Autowired
    private IProcessAgentDao processAgentDao;


    /**
     * 获取代填分页列表
     *
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getAgentPagination(ProcessAgentQueryVO queryVO) {
        Pagination pagination = this.processAgentDao.getAgentPagination(queryVO);
        return pagination;
    }

    @Override
    public int saveAgent(ProcessAgentEO processAgentEO, Long unitId) {

        //判断当前被代填人是否已经设置过代填人
        if(this.processAgentDao.existBeAgent(processAgentEO.getBeAgentUserId(), processAgentEO.getBeAgentOrganId(), processAgentEO.getAgentId(),processAgentEO.getModuleCode())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), processAgentEO.getBeAgentPersonName()+"("+ processAgentEO.getBeAgentOrganName()+") 已设置代填人");
        }
        if(null == processAgentEO.getAgentId()){
            processAgentEO.setCreateUnitId(unitId);
            this.saveEntity(processAgentEO);
        }else{
            ProcessAgentEO eo = this.getEntity(ProcessAgentEO.class, processAgentEO.getAgentId());
            eo.setAgentOrganIds(processAgentEO.getAgentOrganIds());
            eo.setAgentPersonNames(processAgentEO.getAgentPersonNames());
            eo.setAgentUserIds(processAgentEO.getAgentUserIds());
            eo.setBeAgentOrganId(processAgentEO.getBeAgentOrganId());
            eo.setBeAgentUserId(processAgentEO.getBeAgentUserId());
            eo.setBeAgentPersonName(processAgentEO.getBeAgentPersonName());
        }
        return 1;
    }

    /**
     * 根据被代填人S获取代填对象S
     *
     * @param receivers
     * @return
     */
    @Override
    public Map<String, ProcessAgentEO> getAgents(ReceiverVO[] receivers,String moduleCode) {
        if(null == receivers || receivers.length == 0)
            return null;
        List<ProcessAgentEO> list = this.processAgentDao.getAgents(receivers,moduleCode);
        Map<String,ProcessAgentEO> map = new HashMap<String, ProcessAgentEO>(list.size());
        for(ProcessAgentEO eo : list){
            map.put(eo.getBeAgentUserId().toString()+eo.getBeAgentOrganId().toString(),eo);
        }
        return map;
    }
}
