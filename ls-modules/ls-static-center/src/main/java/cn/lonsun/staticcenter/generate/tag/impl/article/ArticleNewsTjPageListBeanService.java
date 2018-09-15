package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ArticleNewsTjPageListBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentService baseContentService;
    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        Long organId = paramObj.getLong("organId");
        if (null == organId) {// 如果单位id为空说明，栏目id是在页面传入的
            organId = context.getColumnId();
        }
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        List<Long> organlist = organService.getDescendantOrganIds(organId);

        return baseContentService.getPageByOrganIds(organlist,siteId,BaseContentEO.TypeCode.articleNews.toString(),pageIndex,pageSize);
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 获取分页对象
        Pagination pagination = (Pagination) resultObj;
        if (null != pagination) {
            Context context = ContextHolder.getContext();// 上下文
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    BaseContentEO eo = (BaseContentEO) o;
                    String path = "";
                    if (!AppUtil.isEmpty(eo.getRedirectLink())) {
                        path = eo.getRedirectLink();
                    } else {
                        path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());// 拿到栏目页和文章页id
                    }
                    eo.setLink(path);
                }
            }
            // 设置连接地址
            String path = context.getPath();
            pagination.setLinkPrefix(path);
        }
        return super.doProcess(resultObj, paramObj);
    }
}