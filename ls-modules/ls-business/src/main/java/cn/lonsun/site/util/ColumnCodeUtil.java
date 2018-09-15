package cn.lonsun.site.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.vo.ColumnMgrVO;

import java.util.List;

/**
 * Created by lonsun on 2017-4-1.
 */
public class ColumnCodeUtil {

     private static   IColumnConfigService columnConfigService = SpringContextHolder.getBean("columnConfigService");

    /**
     * 根据typeCode过滤
     * @param columnMgrEO
     * @param columnMgrVOs
     * @param indicatorId(初始调用传null)
     * @return
     */
    public   static   Boolean  dealHaveTyeCode(ColumnMgrEO columnMgrEO,List<ColumnMgrVO> columnMgrVOs,Long indicatorId,String code){

        Boolean flag =false;
        if(columnMgrEO.getIsParent().equals(0)&& AppUtil.isEmpty(indicatorId)){
            if(columnMgrEO.getColumnTypeCode().equals(code)){
                ColumnMgrVO vo = new ColumnMgrVO();
                AppUtil.copyProperties(vo, columnMgrEO);
                columnMgrVOs.add(vo);
            }

        }
        else {
            List<ColumnMgrEO> list = columnConfigService.getColumnTree(columnMgrEO.getIndicatorId(), null);
            for( ColumnMgrEO columnMgr:   list){
                if(columnMgr.getIsParent().equals(1)){
                    flag =  dealHaveTyeCode(columnMgr, columnMgrVOs, columnMgr.getIndicatorId(),code);
                    if(AppUtil.isEmpty(indicatorId)&&flag){
                        ColumnMgrVO vo = new ColumnMgrVO();
                        AppUtil.copyProperties(vo, columnMgrEO);
                        Boolean has =false;
                        for( ColumnMgrVO mgrVO:columnMgrVOs){
                            if(vo.getIndicatorId().equals(mgrVO.getIndicatorId())){
                                has=true;
                                break;
                            }


                        }
                        if(!has){
                            columnMgrVOs.add(vo);

                        }
                    }

                }
                else if(columnMgr.getIsParent().equals(0)){
                    if(columnMgr.getColumnTypeCode().equals(code)){
                        if(AppUtil.isEmpty(indicatorId)){
                            ColumnMgrVO vo = new ColumnMgrVO();
                            AppUtil.copyProperties(vo, columnMgrEO);
                            Boolean has =false;
                            for( ColumnMgrVO mgrVO:columnMgrVOs){
                                if(vo.getIndicatorId().equals(mgrVO.getIndicatorId())){
                                    has=true;
                                    break;
                                }


                            }
                            if(!has){
                                columnMgrVOs.add(vo);

                            }
                        }else {
                            return true;

                        }

                    }
                }

            }
        }
        return flag;
    }


}
