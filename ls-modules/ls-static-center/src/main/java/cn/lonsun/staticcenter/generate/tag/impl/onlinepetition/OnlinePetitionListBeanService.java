package cn.lonsun.staticcenter.generate.tag.impl.onlinepetition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.lonsun.base.anno.DbInject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.ModelConfigUtil;

import com.alibaba.fastjson.JSONObject;

import static oracle.net.aso.C01.o;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-16<br/>
 */
@Component
public class OnlinePetitionListBeanService extends AbstractBeanService {
    @DbInject("baseContent")
    private IBaseContentDao contentDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.fileDownload.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        String inIds = paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isNotEmpty(inIds)) {// 当传入多栏目时，依第一个栏目为准
            columnId = Long.valueOf(StringUtils.split(inIds, ",")[0]);
        }
        Integer size = ids.length;
        Map<String, Object> map = new HashMap<String, Object>();
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        String where = paramObj.getString(GenerateConstant.WHERE);
        StringBuffer hql = new StringBuffer();
        hql.append(" FROM BaseContentEO c WHERE c.columnId ").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(" AND c.isPublish=1 AND c.recordStatus =:recordStatus AND c.siteId=:siteId");
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
        hql.append(" order by c.create_date desc,c.id desc");
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", ContextHolder.getContext().getSiteId());
        return contentDao.getEntities(hql.toString(), map, num);
    }

    /**
     *
     * 预处理数据
     *
     * @throws GenerateException
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<BaseContentEO> list = (List<BaseContentEO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (BaseContentEO eo : list) {
                String path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());//拿到栏目页和文章页id
                //String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), eo.getColumnId(), eo.getId());
                eo.setLink(path);
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}