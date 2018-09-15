package cn.lonsun.staticcenter.generate.tag.impl.link;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 根据部门统计部门下的发文信息数 ADD REASON. <br/>
 *
 * @date: 2016年8月22日 上午11:49:30 <br/>
 * @author liukun
 */
@Component
public class SiteUpdateCountBeanService extends AbstractBeanService {


    @Resource
    private IBaseContentService baseContentService;

    @Resource
    private IOrganService organService;

    /**
     * 根据部门统计部门下的发文信息数
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

        Integer limit = paramObj.getInteger("num");

        ContentPageVO queryVO = new ContentPageVO();
        queryVO.setSiteId(siteId);
        queryVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());

        List<BaseContentVO> countsList = baseContentService.getCounts(queryVO,null);
        Long[] organIds = new Long[countsList.size()];

        for(int i=0;i<countsList.size();i++){
            organIds[i] = countsList.get(i).getOrganId();
        }
        List<OrganEO> units = organService.getDirrectlyUpLevelUnits(organIds);//查询直属单位
        Map<Long,String> organMap = new HashMap<Long, String>();

        Map<Long,Long> tempMap = new HashMap<Long, Long>();
        for(int i=0;i<countsList.size();i++){
            OrganEO unit = units.get(i);
            organMap.put(unit.getOrganId(),unit.getName());
            if(tempMap.containsKey(unit.getOrganId())){
                Long sum = tempMap.get(unit.getOrganId())+countsList.get(i).getCounts();
                tempMap.put(unit.getOrganId(),sum);
            }else{
                tempMap.put(unit.getOrganId(),countsList.get(i).getCounts());
            }
        }
        ArrayList<Map.Entry<Long,Long>> entries = sortMap(tempMap);
        int num = entries.size()>limit?limit:entries.size();

        List<BaseContentVO> resultList = new ArrayList<BaseContentVO>();
        for(int i=0;i<num;i++){
            BaseContentVO vo = new BaseContentVO();
            vo.setOrganId(entries.get(i).getKey());
            vo.setOrganName(organMap.get(entries.get(i).getKey()));
            vo.setCounts(entries.get(i).getValue());
            resultList.add(vo);
        }

        return resultList;
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