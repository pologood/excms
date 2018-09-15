package cn.lonsun.datacollect.service.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.impl.HtmlCollectDataDao;
import cn.lonsun.datacollect.dao.impl.HtmlCollectTaskDao;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.service.IHtmlCollectDataService;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service
public class HtmlCollectDataService extends MockService<HtmlCollectDataEO> implements IHtmlCollectDataService {

    private static final Logger logger = LoggerFactory.getLogger(HtmlCollectDataService.class);

    @DbInject("htmlCollectData")
    private HtmlCollectDataDao htmlCollectDataDao;

    @Autowired
    private IOrganService organService;

    @Autowired
    private HtmlCollectTaskDao htmlCollectTaskDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return htmlCollectDataDao.getPageEOs(vo);
    }

    @Override
    public void saveData(Map<String, Object> map) {
        htmlCollectDataDao.saveData(map);
    }

    @Override
    public List<HtmlCollectDataEO> getEntityByName(Long taskId, String title) {
        return htmlCollectDataDao.getEntityByName(taskId,title);
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        htmlCollectDataDao.deleteByTaskId(taskId);
    }

    @Override
    public String quoteData(Long columnId,Long cSiteId, Long[] ids) {
        String returnStr = "";
        for(Long id : ids) {
            HtmlCollectDataEO dataEO = htmlCollectDataDao.getEntity(HtmlCollectDataEO.class,id);
            HtmlCollectTaskEO taskEO = htmlCollectTaskDao.getEntity(HtmlCollectTaskEO.class,dataEO.getTaskId());
            if(!AppUtil.isEmpty(dataEO)) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("siteId",taskEO.getSiteId());
                map.put("columnId",taskEO.getColumnId());
                map.put("title",dataEO.getTitle());
                map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                List<BaseContentEO> contents = baseContentService.getEntities(BaseContentEO.class,map);
//                List<BaseContentEO> contents = baseContentService.getBaseContents(LoginPersonUtil.getSiteId(),dataEO.getTitle());
                if(null == contents || contents.isEmpty()) {
                    //没有重复的则新加一个
                    BaseContentEO content = new BaseContentEO();
                    if(null != columnId) {
                        content.setColumnId(columnId);
                        content.setSiteId(cSiteId);
                    } else {
                        content.setColumnId(taskEO.getColumnId());
                        content.setSiteId(taskEO.getcSiteId());
                    }

                    Long _columnId = content.getColumnId();
                    IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, _columnId);
                    Long siteId = indicatorEO.getSiteId();

                    content.setSiteId(siteId);
                    content.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                    content.setTitle(dataEO.getTitle());
                    content.setAuthor(dataEO.getAuthor());
                    content.setResources(dataEO.getFromName());
                    content.setHit(dataEO.getClicks());

                    //发布时间
                    if(dataEO.getPublishDate() != null) {
                        //如果采集的时间格式不对则不引用此时间
                        Date publishDate = DateUtil.trStr2Date(dataEO.getPublishDate());
                        if(publishDate != null) {
                            content.setPublishDate(publishDate);
                        }
                    }
                    //创建时间
                    if(dataEO.getCreateTime() != null) {
                        //如果采集的时间格式不对则不引用此时间

                        Date createDate = DateUtil.trStr2Date(dataEO.getCreateTime());
                        if(createDate != null) {
                            content.setCreateDate(createDate);
                        }
                    }

                    if(!AppUtil.isEmpty(dataEO.getCreateDate())){
                        content.setCreateDate(dataEO.getCreateDate());
                        content.setPublishDate(dataEO.getCreateDate());
                    }
                    if(!AppUtil.isEmpty(dataEO.getUpdateDate())){
                        content.setUpdateDate(dataEO.getUpdateDate());
                    }

                    content.setIsPublish(1);
                    Long unitId = 1000L;
                    try {
                        unitId = LoginPersonUtil.getUnitId();
                    } catch (Exception e) {
                        logger.error("异步执行采集，需要填充单位ID!");
                        OrganEO organEO = organService.getEntity(OrganEO.class,taskEO.getCreateOrganId());
                        if(null != organEO) {
                            OrganEO unit = organService.getUnitByOrganDn(organEO.getDn());
                            if(null != unit) {
                                unitId = unit.getOrganId();
                            }
                        }
                    }
                    content.setUnitId(unitId);

                    Long contentId = baseContentService.saveArticleNews(content, dataEO.getContent(),null,null,null,null);
                    // 生成静态
                    returnStr += content.getSiteId()+ "_" + content.getColumnId() + "_" + contentId + ",";

                    MessageStaticEO eo = new MessageStaticEO(content.getSiteId(), content.getColumnId(), new Long[]{contentId}).setType(MessageEnum.PUBLISH.value());

                    Long userId = 1000L;
                    try {
                        userId = LoginPersonUtil.getUserId();
                    } catch (Exception e) {
                        logger.error("异步执行采集，需要填充用户ID!");
                        userId = taskEO.getCreateUserId();
                    }

                    eo.setUserId(userId);

                   /* //引用消息
                    try {
                        MessageSenderUtil
                                .publishContent(eo, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(">>>>>>发布[" + content.getTitle() + "]消息失败>>>>>>>>");
                    }*/
                } else {
                    logger.info("--已经存在标题为【" + dataEO.getTitle() + "】的新闻--");
                }
                //统一删除已引用或者有重复的数据
                htmlCollectDataDao.deleteById(id);
            }
        }
        if(!AppUtil.isEmpty(returnStr)){//去除最后的逗号
            returnStr = returnStr.substring(0,returnStr.length()-1);
        }
        return returnStr;
    }


}
