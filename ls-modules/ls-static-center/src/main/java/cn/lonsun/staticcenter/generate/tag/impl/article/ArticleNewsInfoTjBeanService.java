package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentTjVO;
import cn.lonsun.content.vo.ContentTotalVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class ArticleNewsInfoTjBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentService baseContentService;
    @Resource
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String strId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (NumberUtils.isNumber(strId)) {
            siteId = NumberUtils.toLong(strId);
        } else {
            siteId = context.getSiteId();
        }
        context.setSiteId(siteId);
        Integer type = paramObj.getInteger("type");

        ContentTotalVO resultVO = new ContentTotalVO();
        resultVO.setLastYearCount(0L);
        resultVO.setThisYearCount(0L);
        resultVO.setThisMonthCount(0L);
        resultVO.setTotal(0L);

        Integer isPublish = 1;
        String typeCode = BaseContentEO.TypeCode.articleNews.toString();

        //发文总数
        List<ContentTjVO> totalList = baseContentService.getCountByCondition(siteId,typeCode,isPublish,null,null);
        Date st = DateUtil.getYear();//今年的开始日期
        List<ContentTjVO> thisYearList = baseContentService.getCountByCondition(siteId,typeCode,isPublish,st,null);
        st=DateUtil.getLastYear();
        Date ed = DateUtil.getYear();
        List<ContentTjVO> lastYearList = baseContentService.getCountByCondition(siteId,typeCode,isPublish,st,ed);
        st=DateUtil.getMonth();
        List<ContentTjVO> thisMonthList = baseContentService.getCountByCondition(siteId,typeCode,isPublish,st,null);

        Long[] organIds = new Long[totalList.size()];

        for(int i=0;i<totalList.size();i++){
            organIds[i] = totalList.get(i).getOrganId();
        }
        List<OrganEO> units = organService.getDirrectlyUpLevelUnits(organIds);//查询直属单位
        Map<Long,OrganEO> organMap = new HashMap<Long, OrganEO>();
        Map<Long,String> unitMap = new HashMap<Long, String>();
        Map<Long,Long> totalMap = new HashMap<Long, Long>();
        Map<Long,Long> thisYearMap = new HashMap<Long, Long>();
        Map<Long,Long> lastYearMap = new HashMap<Long, Long>();
        Map<Long,Long> thisMonthMap = new HashMap<Long, Long>();
        for(int i=0;i<totalList.size();i++){
            organMap.put(totalList.get(i).getOrganId(),units.get(i));
            unitMap.put(units.get(i).getOrganId(),units.get(i).getName());

            //初始值全部为0
            totalMap.put(units.get(i).getOrganId(),0L);
            thisYearMap.put(units.get(i).getOrganId(),0L);
            lastYearMap.put(units.get(i).getOrganId(),0L);
            thisMonthMap.put(units.get(i).getOrganId(),0L);
        }

        //查询结果汇总到上级单位
        totalMap = hz(totalList,organMap,totalMap);
        thisYearMap = hz(thisYearList,organMap,thisYearMap);
        lastYearMap = hz(lastYearList,organMap,lastYearMap);
        thisMonthMap = hz(thisMonthList,organMap,thisMonthMap);

        List<ContentTjVO> resultList = new ArrayList<ContentTjVO>();
        ArrayList<Map.Entry<Long, Long>> entries ;
        if(type == 0){//总数排行
            entries = sortMap(totalMap);
            for(int i=0;i<entries.size();i++){
                ContentTjVO contentTjVO = new ContentTjVO();
                Long organId = entries.get(i).getKey();
                contentTjVO.setTotal(entries.get(i).getValue());
                contentTjVO.setLastYearCount(lastYearMap.get(organId));
                contentTjVO.setThisYearCount(thisYearMap.get(organId));
                contentTjVO.setThisMonthCount(thisMonthMap.get(organId));
                contentTjVO.setOrganId(organId);
                contentTjVO.setOrganName(unitMap.get(organId));
                resultList.add(contentTjVO);

                //合计
                resultVO.setThisMonthCount(resultVO.getThisMonthCount()+thisMonthMap.get(organId));
                resultVO.setLastYearCount(resultVO.getLastYearCount()+lastYearMap.get(organId));
                resultVO.setThisYearCount(resultVO.getThisYearCount()+thisYearMap.get(organId));
                resultVO.setTotal(resultVO.getTotal()+totalMap.get(organId));
            }
        }else if(type == 1){//本年
            entries = sortMap(thisYearMap);
            for(int i=0;i<entries.size();i++){
                ContentTjVO contentTjVO = new ContentTjVO();
                Long organId = entries.get(i).getKey();
                contentTjVO.setThisYearCount(entries.get(i).getValue());
                contentTjVO.setLastYearCount(lastYearMap.get(organId));
                contentTjVO.setThisMonthCount(thisMonthMap.get(organId));
                contentTjVO.setTotal(totalMap.get(organId));
                contentTjVO.setOrganId(organId);
                contentTjVO.setOrganName(unitMap.get(organId));
                resultList.add(contentTjVO);

                //合计
                resultVO.setThisMonthCount(resultVO.getThisMonthCount()+thisMonthMap.get(organId));
                resultVO.setLastYearCount(resultVO.getLastYearCount()+lastYearMap.get(organId));
                resultVO.setThisYearCount(resultVO.getThisYearCount()+thisYearMap.get(organId));
                resultVO.setTotal(resultVO.getTotal()+totalMap.get(organId));
            }
        }else if(type == 2){//上年
            entries = sortMap(lastYearMap);
            for(int i=0;i<entries.size();i++){
                ContentTjVO contentTjVO = new ContentTjVO();
                Long organId = entries.get(i).getKey();
                contentTjVO.setLastYearCount(entries.get(i).getValue());
                contentTjVO.setThisYearCount(thisYearMap.get(organId));
                contentTjVO.setThisMonthCount(thisMonthMap.get(organId));
                contentTjVO.setTotal(totalMap.get(organId));
                contentTjVO.setOrganId(organId);
                contentTjVO.setOrganName(unitMap.get(organId));
                resultList.add(contentTjVO);

                //合计
                resultVO.setThisMonthCount(resultVO.getThisMonthCount()+thisMonthMap.get(organId));
                resultVO.setLastYearCount(resultVO.getLastYearCount()+lastYearMap.get(organId));
                resultVO.setThisYearCount(resultVO.getThisYearCount()+thisYearMap.get(organId));
                resultVO.setTotal(resultVO.getTotal()+totalMap.get(organId));
            }
        }else if(type == 3){//本月
            entries = sortMap(thisMonthMap);
            for(int i=0;i<entries.size();i++){
                ContentTjVO contentTjVO = new ContentTjVO();
                Long organId = entries.get(i).getKey();
                contentTjVO.setThisMonthCount(entries.get(i).getValue());
                contentTjVO.setThisYearCount(thisYearMap.get(organId));
                contentTjVO.setLastYearCount(lastYearMap.get(organId));
                contentTjVO.setTotal(totalMap.get(organId));
                contentTjVO.setOrganId(organId);
                contentTjVO.setOrganName(unitMap.get(organId));
                resultList.add(contentTjVO);

                //合计
                resultVO.setThisMonthCount(resultVO.getThisMonthCount()+thisMonthMap.get(organId));
                resultVO.setLastYearCount(resultVO.getLastYearCount()+lastYearMap.get(organId));
                resultVO.setThisYearCount(resultVO.getThisYearCount()+thisYearMap.get(organId));
                resultVO.setTotal(resultVO.getTotal()+totalMap.get(organId));
            }
        }

        resultVO.setOrganCount(unitMap.size());
        resultVO.setData(resultList);
        return resultVO;
    }


    /**
     * 将部门的数据汇总到上级单位
     * @return
     */
    private Map<Long,Long> hz(List<ContentTjVO> list,Map<Long,OrganEO> organMap,Map<Long,Long> map){
        for(ContentTjVO vo:list){
            Long unitId = organMap.get(vo.getOrganId()).getOrganId();
            Long sum = map.get(unitId) + vo.getTotal();
            map.put(unitId,sum);
        }
        return map;
    }

    public static ArrayList<Map.Entry<Long,Long>> sortMap(Map map){
        List<Map.Entry<Long, Long>> entries = new ArrayList<Map.Entry<Long, Long>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Long, Long>>() {
            public int compare(Map.Entry<Long, Long> obj1 , Map.Entry<Long, Long> obj2) {
                return (int)(obj2.getValue() - obj1.getValue());
            }
        });
        return (ArrayList<Map.Entry<Long, Long>>) entries;
    }

}