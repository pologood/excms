package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 根据部门统计部门下的主动公开和已申请公开信息数 ADD REASON. <br/>
 *
 * @date: 2016年8月26日 上午11:49:30 <br/>
 * @author liukun
 */
@Component
public class PublicOrganTjBeanService extends AbstractBeanService {

    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IPublicApplyService publicApplyService;
    @Resource
    private IOrganConfigService organConfigService;
    @Resource
    private IOrganService organService;

    /**
     * 根据部门统计部门下的主动公开和已申请公开信息数
     *
     * @throws GenerateException
     *
     * @see AbstractBeanService#getObject(JSONObject)
     */

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        // 访问路径
        Context context = ContextHolder.getContext();
        // 合并map
        MapUtil.unionContextToJson(paramObj);

        Long siteId = context.getSiteId();
        if (siteId == null) {
            siteId = Long.parseLong(context.getParamMap().get("siteId"));
            context.setSiteId(siteId);
        }

        Integer type = paramObj.getInteger("type");

        String exceptOrganId = paramObj.getString("exceptOrganId");// 不统计该单位
        String exceptCatIds = paramObj.getString("exceptCatId");// 不统计绑定该目录的单位
        List<Long> exceptOrganIds = new ArrayList<Long>();
        if (!AppUtil.isEmpty(exceptOrganId)) {
            String[] exceptOrgans = exceptOrganId.split(",");
            for (int i = 0; i < exceptOrgans.length; i++) {
                exceptOrganIds.add(Long.parseLong(exceptOrgans[i]));
            }
        }
        if (!AppUtil.isEmpty(exceptCatIds)) {
            String[] exceptCatId = exceptCatIds.split(",");
            List<Long> list;
            for (int i = 0; i < exceptCatId.length; i++) {
                list = organConfigService.getOrganIdsByCatId(Long.parseLong(exceptCatId[i]));
                if (list != null && list.size() > 0) {
                    exceptOrganIds.addAll(list);
                }
            }
        }

        Boolean isOrgan = paramObj.getBoolean("isOrgan");// 是否按单位统计，为空或者true都是按单位统计

        PublicTotalVO resultVO = new PublicTotalVO();
        resultVO.setTotal(0L);
        resultVO.setDrivingCount(0L);
        resultVO.setApplyCount(0L);
        resultVO.setReplyCount(0L);
        resultVO.setOrganCount(0);

        PublicContentQueryVO queryVO = new PublicContentQueryVO();
        PublicContentQueryVO publicContentQueryVO = new PublicContentQueryVO();
        if (null != isOrgan && !isOrgan) {// 按照部门统计
            queryVO.setOrgan(false);
        }
        queryVO.setSiteId(siteId);
        publicContentQueryVO.setSiteId(siteId);
        queryVO.setType(PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getValue());

        String startDate = paramObj.getString("startDate");
        String endDate = paramObj.getString("endDate");

