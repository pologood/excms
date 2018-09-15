package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

        import cn.lonsun.common.util.AppUtil;
        import cn.lonsun.core.base.util.StringUtils;
        import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
        import com.alibaba.fastjson.JSONObject;
        import org.apache.axis.client.Call;
        import org.apache.axis.client.Service;
        import org.springframework.stereotype.Component;

        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-15 11:56
 */
@Component
public class OnlineNavAjjUrlStatisBeanService extends AbstractBeanService {

    private static final String SEPARATOR_MAO = "：";

    private static final String SEPARATOR_FH = ";";

    @Override
    public Object getObject(JSONObject paramObj) {

        String wsdl = paramObj.getString("wsdl");
        String method = paramObj.getString("method");
        String emptyMsg = paramObj.getString("emptyMsg");

        String result = getWS(wsdl,method);

        if(null == result) {
            return emptyMsg;
        }

        return getMap(result);
    }


    /**
     * 获取ws结果
     * @param wsdl        wsdl 地址
     * @param method      方法名称
     * @return
     */
    public static String getWS(String wsdl,String method) {
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(wsdl);
            call.setOperationName(method);//WSDL里面描述的接口名称
            String result = (String) call.invoke(new Object[]{});
            //给方法传递参数，并且调用方法
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String,String> getMap(String text) {
        Map<String,String> map = new HashMap<String, String>();
        if(AppUtil.isEmpty(text)) {
            return map;
        }
        List<String> strs = StringUtils.getListWithString(text,SEPARATOR_FH);
        if(null != strs) {
            for(String str : strs) {
                if(null != str) {
                    List<String> rsts = StringUtils.getListWithString(str,SEPARATOR_MAO);
                    if(null != rsts) {
                        if(null != rsts && rsts.size() == 2 && null != rsts.get(0)) {
                            map.put(rsts.get(0),rsts.get(1));
                        }
                    }
                }
            }
        }

        return map;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        return new HashMap<String, Object>();
    }
}
