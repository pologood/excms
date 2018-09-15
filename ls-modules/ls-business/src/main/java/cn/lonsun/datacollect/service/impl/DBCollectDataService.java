package cn.lonsun.datacollect.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectDataDao;
import cn.lonsun.datacollect.dao.IDBCollectTaskDao;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.entity.DBCollectTaskEO;
import cn.lonsun.datacollect.service.IDBCollectDataService;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service("dbCollectDataService")
public class DBCollectDataService extends MockService<DBCollectDataEO> implements IDBCollectDataService {

    @Autowired
    private IDBCollectDataDao dbCollectDataDao;

    @Autowired
    private IDBCollectTaskDao dbCollectTaskDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return dbCollectDataDao.getPageEOs(vo);
    }

    @Override
    public void saveEO(DBCollectDataEO eo) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.saveEntity(eo);
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        dbCollectDataDao.deleteByTaskId(taskId);
    }

    @Override
    public void quoteData(Long columnId,Long cSiteId,Long[] ids) {
        for(Long id : ids) {
            DBCollectDataEO dataEO = dbCollectDataDao.getEntity(DBCollectDataEO.class,id);
            DBCollectTaskEO taskEO = dbCollectTaskDao.getEntity(DBCollectTaskEO.class,dataEO.getId());
            if(!AppUtil.isEmpty(dataEO)) {
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
                content.setArticle(dataEO.getContent());
                content.setAuthor(dataEO.getAuthor());
                content.setResources(dataEO.getResources());
//                content.setPublishDate(); //需要讨论怎么办

                //引用消息
                baseContentService.saveArticleNews(content, dataEO.getContent(),null,null,null,null);
                dbCollectDataDao.deleteById(id);
            }
        }
    }
}
