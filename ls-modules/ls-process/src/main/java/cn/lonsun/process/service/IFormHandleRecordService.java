package cn.lonsun.process.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.entity.FormHandleRecordEO;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;

import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-23.
 */
public interface IFormHandleRecordService extends IBaseService<FormHandleRecordEO> {


    /**
     * 保存表单办理记录
     * @param userInfo
     * @param map
     */
    void saveFormHandleRecord(UserInfoVO userInfo,Map<String,Object> map);

    /**
     * 获取分页列表
     * @param queryVO
     * @return
     */
    Pagination getFormHandleRecordPagination(ProcessFormQueryVO queryVO);
}
