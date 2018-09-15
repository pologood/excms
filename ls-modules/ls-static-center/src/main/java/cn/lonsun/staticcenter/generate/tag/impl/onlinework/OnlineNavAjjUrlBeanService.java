package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.staticcenter.eo.AhzjjWsRstEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ParameterMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-15 11:56
 */
@Component
public class OnlineNavAjjUrlBeanService extends AbstractBeanService {

    private static final String SEPARATOR_JING = "#";

    private static final String SEPARATOR_FH = ";";

    private static final String SEPARATOR_EQ = "=";

    public enum ParamsName {
        END_,
        CREATE_,
        ASSIGNEE_,
        KEY,
        MC,
        状态,
        审批结果
    }

    @Override
    public Object getObject(JSONObject paramObj) {

        String wsdl = paramObj.getString("wsdl");
        String method = paramObj.getString("method");
        String curPage = paramObj.getString("curPage");
        String pageSize = paramObj.getString("pageSize");
        String projectName = paramObj.getString("projectName");
        String emptyMsg = paramObj.getString("emptyMsg");
        AssertUtil.isEmpty(wsdl, "wsdl地址为空!");

        String result = getWS(wsdl, method, curPage, pageSize, projectName);

        if (null == result) {
            return emptyMsg;
        }

        if (result.endsWith(SEPARATOR_JING)) {
            result = result.substring(0, result.length() - 1);
        }

        List<String> strs = StringUtils.getListWithString(result, SEPARATOR_JING);
        List<AhzjjWsRstEO> ahzjjeos = new ArrayList<AhzjjWsRstEO>();
        if (null != strs && !strs.isEmpty()) {
            String createDate = null;
            for (String str : strs) {
                Map<String, String> map = getMap(str);
                AhzjjWsRstEO eo = new AhzjjWsRstEO();
                eo.setAssignee(map.get(ParamsName.ASSIGNEE_.toString()));
                if (!AppUtil.isEmpty((createDate = map.get("END_"))) && (createDate.length() >= 10)) {
                    eo.setCreateDate(createDate.substring(0, 10));
                } else {
                    eo.setCreateDate(createDate);
                }
                eo.setDealRst(map.get(ParamsName.审批结果.toString()));
                eo.setStats(map.get(ParamsName.状态.toString()));
                eo.setName(map.get(ParamsName.KEY.toString()));
                eo.setUnit(map.get(ParamsName.MC.toString()));
                ahzjjeos.add(eo);
            }
        }

        return ahzjjeos;
    }


    /**
     * 获取ws结果
     *
     * @param wsdl        wsdl 地址
     * @param method      方法名称
     * @param curPage     页码
     * @param pageSize    每页数量
     * @param projectName 审批事项名称
     * @return
     */
    public static String getWS(String wsdl, String method, String curPage, String pageSize, String projectName) {
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(wsdl);
            call.setOperationName(method);//WSDL里面描述的接口名称
            call.addParameter("currPage", XMLType.XSD_STRING,
                ParameterMode.IN);//接口的参数
            call.addParameter("pagesize", XMLType.XSD_STRING,
                ParameterMode.IN);//接口的参数
            call.addParameter("projectName", XMLType.XSD_STRING,
                ParameterMode.IN);//接口的参数
            call.setReturnClass(String.class);//设置返回类型
            String result = (String) call.invoke(new Object[]{curPage, pageSize, projectName});
            //给方法传递参数，并且调用方法
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String> getMap(String text) {
        Map<String, String> map = new HashMap<String, String>();
        if (AppUtil.isEmpty(text)) {
            return map;
        }
        List<String> strs = StringUtils.getListWithString(text, SEPARATOR_FH);
        if (null != strs) {
            for (String str : strs) {
                if (null != str) {
                    List<String> rsts = StringUtils.getListWithString(str, SEPARATOR_EQ);
                    if (null != rsts) {
                        if (null != rsts && rsts.size() == 2 && null != rsts.get(0)) {
                            map.put(rsts.get(0), rsts.get(1));
                        }
                    }
                }
            }
        }

        return map;
    }

    /**
     * 预处理结果
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        return new HashMap<String, Object>();
    }
}
