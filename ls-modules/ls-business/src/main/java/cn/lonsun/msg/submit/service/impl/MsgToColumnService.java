package cn.lonsun.msg.submit.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.util.ContentOptRecordUtil;
import cn.lonsun.content.vo.CopyReferVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.msg.submit.dao.IMsgSubmitDao;
import cn.lonsun.msg.submit.dao.IMsgToColumnDao;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.msg.submit.entity.vo.EmployParamVo;
import cn.lonsun.msg.submit.service.IMsgSubmitClassifyService;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.msg.submit.service.IMsgToColumnService;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Service
public class MsgToColumnService extends BaseService<CmsMsgToColumnEO> implements IMsgToColumnService {

    @Autowired
    private IMsgToColumnDao msgToColumnDao;

    @Autowired
    private IPersonService personService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IMsgSubmitService msgSubmitService;

    @Autowired
    private IMsgSubmitDao msgSubmitDao;

    @Autowired
    private IMsgSubmitClassifyService msgSubmitClassifyService;

    @Override
    public List<CmsMsgToColumnEO> getEOs() {
        return msgToColumnDao.getEOs();
    }

    @Override
    public List<CmsMsgToColumnEO> getEOsByMsgId(Long msgId) {
        List<CmsMsgToColumnEO> list = msgToColumnDao.getEOsByMsgId(msgId);

        for(CmsMsgToColumnEO eo:list) {
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class,eo.getColumnId());
            if(null != indicatorEO) {
                eo.setColumnName(indicatorEO.getName());
                IndicatorEO _indicatorEO = CacheHandler.getEntity(IndicatorEO.class,indicatorEO.getSiteId());
                if(null != _indicatorEO) {
                    eo.setSiteName(_indicatorEO.getName());
                }
            }
            PersonEO personEO = personService.getPersonByUserId(eo.getCreateOrganId(),eo.getCreateUserId());
            if(null != personEO) {
                eo.setUserName(personEO.getName());
            }
        }

