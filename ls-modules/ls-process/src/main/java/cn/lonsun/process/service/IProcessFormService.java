package cn.lonsun.process.service;

import cn.lonsun.base.DealStatus;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.form.FormReturnVO;
import cn.lonsun.process.entity.ProcessFormEO;
import cn.lonsun.process.vo.ProcessFormQueryVO;

import java.util.Map;

/**
 * Created by zhu124866 on 2015-12-18.
 */
public interface IProcessFormService extends IBaseService<ProcessFormEO> {


    /**
     * 保存草稿
     * @param paramMap
     */
    void saveDraft(Map<String, Object> paramMap) throws Exception;

    /**
     * 获取分页列表数据
     * @param queryVO
     * @return
     */
    Pagination getPagination(ProcessFormQueryVO queryVO);

    /**
     * 删除草稿
     * @param processFormIds
     */
    void deleteDrafts(Long[] processFormIds);

    /**
     * 保存流程表单对象
     * @param map
     * @param dealStatus
     * @return
     */
    ProcessFormEO saveProcessForm( Map<String, Object> map, DealStatus dealStatus) throws Exception;

    /**
     * 流程启动保存流程表单
     * @param paramMap
     * @return
     */
    ProcessFormEO saveProcessFormProcessStart(Map<String, Object> paramMap);


    /**
     * 表单标题是否存在
     * @param processFormId
     * @param title
     * @return
     */
    boolean titleIsExist(Long processFormId,String title);


    /**
     * 流程终止
     * @param recordId
     * @param reason
     */
    void processTerminate(Long recordId, String reason);

    FormReturnVO saveFormData(Map<String, Object> paramMap, DealStatus dealStatus);
}
