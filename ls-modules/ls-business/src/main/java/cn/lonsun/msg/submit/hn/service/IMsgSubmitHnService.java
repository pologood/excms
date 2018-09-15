package cn.lonsun.msg.submit.hn.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.msg.submit.hn.entity.CmsMsgSubmitHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToColumnHnEO;
import cn.lonsun.msg.submit.hn.entity.CmsMsgToUserHnEO;
import cn.lonsun.site.template.internal.entity.ParamDto;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:45
 */
public interface IMsgSubmitHnService extends IMockService<CmsMsgSubmitHnEO> {

    /**
     * 加载自己发布的信息
     * @return
     */
    Pagination getPageList(ParamDto dto);

    /**
     * 加载传阅给自己的信息
     * @return
     */
    Pagination getToMePageList(ParamDto dto);

    /**
     * 加载待发布信息
     * @return
     */
    Pagination getTobePageList(ParamDto dto);

    /**
     * 加载发布信息
     * @return
     */
    Pagination getBePageList(ParamDto dto);

    /**
     * 获取传阅给自己信息数量
     * @return
     */
    Long getToMeCount();

    /**
     * 加载待发布信息数量
     * @return
     */
    Long getToBeCount(ParamDto dto);

    /**
     * 加载发布信息数量
     * @return
     */
    Long getBeCount(ParamDto dto);

    /**
     * 保存报送信息
     * @param eo
     * @return
     */
    Long saveEntity(CmsMsgSubmitHnEO eo);

    /**
     * 更新报送信息
     * @param eo
     * @return
     */
    void updateEO(CmsMsgSubmitHnEO eo);

    /**
     * 删除报送信息
     * @param msgIds
     * @return
     */
    void deleteEntities(Long[] msgIds);

    /**
     * 更改报送信息状态为已阅
     * @param msgId
     */
    void readMsg(Long msgId);

    /**
     * 批量转发报送信息
     * @param msgIds
     * @param userHnEOs
     */
    void batchTransmit(Long[] msgIds, List<CmsMsgToUserHnEO> userHnEOs);

    /**
     * 批量转发到其他栏目
     * @param msgIds
     * @param columnHnEOs
     */
    void batchTransmitToColumn(Long[] msgIds, List<CmsMsgToColumnHnEO> columnHnEOs);

    /**
     * 发布消息
     * @param eo
     */
    void publish(CmsMsgSubmitHnEO eo);

    /**
     * 批量发布
     * @param msgIds
     */
    void batchPublish(Long[] msgIds);

    /**
     * 获取栏目列表
     * @param msgId
     * @return
     */
    Pagination getColumnPageList(Long msgId, ParamDto dto);

    /**
     * 取消发布
     * @param msgId
     * @param columnIds
     */
    void cancelPublish(Long msgId, Long[] columnIds);

    /**
     * 推送信息到微博
     * @param msgId
     */
    void pushMsgToWeibo(Long msgId);

    /**
     * 推送信息到微信
     * @param msgId
     */
    void pushMsgToWeixin(Long msgId);
}
