package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用文章新闻和信息公开目录信息
 * Created by fth on 2016/11/14.
 */
@Component
public class ArticleNewsWithPublicInfoBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        AssertUtil.isEmpty(siteId, "siteId不为空");
        Long organId = paramObj.getLong("organId");//单位id
        boolean existOrgan = null != organId && organId > 0L;//单位是否为空
        String columnIds = paramObj.getString("columnIds");//栏目ids
        String catIds = paramObj.getString("catIds");//目录ids
        AssertUtil.andEmpty(columnIds, catIds, "columnIds和catIds必须传入一个");
        String action = ObjectUtils.defaultIfNull(paramObj.getString("action"), "num");//分页和列表，默认查询条数
        String order = paramObj.getString("order");
        AssertUtil.isEmpty(order, "order不为空");//排序值不能为空

        boolean isSql = false;//是否sql查询
        String[] queryFileds = new String[]{"id", "siteId", "columnId", "typeCode", "title", "titleColor", "subTitle",
                "isBold", "isUnderline", "isTilt", "isNew", "num", "resources", "isHot", "isTop", "topValidDate", "redirectLink"
                , "imageLink", "isTitle", "isPublish", "publishDate", "hit", "author", "editor", "responsibilityEditor", "contentPath",
                "remarks", "quoteStatus", "isAllowComments", "workFlowStatus", "isJob", "jobIssueDate", "unitId", "attachSavedName",
                "attachRealName", "attachSize", "handleStatus "};
        StringBuffer hql = new StringBuffer();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isEmpty(columnIds)) {//没有传栏目就只查信息公开
            hql.append(" from BaseContentEO b where b.siteId = :siteId and b.typeCode = :typeCode and b.recordStatus = :recordStatus");
            paramMap.put("siteId", siteId);
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            paramMap.put("typeCode", BaseContentEO.TypeCode.public_content.toString());
            hql.append(" and exists (select 1 from PublicContentEO p where b.id = p.contentId and p.siteId = b.siteId ");
            hql.append(" and b.columnId = p.organId and p.catId in (:catIds) and p.recordStatus = :recordStatus");
            paramMap.put("catIds", cn.lonsun.core.base.util.StringUtils.getArrayWithLong(catIds, ","));
            if (existOrgan) {
                hql.append(" and p.organId = :organId ");
                paramMap.put("organId", organId);
            }
            hql.append(" and p.type =:type) ");
            hql.append(order);//order by
            paramMap.put("type", PublicContentEO.Type.DRIVING_PUBLIC.toString());
        } else if (StringUtils.isEmpty(catIds)) {//没有传目录就只查文章新闻
            hql.append(" from BaseContentEO b where b.siteId = :siteId and b.typeCode = :typeCode ");
            hql.append(" and b.columnId in (:columnIds) and b.recordStatus = :recordStatus ");
            hql.append(order);//order by
            paramMap.put("siteId", siteId);
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            paramMap.put("typeCode", BaseContentEO.TypeCode.articleNews.toString());
            paramMap.put("columnIds", cn.lonsun.core.base.util.StringUtils.getArrayWithLong(columnIds, ","));
        } else {// 合并查询
            isSql = true;
            hql.append("select b.id,b.site_id siteId,b.column_id columnId,b.type_code typeCode,b.title,b.title_color titleColor,");
            hql.append("b.sub_title subTitle,b.is_bold isBold,b.is_underline isUnderline,b.is_tilt isTilt,b.is_new isNew,b.num,b.resources,");
            hql.append("b.is_hot isHot,b.is_top isTop,b.top_valid_date topValidDate,b.redirect_link redirectLink,b.image_link imageLink,b.is_title isTitle,");
            hql.append("b.is_publish isPublish,b.publish_date publishDate,b.hit,b.author,b.editor,b.responsibility_editor responsibilityEditor,");
            hql.append("b.content_path contentPath,b.remarks,b.quote_status quoteStatus,b.is_allow_comments isAllowComments,b.work_flow_status workFlowStatus,");
            hql.append("b.is_job isJob,b.job_issue_date jobIssueDate,b.unit_id unitId,b.attach_saved_name attachSavedName,b.attach_real_name attachRealName,");
            hql.append("b.attach_size attachSize,b.handle_status handleStatus");
            hql.append(" from (select * from cms_base_content bb where bb.site_id = :siteId and bb.type_code = :typeCode1 ");
            hql.append(" and bb.column_id in (:columnIds) and bb.record_status = :recordStatus union all select * from cms_base_content c ");
            hql.append(" where c.type_code = :typeCode2 and c.id in (select p.content_id from cms_public_content p where p.site_id = :siteId ");
            hql.append(" and c.column_id = p.organ_id and p.cat_id in (:catIds) and p.record_status = :recordStatus ");
            if (existOrgan) {
                hql.append(" and p.organ_id = :organId ");
                paramMap.put("organId", organId);
            }
            String newOrder = order.toLowerCase();
            hql.append(" and p.type = :type )) b ").append(newOrder.replace("publishdate", "publish_date"));
            paramMap.put("siteId", siteId);
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            paramMap.put("typeCode1", BaseContentEO.TypeCode.articleNews.toString());
            paramMap.put("typeCode2", BaseContentEO.TypeCode.public_content.toString());
            paramMap.put("columnIds", cn.lonsun.core.base.util.StringUtils.getArrayWithLong(columnIds, ","));
            paramMap.put("catIds", cn.lonsun.core.base.util.StringUtils.getArrayWithLong(catIds, ","));
            paramMap.put("type", PublicContentEO.Type.DRIVING_PUBLIC.toString());
        }
        if ("num".equals(action)) {
            Integer num = paramObj.getInteger(GenerateConstant.NUM);// 标签中定义
            if (isSql) {
                return baseContentDao.getBeansBySql(hql.toString(), paramMap, BaseContentEO.class, queryFileds, num);
            }
            return baseContentDao.getEntities(hql.toString(), paramMap, num);
        }
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        if (isSql) {
            return baseContentDao.getPaginationBySql(pageIndex, pageSize, hql.toString(), paramMap, BaseContentEO.class, queryFileds);
        }
        return baseContentDao.getPagination(pageIndex, pageSize, hql.toString(), paramMap);
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        String action = ObjectUtils.defaultIfNull(paramObj.getString("action"), "num");//分页和列表，默认查询条数
        List<?> resultList = null;
        if ("num".equals(action)) {
            resultList = (List<?>) resultObj;
        } else {
            Pagination pagination = (Pagination) resultObj;
            resultList = pagination.getData();
        }
        // 处理查询结果
        if (null != resultList && !resultList.isEmpty()) {
            for (Object o : resultList) {
                BaseContentEO eo = (BaseContentEO) o;
                if (StringUtils.isNotEmpty(eo.getRedirectLink())) {
                    eo.setLink(eo.getRedirectLink());
                } else {
                    eo.setLink(PathUtil.getLinkPath(null, eo.getColumnId(), eo.getId()));// 拿到栏目页和文章页id
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}