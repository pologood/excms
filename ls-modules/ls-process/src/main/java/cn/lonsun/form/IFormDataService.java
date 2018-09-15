package cn.lonsun.form;

import cn.lonsun.base.DealStatus;

import java.util.Map;

/**
 *
 * 表单数据服务
 * Created by zhusy on 2016-7-30.
 */
public interface IFormDataService {

    /**
     * 保存表单数据
     * @param paramMap
     * @return
     */
    FormReturnVO saveFormData(Map<String,Object> paramMap, DealStatus dealStatus);

    /**
     * 更新表单数据
     * @param dataId
     * @param paramMap
     * @return
     */
    FormReturnVO updateFormData(Long dataId,Map<String,Object> paramMap,DealStatus dealStatus);

    /**
     * 获取表单数据
     * @param dataId
     * @return
     */
    FormReturnVO getFormData(Long dataId);


    /**
     * 更新表单业务实体办理状态
     * @param dataId
     * @param dealStatus
     * @return
     */
    Boolean updateDealStatus(Long dataId,DealStatus dealStatus);








}
