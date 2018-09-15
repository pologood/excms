package cn.lonsun.common.vo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.util.Jacksons;

import java.util.*;

/**
 * 接收单位集合对象
 * Created by zhusy on 2015-4-28.
 */
public class ReceiveUnitCollectionVO {

    private List<ReceiveUnitVO> selfReceiveUnits ;//本平台接收单位集合

    private Map<String,List<ReceiveUnitVO>> externalReceiveUnits;//外平台接收单位集合


    public ReceiveUnitCollectionVO(){}

    public ReceiveUnitCollectionVO(ReceiveUnitVO[] receiveUnits){
        setReceiveUnitCollection(receiveUnits);
    }

    public ReceiveUnitCollectionVO(String jsonStr){
        ReceiveUnitVO[] receiveUnits = Jacksons.json().fromJsonToObject(jsonStr,ReceiveUnitVO[].class);
        setReceiveUnitCollection(receiveUnits);
    }

    private void setReceiveUnitCollection(ReceiveUnitVO[] receiveUnits){
        if(null != receiveUnits && receiveUnits.length > 0){
            List<ReceiveUnitVO> tempList = null;
            for(ReceiveUnitVO vo : receiveUnits){
                if(null == vo.getIsExternalOrgan() || !vo.getIsExternalOrgan()){//本平台
                    if(null == selfReceiveUnits){
                        selfReceiveUnits = new ArrayList<ReceiveUnitVO>();
                    }
                    selfReceiveUnits.add(vo);
                }else{//外平台
                    if(null == externalReceiveUnits){
                        externalReceiveUnits = new HashMap<String, List<ReceiveUnitVO>>();
                    }
                    if(AppUtil.isEmpty(vo.getPlatformCode())) continue;
                    if(externalReceiveUnits.containsKey(vo.getPlatformCode())){
                        externalReceiveUnits.get(vo.getPlatformCode()).add(vo);
                    }else{
                        tempList = new ArrayList<ReceiveUnitVO>();
                        tempList.add(vo);
                        externalReceiveUnits.put(vo.getPlatformCode(),tempList);
                    }
                }
            }
        }
    }

    public List<ReceiveUnitVO> getSelfReceiveUnits() {
        return selfReceiveUnits;
    }

    public void setSelfReceiveUnits(List<ReceiveUnitVO> selfReceiveUnits) {
        this.selfReceiveUnits = selfReceiveUnits;
    }

    public Map<String, List<ReceiveUnitVO>> getExternalReceiveUnits() {
        return externalReceiveUnits;
    }

    public void setExternalReceiveUnits(Map<String, List<ReceiveUnitVO>> externalReceiveUnits) {
        this.externalReceiveUnits = externalReceiveUnits;
    }

    public boolean hasExternal(){
        return null != externalReceiveUnits && externalReceiveUnits.size()>0;
    }

    public boolean hasSelf(){
        return null != selfReceiveUnits;
    }



}
