package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 根据目录名查询相关联的单位信息 ADD REASON. <br/>
 *
 * @date: 2016年8月13日 上午11:49:30 <br/>
 * @author liukun
 */
@Component
public class PublicUnitInfoListBeanService extends AbstractBeanService {

    @Autowired
    private IOrganConfigService organConfigService;

    @Autowired
    private IPublicCatalogService publicCatalogService;

    @Autowired
    private IOrganService organService;

    /**
     * 根据目录名查询相关联的单位信息
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

        if(AppUtil.isEmpty(paramObj.getString("catNames"))){
            return new HashMap<String, Object>();
        }

        String[] catNames = AppUtil.isEmpty(paramObj.getString("catNames"))?null:paramObj.getString("catNames").split(",");

//        String[] catIds = AppUtil.isEmpty(paramObj.getString("catIds"))?null:paramObj.getString("catIds").split(",");

        List<OrganConfigEO> organConfigList = organConfigService.getEntities(OrganConfigEO.class, new HashMap<String, Object>());

        List<Long> organIds = new ArrayList<Long>();
        Map<Long,String> map = new HashMap<Long, String>();

        List<Long> catIds = new ArrayList<Long>();

        if(organConfigList!=null){
            for(OrganConfigEO organConfigEO:organConfigList){
                map.put(organConfigEO.getOrganId(),organConfigEO.getCatId().toString());
                if(!catIds.contains(organConfigEO.getCatId())){
                    catIds.add(organConfigEO.getCatId());
                }
            }
        }

        Map<Long,List<PublicCatalogEO>> catalogMap = new HashMap<Long, List<PublicCatalogEO>>();

        for(Long catId:catIds) {
            catalogMap.put(catId,publicCatalogService.getAllChildListByCatId(catId));
        }



        Iterator<Map.Entry<Long, String>> entries = map.entrySet().iterator();

        Map<Long,Map<Long,String>> organCatMap = new HashMap<Long,Map<Long,String>>();

        while (entries.hasNext()) {
            Map.Entry<Long, String> entry = entries.next();
            Map<Long,String> catMap = new HashMap<Long, String>();

            List<PublicCatalogEO> childList = catalogMap.get(Long.parseLong(entry.getValue()));
            List<String> allCatNames = new ArrayList<String>();
            if(childList!=null&&childList.size()>0){
                for(PublicCatalogEO publicCatalogEO:childList){
//                    entry.setValue(entry.getValue()+","+publicCatalogEO.getId());
                    if (publicCatalogEO.getType() != 2){//去除私有
                        allCatNames.add(publicCatalogEO.getName());
                        if(Arrays.asList(catNames).contains(publicCatalogEO.getName())&&publicCatalogEO.getIsParent()==false){
                            catMap.put(publicCatalogEO.getId(),publicCatalogEO.getName());
                        }
                    }
                }
            }
//            String[] allCatIds = entry.getValue().split(",");
            if(allCatNames.containsAll(Arrays.asList(catNames))){
                organIds.add(entry.getKey());
                Map<Long,String> catsMap = new LinkedHashMap<Long, String>();
                for(int i=0;i<catNames.length;i++){
                    Iterator<Map.Entry<Long, String>> tepmEntries = catMap.entrySet().iterator();
                    while (tepmEntries.hasNext()){
                        Map.Entry<Long, String> tempEntry = tepmEntries.next();
                        if(catNames[i].equals(tempEntry.getValue())){
                            catsMap.put(tempEntry.getKey(),tempEntry.getValue());
                        }
                    }
                }


                organCatMap.put(entry.getKey(),catsMap);
            }
        }

        List<OrganEO> organList = new ArrayList<OrganEO>();
        if(organIds.size()>0){
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("organId",organIds);
            organList = organService.getEntities(OrganEO.class,param);
        }

        Map<String,Object> resultMap = new HashMap<String, Object>();
        resultMap.put("organList",organList);
        resultMap.put("organCatMap",organCatMap);
        return resultMap;
    }



}