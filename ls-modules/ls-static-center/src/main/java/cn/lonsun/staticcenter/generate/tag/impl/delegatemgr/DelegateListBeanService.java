package cn.lonsun.staticcenter.generate.tag.impl.delegatemgr;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 代表管理列表<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-12<br/>
 */
@Component
public class DelegateListBeanService extends AbstractBeanService {

    @Resource
    private IDelegateService delegateService;
    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String session = context.getParamMap().get("session_");
        String delegation = context.getParamMap().get("delegation");
        String deleGroup = context.getParamMap().get("deleGroup");
        String name = context.getParamMap().get("name");
        DelegateQueryVO queryVO=new DelegateQueryVO();
        queryVO.setSiteId(context.getSiteId());
        queryVO.setSession(session);
        queryVO.setDelegation(delegation);
        queryVO.setDeleGroup(deleGroup);
        queryVO.setName(name);
        List<DelegateEO> list=delegateService.orderByDelegation(queryVO);
        Integer num=paramObj.getInteger("num");
        if(num!=null&&list!=null&&list.size()>=num){
            List<DelegateEO> subList=list.subList(0,num);
            return subList;
        }
        return list;
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        List<DelegateEO> list = (List<DelegateEO>) resultObj;
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        Integer num=paramObj.getInteger("num");
        List<DataDictVO> delegationList = DataDictionaryUtil.getItemList("delegation",context.getSiteId());
        if(delegationList==null||delegationList.size()==0){
            return map;
        }
        if(num!=null){
            map.put("delegationList",delegationList);
//            for(DelegateEO eo:list){
//                DataDictVO dictVO = DataDictionaryUtil.getItem("delegation",eo.getDelegation());
//                if(dictVO!=null){
//                    eo.setDelegation(dictVO.getKey());
//                }
//            }
           return map;
        }

        Boolean isName=paramObj.getBoolean("isName");
        if(isName==null){
            isName=false;
        }
        Map<String,List<DelegateEO>> mapList=new LinkedHashMap<String, List<DelegateEO>>() ;
        for(DataDictVO dictVO: delegationList){
            List<DelegateEO> newList=new ArrayList<DelegateEO>();
            mapList.put(dictVO.getCode(),newList);
        }
        if(list==null||list.size()==0){
            map.put("sumCount",0);
            return map;
        }else{
            map.put("sumCount",list.size());
        }

        map.put("mapList",mapList);
        if(list==null||list.size()==0){
           return map;
        }
        for(DelegateEO eo:list){
            if(mapList.containsKey(eo.getDelegation())){
//                DataDictVO nationDictVO = DataDictionaryUtil.getItem("nation",eo.getNation());
//                if(nationDictVO!=null){
//                    eo.setNation(nationDictVO.getKey());
//                }
//                if(isName&&!StringUtils.isEmpty(eo.getSex())&&!StringUtils.isEmpty(eo.getNation())){
//                    eo.setName(eo.getName()+"("+eo.getSex()+","+eo.getNation()+")");
//                }else if(!StringUtils.isEmpty(eo.getSex())){
//                    eo.setName(eo.getName()+"("+eo.getSex()+")");
//                }else if(!StringUtils.isEmpty(eo.getNation())){
//                    eo.setName(eo.getName()+"("+eo.getNation()+")");
//                }
                mapList.get(eo.getDelegation()).add(eo);
            }
        }
        for(DataDictVO dictVO: delegationList){
            mapList.put(dictVO.getKey(),mapList.get(dictVO.getCode()));
            mapList.remove(dictVO.getCode());
        }
        List<DataDictVO> nationList = DataDictionaryUtil.getItemList("nation",context.getSiteId());
        map.put("nationList",nationList);
        String delegation = context.getParamMap().get("delegation");
        String deleGroup = context.getParamMap().get("deleGroup");
        String name = context.getParamMap().get("name");
        map.put("delegation",delegation);
        map.put("deleGroup",deleGroup);
        map.put("name",name);
        return map;

    }

}

