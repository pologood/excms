package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.dao.IColumnConfigDao;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.ColumnUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 * 获取子栏目
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-2<br/>
 */
@Component
public class ChildColumnBeanService extends AbstractBeanService {

    @DbInject("columnConfig")
    private IColumnConfigDao configDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        Boolean isPage = paramObj.getBoolean("isPage");
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger("pageSize");
        if (columnId == null) {
            return null;
        }
        Long siteId = context.getSiteId();
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        if (indicatorEO == null) {
            return null;
        }
        Integer num = paramObj.getInteger("num");
        List<ColumnMgrEO> list1 = new ArrayList<ColumnMgrEO>();
        List<ColumnMgrEO> list = null;
        Pagination pagination = new Pagination();
        if(null != isPage && isPage){
            pagination = getColumnByParentId(pageIndex,pageSize,columnId,false,siteId);
            System.out.println("==================分页对象===================" + JSONObject.toJSONString(pagination));
            list = (List<ColumnMgrEO>)pagination.getData();
            System.out.println("==================列表===================" + JSONObject.toJSONString(list));
        }else{
            list = ColumnUtil.getColumnByPId(columnId, siteId);
        }
        if (list != null && list.size() > 0) {
            for (ColumnMgrEO eo : list) {
                if (num != null && num != 0 && num <= list1.size()) {
                    break;
                }
                if (eo.getIsShow() == 1 && !"linksMgr".equals(eo.getColumnTypeCode())) {
                    list1.add(eo);
                }
            }
        }
        if(null != isPage && isPage){
            System.out.println("==================栏目条数===================" + list1.size());
            pagination.setData(list1);
            return pagination;
        }
        return list1;
    }

    private Pagination getColumnByParentId(Long pageIndex,Integer pageSize,Long indicatorId, boolean flag, Long siteId) {
        StringBuffer str = new StringBuffer(" select r.indicatorId as indicatorId,r.name as name,r.parentId as parentId, r.urlPath as urlPath,r.sortNum as sortNum,")
                .append("r.isParent as isParent,r.createDate as createDate,r.type as type,r.contentModelCode as contentModelCode,r.columnTypeCode as columnTypeCode,")
                .append("r.isStartUrl as isStartUrl,r.transWindow as transWindow,r.transUrl as transUrl,r.genePageIds as genePageIds,r.genePageNames as genePageNames,")
                .append("r.synColumnIds as synColumnIds,r.synColumnNames as synColumnNames,r.keyWords as keyWords,r.description as description,r.content as content,")
                .append("r.referColumnIds as referColumnIds,r.referColumnNames as referColumnNames,r.referOrganCatIds as referOrganCatIds,r.referOrganCatNames as referOrganCatNames ")
                .append(" from ColumnMgrEO r where r.parentId=" + indicatorId);

        if (flag) {
            str.append(" and (r.siteId is null or r.siteId=" + siteId + ")");
        } else {
            str.append(" and r.siteId=" + siteId);
        }

        str.append(" order by r.sortNum asc,r.createDate asc");
        Pagination pagination = configDao.getPagination(pageIndex,pageSize,str.toString(), new Object[]{}, ColumnMgrEO.class);
        return pagination;

    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理栏目页链接问题
        Boolean isPage = paramObj.getBoolean("isPage");
        List<ColumnMgrEO> list = null;
        if(null != isPage && isPage){
            list = (List<ColumnMgrEO>)((Pagination)resultObj).getData();
        }else{
            list = (List<ColumnMgrEO>) resultObj;
        }
        if (null != list && !list.isEmpty()) {
            // 处理栏目链接
            for (ColumnMgrEO eo : list) {
                String path = "";
                if (eo.getIsStartUrl() == 1) {
                    path = eo.getTransUrl();
                } else {
                    path = PathUtil.getLinkPath(eo.getIndicatorId(), null);
                }
                eo.setUri(path);
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}