        return list;
    }

    @Override
    public String batchEmploy(EmployParamVo vo) {
        String returnStr = "";
        StringBuilder tempStr = new StringBuilder();//定义临时字符串变量
        Long[] msgIds = StringUtils.getArrayWithLong(vo.getMsgIds(), ",");
        if(vo.getType() == 0) {
            //采集到当前栏目下
            returnStr = employ(msgIds,null,null,vo.getContent(),vo.getTitle());
        } else if(vo.getType() == 1) {
            //采集到其他栏目中
            Long[] columnIds = StringUtils.getArrayWithLong(vo.getColumnIds(),",");
            Long[] cSiteIds = StringUtils.getArrayWithLong(vo.getcSiteIds(),",");
            int c = 0;
            for(Long columnId : columnIds) {
                //returnStr = employ(msgIds, columnId, cSiteIds[c++], vo.getContent());
                tempStr.append(employ(msgIds, columnId, cSiteIds[c++], vo.getContent(),vo.getTitle())).append(",");
            }
            if(tempStr.length() > 1){
                returnStr = tempStr.substring(0,tempStr.length()-1).toString();//修复批量采用时，只有一个栏目下信息可以生成静态，其他栏目信息不能生成（原代码逻辑中由于循环覆盖变量值，只能保存一个栏目下的信息串）
            }
        } else if(vo.getType() == 2) {
            //批量采集到当前栏目中
            returnStr = employ(msgIds,null,null,null);
        } else if(vo.getType() == 3) {
            //批量采集到其他栏目中
            Long[] columnIds = StringUtils.getArrayWithLong(vo.getColumnIds(),",");
            Long[] cSiteIds = StringUtils.getArrayWithLong(vo.getcSiteIds(),",");
            int c = 0;
            for(Long columnId : columnIds) {
                //returnStr = employ(msgIds,columnId,cSiteIds[c++],null);
                tempStr.append(employ(msgIds,columnId,cSiteIds[c++],null)).append(",");
            }
            if(tempStr.length() > 1){
                returnStr = tempStr.substring(0,tempStr.length()-1).toString();//修复批量采用时，只有一个栏目下信息可以生成静态，其他栏目信息不能生成（原代码逻辑中由于循环覆盖变量值，只能保存一个栏目下的信息串）
            }
        }
        return  returnStr;
    }

    /**
     *
     * @param msgIds
     * @param columnId
     * @param content 消息内容
     */
    private String employ(Long[] msgIds,Long columnId,Long siteId,String content) {
        String returnStr = employ(msgIds,columnId,siteId,content,null);
        return returnStr;

    }

    /**
     *
     * @param msgIds
     * @param columnId
     * @param content 消息内容
     */
    private String employ(Long[] msgIds,Long columnId,Long siteId,String content,String title) {
        String returnStr = "";
        for(Long msgId : msgIds) {
            CmsMsgSubmitEO seo = msgSubmitService.getEntity(CmsMsgSubmitEO.class,msgId);
            BaseContentEO eo = new BaseContentEO();

            CmsMsgSubmitClassifyEO cEO = msgSubmitClassifyService.getEntity(CmsMsgSubmitClassifyEO.class, seo.getClassifyId());
            if(null == columnId) {
                eo.setColumnId(cEO.getColumnId());
                eo.setSiteId(cEO.getcSiteId());
            } else {
                eo.setColumnId(columnId);
                eo.setSiteId(siteId);
            }

            eo.setTitle(null != title?title:seo.getName());
            eo.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
            eo.setAuthor(AppUtil.isEmpty(seo.getAuthor()) ? seo.getProvider() : seo.getAuthor());
            eo.setPublishDate(seo.getPublishDate());
            eo.setImageLink(seo.getImageLink());
            eo.setResources(seo.getFromCode());
            eo.setCreateOrganId(seo.getCreateOrganId());
            eo.setCreateUserId(seo.getCreateUserId());
            eo.setIsPublish(2);
            //报送消息
            Long contentId=baseContentService.saveArticleNews(eo, null == content ? seo.getContent() : content,null, null,null,null);
            try {
                //增加记录
                ContentOptRecordUtil.saveOptRecord(new Long[]{contentId},eo.getIsPublish(), ContentOptRecordEO.Type.submit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CopyReferVO vo = new CopyReferVO();
            vo.setContentId(String.valueOf(eo.getId()));
            String messages = baseContentService.synCloumnInfos(eo.getColumnId(),vo);
           /* MessageSenderUtil.publishContent(
                            new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{contentId}).setType(MessageEnum.PUBLISH.value()), 1);

            */
           //添加采编操作日志
           SysLog.log("采编新闻：类别（"+cEO.getName()+"），标题（"+eo.getTitle()+"），采编到栏目："+ ColumnUtil.getColumnName(eo.getColumnId(),eo.getSiteId()),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());

            //保存引用记录
            CmsMsgToColumnEO toColumnEO = new CmsMsgToColumnEO();
            toColumnEO.setColumnId(null == columnId ? cEO.getColumnId() : columnId);
            toColumnEO.setMsgId(msgId);
            toColumnEO.setSiteId(seo.getSiteId());
            this.saveEntity(toColumnEO);

            //更新引用次数
            seo.setUseCount(seo.getUseCount() + 1);
            //设置引用状态
            seo.setStatus(2);
            //清空退回原因
            seo.setBackReason("");
            msgSubmitService.updateEntity(seo);

            returnStr += eo.getSiteId()+ "_" + eo.getColumnId() + "_" + contentId + ",";

            /*if(!AppUtil.isEmpty(messages)){//生成静态
                String[] msg = messages.split(",");
                for(int i=0;i<msg.length;i++){
                    Long siteid = Long.parseLong(msg[i].split("_")[0]);
                    Long columnid = Long.parseLong(msg[i].split("_")[1]);
                    Long contentid = Long.parseLong(msg[i].split("_")[2]);

                    returnStr += siteid+ "_" + columnid + "_" + contentid + ",";
                   *//* MessageSenderUtil.publishContent(
                            new MessageStaticEO(siteid, columnid, new Long[]{contentid}).setType(MessageEnum.PUBLISH.value()), 1);*//*
                }
            }*/
        }
        if(!AppUtil.isEmpty(returnStr)){//去除最后的逗号
            returnStr = returnStr.substring(0,returnStr.length()-1);
        }
        return returnStr;
    }
}
