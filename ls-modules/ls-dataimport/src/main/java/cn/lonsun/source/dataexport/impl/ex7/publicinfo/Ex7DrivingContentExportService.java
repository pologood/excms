package cn.lonsun.source.dataexport.impl.ex7.publicinfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.publicinfo.IDrivingContentExportService;
import cn.lonsun.source.dataexport.vo.PublicContentQueryVO;
import cn.lonsun.target.datamodel.publicinfo.PublicDrivingContentVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ex7主动公开导入
 * @author zhongjun
 */
@Component("ex7DrivingContentExportService")
public class Ex7DrivingContentExportService extends JdbcAble<PublicDrivingContentVO> implements IDrivingContentExportService {

    private static final String manufacturer = "ex7";

    public Ex7DrivingContentExportService() {
        super();
    }

    @Override
    public List<PublicDrivingContentVO> getDataList(PublicContentQueryVO queryVO) {
        //根据单位和目录id获取数据
        List<PublicDrivingContentVO> result = super.queryListBySqlKey(manufacturer, "getXxgkContentByCatIdUnit", queryVO.getOldOrganId(), queryVO.getOldCatId());
        return result;
    }

    @Override
    public List<PublicDrivingContentVO> getDataByIds(PublicContentQueryVO queryVO, Object... ids) {
        return null;
    }


    private Map<String, Map<String, Object>> getXxgkClassList() {
        List<Map<String,Object>> xxgkClassList =  queryMapBySqlKey(manufacturer, "getXxgkClass");
        Map<String,Map<String,Object>> xxgkClassMap = new HashMap<String, Map<String, Object>>();
        for(Object o : xxgkClassList){
            Map<String, Object> map = (Map<String, Object>) o;
            String classId =  AppUtil.getValue(map.get("classId"));
            String classPid =  AppUtil.getValue(map.get("classPid"));
            String className =  AppUtil.getValue(map.get("className"));
            if(!AppUtil.isEmpty(classId)){
                Map<String,Object> temp = new HashMap<String, Object>();
                temp.put("classId",classId);
                temp.put("classPid",classPid);
                temp.put("className",className);
                xxgkClassMap.put(classId,temp);
            }
        }
        return xxgkClassMap;
    }

    private Map<String,List<Map<String,Object>>> getXxgkClassRelList() {
        List<Map<String, Object>> xxgkClassRelList = queryMapBySqlKey(manufacturer, "getXxgkContentClassRe");
        Map<String,List<Map<String,Object>>> xxgkClassRelMap = new HashMap<String,List<Map<String,Object>>>();
        for(Object o : xxgkClassRelList){
            Map<String, Object> map = (Map<String, Object>) o;
            String contentsId = AppUtil.getValue(map.get("contentsId"));
            String classId = AppUtil.getValue(map.get("classId"));
            if(!AppUtil.isEmpty(contentsId)){
                Map<String,Object> temp = new HashMap<String, Object>();
                temp.put(contentsId,classId);
                List<Map<String,Object>> list = null;
                if(xxgkClassRelMap.containsKey(contentsId)){
                    xxgkClassRelMap.get(contentsId).add(temp);
                }else{
                    list = new ArrayList<Map<String, Object>>();
                    list.add(temp);
                    xxgkClassRelMap.put(contentsId, list);
                }
            }
        }
        return xxgkClassRelMap;
    }

}
