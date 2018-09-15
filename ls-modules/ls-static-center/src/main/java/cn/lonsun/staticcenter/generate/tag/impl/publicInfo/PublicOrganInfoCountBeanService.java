package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 根据部门统计部门下的主动公开信息数 ADD REASON. <br/>
 *
 * @date: 2016年8月13日 上午11:49:30 <br/>
 * @author liukun
 */
@Component
public class PublicOrganInfoCountBeanService extends AbstractBeanService {


    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IOrganConfigService organConfigService;
    @Resource
    private IOrganService organService;
    /**
     * 根据部门统计部门下的主动公开信息数
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

        String type = paramObj.getString(GenerateConstant.TYPE);// 类型
        Integer limit = paramObj.getInteger("limit");

        Long exceptOrganId = paramObj.getLong("exceptOrganId");//不统计该单位
        Long exceptCatId = paramObj.getLong("exceptCatId");//不统计绑定该目录的单位
        List<Long> exceptOrganIds = new ArrayList<Long>();
        if(exceptOrganId!=null){
            exceptOrganIds.add(exceptOrganId);
        }
        if(exceptCatId!=null){
            List<Long> list = organConfigService.getOrganIdsByCatId(exceptCatId);
            if(list!=null&&list.size()>0){
                exceptOrganIds.addAll(list);
            }
        }


        PublicContentQueryVO queryVO = new PublicContentQueryVO();
        queryVO.setSiteId(siteId);
        queryVO.setType(PublicConstant.PublicTypeEnum.DRIVING_PUBLIC.getValue());

        if(!AppUtil.isEmpty(type)&&type.equals("1")){//月度
            queryVO.setStartDate(DateUtil.getMonth());
        }else if(!AppUtil.isEmpty(type)&&type.equals("2")){//季度
            queryVO.setStartDate(DateUtil.getQuarter());
        }else if(!AppUtil.isEmpty(type)&&type.equals("3")){//年度
            queryVO.setStartDate(DateUtil.getYear());
        }

        List<Long> existOrganIds = new ArrayList<Long>();
        existOrganIds.addAll(exceptOrganIds);
        List<PublicContentVO> resultList = publicContentService.getCounts(queryVO,null);
        List<PublicContentVO> result = new ArrayList<PublicContentVO>();
        for(PublicContentVO vo:resultList){ //删除不统计的单位
            if(!exceptOrganIds.contains(vo.getOrganId())){
                result.add(vo);
                existOrganIds.add(vo.getOrganId());
            }
            if(result.size()>=limit){
                break;
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
                //将没更新数据的单位添加到列表中
                PublicContentVO vo = new PublicContentVO();
                vo.setOrganId(organVO.getOrganId());
                vo.setOrganName(organVO.getName());
                vo.setCounts(0l);
                result.add(vo);
            }
            if(result.size()>=limit){
                break;
            }
        }

        return result;
    }

}