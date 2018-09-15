
package cn.lonsun.process.invoke;

import cn.lonsun.webservice.processEngine.vo.UserInfoVO;

import java.util.Map;

/**
 * 业务数据处理接口
 *@date 2014-12-15 21:40  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public interface IBusinessHandler {

    /**
     * 流程流转表单数据处理
     * @param map
     * @return
     */
    Map<String,Object> run(UserInfoVO userInfo,Map<String, Object> map) throws Exception;

    /**
     * 流程启动表单数据处理
     * @param map
     * @return
     */
    Map<String,Object> start(UserInfoVO userInfo,Map<String, Object> map)throws Exception;

    /**
     * 流程办结表单数据处理
     * @param map
     * @return
     */
    Map<String,Object> complete(UserInfoVO userInfo,Map<String, Object> map) throws Exception;

    /**
     * 为业务实体设置流程信息
     * @param procInstId
     * @param recordId
     * @return
     */
    void setProcessInfo(Long procInstId, Long actinstId, String activityName, Long recordId);


    /**
     * 为业务实体设置当前活动实例
     * @param actinstId
     * @param activityName
     * @param recordId
     * @return
     */
    void setCurActivity(Long actinstId, String activityName, Long recordId);

    /**
     * 流程终止业务数据处理
     * @param recordId
     * @param reason
     * @return
     */
    void terminate(Long recordId, String reason);

    
    /**
     * 保存阅件办理记录
     *
     * @param map
     */
    void saveReadLog(Map<String, Object> map);

    /**
     * 退回
     * @param map
     */
    void back(UserInfoVO userInfo,Map<String, Object> map);


    /**
     * 退回操作业务验证
     * @param recordId
     */
    void backBusinessValidate(Long recordId);

    /**
     * 保存表单数据
     * @param map
     */
    Map<String,Object> saveFormData(Map<String, Object> map) throws Exception;


    /**
     * 记录系统操作日志
     * @param recordId
     * @param title
     */
    void saveSystemLog(Long recordId, String title);

}