        if (AppUtil.isEmpty(startDate) && AppUtil.isEmpty(endDate)) {
            if (type != null && type == 1) {// 月度
                queryVO.setStartDate(DateUtil.getMonth());
                publicContentQueryVO.setStartDate(DateUtil.getMonth());
            } else if (type != null && type == 2) {// 季度
                queryVO.setStartDate(DateUtil.getQuarter());
                publicContentQueryVO.setStartDate(DateUtil.getQuarter());
            } else if(type != null && type == 3 ){ //年度
                queryVO.setStartDate(DateUtil.getYear());
                publicContentQueryVO.setStartDate(DateUtil.getYear());
            }
        } else {
            try {
                if (!AppUtil.isEmpty(startDate)) {
                    queryVO.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
                    publicContentQueryVO.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(startDate));
                }

                if (!AppUtil.isEmpty(endDate)) {
                    queryVO.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                    publicContentQueryVO.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDate + " 23:59:59"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        List<Long> existOrganIds = new ArrayList<Long>();

        List<PublicContentVO> drivingCountList = publicContentService.getCounts(queryVO, null);
        Map<Long, Long> drivingMap = new HashMap<Long, Long>();
        Map<Long, String> organMap = new HashMap<Long, String>();
        for (PublicContentVO publicContentVO : drivingCountList) {
            if (!exceptOrganIds.contains(publicContentVO.getOrganId())) {// 不统计该单位
                drivingMap.put(publicContentVO.getOrganId(), publicContentVO.getCounts());
                // 合计
                resultVO.setDrivingCount(resultVO.getDrivingCount() + publicContentVO.getCounts());
                organMap.put(publicContentVO.getOrganId(), publicContentVO.getOrganName());
                existOrganIds.add(publicContentVO.getOrganId());//有数据的单位
            }
        }

        PublicTotalVO applyCountVO = publicApplyService.getPublicTotalVO(publicContentQueryVO);
        Map<Long, Long> applyMap = new HashMap<Long, Long>();
        Map<Long, Long> replyMap = new HashMap<Long, Long>();
        if (!AppUtil.isEmpty(applyCountVO) && !AppUtil.isEmpty(applyCountVO.getData())) {
            for (PublicTjVO publicTjVO : applyCountVO.getData()) {
                if (!exceptOrganIds.contains(publicTjVO.getOrganId())) {
                    applyMap.put(publicTjVO.getOrganId(), publicTjVO.getApplyCount());
                    replyMap.put(publicTjVO.getOrganId(), publicTjVO.getReplyCount());
                    // 合计
                    resultVO.setApplyCount(resultVO.getApplyCount() + publicTjVO.getApplyCount());
                    resultVO.setReplyCount(resultVO.getReplyCount() + publicTjVO.getReplyCount());
                    if (!organMap.containsKey(publicTjVO.getOrganId())) {
                        organMap.put(publicTjVO.getOrganId(), publicTjVO.getOrganName());
                        existOrganIds.add(publicTjVO.getOrganId());//有数据的单位
                    }
                }
            }
        }

        //查询出所有的信息公开单位
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, queryVO.getSiteId());
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        if (null != organList && !organList.isEmpty()) {
            PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
            PublicCatalogUtil.sortOrgan(organVOList);// 排序
        }
        for(OrganVO organVO:organVOList){
            if(!existOrganIds.contains(organVO.getOrganId())){
                organMap.put(organVO.getOrganId(), organVO.getName());//将没更新数据的单位添加到列表中
            }
        }



        Map<Long, Long> totalMap = new HashMap<Long, Long>();
        for (Map.Entry<Long, String> entry : organMap.entrySet()) {
            Long driving = drivingMap.get(entry.getKey()) == null ? 0L : drivingMap.get(entry.getKey());
            Long apply = applyMap.get(entry.getKey()) == null ? 0L : applyMap.get(entry.getKey());
            totalMap.put(entry.getKey(), driving + apply);
        }

        ArrayList<Map.Entry<Long, Long>> entries = sortMap(totalMap);
        List<PublicTjVO> resultList = new ArrayList<PublicTjVO>();
        for (Map.Entry<Long, Long> map : entries) {
            PublicTjVO vo = new PublicTjVO();
            vo.setOrganId(map.getKey());
            vo.setOrganName(organMap.get(map.getKey()));
            vo.setTotal(map.getValue());
            vo.setApplyCount(applyMap.get(map.getKey()) == null ? 0L : applyMap.get(map.getKey()));
            vo.setReplyCount(replyMap.get(map.getKey()) == null ? 0L : replyMap.get(map.getKey()));
            vo.setDrivingCount(drivingMap.get(map.getKey()) == null ? 0L : drivingMap.get(map.getKey()));
            resultList.add(vo);
            resultVO.setTotal(resultVO.getTotal() + map.getValue());
        }
        resultVO.setData(resultList);
        resultVO.setOrganCount(organMap.size());

        return resultVO;
    }

    public static ArrayList<Map.Entry<Long, Long>> sortMap(Map map) {
        List<Map.Entry<Long, Long>> entries = new ArrayList<Map.Entry<Long, Long>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Long, Long>>() {
            public int compare(Map.Entry<Long, Long> obj1, Map.Entry<Long, Long> obj2) {
                return (int) (obj2.getValue() - obj1.getValue());
            }
        });
        return (ArrayList<Map.Entry<Long, Long>>) entries;
    }
}