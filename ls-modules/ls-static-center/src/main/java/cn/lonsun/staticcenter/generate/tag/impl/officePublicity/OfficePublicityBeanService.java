package cn.lonsun.staticcenter.generate.tag.impl.officePublicity;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.officePublicity.internal.dao.IOfficePublicityDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangxx on 2017/2/27.
 */
@Component
public class OfficePublicityBeanService  extends AbstractBeanService{

    @Autowired
    private IOfficePublicityDao officePublicityDao;


    @Override
    public Object getObject(JSONObject paramObj) {


        Context context = ContextHolder.getContext();
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Long siteId = context.getSiteId();
        Long columnId = paramObj.getLong("id");
        Integer nums = paramObj.getInteger("nums");//相当于分页pageSize，为空则全部显示

        List values = new ArrayList();
        StringBuffer hql = new StringBuffer();
        hql.append(" select t.acceptanceItem as acceptanceItem,t.acceptanceDepartment as acceptanceDepartment,t.declarePerson as declarePerson,")
                .append(" t.declareDate as declareDate,t.shouldFinishDate as shouldFinishDate,t.officeStatus as officeStatus")
                .append(" from CmsOfficePublicityEO t")
                .append(" where recordStatus =?");

        values.add(AMockEntity.RecordStatus.Normal.toString());
        if(null != siteId) {
            hql.append(" and t.siteId=?");
            values.add(siteId);
        }
        if(null != columnId) {
            hql.append(" and t.columnId=?");
            values.add(columnId);
        }
        hql.append(" and t.checkIn = 1 order by t.createDate desc");

        //需要分页，用page接受
        Pagination page = officePublicityDao.getPagination(pageIndex,nums,hql.toString(),values.toArray(),CmsOfficePublicityEO.class);
        List<CmsOfficePublicityEO> list  = (List<CmsOfficePublicityEO>)page.getData();
        if(list != null ) {
            //前台页面分页用
            String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), columnId, null);
            page.setLinkPrefix(path);
            //转换办件状态字符串
            for(CmsOfficePublicityEO eo : list) {
                String officeStatus = eo.getOfficeStatus().toString();
                if(!AppUtil.isEmpty(officeStatus)) {
                    if(String.valueOf("Finished").equals(officeStatus)) {
                        eo.setOfficeStatus("已办结");
                    } else {
                        eo.setOfficeStatus("未办结");
                    }
                }
            }
        }

        return page;
    }

    /**
     * 预处理结果
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = new HashMap<String, Object>();
        /*map.put("linkPrefix", "");*/
        return map;
    }
}
