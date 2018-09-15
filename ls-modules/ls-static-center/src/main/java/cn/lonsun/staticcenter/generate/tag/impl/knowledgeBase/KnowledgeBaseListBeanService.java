package cn.lonsun.staticcenter.generate.tag.impl.knowledgeBase;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IKnowledgeBaseDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhangmf
 * @version 2018-06-26 14:48
 */
@Component
public class KnowledgeBaseListBeanService extends AbstractBeanService {

    @Autowired
    IKnowledgeBaseDao knowledgeBaseDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        Long columnId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            siteId = context.getSiteId();
        } else {
            siteId = Long.valueOf(strSiteId);
        }
        columnId = context.getColumnId();
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);

        ContentPageVO query = new ContentPageVO();
        if (!AppUtil.isEmpty(siteId)) {
            query.setSiteId(siteId);
        }
        if (!AppUtil.isEmpty(columnId)) {
            query.setColumnId(columnId);
        }
        query.setIsPublish(1);
        query.setTypeCode(BaseContentEO.TypeCode.knowledgeBase.toString());
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        query.setNoAuthority(true);

        return knowledgeBaseDao.getPage(query);
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
                    KnowledgeBaseVO vo = (KnowledgeBaseVO) o;
                    String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getContentId());// 拿到栏目页和文章页id
                    vo.setRedirectLink(path);
                }
            }
            // 设置连接地址
            if (context.getColumnId() != null) {
                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
                pagination.setLinkPrefix(path);
            } else {
                pagination.setLinkPrefix(context.getPath());
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}
