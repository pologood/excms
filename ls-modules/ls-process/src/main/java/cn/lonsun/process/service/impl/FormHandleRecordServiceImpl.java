package cn.lonsun.process.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.dao.IFormHandleRecordDao;
import cn.lonsun.process.entity.FormHandleRecordEO;
import cn.lonsun.process.service.IFormHandleRecordService;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-23.
 */
@Service
public class FormHandleRecordServiceImpl extends BaseService<FormHandleRecordEO> implements IFormHandleRecordService {

    @Autowired
    private IFormHandleRecordDao formHandleRecordDao;


    /**
     * 保存表单办理记录
     * @param userInfo
     * @param map
     */
    @Override
    public void saveFormHandleRecord(UserInfoVO userInfo, Map<String, Object> map) {
        FormHandleRecordEO formHandleRecord = new FormHandleRecordEO();
        formHandleRecord.setProcessFormId(AppUtil.getLong(map.get("processFormId")));
        formHandleRecord.setActivityName(AppUtil.getValue(map.get("activityName")));
        formHandleRecord.setHandlePersonName(userInfo.getUserName());
        formHandleRecord.setCreateUnitId(userInfo.getUnitId());
        formHandleRecord.setCreateUnitName(userInfo.getUnitName());
        if(null != map.get("taskId")){
            formHandleRecord.setTaskId(AppUtil.getLong(map.get("taskId")));//设置办理任务ID
        }
        saveEntity(formHandleRecord);
    }

    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getFormHandleRecordPagination(ProcessFormQueryVO queryVO) {
        return formHandleRecordDao.getFormHandleRecordPagination(queryVO);
    }

}
