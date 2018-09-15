package cn.lonsun.common.vo;

import cn.lonsun.common.util.AppUtil;

import cn.lonsun.core.exception.util.Jacksons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单位部门接收对象集合VO
 * Created by zhusy on 2015-5-21.
 */
public class RecUnitOrganCollectionVO {

    private List<RecUnitOrganVO> selfRecUnitOrgans ;//本平台接收对象集合

    private Map<String,List<RecUnitOrganVO>> externalRecUnitOrgans;//外平台接收对象集合

    public RecUnitOrganCollectionVO(){}

    public RecUnitOrganCollectionVO(RecUnitOrganVO[] recUnitOrgans){
        setRecUnitOrganCollectionVO(recUnitOrgans);
    }

    public RecUnitOrganCollectionVO(String jsonStr){
        RecUnitOrganVO[] recUnitOrgans = Jacksons.json().fromJsonToObject(jsonStr,RecUnitOrganVO[].class);
        setRecUnitOrganCollectionVO(recUnitOrgans);
    }

    private void setRecUnitOrganCollectionVO(RecUnitOrganVO[] recUnitOrgans){
        if(null != recUnitOrgans && recUnitOrgans.length > 0){
            List<RecUnitOrganVO> tempList = null;
            for(RecUnitOrganVO vo : recUnitOrgans){
                if(null == vo.getIsExternalOrgan() || !vo.getIsExternalOrgan()){//本平台
                    if(null == selfRecUnitOrgans){
                        selfRecUnitOrgans = new ArrayList<RecUnitOrganVO>();
                    }
                    selfRecUnitOrgans.add(vo);
                }else{//外平台
                    if(null == externalRecUnitOrgans){
                        externalRecUnitOrgans = new HashMap<String, List<RecUnitOrganVO>>();
                    }
                    if(AppUtil.isEmpty(vo.getPlatformCode())) continue;
                    if(externalRecUnitOrgans.containsKey(vo.getPlatformCode())){
                        externalRecUnitOrgans.get(vo.getPlatformCode()).add(vo);
                    }else{
                        tempList = new ArrayList<RecUnitOrganVO>();
                        tempList.add(vo);
                        externalRecUnitOrgans.put(vo.getPlatformCode(),tempList);
                    }
                }
            }
        }
    }

    public Map<String, List<RecUnitOrganVO>> getExternalRecUnitOrgans() {
        return externalRecUnitOrgans;
    }

    public void setExternalRecUnitOrgans(Map<String, List<RecUnitOrganVO>> externalRecUnitOrgans) {
        this.externalRecUnitOrgans = externalRecUnitOrgans;
    }

    public List<RecUnitOrganVO> getSelfRecUnitOrgans() {
        return selfRecUnitOrgans;
    }

    public void setSelfRecUnitOrgans(List<RecUnitOrganVO> selfRecUnitOrgans) {
        this.selfRecUnitOrgans = selfRecUnitOrgans;
    }

    public boolean hasExternal(){
        return null != externalRecUnitOrgans && externalRecUnitOrgans.size()>0;
    }

    public boolean hasSelf(){
        return null != selfRecUnitOrgans && selfRecUnitOrgans.size() > 0;
    }

    /**
     *  获取部门列表
     */
    public List<RecUnitOrganVO> getRecOrganList(){
        List<RecUnitOrganVO> recOrganList = new ArrayList<RecUnitOrganVO>();
        if(null != selfRecUnitOrgans && selfRecUnitOrgans.size() > 0){
            for(RecUnitOrganVO vo : selfRecUnitOrgans){
                if(vo.getType().equals(1)){
                    recOrganList.add(vo);
                }
            }
        }
        if(null != externalRecUnitOrgans && externalRecUnitOrgans.size() > 0){
            List<RecUnitOrganVO> tempList = null;
            for(String key : externalRecUnitOrgans.keySet()){
                tempList = externalRecUnitOrgans.get(key);
                if(null != tempList && tempList.size() > 0){
                    for(RecUnitOrganVO vo : tempList){
                        if(vo.getType().equals(1)){
                            recOrganList.add(vo);
                        }
                    }
                }
            }
        }
        return recOrganList;
    }
}
