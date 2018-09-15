
package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取指定栏目下第一条带缩略图的新闻和指定数量（不包含前面带缩略图的）的新闻列表 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2016年11月15日 <br/>
 */
@Component
public class IndexPicDocListBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;
    @Autowired
    private IOrganService organService;
    /**
     * 查询文章列表
     *
     * @see AbstractBeanService
     */
    @Override
    public Object getObject(JSONObject paramObj) {

        Context context = ContextHolder.getContext();
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.articleNews.toString());

        // 站点ID
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            siteId = context.getSiteId();
        } else {
            siteId = Long.valueOf(strSiteId);
        }

        Integer num = paramObj.getInteger(GenerateConstant.NUM);

        BaseContentEO eo = getIndexPicNews(siteId,ids);

        List<BaseContentEO> newsList = getNewsList(siteId,ids,num,eo);

        Map<String,Object> resultMap = new HashMap<String, Object>();

        resultMap.put("picEO",eo);

        resultMap.put("newsList",newsList);

        return resultMap;
    }


    /**
     * 获取栏目下第一条带缩略图的新闻
     * @param siteId
     * @param ids
     * @return
     */
    private BaseContentEO getIndexPicNews(Long siteId, Long[] ids){
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus AND c.imageLink <> '' AND c.imageLink IS NOT NULL ");

        hql.append(ModelConfigUtil.getOrderByHql(ids[0],siteId, BaseContentEO.TypeCode.articleNews.toString()));

        map.put("ids", ids);
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, 1);
        BaseContentEO eo = null;
        if(list!=null&&list.size()>0){
            eo = list.get(0);
            //设置跳转路径
            String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
            eo.setLink(path);
            //获取文章内容并去除空格符
            String article = MongoUtil.queryById(eo.getId());
            eo.setArticle(article.replace("&nbsp;",""));
        }
        return eo;
    }


    /**
     * 获取栏目下不包含第一条带缩略图新闻的指定数量的新闻
     * @param siteId
     * @param ids
     * @param num
     * @param exceptEO
     * @return
     */
    private List<BaseContentEO> getNewsList(Long siteId, Long[] ids, Integer num, BaseContentEO exceptEO){
        Map<String, Object> map = new HashMap<String, Object>();

        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus");

        if(exceptEO!=null){//去除第一条带缩略图新闻
            hql.append(" AND c.id <> :id ");
            map.put("id", exceptEO.getId());
        }

        hql.append(ModelConfigUtil.getOrderByHql(ids[0],siteId, BaseContentEO.TypeCode.articleNews.toString()));

        map.put("ids", ids);
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, num);
        if(list!=null&&list.size()>0){
            for(BaseContentEO eo : list){
                //设置跳转路径
                String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
                eo.setLink(path);

                if(!AppUtil.isEmpty(eo.getCreateOrganId())){
                    OrganEO organ = organService.getDirectlyUpLevelUnit(eo.getCreateOrganId());
                    if(organ!=null){
                        eo.setOrganName(organ.getName());
                        eo.setOrganId(organ.getOrganId());
                    }
                }
            }

        }
        return list;
    }

}