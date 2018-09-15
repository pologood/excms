/*
 * 2014-12-13 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.util;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.processEngine.vo.ModuleVO;
import cn.lonsun.webservice.processEngine.vo.UserInfoVO;

import java.util.HashMap;
import java.util.Map;

public class EngineUtil {
    private static Map<String, Object> modules = new HashMap<String, Object>(0);
    private enum Codes{
        process_getReModuleByCode
    }
    /*public static ModuleVO getModuleVO(UserInfoVO user,String mcode){
          if(null == modules.get(mcode)){
              String code = Codes.process_getReModuleByCode.toString();
              Object[] params = new Object[]{JSONConvertUtil.toJsonString(user), mcode};
              modules.put(mcode, WSClient.getObject(code, params,ModuleVO.class)) ;
          }
         return (ModuleVO) modules.get(mcode);
    }*/

    public static Map<String,String> parseData(String data){
        Map<String,String> rest = new HashMap<String, String>(0);
        if(null !=data && !"".equals(data)){
            String[] _d1 = data.split(",");
            for(String d2:_d1){
                String[]  _temp = d2.replace("{","").replace("}","").split(":");
                if(_temp.length==2){
                    rest.put(_temp[0],_temp[1].replace("\"",""));
                }

            }
        }
        return rest;
    }


}
