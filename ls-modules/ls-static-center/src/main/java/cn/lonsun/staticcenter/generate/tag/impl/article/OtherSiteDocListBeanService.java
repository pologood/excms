package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用其他站点文章列表页 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2016年11月09日 <br/>
 */
@Component
public class OtherSiteDocListBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao contentDao;
    @Autowired
    private IOrganService organService;
    /**
     * 查询文章列表
     *
     * @see AbstractBeanService#getObject(Object)
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.articleNews.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        String inIds = paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isNotEmpty(inIds)) {// 当传入多栏目时，依第一个栏目为准
            columnId = Long.valueOf(StringUtils.split(inIds, ",")[0]);
        }

        // 传入其他站点站点ID
        Long siteId = paramObj.getLong("site");

        Boolean isTitleTop = paramObj.getBoolean("isTitleTop");

        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        StringBuffer hql = new StringBuffer();
        hql.append("FROM BaseContentEO c WHERE c.columnId ").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(" AND c.siteId=:siteId AND c.isPublish=1 AND c.recordStatus =:recordStatus ");
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
        hql.append(ModelConfigUtil.getOrderByHql(columnId, context.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("siteId", siteId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        List<BaseContentEO> list = contentDao.getEntities(hql.toString(), map, num);
        List<BaseContentEO> resultList = new ArrayList<BaseContentEO>();
        List<BaseContentEO> tempList = new ArrayList<BaseContentEO>();
        if(isTitleTop!=null&&isTitleTop){//标题新闻放列表最前面
            for (BaseContentEO eo : list) {
                if (eo.getIsTitle()!=null&&eo.getIsTitle()==1){
                    resultList.add(eo);
                }else{
                    tempList.add(eo);
                }
            }
            resultList.addAll(tempList);
        }else{
            resultList.addAll(list);
        }
        return resultList;
    }

    /**
     * 预处理数据
     *
     * @throws GenerateException
     * @see AbstractBeanService#doProcess(Object,
     * Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<BaseContentEO> list = (List<BaseContentEO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (BaseContentEO eo : list) {
                String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());
                eo.setLink(path);

                if(!AppUtil.isEmpty(eo.getCreateOrganId())){
                    OrganEO organ = organService.getDirectlyUpLevelUnit(eo.getCreateOrganId());
                    if(organ!=null){
                        eo.setOrganId(organ.getOrganId());
                        eo.setOrganName(organ.getName());
                    }
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}