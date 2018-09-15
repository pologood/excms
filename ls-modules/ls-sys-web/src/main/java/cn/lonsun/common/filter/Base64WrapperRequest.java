package cn.lonsun.common.filter;

import org.apache.axiom.util.base64.Base64Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @auth zhongjun
 * @createDate 2018-08-02 18:32
 */
public class Base64WrapperRequest extends HttpServletRequestWrapper {

    // 日志
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Base64WrapperRequest(HttpServletRequest request) {
        super(request);
    }

    public String getParameter(String name) {
        return decodeStr(super.getParameter(name));
    }

    public String decodeStr(String value) {
        if(value == null){
            return null;
        }
        byte[] param = Base64Utils.decode(value);
        try {
            return new String(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("参数编码错误", e);
            return value;
        }
    }

    public String[] decodeArr(String[] value) {
        if(value == null){
            return null;
        }
        for(int i = 0; i < value.length; i++){
            value[i] = decodeStr(value[i]);
        }
        return value;
    }

    public boolean needDecode(String name){
        String[] base64Field = super.getParameterValues("base64");
        if(base64Field == null){
            return false;
        }
        for(String field : base64Field){
            if(field.equals(name)){
                return true;
            }
        }
        return false;
    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> ParameterMap = super.getParameterMap();
        String[] base64Field = ParameterMap.get("base64");
        for(String field : base64Field){
            ParameterMap.put("field", decodeArr(ParameterMap.get(field)));
        }
        return ParameterMap;
    }

    public String[] getParameterValues(String name) {
        if(needDecode(name)){
            return  decodeArr(super.getParameterValues(name));
        }
        String[] params = super.getParameterValues(name);
        return params;
    }

    public String getHeader(String name) {
        return super.getHeader(name);
    }

    public String getQueryString() {
        return super.getQueryString();
    }
}
