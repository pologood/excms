package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.staticcenter.exproject.ahszjj.AhszjjConstant.siteId;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavBeanService extends AbstractBeanService {

    private static Logger logger = LoggerFactory.getLogger(OnlineNavBeanService.class);

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IContentModelService contentModelService;


    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();

        Map<String,Object> map = new HashMap<String, Object>();

        Map<String, String> pmap = context.getParamMap();
        boolean organShow = false;
        boolean subshow = true;
        String columnName = null;
        String columnIds = null;
        Long parentColumnId = null;
        if(pmap != null && pmap.size() > 0) {
            if(!AppUtil.isEmpty(pmap.get("organShow"))) {
                organShow = Boolean.parseBoolean(pmap.get("organShow"));
            }

            if(!AppUtil.isEmpty(pmap.get("subshow"))) {
                subshow = Boolean.parseBoolean(pmap.get("subshow"));
            }

            if(!AppUtil.isEmpty(pmap.get("columnName"))) {
                columnName = String.valueOf(pmap.get("columnName"));
                map.put("columnName",columnName);
            }

            if(!AppUtil.isEmpty(pmap.get("parentColumnId"))) {
                parentColumnId = Long.valueOf(pmap.get("parentColumnId"));
                map.put("parentColumnId",parentColumnId);
            }

            if(!AppUtil.isEmpty(pmap.get("columnIds"))) {
                columnIds = String.valueOf(pmap.get("columnIds"));
                map.put("columnIds",columnIds);
            }
        }

        if(organShow) {
            //显示单位
            Long organId = null;
            if(pmap != null && pmap.size() > 0) {
                if(!AppUtil.isEmpty(pmap.get("organId"))) {
                    organId = Long.valueOf(pmap.get("organId"));
                }
            }

            List<OrganEO> result = new ArrayList<OrganEO>();
            if(null != columnIds) {
                List<Long> columnIdList = StringUtils.getListWithLong(columnIds,",");
                List<ContentModelParaVO> assem = new ArrayList<ContentModelParaVO>();
                for(Long columnId : columnIdList) {
                    List<ContentModelParaVO> single = ModelConfigUtil.getParam(columnId,siteId,null);
                    if(null != single) {
                        assem.addAll(single);
                    }
                }
                for(ContentModelParaVO vo : assem) {
                    OrganEO eo = new OrganEO();
                    eo.setOrganId(vo.getRecUnitId());
                    eo.setName(vo.getRecUnitName());
                    if(!result.contains(eo)) {
                        result.add(eo);
                    }
                }
            } else {
                //获取所有网上办事绑定的单位
                result = contentModelService.getAllBindUnit(ContextHolder.getContext().getSiteId());
            }

            List<OrganEO> listtemp = new ArrayList<OrganEO>();
            if(null != result && !result.isEmpty()) {
                for(OrganEO eo : result) {
                    if(null != eo && null != eo.getOrganId()) {
                        if(null != organId && eo.getOrganId().intValue() == organId.intValue()) {
                            eo.setActive("active");
                        }
                        listtemp.add(eo);
                    }
                }
            }
            map.put("eos",listtemp);
            map.put("gotoId",context.getColumnId());
            map.put("organShow",true);
        } else {

            //显示条数
            int num = paramObj.getInteger("num");
            PageQueryVO vo = null;
            if(!AppUtil.isEmpty(num)) {
                vo = new PageQueryVO();
                vo.setPageIndex(0L);
                vo.setPageSize(num);
            }

            //来自传参的ID
            Long columnId = context.getColumnId();

            //查询条件
            String where = paramObj.getString("where");

            //排除不显示的栏目
            String exclude = null;
            try {
                exclude = paramObj.getString("excludeColumnIds");
            } catch (Exception e) {
                logger.error(e.getMessage());
                exclude = null;
            }
            logger.info(">>>>>>排除栏目Ids:" + exclude);
            List<Long> idlist = null;
            if (!AppUtil.isEmpty(exclude)) {
                idlist = StringUtils.getListWithLong(exclude, ",");
            }

            List<ColumnMgrEO> list = columnConfigService.getLevelColumnTree(columnId,new int[]{2},where,vo);
            List<ColumnMgrEO> temp = new ArrayList<ColumnMgrEO>();
            if(subshow && (null == list || list.size() <=0)) {
                IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class,columnId);
                list = columnConfigService.getLevelColumnTree(eo.getParentId(),new int[]{2},where,vo);
                for(ColumnMgrEO _eo : list) {
                    if(_eo.getIndicatorId().intValue() == columnId.intValue()) {
                        _eo.setActive("active");
                    }
                }
            }

            for(ColumnMgrEO eo : list) {
                if (!AppUtil.isEmpty(exclude)) {
                    if (!idlist.contains(eo.getIndicatorId())) {
                        temp.add(eo);
                    }
                } else {
                    temp.add(eo);
                }
            }

            map.put("eos",temp);
            map.put("organShow",false);
        }
        return map;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Map<String, Object> map = (Map<String, Object>) resultObj;
        return map;
    }
}
